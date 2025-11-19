package ca.tweetzy.markets.model.sync;

import ca.tweetzy.flight.database.sync.DatabaseEvent;
import ca.tweetzy.flight.database.sync.DatabaseEventListener;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.api.market.core.MarketUser;
import ca.tweetzy.markets.api.market.BankEntry;
import ca.tweetzy.markets.api.market.Request;
import ca.tweetzy.markets.api.market.Transaction;
import ca.tweetzy.markets.api.market.offer.Offer;
import ca.tweetzy.markets.api.market.core.Rating;
import ca.tweetzy.markets.api.currency.Payment;
import ca.tweetzy.markets.database.DataManager;
import ca.tweetzy.markets.impl.*;
import ca.tweetzy.markets.model.manager.*;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central manager for cross-server synchronization
 * Handles incoming events and updates local cache
 */
public class CrossServerSyncManager implements DatabaseEventListener {
	
	private final Markets plugin;
	private final DataManager dataManager;
	private final Map<String, Long> processedEvents = new ConcurrentHashMap<>();
	private static final long EVENT_TTL = 24 * 60 * 60 * 1000; // 24 hours
	private BukkitRunnable cleanupTask;
	
	public CrossServerSyncManager(@NonNull Markets plugin, @NonNull DataManager dataManager) {
		this.plugin = plugin;
		this.dataManager = dataManager;
		
		// Register this as a listener
		if (dataManager.getRedisSyncManager() != null && dataManager.getRedisSyncManager().isEnabled()) {
			dataManager.registerDatabaseEventListener(this);
			
			// Cleanup old events periodically
			this.cleanupTask = new BukkitRunnable() {
				@Override
				public void run() {
					cleanupProcessedEvents();
				}
			};
			this.cleanupTask.runTaskTimerAsynchronously(plugin, 3600 * 20L, 3600 * 20L); // Every hour
		}
	}
	
