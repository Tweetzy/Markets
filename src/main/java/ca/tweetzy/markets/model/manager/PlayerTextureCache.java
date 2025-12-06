package ca.tweetzy.markets.model.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.settings.Settings;
import lombok.Getter;

public final class PlayerTextureCache {

	private static final String DEFAULT_TEXTURE = "http://textures.minecraft.net/texture/ee7700096b5a2a87386d6205b4ddcc14fd33cf269362fa6893499431ce77bf9";
	private static final long DEFAULT_CACHE_TTL = 24 * 60 * 60 * 1000L; // 24 hours

	@Getter
	private final Map<UUID, CachedTexture> textureCache = new ConcurrentHashMap<>();
	private final Set<UUID> pendingFetches = ConcurrentHashMap.newKeySet();
	private final Queue<UUID> fetchQueue = new ConcurrentLinkedQueue<>();
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final ExecutorService fetchExecutor = Executors.newFixedThreadPool(2);
	private final AtomicInteger activeFetches = new AtomicInteger(0);
	
	private BukkitTask refreshTask;
	private static final int MAX_CONCURRENT_FETCHES = 5;
	private static final long RATE_LIMIT_DELAY_MS = 200; // 5 requests per second max

	public PlayerTextureCache() {
		// Initialization deferred to start() method to avoid accessing Markets.getInstance()
		// during plugin construction
	}

	/**
	 * Get texture URL for a player, using cache if available
	 * Returns default texture immediately if not cached
	 * 
	 * Thread-safe: Can be called from main thread safely.
	 * Only does fast ConcurrentHashMap lookups, never blocks.
	 */
	public String getTexture(OfflinePlayer player) {
		if (player == null || !player.hasPlayedBefore()) {
			return DEFAULT_TEXTURE;
		}

		UUID uuid = player.getUniqueId();
		CachedTexture cached = textureCache.get(uuid);

		// Return cached texture if valid
		// ConcurrentHashMap.get() is O(1) and thread-safe, won't block
		if (cached != null && !cached.isExpired()) {
			return cached.getTexture();
		}

		// Queue for async fetch if not already pending
		// These operations are thread-safe and fast (set/queue operations)
		if (!pendingFetches.contains(uuid)) {
			queueFetch(uuid, player);
		}

		// Return default texture immediately while fetching
		// Main thread never waits for network I/O
		return DEFAULT_TEXTURE;
	}

	/**
	 * Get texture URL synchronously if cached, otherwise return default
	 * 
	 * Thread-safe: Can be called from main thread safely.
	 * Only does fast ConcurrentHashMap lookups, never blocks.
	 */
	public String getCachedTexture(OfflinePlayer player) {
		if (player == null || !player.hasPlayedBefore()) {
			return DEFAULT_TEXTURE;
		}

		// Fast O(1) lookup, thread-safe, never blocks main thread
		CachedTexture cached = textureCache.get(player.getUniqueId());
		return (cached != null && !cached.isExpired()) ? cached.getTexture() : DEFAULT_TEXTURE;
	}

	/**
	 * Check if texture is cached and valid
	 */
	public boolean isCached(UUID uuid) {
		CachedTexture cached = textureCache.get(uuid);
		return cached != null && !cached.isExpired();
	}

	/**
	 * Queue a texture fetch for async processing
	 */
	private void queueFetch(UUID uuid, OfflinePlayer player) {
		if (pendingFetches.add(uuid)) {
			fetchQueue.offer(uuid);
			processFetchQueue();
		}
	}

	/**
	 * Process the fetch queue with rate limiting
	 */
	private void processFetchQueue() {
		if (activeFetches.get() >= MAX_CONCURRENT_FETCHES) {
			return;
		}

		UUID uuid = fetchQueue.poll();
		if (uuid == null) {
			return;
		}

		activeFetches.incrementAndGet();
		
		// Rate limiting delay - runs on scheduler thread (async, won't block main thread)
		scheduler.schedule(() -> {
			try {
				// getOfflinePlayer() is safe to call from async threads
				// It may do disk I/O but won't block the main thread
				OfflinePlayer player = Markets.getInstance().getServer().getOfflinePlayer(uuid);
				fetchTextureAsync(uuid, player);
			} catch (Exception e) {
				// Handle any errors gracefully
				activeFetches.decrementAndGet();
				pendingFetches.remove(uuid);
			} finally {
				// Process next in queue (only if we successfully started the fetch)
				if (activeFetches.get() < MAX_CONCURRENT_FETCHES && !fetchQueue.isEmpty()) {
					// Schedule next fetch with a small delay to prevent tight loops
					scheduler.schedule(this::processFetchQueue, RATE_LIMIT_DELAY_MS, TimeUnit.MILLISECONDS);
				}
			}
		}, RATE_LIMIT_DELAY_MS, TimeUnit.MILLISECONDS);
	}

