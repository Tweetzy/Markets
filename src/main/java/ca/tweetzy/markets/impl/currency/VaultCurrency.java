package ca.tweetzy.markets.impl.currency;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import org.bukkit.OfflinePlayer;

public final class VaultCurrency extends AbstractCurrency {

	public VaultCurrency() {
		super("Vault", "Vault", "");
		this.displayName = "Vault ($)";
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		return Markets.getEconomy().has(player, amount);
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		return Markets.getEconomy().withdrawPlayer(player, amount).transactionSuccess();
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		return Markets.getEconomy().depositPlayer(player, amount).transactionSuccess();
	}
}
