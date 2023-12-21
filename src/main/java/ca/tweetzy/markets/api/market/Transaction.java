package ca.tweetzy.markets.api.market;

import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Trackable;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.Date;
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

	default String getFormattedDate() {
		Date date = new Date(getTimeCreated());
		SimpleDateFormat formatter = new SimpleDateFormat(Settings.DATETIME_FORMAT.getString());

		return formatter.format(date);
	}
}
