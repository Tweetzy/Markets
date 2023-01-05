package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.api.currency.FlightCurrency;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.model.currency.VaultCurrency;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

public final class CurrencyManager extends ListManager<FlightCurrency> {

	public CurrencyManager() {
		super("Currency");

		add(new VaultCurrency());
	}

	public boolean has(@NonNull final FlightCurrency currency, @NonNull OfflinePlayer offlinePlayer, double amount) {
		return currency.has(offlinePlayer, amount);
	}

	public boolean withdraw(@NonNull final FlightCurrency currency, @NonNull OfflinePlayer offlinePlayer, double amount) {
		return currency.withdraw(offlinePlayer, amount);
	}

	public boolean deposit(@NonNull final FlightCurrency currency, @NonNull OfflinePlayer offlinePlayer, double amount) {
		return currency.deposit(offlinePlayer, amount);
	}

	@Override
	public void load() {

	}
}
