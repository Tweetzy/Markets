package ca.tweetzy.markets.api.market;

import ca.tweetzy.flight.utils.ItemUtil;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Synchronize;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface BankEntry extends Identifiable, Synchronize, Storeable<BankEntry> {

	@NonNull UUID getOwner();

	@NonNull ItemStack getItem();

	int getQuantity();

	void setQuantity(final int amount);

	String getCurrency();

	void setCurrency(String string);

	ItemStack getCurrencyItem();

	void setCurrencyItem(ItemStack currencyItem);

	double getPrice();

	void setPrice(double price);

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
}
