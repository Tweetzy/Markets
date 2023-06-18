package ca.tweetzy.markets.api.market.offer;

import ca.tweetzy.flight.utils.ItemUtil;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Trackable;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.api.currency.TransactionResult;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface Offer extends Identifiable, Trackable, Storeable<Offer> {

	@NonNull UUID getOfferSender();

	@NonNull String getOfferSenderName();

	@NonNull UUID getOfferFor();

	@NonNull UUID getMarketItem();

	int getRequestAmount();

	@NonNull String getCurrency();

	ItemStack getCurrencyItem();

	double getOfferedAmount();

	void setCurrency(final String currency);

	void setCurrencyItem(@NonNull final ItemStack currencyItem);

	void setOfferedAmount(final double amount);

	void accept(@NonNull final Consumer<TransactionResult> result);

	void reject(@NonNull final BiConsumer<TransactionResult, OfferRejectReason> result);

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
