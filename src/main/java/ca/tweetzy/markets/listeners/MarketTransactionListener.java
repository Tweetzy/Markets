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
		final Transaction transaction = new MarketTransaction(
				UUID.randomUUID(),
				event.getBuyer().getUniqueId(),
				event.getBuyer().getName(),
				event.getSeller().getUniqueId(),
				event.getSeller().getUniqueId().equals(UUID.fromString(Settings.SERVER_MARKET_UUID.getString())) ? Settings.NAME.getString() : event.getSeller().getName(),
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
