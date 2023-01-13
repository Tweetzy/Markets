package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.model.currency.FundsEconomyLoader;
import ca.tweetzy.markets.model.currency.UltraEconomyLoader;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

import java.util.List;

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

	@Override
	public void load() {
		clear();

		// load currencies from providers that allow multiple currencies

		List.of(
				new UltraEconomyLoader(),
				new FundsEconomyLoader()
		).forEach(loader -> loader.getCurrencies().forEach(this::add));
	}
}
