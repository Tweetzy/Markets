package ca.tweetzy.markets.api.market.core;

import ca.tweetzy.flight.utils.ItemUtil;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Synchronize;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.api.currency.TransactionResult;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public interface MarketItem extends Identifiable, Synchronize, UserViewable, Storeable<MarketItem> {

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

	boolean isInfinite();

	void setInfinite(boolean infinite);

	void performPurchase(@NonNull final Market market, @NonNull final Player buyer, final int quantity, Consumer<TransactionResult> transactionResult);

	// todo this is needs to be changed
	default void addStock(@NonNull final ItemStack item, @NonNull final Consumer<SynchronizeResult> resultConsumer) {
		if (getItem().isSimilar(item)) {
			setStock(getStock() + item.getAmount());
			sync(resultConsumer);
		}
	}

	default boolean isCurrencyOfItem() {
		final String[] split = getCurrency().split("/");
		return split[1].equalsIgnoreCase("item");
	}

	default String getCurrencyDisplayName() {
		if (isCurrencyOfItem())
			return ItemUtil.getItemName(this.getCurrencyItem());

		final String[] split = getCurrency().split("/");

		final AbstractCurrency abstractCurrency = Markets.getCurrencyManager().locateCurrency(split[0], split[1]);

		if (!split[0].equalsIgnoreCase("vault") && !split[1].equalsIgnoreCase("vault") && abstractCurrency != null)
			return abstractCurrency.getDisplayName();

		return Settings.CURRENCY_VAULT_SYMBOL.getString();
	}

	default int getPlusOneStock() {
		return getStock() <= 0 ? 1 : getStock();
	}

	default Market getOwningMarket() {
		final Category category = Markets.getCategoryManager().getByUUID(getOwningCategory());
		if (category == null) return null;
		return Markets.getMarketManager().getByUUID(category.getOwningMarket());
	}
}
