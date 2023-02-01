package ca.tweetzy.markets.api.market;

import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Trackable;
import ca.tweetzy.markets.api.currency.TransactionResult;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public interface Offer extends Identifiable, Trackable, Storeable<Offer> {

	@NonNull UUID getOfferSender();

	@NonNull String getOfferSenderName();

	@NonNull UUID getOfferFor();

	@NonNull UUID getMarketItem();

	@NonNull String getCurrency();

	ItemStack getCurrencyItem();

	double getOfferedAmount();

	void accept(@NonNull final Consumer<TransactionResult> result);

	void reject(@NonNull final Consumer<TransactionResult> result);
}
