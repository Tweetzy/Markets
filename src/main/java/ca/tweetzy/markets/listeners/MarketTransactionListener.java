package ca.tweetzy.markets.listeners;

import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.event.MarketTransactionEvent;
import ca.tweetzy.markets.api.market.Transaction;
import ca.tweetzy.markets.impl.MarketTransaction;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public final class MarketTransactionListener implements Listener {

	@EventHandler
	public void onTransactionEvent(final MarketTransactionEvent event) {
		// Get seller name with fallback logic
		final UUID sellerUUID = event.getSeller().getUniqueId();
		String sellerName = null;
		
		// Try to get Market first - most reliable source for owner name
		final var market = Markets.getMarketManager().getByOwner(sellerUUID);
		if (market != null) {
			sellerName = market.getOwnerName();
		}
		
		// Fallback to server market name or OfflinePlayer name
		if (sellerName == null || sellerName.isEmpty()) {
			if (sellerUUID.equals(UUID.fromString(Settings.SERVER_MARKET_UUID.getString()))) {
				sellerName = Settings.NAME.getString();
			} else {
				sellerName = event.getSeller().getName();
			}
		}
		
		// Final fallback to UUID string if name is still null
		if (sellerName == null || sellerName.isEmpty()) {
			sellerName = sellerUUID.toString();
			Markets.getInstance().getLogger().warning("Could not retrieve seller name for transaction, using UUID: " + sellerUUID);
		}
		
		// Get buyer name with fallback logic
		String buyerName = event.getBuyer().getName();
		if (buyerName == null || buyerName.isEmpty()) {
			buyerName = event.getBuyer().getUniqueId().toString();
			Markets.getInstance().getLogger().warning("Could not retrieve buyer name for transaction, using UUID: " + event.getBuyer().getUniqueId());
		}
		
		final Transaction transaction = new MarketTransaction(
				UUID.randomUUID(),
				event.getBuyer().getUniqueId(),
				buyerName,
				sellerUUID,
				sellerName,
				event.getType(),
				event.getItem(),
				event.getCurrency(),
				event.getQuantity(),
				event.getPrice(),
				System.currentTimeMillis()
		);

		transaction.store(storeTransaction -> {
			if (storeTransaction == null) {
				// Log detailed error information
				try {
					// Since store() doesn't provide error details directly, we'll log what we can
					Common.log("&CSomething went wrong while trying to store transaction: &d" + transaction.getId().toString());
					Common.log("&cTransaction details - Buyer: &f" + transaction.getBuyerName() + 
					           " &cSeller: &f" + transaction.getSellerName() + 
					           " &cType: &f" + transaction.getType() + 
					           " &cPrice: &f" + transaction.getPrice());
					
					// Check if this might be a connection issue
					Markets.getInstance().getLogger().severe("Failed to store transaction " + transaction.getId() + 
						". This may be due to a database connection issue. Check database connectivity and connection pool settings.");
					Markets.getInstance().getLogger().severe("Transaction details: Buyer=" + transaction.getBuyerName() + 
						", Seller=" + transaction.getSellerName() + ", Type=" + transaction.getType() + 
						", Price=" + transaction.getPrice() + ", Quantity=" + transaction.getQuantity());
				} catch (Exception e) {
					Markets.getInstance().getLogger().severe("Error while logging transaction storage failure: " + e.getMessage());
					e.printStackTrace();
				}
			} else {
				Markets.getTransactionManager().add(storeTransaction);
			}
		});

		// create an offline notification for the player

	}
}