	@Override
	public void onDatabaseEvent(@NonNull DatabaseEvent event) {
		// Check plugin state before processing - wrap in try-catch because isEnabled() might throw
		boolean pluginEnabled;
		try {
			pluginEnabled = plugin.isEnabled();
		} catch (IllegalStateException | NoClassDefFoundError e) {
			// Classloader closed - plugin is disabled/reloading, silently ignore
			return;
		}
		
		if (!pluginEnabled) {
			return; // Plugin disabled, ignore event
		}
		
		// Check if we've already processed this event
		if (processedEvents.containsKey(event.getEventId())) {
			return; // Already processed
		}
		
		// Mark as processed
		processedEvents.put(event.getEventId(), System.currentTimeMillis());
		
		// Process event asynchronously with proper exception handling
		try {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				// Double-check plugin state in async thread
				boolean stillEnabled;
				try {
					stillEnabled = plugin.isEnabled();
				} catch (IllegalStateException | NoClassDefFoundError e) {
					// Classloader closed, silently ignore
					return;
				}
				
				if (!stillEnabled) {
					return; // Plugin disabled, ignore
				}
				
				try {
					handleDatabaseEvent(event);
				} catch (Exception e) {
					// Only log if plugin is still enabled
					try {
						if (plugin.isEnabled()) {
							Common.log("&cError processing database event: " + e.getMessage());
							e.printStackTrace();
						}
					} catch (IllegalStateException | NoClassDefFoundError ignored) {
						// Classloader closed, silently ignore
					}
				}
			});
		} catch (IllegalPluginAccessException e) {
			// Plugin disabled between check and scheduling - ignore silently
			// Don't check plugin.isEnabled() here as it might throw
		} catch (IllegalStateException e) {
			// Plugin disabled or classloader closed - check if it's a classloader issue
			String msg = e.getMessage();
			if (msg != null && (msg.contains("zip file closed") || msg.contains("classloader"))) {
				// Silently ignore classloader closed errors
				return;
			}
			// For other errors, try to check plugin state safely
			try {
				if (plugin.isEnabled()) {
					Common.log("&cFailed to schedule database event task: " + msg);
				}
			} catch (IllegalStateException | NoClassDefFoundError ignored) {
				// Classloader closed, silently ignore
			}
		} catch (NoClassDefFoundError e) {
			// Classloader issue - silently ignore
		}
	}
	
	@Override
	public String getTableName() {
		return null; // Listen to all tables
	}
	
	@Override
	public String getTablePrefix() {
		return dataManager.getTablePrefix();
	}
	
	/**
	 * Handle a database event
	 */
	private void handleDatabaseEvent(@NonNull DatabaseEvent event) {
		String tableName = event.getTableName();
		Map<String, Object> data = event.getData();
		
		switch (tableName) {
			case "markets":
				handleMarketEvent(event.getEventType(), data);
				break;
			case "category":
				handleCategoryEvent(event.getEventType(), data);
				break;
			case "category_item":
				handleMarketItemEvent(event.getEventType(), data);
				break;
			case "user":
				handleMarketUserEvent(event.getEventType(), data);
				break;
			case "offer":
				handleOfferEvent(event.getEventType(), data);
				break;
			case "review":
				handleRatingEvent(event.getEventType(), data);
				break;
			case "request":
				handleRequestEvent(event.getEventType(), data);
				break;
			case "transaction":
				handleTransactionEvent(event.getEventType(), data);
				break;
			case "bank_entry":
				handleBankEntryEvent(event.getEventType(), data);
				break;
			case "payment":
				handlePaymentEvent(event.getEventType(), data);
				break;
			default:
				// Unknown table, ignore
				break;
		}
	}
	
	private void handleMarketEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull Map<String, Object> data) {
		MarketManager marketManager = Markets.getMarketManager();
		
		if (eventType == DatabaseEvent.EventType.DELETE) {
			UUID marketId = UUID.fromString((String) data.get("id"));
			Market market = marketManager.getByUUID(marketId);
			if (market != null) {
				marketManager.remove(market);
			}
		} else {
			// INSERT or UPDATE - reload from database
			UUID marketId = UUID.fromString((String) data.get("id"));
			marketManager.load(); // Reload all markets (could be optimized to reload single market)
		}
	}
	
	private void handleCategoryEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull Map<String, Object> data) {
		CategoryManager categoryManager = Markets.getCategoryManager();
		
		if (eventType == DatabaseEvent.EventType.DELETE) {
			UUID categoryId = UUID.fromString((String) data.get("id"));
			Category category = categoryManager.getByUUID(categoryId);
			if (category != null) {
				categoryManager.remove(category);
			}
		} else {
			// INSERT or UPDATE - reload from database
			categoryManager.load();
		}
	}
	
	private void handleMarketItemEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull Map<String, Object> data) {
		CategoryItemManager itemManager = Markets.getCategoryItemManager();
		
		if (eventType == DatabaseEvent.EventType.DELETE) {
			UUID itemId = UUID.fromString((String) data.get("id"));
			MarketItem item = itemManager.getByUUID(itemId);
			if (item != null) {
				itemManager.remove(item);
				// Also remove from category
				Category category = Markets.getCategoryManager().getByUUID(item.getOwningCategory());
				if (category != null) {
					category.getItems().removeIf(categoryItem -> categoryItem.getId().equals(itemId));
				}
			}
		} else {
			// INSERT or UPDATE - reload from database
			UUID categoryId = UUID.fromString((String) data.get("owning_category"));
			Category category = Markets.getCategoryManager().getByUUID(categoryId);
			if (category != null) {
				// Reload items for this category
				Markets.getDataManager().getMarketItemsByCategory(categoryId, (error, items) -> {
					if (error == null && items != null) {
						// Remove old items from manager that are no longer in the category
						category.getItems().forEach(oldItem -> {
							if (!items.stream().anyMatch(newItem -> newItem.getId().equals(oldItem.getId()))) {
								itemManager.remove(oldItem);
							}
						});
						
						// Clear and update category items
						category.getItems().clear();
						category.getItems().addAll(items);
						
						// Ensure all items are in the CategoryItemManager cache
						items.forEach(item -> {
							if (itemManager.getByUUID(item.getId()) == null) {
								itemManager.add(item);
							}
						});
					}
				});
			}
		}
	}
	
	private void handleMarketUserEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull Map<String, Object> data) {
		PlayerManager playerManager = Markets.getPlayerManager();
		
		if (eventType == DatabaseEvent.EventType.DELETE) {
			UUID userId = UUID.fromString((String) data.get("id"));
			MarketUser user = playerManager.get(userId);
			if (user != null) {
				playerManager.remove(userId);
			}
		} else {
			// INSERT or UPDATE - reload from database
			playerManager.load();
		}
	}
	
	private void handleOfferEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull Map<String, Object> data) {
		OfferManager offerManager = Markets.getOfferManager();
		
		if (eventType == DatabaseEvent.EventType.DELETE) {
			UUID offerId = UUID.fromString((String) data.get("id"));
			Offer offer = offerManager.getByUUID(offerId);
			if (offer != null) {
				offerManager.remove(offer);
			}
		} else {
			// INSERT or UPDATE - reload from database
			offerManager.load();
		}
	}
	
	private void handleRatingEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull Map<String, Object> data) {
		RatingManager ratingManager = Markets.getRatingManager();
		
		if (eventType == DatabaseEvent.EventType.DELETE) {
			UUID ratingId = UUID.fromString((String) data.get("id"));
			Rating rating = ratingManager.getByUUID(ratingId);
			if (rating != null) {
				ratingManager.remove(rating);
			}
		} else {
			// INSERT or UPDATE - reload from database
			ratingManager.load();
		}
	}
	
	private void handleRequestEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull Map<String, Object> data) {
		RequestManager requestManager = Markets.getRequestManager();
		
		if (eventType == DatabaseEvent.EventType.DELETE) {
			UUID requestId = UUID.fromString((String) data.get("id"));
			Request request = requestManager.getByUUID(requestId);
			if (request != null) {
				requestManager.remove(request);
			}
		} else {
			// INSERT or UPDATE - reload from database
			requestManager.load();
		}
	}
	
	private void handleTransactionEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull Map<String, Object> data) {
		TransactionManager transactionManager = Markets.getTransactionManager();
		
		if (eventType == DatabaseEvent.EventType.DELETE) {
			UUID transactionId = UUID.fromString((String) data.get("id"));
			Transaction transaction = transactionManager.getByUUID(transactionId);
			if (transaction != null) {
				transactionManager.remove(transaction);
			}
		} else {
			// INSERT or UPDATE - reload from database
			transactionManager.load();
		}
	}
	
	private void handleBankEntryEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull Map<String, Object> data) {
		BankManager bankManager = Markets.getBankManager();
		
		if (eventType == DatabaseEvent.EventType.DELETE) {
			UUID entryId = UUID.fromString((String) data.get("id"));
			BankEntry entry = bankManager.getByUUID(entryId);
			if (entry != null) {
				bankManager.remove(entry);
			}
		} else {
			// INSERT or UPDATE - reload from database
			bankManager.load();
		}
	}
	
	private void handlePaymentEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull Map<String, Object> data) {
		OfflineItemPaymentManager paymentManager = Markets.getOfflineItemPaymentManager();
		
		if (eventType == DatabaseEvent.EventType.DELETE) {
			UUID paymentId = UUID.fromString((String) data.get("id"));
			Payment payment = paymentManager.getByUUID(paymentId);
			if (payment != null) {
				paymentManager.remove(payment);
			}
		} else {
			// INSERT or UPDATE - reload from database
			paymentManager.load();
		}
	}
	
	/**
	 * Clean up old processed events
	 */
	private void cleanupProcessedEvents() {
		long now = System.currentTimeMillis();
		processedEvents.entrySet().removeIf(entry -> (now - entry.getValue()) > EVENT_TTL);
	}
	
	/**
	 * Shutdown and cleanup resources
	 */
	public void shutdown() {
		if (this.cleanupTask != null) {
			this.cleanupTask.cancel();
			this.cleanupTask = null;
		}
		processedEvents.clear();
		if (dataManager.getRedisSyncManager() != null && dataManager.getRedisSyncManager().isEnabled()) {
			dataManager.unregisterDatabaseEventListener(this);
		}
	}
}

