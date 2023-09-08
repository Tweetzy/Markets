package ca.tweetzy.markets.model.currency;

import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.impl.currency.UltraEconomyCurrency;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.objects.Currency;

import java.util.ArrayList;
import java.util.List;

public final class UltraEconomyLoader extends CurrencyLoader {

	public UltraEconomyLoader() {
		super("UltraEconomy");
	}


	@Override
	public List<AbstractCurrency> getCurrencies() {
		final List<AbstractCurrency> currencies = new ArrayList<>();

		for (Currency currency : UltraEconomy.getInstance().getCurrencies()) {
			currencies.add(new UltraEconomyCurrency(currency.getName()));
		}

		return currencies;
	}
}
