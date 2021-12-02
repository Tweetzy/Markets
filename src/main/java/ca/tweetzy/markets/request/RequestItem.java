package ca.tweetzy.markets.request;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 05 2021
 * Time Created: 2:19 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class RequestItem {

	private UUID requestId;

	private ItemStack item;
	private ItemStack currency;

	private int amount;
	private double price;

	private boolean fulfilled;
	private boolean useCustomCurrency;

	public RequestItem(UUID requestId, ItemStack item, ItemStack currency, int amount, double price, boolean fulfilled, boolean useCustomCurrency) {
		this.requestId = requestId;
		this.item = item;
		this.currency = currency;
		this.amount = amount;
		this.price = price;
		this.fulfilled = fulfilled;
		this.useCustomCurrency = useCustomCurrency;
	}
}