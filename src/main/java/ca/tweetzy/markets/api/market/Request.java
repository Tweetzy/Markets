package ca.tweetzy.markets.api.market;

import ca.tweetzy.flight.utils.ItemUtil;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Trackable;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface Request extends Identifiable, Trackable, Storeable<Request> { // todo probs extract the currency shit since it's repeated in other interfaces

	UUID getOwner();

	String getOwnerName();

	ItemStack getRequestItem();

	double getPrice();

	int getRequestedAmount();

	@NonNull String getCurrency();

	ItemStack getCurrencyItem();

	void setRequestedItem(@NonNull final ItemStack item);

	void setCurrency(final String currency);

	void setCurrencyItem(@NonNull final ItemStack currencyItem);

	void setPrice(final double price);

	void setRequestedAmount(final int requestedAmount);

	default boolean isCurrencyOfItem() {
		final String[] split = getCurrency().split("/");
		return split[1].equalsIgnoreCase("item");
	}

	default String getCurrencyDisplayName() {
		if (isCurrencyOfItem())
			return ItemUtil.getStackName(this.getCurrencyItem());

		final String[] split = getCurrency().split("/");

		final AbstractCurrency abstractCurrency = Markets.getCurrencyManager().locateCurrency(split[0], split[1]);

		if (!split[0].equalsIgnoreCase("vault") && !split[1].equalsIgnoreCase("vault") && abstractCurrency != null)
			return abstractCurrency.getDisplayName();

		return Settings.CURRENCY_VAULT_SYMBOL.getString();
	}
}
