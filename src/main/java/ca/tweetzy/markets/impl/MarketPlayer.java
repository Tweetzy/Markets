package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.database.annotations.Column;
import ca.tweetzy.flight.database.annotations.Id;
import ca.tweetzy.flight.database.annotations.Ignore;
import ca.tweetzy.flight.database.annotations.Nested;
import ca.tweetzy.flight.database.annotations.Table;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.MarketSortType;
import ca.tweetzy.markets.api.market.core.MarketUser;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Table("user")
public final class MarketPlayer implements MarketUser {

	@Id
	@Column("id")
	private UUID uuid;
	
	@Ignore
	private Player player;
	
	@Column("last_known_name")
	private String lastKnownName;
	
	@Nested
	@Column("bio")
	private List<String> bio;

	@Column("preferred_language")
	private String preferredLanguage;

	@Column("currency_format_country")
	private String currencyFormatCountry;
	
	@Ignore
	private MarketSortType marketSortType;

	@Column("last_seen_at")
	private long lastSeenAt;

	public MarketPlayer() {
	}

	public MarketPlayer(@NonNull UUID uuid, Player player, @NonNull String lastKnownName, @NonNull List<String> bio, @NonNull String preferredLanguage, @NonNull String currencyFormatCountry, MarketSortType marketSortType, long lastSeenAt) {
		this.uuid = uuid;
		this.player = player;
		this.lastKnownName = lastKnownName;
		this.bio = bio;
		this.preferredLanguage = preferredLanguage;
		this.currencyFormatCountry = currencyFormatCountry;
		this.marketSortType = marketSortType;
		this.lastSeenAt = lastSeenAt;
	}

	@Override
	public @NonNull UUID getUUID() {
		return this.uuid;
	}

	@Override
	public @Nullable Player getPlayer() {
		return this.player;
	}

	@Override
	public @NonNull String getLastKnownName() {
		return this.lastKnownName;
	}

	@Override
	public @NonNull List<String> getBio() {
		if (this.bio == null) {
			this.bio = new ArrayList<>();
		}
		return this.bio;
	}

	@Override
	public @NonNull String getPreferredLanguage() {
		return this.preferredLanguage;
	}

	@NotNull
	@Override
	public String getCurrencyFormatCountry() {
		return this.currencyFormatCountry;
	}

	@Override
	public long getLastSeenAt() {
		return this.lastSeenAt;
	}

	@Override
	public MarketSortType getMarketSortType() {
		if (this.marketSortType == null) {
			// Return the first enabled sort type as default
			for (MarketSortType type : MarketSortType.values()) {
				if (type.isEnabled()) {
					this.marketSortType = type;
					return type;
				}
			}
			// Fallback to NAME if no enabled types found
			this.marketSortType = MarketSortType.NAME;
		}
		return this.marketSortType;
	}

	@Override
	public void setLastKnownName(@NonNull String lastKnownName) {
		this.lastKnownName = lastKnownName;
	}

	@Override
	public void setPlayer(@NonNull Player player) {
		this.player = player;
	}

	@Override
	public void setBio(@NonNull List<String> bio) {
		this.bio = bio;
	}

	@Override
	public void setPreferredLanguage(@NotNull String preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}

	@Override
	public void setCurrencyFormatCountry(@NotNull String currencyFormatCountry) {
		this.currencyFormatCountry = currencyFormatCountry;
	}

	@Override
	public void setLastSeenAt(long lastSeenAt) {
		this.lastSeenAt = lastSeenAt;
	}

	@Override
	public void setMarketSortType(MarketSortType marketSortType) {
		this.marketSortType = marketSortType;
	}

	@Override
	public void store(@NonNull Consumer<MarketUser> stored) {
		Markets.getMarketUserRepository().save(this, (error, created) -> {
			if (error == null)
				stored.accept(created);
		});
	}

	public void sync(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getMarketUserRepository().save(this, (error, saved) -> {
			if (syncResult != null)
				syncResult.accept(error == null ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}
}
