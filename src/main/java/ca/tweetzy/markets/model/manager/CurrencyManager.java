package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.impl.currency.ItemCurrency;
import ca.tweetzy.markets.impl.currency.VaultCurrency;
import ca.tweetzy.markets.model.currency.FundsEconomyLoader;
import ca.tweetzy.markets.model.currency.UltraEconomyLoader;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public final class CurrencyManager extends ListManager<AbstractCurrency> {

	public CurrencyManager() {
		super("Currency");
	}

	public AbstractCurrency locateCurrency(@NonNull final String owningPlugin, @NonNull final String currencyName) {
		return getManagerContent().stream().filter(currency -> currency.getOwningPlugin().equals(owningPlugin) && currency.getCurrencyName().equals(currencyName)).findFirst().orElse(null);
	}

	public boolean has(@NonNull final OfflinePlayer offlinePlayer, @NonNull final String owningPlugin, @NonNull final String currencyName, final double amount) {
		if (owningPlugin.equalsIgnoreCase("vault") || currencyName.equalsIgnoreCase("vault"))
			return Markets.getEconomy().has(offlinePlayer, amount);

		return locateCurrency(owningPlugin, currencyName).has(offlinePlayer, amount);
	}

	public boolean withdraw(@NonNull final OfflinePlayer offlinePlayer, @NonNull final String owningPlugin, @NonNull final String currencyName, final double amount) {
		if (owningPlugin.equalsIgnoreCase("vault") || currencyName.equalsIgnoreCase("vault")) {
			Markets.getEconomy().withdrawPlayer(offlinePlayer, amount);
			return true;
		}

		return locateCurrency(owningPlugin, currencyName).withdraw(offlinePlayer, amount);
	}

	public boolean deposit(@NonNull final OfflinePlayer offlinePlayer, @NonNull final String owningPlugin, @NonNull final String currencyName, final double amount) {
		if (owningPlugin.equalsIgnoreCase("vault") || currencyName.equalsIgnoreCase("vault")) {
			Markets.getEconomy().depositPlayer(offlinePlayer, amount);
			return true;
		}

		return locateCurrency(owningPlugin, currencyName).deposit(offlinePlayer, amount);
	}

	public boolean has(@NonNull final OfflinePlayer offlinePlayer, @NonNull final ItemStack itemStack, final int amount) {
		return ((ItemCurrency) locateCurrency("Markets", "Item")).has(offlinePlayer, amount, itemStack);

	}

	public boolean withdraw(@NonNull final OfflinePlayer offlinePlayer, @NonNull final ItemStack itemStack, final int amount) {
		return ((ItemCurrency) locateCurrency("Markets", "Item")).withdraw(offlinePlayer, amount, itemStack);
	}

	public boolean deposit(@NonNull final OfflinePlayer offlinePlayer, @NonNull final ItemStack itemStack, final int amount) {
		return ((ItemCurrency) locateCurrency("Markets", "Item")).deposit(offlinePlayer, amount, itemStack);
	}

	@Override
	public void load() {
		clear();

		if (Settings.CURRENCY_USE_ITEM_ONLY.getBoolean()) {
			add(new ItemCurrency());
			return;
		}

		// add vault by default
		add(new VaultCurrency());
		add(new ItemCurrency());

		// load currencies from providers that allow multiple currencies
		if (Bukkit.getServer().getPluginManager().isPluginEnabled("UltraEconomy"))
			new UltraEconomyLoader().getCurrencies().forEach(this::add);

		if (Bukkit.getServer().getPluginManager().isPluginEnabled("Funds"))
			new FundsEconomyLoader().getCurrencies().forEach(this::add);
	}
}
