package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.database.annotations.Column;
import ca.tweetzy.flight.database.annotations.Id;
import ca.tweetzy.flight.database.annotations.Nested;
import ca.tweetzy.flight.database.annotations.Table;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.Request;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

@Table("request")
public final class MarketRequest implements Request {

	@Id
	@Column("id")
	private UUID uuid;
	
	@Column("owner")
	private UUID owner;
	
	@Column("owner_name")
	private String ownerName;

	@Nested
	@Column("requested_item")
	private ItemStack requestedItem;
	
	@Column("currency")
	private String currency;
	
	@Nested
	@Column("currency_item")
	private ItemStack currencyItem;

	@Column("price")
	private double price;
	
	@Column("requested_amount")
	private int requestedAmount;
	
	@Column("requested_at")
	private long requestedAt;

	public MarketRequest() {
	}

	public MarketRequest(@NonNull UUID uuid, @NonNull UUID owner, @NonNull String ownerName, @NonNull ItemStack requestedItem, @NonNull String currency, @NonNull ItemStack currencyItem, double price, int requestedAmount, long requestedAt) {
		this.uuid = uuid;
		this.owner = owner;
		this.ownerName = ownerName;
		this.requestedItem = requestedItem;
		this.currency = currency;
		this.currencyItem = currencyItem;
		this.price = price;
		this.requestedAmount = requestedAmount;
		this.requestedAt = requestedAt;
	}

	public MarketRequest(@NonNull final Player requester) {
		this(UUID.randomUUID(), requester.getUniqueId(), requester.getName(), CompMaterial.AIR.parseItem(), Settings.CURRENCY_DEFAULT_SELECTED.getString(), CompMaterial.AIR.parseItem(), 1.0, 1, System.currentTimeMillis());
	}

	@Override
	public @NonNull UUID getId() {
		return this.uuid;
	}

	@Override
	public UUID getOwner() {
		return this.owner;
	}

	@Override
	public String getOwnerName() {
		return this.ownerName;
	}

	@Override
	public ItemStack getRequestItem() {
		return this.requestedItem;
	}

	@Override
	public double getPrice() {
		return this.price;
	}

	@Override
	public int getRequestedAmount() {
		return this.requestedAmount;
	}

	@Override
	public @NonNull String getCurrency() {
		return this.currency;
	}

	@Override
	public ItemStack getCurrencyItem() {
		return this.currencyItem;
	}

	@Override
	public void setRequestedItem(@NonNull ItemStack item) {
		this.requestedItem = item;
	}

	@Override
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public void setCurrencyItem(@NonNull ItemStack currencyItem) {
		this.currencyItem = currencyItem;
	}

	@Override
	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public void setRequestedAmount(int requestedAmount) {
		this.requestedAmount = requestedAmount;
	}

	@Override
	public long getTimeCreated() {
		return this.requestedAt;
	}

	@Override
	public long getLastUpdated() {
		return this.requestedAt;
	}

	@Override
	public void store(@NonNull Consumer<Request> stored) {
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().createRequest(this, (error, created) -> {
			if (error == null && created != null) {
				// Log successful request creation
				if (Markets.getTransactionLogger() != null) {
					Markets.getTransactionLogger().logRequestCreate(this.ownerName, 
						ca.tweetzy.flight.utils.ItemUtil.getItemName(this.requestedItem), 
						this.requestedAmount, this.price, this.currency);
				}
				stored.accept(created);
			} else if (error != null) {
				stored.accept(null);
			}
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getRequestRepository().deleteById(this.uuid, (error, deleted) -> {
			if (deleted != null && deleted) {
				Markets.getRequestManager().remove(this);
				
				// Log request deletion
				if (Markets.getTransactionLogger() != null) {
					Markets.getTransactionLogger().logRequestDelete(this.uuid.toString(), 
						this.ownerName, "Deleted by user or fulfilled");
				}
			}

			if (syncResult != null)
				syncResult.accept(error == null && deleted != null && deleted ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}
}
