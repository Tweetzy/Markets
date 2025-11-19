package ca.tweetzy.markets.model.sync;

import ca.tweetzy.flight.database.sync.RedisLockManager;
import ca.tweetzy.markets.Markets;
import lombok.NonNull;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages stock reservations using distributed locks
 * Prevents overselling across multiple servers
 */
public class StockReservationManager {
	
	private final Markets plugin;
	private final RedisLockManager lockManager;
	private final Map<UUID, ReservationInfo> activeReservations = new ConcurrentHashMap<>();
	private static final long RESERVATION_TIMEOUT = 30; // seconds
	private BukkitRunnable cleanupTask;
	
	private static class ReservationInfo {
		final UUID itemId;
		final int quantity;
		final long expirationTime;
		final Thread reservationThread;
		
		ReservationInfo(UUID itemId, int quantity, long expirationTime, Thread reservationThread) {
			this.itemId = itemId;
			this.quantity = quantity;
			this.expirationTime = expirationTime;
			this.reservationThread = reservationThread;
		}
	}
	
	public StockReservationManager(@NonNull Markets plugin) {
		this.plugin = plugin;
		this.lockManager = Markets.getDataManager().getRedisLockManager();
		
		// Cleanup expired reservations periodically
		this.cleanupTask = new BukkitRunnable() {
			@Override
			public void run() {
				cleanupExpiredReservations();
			}
		};
		this.cleanupTask.runTaskTimerAsynchronously(plugin, 60 * 20L, 60 * 20L); // Every minute
	}
	
	/**
	 * Reserve stock for a purchase
	 * 
	 * @param itemId The market item ID
	 * @param quantity The quantity to reserve
	 * @return true if reservation was successful, false otherwise
	 */
	public boolean reserveStock(@NonNull UUID itemId, int quantity) {
		if (lockManager == null) {
			return true; // No Redis, allow reservation (single-server mode)
		}
		
		String lockKey = "market_item:" + itemId + ":purchase";
		
		// Try to acquire lock
		if (!lockManager.acquireLock(lockKey, RESERVATION_TIMEOUT)) {
			return false; // Lock acquisition failed
		}
		
		// Store reservation info
		long expirationTime = System.currentTimeMillis() + (RESERVATION_TIMEOUT * 1000);
		activeReservations.put(itemId, new ReservationInfo(itemId, quantity, expirationTime, Thread.currentThread()));
		
		return true;
	}
	
	/**
	 * Release stock reservation
	 * 
	 * @param itemId The market item ID
	 * @return true if release was successful, false otherwise
	 */
	public boolean releaseReservation(@NonNull UUID itemId) {
		if (lockManager == null) {
			return true; // No Redis, allow release
		}
		
		ReservationInfo reservation = activeReservations.get(itemId);
		if (reservation == null) {
			return false; // No active reservation
		}
		
		// Only release if held by current thread
		if (reservation.reservationThread != Thread.currentThread()) {
			return false;
		}
		
		String lockKey = "market_item:" + itemId + ":purchase";
		boolean released = lockManager.releaseLock(lockKey);
		
		if (released) {
			activeReservations.remove(itemId);
		}
		
		return released;
	}
	
	/**
	 * Check if stock is currently reserved
	 * 
	 * @param itemId The market item ID
	 * @return true if reserved, false otherwise
	 */
	public boolean isReserved(@NonNull UUID itemId) {
		if (lockManager == null) {
			return false; // No Redis, not reserved
		}
		
		String lockKey = "market_item:" + itemId + ":purchase";
		return lockManager.isLocked(lockKey);
	}
	
	/**
	 * Renew a reservation's expiration time
	 * 
	 * @param itemId The market item ID
	 * @param additionalSeconds Additional seconds to add
	 * @return true if renewed, false otherwise
	 */
	public boolean renewReservation(@NonNull UUID itemId, long additionalSeconds) {
		if (lockManager == null) {
			return true; // No Redis, allow renewal
		}
		
		ReservationInfo reservation = activeReservations.get(itemId);
		if (reservation == null) {
			return false; // No active reservation
		}
		
		// Only renew if held by current thread
		if (reservation.reservationThread != Thread.currentThread()) {
			return false;
		}
		
		String lockKey = "market_item:" + itemId + ":purchase";
		boolean renewed = lockManager.renewLock(lockKey, additionalSeconds);
		
		if (renewed) {
			// Update expiration time
			long newExpirationTime = System.currentTimeMillis() + (additionalSeconds * 1000);
			activeReservations.put(itemId, new ReservationInfo(
				reservation.itemId,
				reservation.quantity,
				newExpirationTime,
				reservation.reservationThread
			));
		}
		
		return renewed;
	}
	
	/**
	 * Clean up expired reservations
	 */
	private void cleanupExpiredReservations() {
		long now = System.currentTimeMillis();
		activeReservations.entrySet().removeIf(entry -> {
			ReservationInfo info = entry.getValue();
			if (info.expirationTime < now) {
				// Release the lock
				String lockKey = "market_item:" + info.itemId + ":purchase";
				if (lockManager != null) {
					lockManager.releaseLock(lockKey);
				}
				return true;
			}
			return false;
		});
	}
	
	/**
	 * Shutdown and cleanup resources
	 */
	public void shutdown() {
		if (this.cleanupTask != null) {
			this.cleanupTask.cancel();
			this.cleanupTask = null;
		}
		// Release all active reservations
		activeReservations.forEach((itemId, info) -> {
			String lockKey = "market_item:" + itemId + ":purchase";
			if (lockManager != null) {
				lockManager.releaseLock(lockKey);
			}
		});
		activeReservations.clear();
	}
}

