package ca.tweetzy.markets.api.market.core;

import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Synchronize;
import ca.tweetzy.markets.api.market.MarketSortType;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public interface MarketUser extends Synchronize, Storeable<MarketUser> {

	@NonNull UUID getUUID();

	@Nullable Player getPlayer();

	@NonNull String getLastKnownName();

	@NonNull List<String> getBio();

	@NonNull String getPreferredLanguage();

	@NonNull String getCurrencyFormatCountry();

	long getLastSeenAt();

	MarketSortType getMarketSortType();

	void setLastKnownName(@NonNull final String name);

	void setPlayer(@NonNull final Player player);

	void setBio(@NonNull final List<String> bio);

	void setPreferredLanguage(@NonNull final String preferredLanguage);

	void setCurrencyFormatCountry(@NonNull final String currencyFormatCountry);

	void setLastSeenAt(final long lastSeenAt);

	void setMarketSortType(MarketSortType marketSortType);

	default boolean isServerMarket() {
		return getUUID().equals(UUID.fromString(Settings.SERVER_MARKET_UUID.getString()));
	}
}