	/**
	 * Fetch texture asynchronously
	 * Runs on fetchExecutor thread pool (separate from main thread)
	 */
	private void fetchTextureAsync(UUID uuid, OfflinePlayer player) {
		fetchExecutor.submit(() -> {
			try {
				// Use the Flight library's profile system to get texture
				// This will make the API call asynchronously on this thread
				// All network I/O happens here, never on main thread
				String texture = fetchTextureFromPlayer(player);
				
				if (texture != null && !texture.isEmpty()) {
					long ttl = Settings.PLAYER_TEXTURE_CACHE_TTL.getInt() * 1000L; // Convert seconds to milliseconds
					if (ttl <= 0) ttl = DEFAULT_CACHE_TTL;
					
					// ConcurrentHashMap.put() is thread-safe and fast
					// This won't block even if called from main thread later
					textureCache.put(uuid, new CachedTexture(texture, System.currentTimeMillis() + ttl));
					
					// Log successful texture fetch if logging is enabled
					if (Settings.PLAYER_TEXTURE_CACHE_LOGGING_ENABLED.getBoolean()) {
						Markets.getInstance().getLogger().info("Cached texture for " + player.getName() + " (" + uuid + "): " + texture);
					}
				} else {
					// Log failed texture fetch if logging is enabled
					if (Settings.PLAYER_TEXTURE_CACHE_LOGGING_ENABLED.getBoolean()) {
						Markets.getInstance().getLogger().warning("Failed to fetch texture for " + player.getName() + " (" + uuid + ") - returned null");
					}
				}
			} catch (Exception e) {
				// Silently fail and use default texture
				// Logging could be added here if needed, but avoid blocking
			} finally {
				// Decrement counter and remove from pending set
				// These operations are thread-safe (ConcurrentHashMap operations)
				activeFetches.decrementAndGet();
				pendingFetches.remove(uuid);
				
				// Process next in queue if available
				if (!fetchQueue.isEmpty() && activeFetches.get() < MAX_CONCURRENT_FETCHES) {
					processFetchQueue();
				}
			}
		});
	}

	/**
	 * Fetch texture from player using Mojang API
	 * Fetches the player's skin texture URL from Mojang's session server
	 * 
	 * @param player The offline player to fetch texture for
	 * @return The texture URL, or null if fetching failed
	 */
	private String fetchTextureFromPlayer(OfflinePlayer player) {
		if (player == null || !player.hasPlayedBefore()) {
			return null;
		}

		UUID uuid = player.getUniqueId();
		String uuidString = uuid.toString().replace("-", "");

		try {
			// Call Mojang's session server API
			URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000); // 5 second timeout
			connection.setReadTimeout(5000);
			connection.setRequestProperty("User-Agent", "Markets-Plugin/1.0");

			int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				// Player might not exist or API is unavailable
				return null;
			}

