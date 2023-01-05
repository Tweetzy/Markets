package ca.tweetzy.markets.api.currency;

public interface Currency {

	String getPluginName();

	String getCurrencyName();

	CurrencyType getCurrencyType();
}
