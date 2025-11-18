package ca.tweetzy.markets.model.sync;

import ca.tweetzy.flight.database.sync.RedisSyncManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.database.DataManager;
import ca.tweetzy.markets.settings.Translations;
import ca.tweetzy.flight.settings.TranslationManager;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages cross-server notifications
 * Sends notifications to players on other servers via Redis
 */
public class CrossServerNotificationManager {
	
	private final Markets plugin;
	private final RedisSyncManager redisSyncManager;
	private final PlayerPresenceTracker presenceTracker;
	private static final String NOTIFICATION_CHANNEL = "markets_notifications";
	private final Map<String, Long> processedNotifications = new ConcurrentHashMap<>();
	private static final long NOTIFICATION_TTL = 24 * 60 * 60 * 1000; // 24 hours
	private Thread subscriberThread;
	private JedisPubSub pubSub;
	private volatile boolean shutdown = false;
	
	public CrossServerNotificationManager(@NonNull Markets plugin, @NonNull DataManager dataManager) {
		this.plugin = plugin;
		this.redisSyncManager = dataManager.getRedisSyncManager();
		this.presenceTracker = new PlayerPresenceTracker(plugin);
		
		if (redisSyncManager != null && redisSyncManager.isEnabled()) {
			// Subscribe to notification channel
			subscribeToNotifications();
			
			// Cleanup old notifications periodically
			new BukkitRunnable() {
				@Override
				public void run() {
					cleanupProcessedNotifications();
				}
			}.runTaskTimerAsynchronously(plugin, 3600 * 20L, 3600 * 20L); // Every hour
		}
	}
	
	/**
	 * Subscribe to notification channel
	 */
	private void subscribeToNotifications() {
		// Subscribe to notification channel using a separate thread
		subscriberThread = new Thread(() -> {
			while (!shutdown && redisSyncManager != null && redisSyncManager.isEnabled() && !Thread.currentThread().isInterrupted()) {
				Jedis jedis = null;
				try {
					JedisPool jedisPool = redisSyncManager.getJedisPool();
					if (jedisPool == null || jedisPool.isClosed()) {
						Thread.sleep(5000);
						continue;
					}
					
					// Get connection outside try-with-resources since subscribe() blocks
					jedis = jedisPool.getResource();
					
					// JedisPubSub is an abstract class, not an interface, so we need to extend it
					pubSub = new JedisPubSub() {
						@Override
						public void onMessage(String channel, String message) {
							if (NOTIFICATION_CHANNEL.equals(channel)) {
								// Check if plugin is enabled before scheduling task
								if (!plugin.isEnabled() || shutdown) {
									return;
								}
								
								try {
									Bukkit.getScheduler().runTask(plugin, () -> {
										if (!plugin.isEnabled() || shutdown) {
											return;
										}
										try {
											NotificationEvent event = NotificationEvent.fromJson(message);
											handleIncomingNotification(event);
										} catch (IllegalStateException | NoClassDefFoundError e) {
											// Classloader issue - plugin may be disabled/reloading
											if (plugin.isEnabled() && !shutdown) {
												plugin.getLogger().warning("Failed to process notification (classloader issue): " + e.getMessage());
											}
										} catch (Exception e) {
											if (plugin.isEnabled() && !shutdown) {
												plugin.getLogger().warning("Failed to process notification: " + e.getMessage());
											}
										}
									});
								} catch (org.bukkit.plugin.IllegalPluginAccessException e) {
									// Plugin disabled between check and scheduling - ignore silently
									if (plugin.isEnabled() && !shutdown) {
										plugin.getLogger().warning("Failed to schedule notification task: " + e.getMessage());
									}
								} catch (IllegalStateException e) {
									// Plugin disabled, ignore
									if (plugin.isEnabled() && !shutdown) {
										plugin.getLogger().warning("Failed to schedule notification task: " + e.getMessage());
									}
								}
							}
						}
					};
					
					// This call blocks until unsubscribe() is called
					jedis.subscribe(pubSub, NOTIFICATION_CHANNEL);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					break;
				} catch (Exception ex) {
					if (!shutdown && redisSyncManager != null && redisSyncManager.isEnabled() && !Thread.currentThread().isInterrupted()) {
						// Only log if not shutting down
						if (plugin.isEnabled()) {
							plugin.getLogger().warning("Notification subscriber error: " + ex.getMessage());
							if (ex.getCause() != null) {
								ex.getCause().printStackTrace();
							}
						}
						try {
							Thread.sleep(5000);
						} catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
							break;
						}
					}
				} finally {
					// Close connection if it was opened
					if (jedis != null) {
						try {
							// Unsubscribe before closing
							if (pubSub != null && pubSub.isSubscribed()) {
								pubSub.unsubscribe();
							}
						} catch (Exception e) {
							// Ignore unsubscribe errors
						}
						try {
							// Return connection to pool instead of closing directly
							// Jedis 5.1.0 handles connection pooling differently
							if (jedis.isConnected()) {
								jedis.close();
							}
						} catch (Exception e) {
							// Ignore close errors - connection may already be closed or invalid
						}
					}
				}
			}
		}, "Markets-NotificationSubscriber");
		
