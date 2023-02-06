package ca.tweetzy.markets.api.market;

import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Trackable;
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

	void setCurrency(final String currency);

	void setCurrencyItem(@NonNull final ItemStack currencyItem);

	void setPrice(final double price);

	void setRequestedAmount(final int requestedAmount);
}
