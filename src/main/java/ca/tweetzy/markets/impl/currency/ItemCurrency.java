package ca.tweetzy.markets.impl.currency;

import ca.tweetzy.markets.api.currency.AbstractCurrency;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public final class ItemCurrency extends AbstractCurrency {

	public ItemCurrency() {
		super("Markets", "Item", "&bCustom Item");
	}

	public boolean has(OfflinePlayer player, double amount, ItemStack item) {
		return false;
	}

	public boolean withdraw(OfflinePlayer player, double amount, ItemStack item) {
		return false;
	}

	public boolean deposit(OfflinePlayer player, double amount, ItemStack item) {
		return false;
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return false;
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		return false;
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		return false;
	}
}
