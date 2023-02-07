package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.Request;
import ca.tweetzy.markets.settings.Settings;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class MarketRequest implements Request {

	private final UUID uuid;
	private final UUID owner;
	private final String ownerName;

	private ItemStack requestedItem;
	private String currency;
	private ItemStack currencyItem;

	private double price;
	private int requestedAmount;
	private final long requestedAt;

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
		Markets.getDataManager().createRequest(this, (error, created) -> {
			if (error == null) {
				stored.accept(created);
			}
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getDataManager().deleteRequest(this, (error, updateStatus) -> {
			if (updateStatus) {
				Markets.getRequestManager().remove(this);
			}

			if (syncResult != null)
				syncResult.accept(error == null ? updateStatus ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE : SynchronizeResult.FAILURE);
		});
	}
}