			// Read response
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}

				// Parse JSON response
				JsonObject profileJson = JsonParser.parseString(response.toString()).getAsJsonObject();
				
				// Get the properties array
				if (!profileJson.has("properties") || !profileJson.get("properties").isJsonArray()) {
					return null;
				}

				var propertiesArray = profileJson.getAsJsonArray("properties");
				if (propertiesArray.size() == 0) {
					return null;
				}

				// Find the textures property
				for (var element : propertiesArray) {
					JsonObject property = element.getAsJsonObject();
					if ("textures".equals(property.get("name").getAsString())) {
						String value = property.get("value").getAsString();
						
						// Decode base64 value
						String decoded = new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
						JsonObject texturesJson = JsonParser.parseString(decoded).getAsJsonObject();
						
						// Get the SKIN texture URL
						if (texturesJson.has("textures") && texturesJson.get("textures").isJsonObject()) {
							JsonObject textures = texturesJson.getAsJsonObject("textures");
							if (textures.has("SKIN") && textures.get("SKIN").isJsonObject()) {
								JsonObject skin = textures.getAsJsonObject("SKIN");
								if (skin.has("url")) {
									String textureUrl = skin.get("url").getAsString();
									// Ensure URL uses HTTP (not HTTPS) for compatibility with QuickItem/XSkull
									// Mojang may return HTTPS URLs, but we need HTTP for texture loading
									if (textureUrl.startsWith("https://")) {
										textureUrl = textureUrl.replace("https://", "http://");
									}
									return textureUrl;
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// Silently fail - return null to use default texture
			// Logging could be added here if needed for debugging
		}

		return null;
	}

	/**
	 * Pre-fetch textures for a batch of players
	 * Thread-safe: Multiple threads can call this simultaneously without causing duplicate fetches.
	 * The queueFetch method uses atomic operations to ensure each UUID is only queued once.
	 */
	public void prefetchTextures(List<OfflinePlayer> players) {
		for (OfflinePlayer player : players) {
			if (player != null && player.hasPlayedBefore()) {
				UUID uuid = player.getUniqueId();
				// Only check cache - queueFetch will atomically check pendingFetches
				// This eliminates race condition window and redundant checks
				if (!isCached(uuid)) {
					queueFetch(uuid, player);
				}
			}
		}
	}

	/**
	 * Pre-fetch textures for market owners at startup
	 * Only prefetches owners with open markets (most likely to be viewed)
	 * Runs asynchronously and respects the configured limit
	 * 
	 * @param marketOwners List of UUIDs of market owners to prefetch
	 * @param limit Maximum number of textures to prefetch
	 */
	public void prefetchMarketOwnersAtStartup(List<UUID> marketOwners, int limit) {
		if (marketOwners == null || marketOwners.isEmpty() || limit <= 0) {
			return;
		}

		// Run asynchronously to not block startup
		fetchExecutor.submit(() -> {
			int prefetched = 0;
			int skipped = 0;
			
			for (UUID ownerUUID : marketOwners) {
				if (prefetched >= limit) {
					break;
				}

				try {
					// Check if already cached
					if (isCached(ownerUUID)) {
						skipped++;
						continue;
					}

					// Get offline player (may do disk I/O, but we're on async thread)
					OfflinePlayer player = Markets.getInstance().getServer().getOfflinePlayer(ownerUUID);
					
					if (player != null && player.hasPlayedBefore()) {
						// Queue for fetch
						if (!pendingFetches.contains(ownerUUID)) {
							queueFetch(ownerUUID, player);
							prefetched++;
							
							// Log progress every 25 players if logging is enabled
							if (Settings.PLAYER_TEXTURE_CACHE_LOGGING_ENABLED.getBoolean() && prefetched % 25 == 0) {
								Markets.getInstance().getLogger().info("Prefetching textures: " + prefetched + "/" + Math.min(limit, marketOwners.size()) + " queued...");
							}
						} else {
							skipped++;
						}
					} else {
						skipped++;
					}
				} catch (Exception e) {
					// Skip on error, continue with next player
					skipped++;
				}
			}

			if (Settings.PLAYER_TEXTURE_CACHE_LOGGING_ENABLED.getBoolean()) {
				Markets.getInstance().getLogger().info("Texture prefetch queued: " + prefetched + " players (skipped " + skipped + " already cached/pending)");
			}
		});
	}

	/**
	 * Start the cache and begin periodic cleanup of expired cache entries
	 * Should be called after plugin is fully initialized
	 */
	public void start() {
		startPeriodicCleanup();
	}

	/**
	 * Start periodic cleanup of expired cache entries
	 */
	private void startPeriodicCleanup() {
		refreshTask = Markets.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(
			Markets.getInstance(),
			this::cleanupExpiredEntries,
			6000L, // 5 minutes
			6000L  // Every 5 minutes
		);
	}

	/**
	 * Remove expired cache entries
	 */
	private void cleanupExpiredEntries() {
		long now = System.currentTimeMillis();
		textureCache.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
	}

	/**
	 * Clear all cache entries
	 */
	public void clearCache() {
		textureCache.clear();
		pendingFetches.clear();
		fetchQueue.clear();
	}

	/**
	 * Shutdown the cache and cleanup resources
	 * Should be called on plugin disable to prevent thread leaks
	 */
	public void shutdown() {
		if (refreshTask != null) {
			refreshTask.cancel();
		}
		
		// Shutdown executors gracefully
		// shutdown() prevents new tasks but allows existing ones to complete
		scheduler.shutdown();
		fetchExecutor.shutdown();
		
		try {
			// Wait up to 5 seconds for tasks to complete
			if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
				scheduler.shutdownNow(); // Force shutdown if timeout
			}
			if (!fetchExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
				fetchExecutor.shutdownNow(); // Force shutdown if timeout
			}
		} catch (InterruptedException e) {
			// Thread was interrupted, force shutdown
			scheduler.shutdownNow();
			fetchExecutor.shutdownNow();
			Thread.currentThread().interrupt();
		}
		
		clearCache();
	}

	/**
	 * Cached texture entry with expiration
	 */
	private static class CachedTexture {
		private final String texture;
		private final long expiresAt;

		public CachedTexture(String texture, long expiresAt) {
			this.texture = texture;
			this.expiresAt = expiresAt;
		}

		public String getTexture() {
			return texture;
		}

		public boolean isExpired() {
			return isExpired(System.currentTimeMillis());
		}

		public boolean isExpired(long now) {
			return now >= expiresAt;
		}
	}
}

