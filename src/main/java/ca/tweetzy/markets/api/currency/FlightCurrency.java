package ca.tweetzy.markets.api.currency;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

@AllArgsConstructor
public abstract class FlightCurrency implements Currency {

	protected String pluginName;
	protected String currencyName;
	protected CurrencyType currencyType;

	@Override
	public String getPluginName() {
		return this.pluginName;
	}

	@Override
	public String getCurrencyName() {
		return this.currencyName;
	}

	@Override
	public CurrencyType getCurrencyType() {
		return this.currencyType;
	}

	public abstract boolean isEnabled();

	public abstract boolean has(@NonNull final OfflinePlayer offlinePlayer, final double amount);

	public abstract boolean withdraw(@NonNull final OfflinePlayer offlinePlayer, final double amount);

	public abstract boolean deposit(@NonNull final OfflinePlayer offlinePlayer, final double amount);
}
