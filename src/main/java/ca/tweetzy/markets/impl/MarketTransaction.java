package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.database.annotations.Column;
import ca.tweetzy.flight.database.annotations.Id;
import ca.tweetzy.flight.database.annotations.Nested;
import ca.tweetzy.flight.database.annotations.Table;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Transaction;
import ca.tweetzy.markets.api.market.TransactionType;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

@Table("transaction")
public final class MarketTransaction implements Transaction {

	@Id
	@Column("id")
	private UUID id;
	
	@Column("buyer")
	private UUID buyer;
	
	@Column("buyer_name")
	private String buyerName;
	
	@Column("seller")
	private UUID seller;
	
	@Column("seller_name")
	private String sellerName;
	
	@Column("type")
	private TransactionType type;
	
	@Nested
	@Column("item")
	private ItemStack item;
	
	@Column("currency")
	private String currency;
	
	@Column("quantity")
	private int quantity;
	
	@Column("price")
	private double price;
	
	@Column("created_at")
	private long createdAt;

	public MarketTransaction() {
	}

	public MarketTransaction(@NonNull UUID id, @NonNull UUID buyer, @NonNull String buyerName, @NonNull UUID seller, @NonNull String sellerName, @NonNull TransactionType type, @NonNull ItemStack item, @NonNull String currency, int quantity, double price, long createdAt) {
		this.id = id;
		this.buyer = buyer;
		this.buyerName = buyerName;
		this.seller = seller;
		this.sellerName = sellerName;
		this.type = type;
		this.item = item;
		this.currency = currency;
		this.quantity = quantity;
		this.price = price;
		this.createdAt = createdAt;
	}

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
		Markets.getTransactionRepository().save(this, (error, created) -> {
			if (error == null) {
				stored.accept(created);
			}
		});
	}
}
