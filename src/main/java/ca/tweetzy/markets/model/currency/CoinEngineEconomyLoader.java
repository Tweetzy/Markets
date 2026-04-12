package ca.tweetzy.markets.model.currency;

import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.impl.currency.CoinEngineCurrency;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;
import su.nightexpress.excellenteconomy.api.currency.ExcellentCurrency;

import java.util.ArrayList;
import java.util.List;

public final class CoinEngineEconomyLoader extends CurrencyLoader {

	public CoinEngineEconomyLoader() {
		super("CoinsEngine");
	}

	@Override
	public List<AbstractCurrency> getCurrencies() {
		final List<AbstractCurrency> currencies = new ArrayList<>();

		RegisteredServiceProvider<ExcellentEconomyAPI> provider =
				Bukkit.getServer().getServicesManager().getRegistration(ExcellentEconomyAPI.class);
		if (provider == null)
			return currencies;

		ExcellentEconomyAPI api = provider.getProvider();
		for (ExcellentCurrency currency : api.getCurrencies()) {
			boolean blackListed = false;

			for (String blacklisted : Settings.CURRENCY_BLACKLISTED.getStringList()) {
				final String[] blacklistSplit = blacklisted.split(":");

				if (blacklistSplit.length != 2) continue;
				if (!blacklistSplit[0].equalsIgnoreCase(this.owningPlugin)) continue;

				if (blacklistSplit[1].equalsIgnoreCase(currency.getId()))
					blackListed = true;
			}

			if (!blackListed)
				currencies.add(new CoinEngineCurrency(currency.getId()));
		}

		return currencies;
	}
}