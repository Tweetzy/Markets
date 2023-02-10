package ca.tweetzy.markets.api.market;

import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Trackable;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface Transaction extends Identifiable, Trackable, Storeable<Transaction> {

	UUID getBuyer();

	String getBuyerName();

	UUID getSeller();

	String getSellerName();

	TransactionType getType();

	ItemStack getItem();

	String getCurrency();

	int getQuantity();

	double getPrice();
}
