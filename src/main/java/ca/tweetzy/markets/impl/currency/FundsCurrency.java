package ca.tweetzy.markets.impl.currency;

import ca.tweetzy.funds.api.FundsAPI;
import ca.tweetzy.funds.api.interfaces.Account;
import ca.tweetzy.funds.api.interfaces.Currency;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

public final class FundsCurrency extends AbstractCurrency {

	private final Currency currency;

	@Getter
	private String currencyDisplayName;

	public FundsCurrency(String currencyName) {
		super("Funds", currencyName);
		this.currency = FundsAPI.getInstance().getCurrency(currencyName);

		if (this.currency != null)
			this.currencyDisplayName = this.currency.getName();
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		final Account account = FundsAPI.getInstance().getAccount(player);

		if (account == null || this.currency == null)
			return false;

		return account.getCurrencies().getOrDefault(this.currency, 0D) >= amount;
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		final Account account = FundsAPI.getInstance().getAccount(player);

		if (account == null || this.currency == null)
			return false;

		account.withdrawCurrency(this.currency, amount);
		account.sync(true);
		return true;
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		final Account account = FundsAPI.getInstance().getAccount(player);

		if (account == null || this.currency == null)
			return false;

		account.depositCurrency(this.currency, amount);
		account.sync(true);
		return true;
	}
}

