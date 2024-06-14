package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.Transaction;
import ca.tweetzy.markets.api.market.core.MarketUser;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class TransactionManager extends ListManager<Transaction> {

	public TransactionManager() {
		super("Transaction");
	}

	public int getTransactionsMadeToMarket(@NonNull final UUID sellerUUID, @NonNull final UUID buyerUUID) {
		return (int) this.managerContent.stream().filter(transaction -> transaction.getBuyer().equals(buyerUUID) && transaction.getSeller().equals(sellerUUID)).count();
	}

	public List<Transaction> getOfflineTransactionsFor(@NonNull final UUID sellerUUID) {
		final MarketUser user = Markets.getPlayerManager().get(sellerUUID);

		return getManagerContent().stream().filter(transaction -> transaction.getSeller().equals(sellerUUID) && transaction.getTimeCreated() >= user.getLastSeenAt()).collect(Collectors.toList());
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
