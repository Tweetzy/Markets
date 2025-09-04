package ca.tweetzy.markets.impl.currency;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.markets.api.currency.IconableCurrency;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.OfflinePlayer;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

public final class CoinEngineCurrency extends IconableCurrency {

	private final Currency currency;

	public CoinEngineCurrency(String currencyName) {
		super("CoinsEngine", currencyName, "", CompMaterial.PAPER.parseItem());
		this.currency = CoinsEngineAPI.getCurrency(currencyName);

		if (this.currency != null) {
			setDisplayName(this.currency.getName());

			if (Settings.CURRENCY_ICONS_OVERRIDE.getBoolean())
				setIcon(Settings.CURRENCY_ICONS.getItemStack());

			if (this.currency.isVaultEconomy())
				setVault(true);
		}
	}

	@Override
	public boolean has(OfflinePlayer player, double amount) {
		if (this.currency == null)
			return false;

		return CoinsEngineAPI.getBalance(player.getUniqueId(), this.currency) >= amount;
	}

	@Override
	public boolean withdraw(OfflinePlayer player, double amount) {
		if (this.currency == null)
			return false;

		return CoinsEngineAPI.removeBalance(player.getUniqueId(), this.currency, amount);
	}

	@Override
	public boolean deposit(OfflinePlayer player, double amount) {
		if (this.currency == null)
			return false;

		return CoinsEngineAPI.addBalance(player.getUniqueId(), this.currency, amount);
	}
}

