package ca.tweetzy.markets.impl.currency;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.markets.api.currency.IconableCurrency;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import su.nightexpress.excellenteconomy.api.ExcellentEconomyAPI;

public final class CoinEngineCurrency extends IconableCurrency {

	private final ExcellentEconomyAPI api;
	private final String currencyId;

	public CoinEngineCurrency(String currencyId) {
		super("CoinsEngine", currencyId, "", CompMaterial.PAPER.parseItem());

		RegisteredServiceProvider<ExcellentEconomyAPI> provider =
				Bukkit.getServer().getServicesManager().getRegistration(ExcellentEconomyAPI.class);
		this.api = provider != null ? provider.getProvider() : null;
		this.currencyId = currencyId;

		if (this.api != null && this.api.hasCurrency(currencyId)) {
			setDisplayName(currencyId);
		}
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		if (this.currencyId == null || this.api == null)
			return false;

		Double balance = this.api.getBalanceAsync(player.getUniqueId(), this.currencyId)
				.exceptionally(ex -> 0.0)
				.join();

		return balance >= amount;
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		if (this.currencyId == null || this.api == null)
			return false;

		Player online = player.getPlayer();
		if (online != null && online.isOnline())
			return this.api.withdraw(online, this.currencyId, amount);

		return this.api.withdrawAsync(player.getUniqueId(), this.currencyId, amount)
				.thenApply(r -> r != null && r.success())
				.exceptionally(ex -> false)
				.join();
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		if (this.currencyId == null || this.api == null)
			return false;

		Player online = player.getPlayer();
		if (online != null && online.isOnline())
			return this.api.deposit(online, this.currencyId, amount);

		return this.api.depositAsync(player.getUniqueId(), this.currencyId, amount)
				.thenApply(r -> r != null && r.success())
				.exceptionally(ex -> false)
				.join();
	}
}


