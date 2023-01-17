package ca.tweetzy.markets.api.market;

import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Synchronize;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface MarketItem extends Identifiable, Synchronize, Storeable<MarketItem> {

	@NonNull UUID getOwningCategory();

	@NonNull ItemStack getItem();

	@NonNull String getCurrency();

	ItemStack getCurrencyItem();

	double getPrice();

	int getStock();

	boolean isPriceForAll();

	boolean isAcceptingOffers();

	void setItem(@NonNull final ItemStack item);

	void setCurrency(@NonNull final String currency);

	void setPrice(final double price);

	void setCurrencyItem(@NonNull final ItemStack currencyItem);

	void setStock(final int stock);

	void setPriceIsForAll(final boolean priceIsForAll);

	void setIsAcceptingOffers(final boolean acceptingOffers);

	// todo this is needs to be changed
	default void addStock(@NonNull final ItemStack item) {
		if (getItem().equals(item)) {
			setStock(getStock() + item.getAmount());
		}
	}

	default boolean isCurrencyOfItem() {
		final String[] split = getCurrency().split("/");
		return split[1].equalsIgnoreCase("item");
	}
}
