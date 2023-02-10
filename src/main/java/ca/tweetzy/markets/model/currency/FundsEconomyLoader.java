package ca.tweetzy.markets.model.currency;

import ca.tweetzy.funds.api.FundsAPI;
import ca.tweetzy.funds.api.interfaces.Currency;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.impl.currency.FundsCurrency;

import java.util.ArrayList;
import java.util.List;

public final class FundsEconomyLoader extends CurrencyLoader {

	public FundsEconomyLoader() {
		super("Funds");
	}

	@Override
	public List<AbstractCurrency> getCurrencies() {
		final List<AbstractCurrency> currencies = new ArrayList<>();

		for (Currency currency : FundsAPI.getInstance().getCurrencies()) {
			currencies.add(new FundsCurrency(currency.getId()));
		}

		return currencies;
	}
}
