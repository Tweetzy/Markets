package ca.tweetzy.markets.model.currency;

import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.impl.currency.EcoBitsCurrency;
import ca.tweetzy.markets.settings.Settings;
import com.willfp.ecobits.currencies.Currencies;
import com.willfp.ecobits.currencies.Currency;

import java.util.ArrayList;
import java.util.List;

public final class EcoBitsEconomyLoader extends CurrencyLoader {

	public EcoBitsEconomyLoader() {
		super("EcoBits");
	}


	@Override
	public List<AbstractCurrency> getCurrencies() {
		final List<AbstractCurrency> currencies = new ArrayList<>();

		for (Currency currency : Currencies.values()) {
			boolean blackListed = false;

			for (String blacklisted : Settings.CURRENCY_BLACKLISTED.getStringList()) {
				final String[] blacklistSplit = blacklisted.split(":");

				if (blacklistSplit.length != 2) continue;
				if (!blacklistSplit[0].equalsIgnoreCase(this.owningPlugin)) continue;

				if (blacklistSplit[1].equalsIgnoreCase(currency.getId()))
					blackListed = true;

			}

			if (!blackListed)
				currencies.add(new EcoBitsCurrency(currency.getId()));
		}

		return currencies;
	}
}
