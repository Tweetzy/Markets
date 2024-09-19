package ca.tweetzy.markets.api.market;

import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Synchronize;
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
}
