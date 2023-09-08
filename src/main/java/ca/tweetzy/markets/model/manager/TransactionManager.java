package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.Transaction;
import ca.tweetzy.markets.api.market.core.Market;
import lombok.NonNull;

import java.util.UUID;

public final class TransactionManager extends ListManager<Transaction> {

	public TransactionManager() {
		super("Transaction");
	}

	public int getTransactionsMadeToMarket(@NonNull final UUID sellerUUID, @NonNull final UUID buyerUUID) {
		return (int) this.managerContent.stream().filter(transaction -> transaction.getBuyer().equals(buyerUUID) && transaction.getSeller().equals(sellerUUID)).count();
	}

	@Override
	public void load() {
		clear();

		Markets.getDataManager().getTransactions((error, found) -> {
			if (error != null) return;
			found.forEach(this::add);
		});
	}
}
