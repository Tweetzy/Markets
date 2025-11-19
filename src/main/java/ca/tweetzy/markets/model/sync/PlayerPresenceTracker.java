package ca.tweetzy.markets.model.sync;

import ca.tweetzy.flight.database.sync.RedisSyncManager;
import ca.tweetzy.markets.Markets;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Tracks which server each player is currently on using Redis
 */
public class PlayerPresenceTracker implements Listener {
	
	private final Markets plugin;
	private final RedisSyncManager redisSyncManager;
	private static final String PRESENCE_KEY_PREFIX = "player_presence:";
	private static final long PRESENCE_TTL = 300; // 5 minutes
	
	public PlayerPresenceTracker(@NonNull Markets plugin) {
		this.plugin = plugin;
		this.redisSyncManager = Markets.getDataManager().getRedisSyncManager();
		
		if (redisSyncManager != null && redisSyncManager.isEnabled()) {
			Bukkit.getPluginManager().registerEvents(this, plugin);
			// Initialize presence for all currently online players (handles plugin reloads)
			initializeOnlinePlayers();
			// Start periodic refresh task to keep presence updated (refreshes TTL every 2 minutes)
			startPresenceRefreshTask();
		}
	}
	
	/**
	 * Initialize presence tracking for all players currently online
	 * This is important when the plugin reloads, as those players won't trigger PlayerJoinEvent
	 */
	private void initializeOnlinePlayers() {
		// Run on next tick to ensure plugin is fully loaded
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			if (redisSyncManager == null || !redisSyncManager.isEnabled()) {
				return;
			}
			
			// Register all currently online players
			for (Player player : Bukkit.getOnlinePlayers()) {
				updatePresence(player.getUniqueId(), true);
			}
			
			if (!Bukkit.getOnlinePlayers().isEmpty()) {
				plugin.getLogger().info("Initialized presence tracking for " + Bukkit.getOnlinePlayers().size() + " online player(s)");
			}
		}, 1L); // Run 1 tick after plugin load
	}
	
	/**
	 * Start a periodic task to refresh presence for all online players
	 * This ensures presence TTL is kept up-to-date for long sessions
	 */
	private void startPresenceRefreshTask() {
		// Refresh every 2 minutes (120 seconds) - well before the 5 minute TTL expires
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			if (redisSyncManager == null || !redisSyncManager.isEnabled()) {
				return;
			}
			
			// Refresh presence for all online players
			for (Player player : Bukkit.getOnlinePlayers()) {
				updatePresence(player.getUniqueId(), true);
			}
		}, 120 * 20L, 120 * 20L); // Every 2 minutes
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(@NonNull PlayerJoinEvent event) {
		if (redisSyncManager == null || !redisSyncManager.isEnabled()) {
			return;
		}
		
		updatePresence(event.getPlayer().getUniqueId(), true);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(@NonNull PlayerQuitEvent event) {
		if (redisSyncManager == null || !redisSyncManager.isEnabled()) {
			return;
		}
		
		updatePresence(event.getPlayer().getUniqueId(), false);
	}
	
	/**
	 * Update player presence in Redis
	 */
	private void updatePresence(@NonNull UUID playerId, boolean online) {
		if (redisSyncManager == null || !redisSyncManager.isEnabled()) {
			return;
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				JedisPool jedisPool = redisSyncManager.getJedisPool();
				if (jedisPool == null) {
					return;
				}
				
				try (Jedis jedis = jedisPool.getResource()) {
					String key = PRESENCE_KEY_PREFIX + playerId.toString();
					String serverId = redisSyncManager.getServerId();
					
					if (online) {
						// Set presence with TTL
						jedis.setex(key, (int) PRESENCE_TTL, serverId);
					} else {
						// Remove presence
						jedis.del(key);
					}
				}
			} catch (Exception e) {
				plugin.getLogger().warning("Failed to update player presence for " + playerId + ": " + e.getMessage());
			}
		});
	}
	
	/**
	 * Public method to update presence (used by initialization)
	 */
	public void updatePlayerPresence(@NonNull UUID playerId, boolean online) {
		updatePresence(playerId, online);
	}
	
	/**
	 * Get which server a player is currently on (async version)
	 * 
	 * @param playerId The player UUID
	 * @param callback Callback that receives the server ID, or null if player is offline or not found
	 */
	public void getPlayerServerAsync(@NonNull UUID playerId, @NonNull Consumer<String> callback) {
		if (redisSyncManager == null || !redisSyncManager.isEnabled()) {
			// Check if player is online on this server
			Player player = Bukkit.getPlayer(playerId);
			if (player != null && player.isOnline()) {
				callback.accept(redisSyncManager != null ? redisSyncManager.getServerId() : null);
			} else {
				callback.accept(null);
			}
			return;
		}
		
		// Check local server first (fast path)
		Player localPlayer = Bukkit.getPlayer(playerId);
		if (localPlayer != null && localPlayer.isOnline()) {
			callback.accept(redisSyncManager.getServerId());
			return;
		}
		
		// Check Redis asynchronously
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				JedisPool jedisPool = redisSyncManager.getJedisPool();
				if (jedisPool == null) {
					callback.accept(null);
					return;
				}
				
				try (Jedis jedis = jedisPool.getResource()) {
					String key = PRESENCE_KEY_PREFIX + playerId.toString();
					String serverId = jedis.get(key);
					
					// If not in Redis, check if online on this server (double-check)
					if (serverId == null) {
						Bukkit.getScheduler().runTask(plugin, () -> {
							Player player = Bukkit.getPlayer(playerId);
							if (player != null && player.isOnline()) {
								callback.accept(redisSyncManager.getServerId());
							} else {
								callback.accept(null);
							}
						});
					} else {
						callback.accept(serverId);
					}
				}
			} catch (Exception e) {
				plugin.getLogger().warning("Failed to get player server: " + e.getMessage());
				callback.accept(null);
			}
		});
	}
	
	/**
	 * Get which server a player is currently on (synchronous version - for backwards compatibility)
	 * Note: This may block if Redis is slow. Prefer getPlayerServerAsync() when possible.
	 * 
	 * @param playerId The player UUID
	 * @return The server ID, or null if player is offline or not found
	 */
	@Nullable
	@Deprecated
	public String getPlayerServer(@NonNull UUID playerId) {
		if (redisSyncManager == null || !redisSyncManager.isEnabled()) {
			// Check if player is online on this server
			Player player = Bukkit.getPlayer(playerId);
			if (player != null && player.isOnline()) {
				return redisSyncManager != null ? redisSyncManager.getServerId() : null;
			}
			return null;
		}
		
		// Check local server first (fast path)
		Player localPlayer = Bukkit.getPlayer(playerId);
		if (localPlayer != null && localPlayer.isOnline()) {
			return redisSyncManager.getServerId();
		}
		
		try {
			JedisPool jedisPool = redisSyncManager.getJedisPool();
			if (jedisPool == null) {
				return null;
			}
			
			try (Jedis jedis = jedisPool.getResource()) {
				String key = PRESENCE_KEY_PREFIX + playerId.toString();
				String serverId = jedis.get(key);
				
				// If not in Redis, check if online on this server
				if (serverId == null) {
					Player player = Bukkit.getPlayer(playerId);
					if (player != null && player.isOnline()) {
						return redisSyncManager.getServerId();
					}
				}
				
				return serverId;
			}
		} catch (Exception e) {
			plugin.getLogger().warning("Failed to get player server: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * Check if a player is online on any server
	 */
	public boolean isPlayerOnline(@NonNull UUID playerId) {
		String serverId = getPlayerServer(playerId);
		return serverId != null;
	}
}
