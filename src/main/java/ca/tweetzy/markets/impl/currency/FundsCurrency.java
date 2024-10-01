package ca.tweetzy.markets.impl.currency;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.funds.api.FundsAPI;
import ca.tweetzy.funds.api.interfaces.Account;
import ca.tweetzy.funds.api.interfaces.Currency;
import ca.tweetzy.markets.api.currency.IconableCurrency;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.OfflinePlayer;

public final class FundsCurrency extends IconableCurrency {

	private final Currency currency;

	public FundsCurrency(String currencyName) {
		super("Funds", currencyName, "", CompMaterial.PAPER.parseItem());
		this.currency = FundsAPI.getInstance().getCurrency(currencyName);

		if (this.currency != null) {
			setDisplayName(this.currency.getName());

			if (Settings.CURRENCY_ICONS_OVERRIDE.getBoolean())
				setIcon(Settings.CURRENCY_ICONS.getItemStack());
			else
				setIcon(this.currency.getIcon().parseItem());

			if (this.currency.isVaultCurrency())
				setVault(true);
		}
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

