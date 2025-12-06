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
		// Ensure type is never null before saving
		if (this.type == null) {
			Markets.getInstance().getLogger().warning("MarketTransaction with null type detected, defaulting to ITEM_PURCHASE. ID: " + this.id);
			this.type = TransactionType.ITEM_PURCHASE;
		}
		
		// Validate sellerName is not null before saving
		if (this.sellerName == null || this.sellerName.isEmpty()) {
			Markets.getInstance().getLogger().warning("MarketTransaction with null/empty sellerName detected, using UUID as fallback. ID: " + this.id + ", Seller UUID: " + this.seller);
			this.sellerName = this.seller != null ? this.seller.toString() : "Unknown";
		}
		
		// Validate buyerName is not null before saving
		if (this.buyerName == null || this.buyerName.isEmpty()) {
			Markets.getInstance().getLogger().warning("MarketTransaction with null/empty buyerName detected, using UUID as fallback. ID: " + this.id + ", Buyer UUID: " + this.buyer);
			this.buyerName = this.buyer != null ? this.buyer.toString() : "Unknown";
		}
		
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().createTransaction(this, (error, created) -> {
			if (error == null && created != null) {
				stored.accept(created);
			} else if (error != null) {
				// Log the actual error for debugging
				Markets.getInstance().getLogger().severe("Failed to store transaction " + this.id + ": " + error.getMessage());
				if (error.getCause() != null) {
					Markets.getInstance().getLogger().severe("Caused by: " + error.getCause().getMessage());
					error.getCause().printStackTrace();
				} else {
					error.printStackTrace();
				}
				stored.accept(null);
			} else {
				// Error is null but created is also null - this shouldn't happen, but handle it
				Markets.getInstance().getLogger().warning("Transaction storage returned null without error for transaction: " + this.id);
				stored.accept(null);
			}
		});
	}
}
