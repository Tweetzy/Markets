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

	double getPrice();

	int getStock();

	boolean isPriceForAll();

	void setCurrency(@NonNull final String currency);

	void setPrice(final double price);

	void setStock(final int stock);

	void setPriceIsForAll(final boolean priceIsForAll);

	// todo this is needs to be changed
	default void addStock(@NonNull final ItemStack item) {
		if (getItem().equals(item)) {
			setStock(getStock() + item.getAmount());
		}
	}
}