		subscriberThread.setDaemon(true);
		subscriberThread.start();
	}
	
	/**
	 * Send a notification to a player (cross-server or local)
	 */
	public void sendNotification(@NonNull UUID playerId, @NonNull NotificationEvent.NotificationType type, @NonNull Map<String, Object> data) {
		if (redisSyncManager == null || !redisSyncManager.isEnabled()) {
			// No Redis - send locally if player is online
			Player player = Bukkit.getPlayer(playerId);
			if (player != null && player.isOnline()) {
				handleNotificationLocally(player, type, data);
			}
			return;
		}
		
		// Check local server first (fast path - avoids async delay)
		Player localPlayer = Bukkit.getPlayer(playerId);
		if (localPlayer != null && localPlayer.isOnline()) {
			// Player is on this server - send directly
			handleNotificationLocally(localPlayer, type, data);
			return;
		}
		
		// Use async lookup to find player on other servers
		presenceTracker.getPlayerServerAsync(playerId, serverId -> {
			if (serverId == null) {
				// Player is offline - queue notification in database
				queueOfflineNotification(playerId, type, data);
				return;
			}
			
			if (serverId.equals(redisSyncManager.getServerId())) {
				// Player is on this server - double-check and send directly
				// (might have come online between the initial check and async lookup)
				Player player = Bukkit.getPlayer(playerId);
				if (player != null && player.isOnline()) {
					handleNotificationLocally(player, type, data);
				} else {
					// Player went offline, queue notification
					queueOfflineNotification(playerId, type, data);
				}
			} else {
				// Player is on another server - send via Redis
				publishNotification(playerId, type, data);
			}
		});
	}
	
	/**
	 * Publish notification to Redis
	 */
	private void publishNotification(@NonNull UUID playerId, @NonNull NotificationEvent.NotificationType type, @NonNull Map<String, Object> data) {
		if (!plugin.isEnabled()) {
			return;
		}
		
		try {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				if (!plugin.isEnabled() || shutdown) {
					return;
				}
				try {
					NotificationEvent event = new NotificationEvent(
						redisSyncManager.getServerId(),
						type,
						playerId,
						data
					);
					
					JedisPool jedisPool = redisSyncManager.getJedisPool();
					if (jedisPool == null || jedisPool.isClosed()) {
						plugin.getLogger().warning("Cannot publish notification: JedisPool is null or closed");
						return;
					}
					
					try (Jedis jedis = jedisPool.getResource()) {
						String json = event.toJson();
						jedis.publish(NOTIFICATION_CHANNEL, json);
						plugin.getLogger().fine("Published notification to Redis for player " + playerId + " type " + type);
					} catch (Exception e) {
						// Connection may be invalid - ignore if shutting down
						if (plugin.isEnabled() && !shutdown) {
							plugin.getLogger().warning("Failed to publish notification to Redis for player " + playerId + ": " + e.getMessage());
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					if (plugin.isEnabled() && !shutdown) {
						plugin.getLogger().warning("Failed to create/publish notification for player " + playerId + ": " + e.getMessage());
						e.printStackTrace();
					}
				}
			});
		} catch (org.bukkit.plugin.IllegalPluginAccessException e) {
			// Plugin disabled between check and scheduling - ignore silently
			if (plugin.isEnabled() && !shutdown) {
				plugin.getLogger().warning("Failed to schedule notification publish task: " + e.getMessage());
			}
		} catch (IllegalStateException e) {
			// Plugin disabled, ignore
			if (plugin.isEnabled() && !shutdown) {
				plugin.getLogger().warning("Failed to schedule notification publish task: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Handle notification locally (player is on this server)
	 */
	private void handleNotificationLocally(@NonNull Player player, @NonNull NotificationEvent.NotificationType type, @NonNull Map<String, Object> data) {
		if (!plugin.isEnabled()) {
			return;
		}
		
		try {
			Bukkit.getScheduler().runTask(plugin, () -> {
				if (!plugin.isEnabled() || shutdown) {
					return;
				}
				switch (type) {
				case PURCHASE:
					String buyerName = (String) data.get("buyer_name");
					String itemName = (String) data.get("item_name");
					Object quantity = data.get("purchase_quantity");
					Object price = data.get("purchase_price");
					
					Common.tell(player, TranslationManager.string(player, Translations.MARKET_ITEM_BOUGHT_SELLER,
						"buyer_name", buyerName != null ? buyerName : "Unknown",
						"item_name", itemName != null ? itemName : "Item",
						"purchase_quantity", quantity != null ? quantity.toString() : "1",
						"purchase_price", price != null ? price.toString() : "0"
					));
					break;
					
				case OUT_OF_STOCK:
					Common.tell(player, TranslationManager.string(player, Translations.ITEM_OUT_OF_STOCK));
					// Could add more specific message here
					break;
					
				case OFFER_ACCEPTED:
					String ownerName = (String) data.get("owner_name");
					String marketItemName = (String) data.get("market_item_name");
					Common.tell(player, TranslationManager.string(player, Translations.OFFER_ACCEPTED,
						"owner_name", ownerName != null ? ownerName : "Someone",
						"market_item_name", marketItemName != null ? marketItemName : "an item"
					));
					break;
					
				case OFFER_REJECTED:
					String rejectOwnerName = (String) data.get("owner_name");
					String rejectMarketItemName = (String) data.get("market_item_name");
					String rejectReason = (String) data.get("reject_reason");
					
					if (rejectReason != null) {
						switch (rejectReason) {
							case "ITEM_NO_LONGER_AVAILABLE":
								Common.tell(player, TranslationManager.string(player, Translations.OFFER_REJECT_NOT_AVAILABLE,
									"owner_name", rejectOwnerName != null ? rejectOwnerName : "Someone"
								));
								break;
							case "INSUFFICIENT_STOCK":
								Common.tell(player, TranslationManager.string(player, Translations.OFFER_REJECT_INSUFFICIENT_STOCK,
									"owner_name", rejectOwnerName != null ? rejectOwnerName : "Someone",
									"market_item_name", rejectMarketItemName != null ? rejectMarketItemName : "an item"
								));
								break;
							case "NOT_ACCEPTED":
							default:
								Common.tell(player, TranslationManager.string(player, Translations.OFFER_REJECT_NOT_ACCEPTED,
									"owner_name", rejectOwnerName != null ? rejectOwnerName : "Someone",
									"market_item_name", rejectMarketItemName != null ? rejectMarketItemName : "an item"
								));
								break;
						}
					} else {
						Common.tell(player, TranslationManager.string(player, Translations.OFFER_REJECT_NOT_ACCEPTED,
							"owner_name", rejectOwnerName != null ? rejectOwnerName : "Someone",
							"market_item_name", rejectMarketItemName != null ? rejectMarketItemName : "an item"
						));
					}
					break;
					
				case OFFER_RECEIVED:
					String offerSenderName = (String) data.get("sender_name");
					Common.tell(player, TranslationManager.string(player, Translations.OFFER_RECEIVED,
						"sender_name", offerSenderName != null ? offerSenderName : "Someone"
					));
					break;
					
				case PAYMENT_RECEIVED:
					Common.tell(player, TranslationManager.string(player, Translations.REQUEST_PAYMENT));
					break;
					
				case REQUEST_FULFILLED:
					String fulfillName = (String) data.get("fulfill_name");
					String requestItemName = (String) data.get("request_item_name");
					Common.tell(player, TranslationManager.string(player, Translations.REQUEST_FULFILLED,
						"fulfill_name", fulfillName != null ? fulfillName : "Someone",
						"request_item_name", requestItemName != null ? requestItemName : "an item"
					));
					break;
					
				case REQUEST_NEW:
					// New request created - could notify player about new request opportunity
					// For now, using generic message. Could be enhanced with specific request details
					Common.tell(player, TranslationManager.string(player, Translations.REQUEST_PAYMENT));
					break;
					
				case REVIEW_CREATED:
					// New review created for player's market - could notify with review details
					// For now, using generic message. Could be enhanced with specific review details
					Common.tell(player, TranslationManager.string(player, Translations.REQUEST_PAYMENT));
					break;
				}
			});
		} catch (org.bukkit.plugin.IllegalPluginAccessException e) {
			// Plugin disabled between check and scheduling - ignore silently
			if (plugin.isEnabled() && !shutdown) {
				plugin.getLogger().warning("Failed to schedule notification task: " + e.getMessage());
			}
		} catch (IllegalStateException e) {
			// Plugin disabled, ignore
			if (plugin.isEnabled() && !shutdown) {
				plugin.getLogger().warning("Failed to schedule notification task: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Queue notification for offline player
	 */
	private void queueOfflineNotification(@NonNull UUID playerId, @NonNull NotificationEvent.NotificationType type, @NonNull Map<String, Object> data) {
		// Store in database for later delivery
		// This would require a new table or extending an existing one
		// For now, we'll just log it - can be enhanced later
		plugin.getLogger().info("Queued notification for offline player: " + playerId + " type: " + type);
	}
	
	/**
	 * Handle incoming notification from Redis
	 */
	public void handleIncomingNotification(@NonNull NotificationEvent event) {
		// Check if we've already processed this notification
		if (processedNotifications.containsKey(event.getEventId())) {
			return;
		}
		
		// Mark as processed
		processedNotifications.put(event.getEventId(), System.currentTimeMillis());
		
		// Don't process notifications from this server
		if (event.getServerId().equals(redisSyncManager.getServerId())) {
			return;
		}
		
		// Check if target player is online on this server
		Player player = Bukkit.getPlayer(event.getTargetPlayer());
		if (player != null && player.isOnline()) {
			handleNotificationLocally(player, event.getType(), event.getData());
		} else {
			// Player not online - queue for later
			queueOfflineNotification(event.getTargetPlayer(), event.getType(), event.getData());
		}
	}
	
	/**
	 * Clean up old processed notifications
	 */
	private void cleanupProcessedNotifications() {
		long now = System.currentTimeMillis();
		processedNotifications.entrySet().removeIf(entry -> (now - entry.getValue()) > NOTIFICATION_TTL);
	}
	
	/**
	 * Shutdown the notification manager and clean up resources
	 */
	public void shutdown() {
		shutdown = true;
		
		// Unsubscribe from Redis channel
		if (pubSub != null) {
			try {
				if (pubSub.isSubscribed()) {
					pubSub.unsubscribe();
				}
			} catch (Exception e) {
				// Ignore unsubscribe errors
			}
		}
		
		// Interrupt and wait for subscriber thread to stop
		if (subscriberThread != null && subscriberThread.isAlive()) {
			subscriberThread.interrupt();
			try {
				subscriberThread.join(5000); // Wait up to 5 seconds
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		// Clear processed notifications
		processedNotifications.clear();
	}
}
