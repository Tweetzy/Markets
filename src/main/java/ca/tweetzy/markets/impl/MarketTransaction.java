package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Transaction;
import ca.tweetzy.markets.api.market.TransactionType;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class MarketTransaction implements Transaction {

	private final UUID id;
	private final UUID buyer;
	private final String buyerName;
	private final UUID seller;
	private final String sellerName;
	private final TransactionType type;
	private final ItemStack item;
	private final String currency;
	private final int quantity;
	private final double price;
	private final long createdAt;

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@Override
	public UUID getBuyer() {
		return this.buyer;
	}

	@Override
	public String getBuyerName() {
		return this.buyerName;
	}

	@Override
	public UUID getSeller() {
		return this.seller;
	}

	@Override
	public String getSellerName() {
		return this.sellerName;
	}

	@Override
	public TransactionType getType() {
		return this.type;
	}

	@Override
	public ItemStack getItem() {
		return this.item;
	}

	@Override
	public String getCurrency() {
		return this.currency;
	}

	@Override
	public int getQuantity() {
		return this.quantity;
	}

	@Override
	public double getPrice() {
		return this.price;
	}

	@Override
	public long getTimeCreated() {
		return this.createdAt;
	}

	@Override
	public long getLastUpdated() {
		return this.createdAt;
	}

	@Override
	public void store(@NonNull Consumer<Transaction> stored) {
		Markets.getDataManager().createTransaction(this, (error, created) -> {
			if (error == null) {
				stored.accept(created);
			}
		});
	}
}
