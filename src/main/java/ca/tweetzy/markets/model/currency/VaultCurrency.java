package ca.tweetzy.markets.model.currency;

import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.api.currency.CurrencyType;
import ca.tweetzy.markets.api.currency.FlightCurrency;
import lombok.NonNull;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultCurrency extends FlightCurrency {

	private Economy economy = null;

	public VaultCurrency() {
		super("Vault", "Vault", CurrencyType.MONEY);
		if (setupEconomy()) {
			Common.log("&aVault hooked into successfully");
		}
	}

	@Override
	public boolean isEnabled() {
		return this.economy != null && this.economy.isEnabled();
	}

	@Override
	public boolean has(@NonNull OfflinePlayer offlinePlayer, double amount) {
		return this.economy.has(offlinePlayer, amount);
	}

	@Override
	public boolean withdraw(@NonNull OfflinePlayer offlinePlayer, double amount) {
		return this.economy.withdrawPlayer(offlinePlayer, amount).transactionSuccess();
	}

	@Override
	public boolean deposit(@NonNull OfflinePlayer offlinePlayer, double amount) {
		return this.economy.depositPlayer(offlinePlayer, amount).transactionSuccess();
	}

	private boolean setupEconomy() {
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}

		RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

		if (rsp == null) {
			return false;
		}

		this.economy = rsp.getProvider();
		return this.economy != null;
	}
}
