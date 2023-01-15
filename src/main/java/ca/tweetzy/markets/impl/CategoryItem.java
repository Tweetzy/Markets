package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.MarketItem;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

public final class CategoryItem implements MarketItem {

	private final UUID id;
	private final UUID owningCategory;
	private ItemStack item;
	private String currency;
	private ItemStack currencyItem;
	private double price;
	private int stock;
	private boolean priceIsForAll;
	private boolean acceptingOffers;

	public CategoryItem(
			@NonNull final UUID id,
			@NonNull final UUID owningCategory,
			@NonNull final ItemStack item,
			@NonNull final String currency,
			@NonNull final ItemStack currencyItem,
			final double price,
			final int stock,
			final boolean priceIsForAll,
			final boolean acceptingOffers
	) {
		this.id = id;
		this.owningCategory = owningCategory;
		this.item = item;
		this.currency = currency;
		this.currencyItem = currencyItem;
		this.price = price;
		this.stock = stock;
		this.priceIsForAll = priceIsForAll;
		this.acceptingOffers = acceptingOffers;
	}

	public CategoryItem(@NonNull final UUID owningCategory) {
		this(UUID.randomUUID(), owningCategory, CompMaterial.AIR.parseItem(), "Vault/Vault/Vault ($)", CompMaterial.AIR.parseItem(), 1, 0, false, true);
	}

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@Override
	public @NonNull UUID getOwningCategory() {
		return this.owningCategory;
	}

	@Override
	public @NonNull ItemStack getItem() {
		return this.item;
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
	public double getPrice() {
		return this.price;
	}

	@Override
	public int getStock() {
		return this.stock;
	}

	@Override
	public boolean isPriceForAll() {
		return this.priceIsForAll;
	}

	@Override
	public boolean isAcceptingOffers() {
		return this.acceptingOffers;
	}

	@Override
	public void setItem(@NonNull ItemStack item) {
		this.item = item;
	}

	@Override
	public void setCurrency(@NonNull String currency) {
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
	public void setStock(int stock) {
		this.stock = stock;
	}

	@Override
	public void setPriceIsForAll(boolean priceIsForAll) {
		this.priceIsForAll = priceIsForAll;
	}

	@Override
	public void setIsAcceptingOffers(boolean acceptingOffers) {
		this.acceptingOffers = acceptingOffers;
	}

	@Override
	public void store(@NonNull Consumer<MarketItem> stored) {
		Markets.getDataManager().createMarketItem(this, (error, created) -> {
			if (error == null)
				stored.accept(created);
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getDataManager().deleteMarketItem(this, (error, updateStatus) -> {

			if (updateStatus) {
				Markets.getCategoryManager().getByUUID(this.owningCategory).getItems().removeIf(category -> category.getId().equals(this.id));
				Markets.getCategoryItemManager().remove(this);
			}

			if (syncResult != null)
				syncResult.accept(error == null ? updateStatus ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE : SynchronizeResult.FAILURE);
		});
	}

	@Override
	public void sync(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getDataManager().updateMarketItem(this, (error, updateStatus) -> {
			if (syncResult != null)
				syncResult.accept(error == null ? updateStatus ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE : SynchronizeResult.FAILURE);
		});
	}
}
