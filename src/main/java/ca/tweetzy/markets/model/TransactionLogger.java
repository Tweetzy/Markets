package ca.tweetzy.markets.model;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.Bukkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

/**
 * Handles transaction logging to daily-rotated files
 */
public final class TransactionLogger {

	private final Markets plugin;
	private final File logsDirectory;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private BufferedWriter writer;
	private String currentLogDate;
	private final ConcurrentLinkedQueue<String> messageQueue = new ConcurrentLinkedQueue<>();
	private int taskId = -1;
	private int cleanupTaskId = -1;
	
	private volatile boolean running = false;

	public TransactionLogger(Markets plugin) {
		this.plugin = plugin;
		this.logsDirectory = new File(plugin.getDataFolder(), "logs");
		
		// Create logs directory if it doesn't exist
		if (!this.logsDirectory.exists()) {
			this.logsDirectory.mkdirs();
		}
	}

	/**
	 * Starts the transaction logger
	 */
	public void start() {
		if (running) return;
		
		running = true;
		openLogFile();
		
		// Start async task to flush messages
		taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			try {
				flushMessages();
			} catch (Exception e) {
				plugin.getLogger().severe("Error flushing transaction log messages: " + e.getMessage());
				e.printStackTrace();
			}
		}, 20L, 20L).getTaskId(); // Flush every second
		
		// Start cleanup task (runs daily at 3 AM server time)
		cleanupTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
			try {
				cleanupOldLogs();
			} catch (Exception e) {
				plugin.getLogger().severe("Error cleaning up old transaction logs: " + e.getMessage());
				e.printStackTrace();
			}
		}, 20L * 60L * 60L, 20L * 60L * 60L * 24L).getTaskId(); // Run every 24 hours, starting 1 hour after startup
	}

	/**
	 * Stops the transaction logger
	 */
	public void stop() {
		running = false;
		
		// Cancel tasks
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		
		if (cleanupTaskId != -1) {
			Bukkit.getScheduler().cancelTask(cleanupTaskId);
			cleanupTaskId = -1;
		}
		
		// Flush remaining messages
		flushMessages();
		
		// Close writer
		closeLogFile();
	}

	/**
	 * Opens or rotates the log file based on current date
	 */
	private synchronized void openLogFile() {
		String today = dateFormat.format(new Date());
		
		// Check if we need to rotate
		if (currentLogDate != null && currentLogDate.equals(today) && writer != null) {
			return; // Already using correct file
		}
		
		// Close existing writer if any
		if (writer != null) {
			closeLogFile();
		}
		
		try {
			File logFile = new File(logsDirectory, "transactions-" + today + ".log");
			writer = new BufferedWriter(new FileWriter(logFile, true)); // Append mode
			currentLogDate = today;
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to open transaction log file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Closes the current log file
	 */
	private synchronized void closeLogFile() {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				plugin.getLogger().severe("Failed to close transaction log file: " + e.getMessage());
				e.printStackTrace();
			}
			writer = null;
		}
	}

	/**
	 * Flushes queued messages to the log file
	 */
	private synchronized void flushMessages() {
		if (!running || writer == null) return;
		
		// Check if we need to rotate the log file
		String today = dateFormat.format(new Date());
		if (!today.equals(currentLogDate)) {
			openLogFile();
		}
		
		String message;
		while ((message = messageQueue.poll()) != null) {
			try {
				writer.write(message);
				writer.newLine();
			} catch (IOException e) {
				plugin.getLogger().severe("Failed to write to transaction log: " + e.getMessage());
				e.printStackTrace();
				// Attempt to reopen file
				openLogFile();
				break;
			}
		}
		
		// Flush to disk
		try {
			if (writer != null) {
				writer.flush();
			}
		} catch (IOException e) {
			plugin.getLogger().severe("Failed to flush transaction log: " + e.getMessage());
		}
	}

	/**
	 * Logs a message to the transaction log
	 */
	private void log(String message) {
		if (!running) return;
		
		String timestamp = timestampFormat.format(new Date());
		messageQueue.offer("[" + timestamp + "] " + message);
	}

	// ===== Transaction Type Logging Methods =====

	/**
	 * Logs a bank withdrawal
	 */
	public void logBankWithdrawal(String playerName, String itemName, int quantity, boolean chunked) {
		log("BANK_WITHDRAW | Player: " + playerName + " | Item: " + itemName + " x" + quantity + 
			(chunked ? " (chunked)" : " (instant)") + " | Status: SUCCESS");
	}

	/**
	 * Logs a bank withdrawal chunk completion
	 */
	public void logBankWithdrawalChunk(String playerName, int chunkNumber, int totalChunks, int itemCount) {
		log("BANK_WITHDRAW_CHUNK | Player: " + playerName + " | Chunk: " + chunkNumber + "/" + totalChunks + 
			" (" + itemCount + " items) | Status: SUCCESS");
	}

	/**
	 * Logs a bank withdrawal completion
	 */
	public void logBankWithdrawalComplete(String playerName, int totalQuantity) {
		log("BANK_WITHDRAW_COMPLETE | Player: " + playerName + " | Total: " + totalQuantity + " items | Status: SUCCESS");
	}

	/**
	 * Logs a bank entry deletion
	 */
	public void logBankEntryDelete(String playerName, String entryId, String reason) {
		log("BANK_ENTRY_DELETE | Player: " + playerName + " | EntryID: " + entryId + " | Reason: " + reason + " | Status: SUCCESS");
	}

	/**
	 * Logs a bank entry creation
	 */
	public void logBankEntryCreate(String ownerName, String itemName, int quantity, String currency, double price) {
		log("BANK_ENTRY_CREATE | Owner: " + ownerName + " | Item: " + itemName + " x" + quantity + 
			" | Price: " + price + " (" + currency + ") | Status: SUCCESS");
	}

	/**
	 * Logs a bank entry update
	 */
	public void logBankEntryUpdate(String ownerName, String entryId, String itemName, int newQuantity) {
		log("BANK_ENTRY_UPDATE | Owner: " + ownerName + " | EntryID: " + entryId + " | Item: " + itemName + 
			" | NewQuantity: " + newQuantity + " | Status: SUCCESS");
	}

	/**
	 * Logs an item purchase
	 */
	public void logItemPurchase(String buyerName, String itemName, int quantity, double price, String currency, 
								 String marketName, String sellerName, boolean success) {
		log("ITEM_PURCHASE | Buyer: " + buyerName + " | Item: " + itemName + " x" + quantity + 
			" | Price: " + price + " (" + currency + ") | Market: " + marketName + " | Seller: " + sellerName + 
			" | Status: " + (success ? "SUCCESS" : "FAILED"));
	}

	/**
	 * Logs a stock update
	 */
	public void logStockUpdate(String itemId, String itemName, int oldStock, int newStock, String reason) {
		log("STOCK_UPDATE | ItemID: " + itemId + " | Item: " + itemName + 
			" | OldStock: " + oldStock + " | NewStock: " + newStock + " | Reason: " + reason + " | Status: SUCCESS");
	}

	/**
	 * Logs a stock withdrawal from market
	 */
	public void logStockWithdrawal(String playerName, String marketName, String itemName, int quantity) {
		log("STOCK_WITHDRAW | Player: " + playerName + " | Market: " + marketName + 
			" | Item: " + itemName + " x" + quantity + " | Status: SUCCESS");
	}

	/**
	 * Logs a category creation
	 */
	public void logCategoryCreate(String playerName, String marketName, String categoryName, String categoryId) {
		log("CATEGORY_CREATE | Player: " + playerName + " | Market: " + marketName + 
			" | Category: " + categoryName + " | CategoryID: " + categoryId + " | Status: SUCCESS");
	}

	/**
	 * Logs a category update
	 */
	public void logCategoryUpdate(String categoryId, String categoryName, String marketName) {
		log("CATEGORY_UPDATE | CategoryID: " + categoryId + " | Category: " + categoryName + 
			" | Market: " + marketName + " | Status: SUCCESS");
	}

	/**
	 * Logs a category deletion
	 */
	public void logCategoryDelete(String categoryId, String categoryName, String marketName) {
		log("CATEGORY_DELETE | CategoryID: " + categoryId + " | Category: " + categoryName + 
			" | Market: " + marketName + " | Status: SUCCESS");
	}

	/**
	 * Logs a market creation
	 */
	public void logMarketCreate(String ownerName, String marketName, String marketId, boolean isServerMarket) {
		log("MARKET_CREATE | Owner: " + ownerName + " | Market: " + marketName + " | MarketID: " + marketId + 
			" | Type: " + (isServerMarket ? "SERVER" : "PLAYER") + " | Status: SUCCESS");
	}

	/**
	 * Logs a market update
	 */
	public void logMarketUpdate(String marketId, String marketName, String ownerName) {
		log("MARKET_UPDATE | MarketID: " + marketId + " | Market: " + marketName + 
			" | Owner: " + ownerName + " | Status: SUCCESS");
	}

	/**
	 * Logs a market deletion
	 */
	public void logMarketDelete(String marketId, String marketName, String ownerName) {
		log("MARKET_DELETE | MarketID: " + marketId + " | Market: " + marketName + 
			" | Owner: " + ownerName + " | Status: SUCCESS");
	}

	/**
	 * Logs an item addition to market
	 */
	public void logItemAdd(String playerName, String marketName, String categoryName, String itemName, 
						   int stock, double price, String currency) {
		log("ITEM_ADD | Player: " + playerName + " | Market: " + marketName + " | Category: " + categoryName + 
			" | Item: " + itemName + " x" + stock + " | Price: " + price + " (" + currency + ") | Status: SUCCESS");
	}

	/**
	 * Logs an item removal from market
	 */
	public void logItemRemove(String itemId, String itemName, String marketName, String reason) {
		log("ITEM_REMOVE | ItemID: " + itemId + " | Item: " + itemName + " | Market: " + marketName + 
			" | Reason: " + reason + " | Status: SUCCESS");
	}

	/**
	 * Logs an offer creation
	 */
	public void logOfferCreate(String senderName, String recipientName, String itemName, int quantity, 
								double offeredAmount, String currency) {
		log("OFFER_CREATE | Sender: " + senderName + " | Recipient: " + recipientName + 
			" | Item: " + itemName + " x" + quantity + " | Offered: " + offeredAmount + " (" + currency + ") | Status: SUCCESS");
	}

	/**
	 * Logs an offer acceptance
	 */
	public void logOfferAccept(String offerId, String senderName, String recipientName, String itemName, 
								int quantity, double amount) {
		log("OFFER_ACCEPT | OfferID: " + offerId + " | Sender: " + senderName + " | Recipient: " + recipientName + 
			" | Item: " + itemName + " x" + quantity + " | Amount: " + amount + " | Status: SUCCESS");
	}

	/**
	 * Logs an offer rejection
	 */
	public void logOfferReject(String offerId, String senderName, String recipientName, String reason) {
		log("OFFER_REJECT | OfferID: " + offerId + " | Sender: " + senderName + " | Recipient: " + recipientName + 
			" | Reason: " + reason + " | Status: SUCCESS");
	}

	/**
	 * Logs an offer deletion
	 */
	public void logOfferDelete(String offerId, String reason) {
		log("OFFER_DELETE | OfferID: " + offerId + " | Reason: " + reason + " | Status: SUCCESS");
	}

	/**
	 * Logs a request creation
	 */
	public void logRequestCreate(String playerName, String itemName, int quantity, double price, String currency) {
		log("REQUEST_CREATE | Player: " + playerName + " | Item: " + itemName + " x" + quantity + 
			" | Price: " + price + " (" + currency + ") | Status: SUCCESS");
	}

	/**
	 * Logs a request fulfillment
	 */
	public void logRequestFulfill(String requestId, String requesterName, String fulfillerName, 
								   String itemName, int quantity, double price) {
		log("REQUEST_FULFILL | RequestID: " + requestId + " | Requester: " + requesterName + 
			" | Fulfiller: " + fulfillerName + " | Item: " + itemName + " x" + quantity + 
			" | Price: " + price + " | Status: SUCCESS");
	}

	/**
	 * Logs a request deletion
	 */
	public void logRequestDelete(String requestId, String ownerName, String reason) {
		log("REQUEST_DELETE | RequestID: " + requestId + " | Owner: " + ownerName + 
			" | Reason: " + reason + " | Status: SUCCESS");
	}

	/**
	 * Logs an error
	 */
	public void logError(String transactionType, String details, String errorMessage) {
		log("ERROR | Type: " + transactionType + " | Details: " + details + 
			" | Error: " + errorMessage + " | Status: FAILED");
	}

	/**
	 * Logs a warning
	 */
	public void logWarning(String transactionType, String details, String warningMessage) {
		log("WARNING | Type: " + transactionType + " | Details: " + details + 
			" | Warning: " + warningMessage);
	}

	/**
	 * Cleans up old log files based on retention days setting
	 */
	public void cleanupOldLogs() {
		int retentionDays = Settings.TRANSACTION_LOGGING_RETENTION_DAYS.getInt();
		
		// If retention is 0, never cleanup
		if (retentionDays <= 0) {
			return;
		}
		
		File[] logFiles = logsDirectory.listFiles((dir, name) -> 
			name.startsWith("transactions-") && name.endsWith(".log")
		);
		
		if (logFiles == null || logFiles.length == 0) {
			return;
		}
		
		long cutoffTime = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(retentionDays);
		int deletedCount = 0;
		
		for (File logFile : logFiles) {
			if (logFile.lastModified() < cutoffTime) {
				if (logFile.delete()) {
					deletedCount++;
				} else {
					plugin.getLogger().warning("Failed to delete old transaction log: " + logFile.getName());
				}
			}
		}
		
		if (deletedCount > 0) {
			plugin.getLogger().info("Cleaned up " + deletedCount + " old transaction log file(s)");
		}
	}
}

