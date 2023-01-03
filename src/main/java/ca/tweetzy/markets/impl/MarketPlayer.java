package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.api.MarketUser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class MarketPlayer implements MarketUser {

	private final UUID uuid;
	private Player player;
	private String lastKnownName;
	private List<String> bio;
	private long lastSeenAt;

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
		return this.bio;
	}

	@Override
	public long getLastSeenAt() {
		return this.lastSeenAt;
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
	public void setLastSeenAt(long lastSeenAt) {
		this.lastSeenAt = lastSeenAt;
	}

	@Override
	public void store(@NonNull Consumer<MarketUser> stored) {

	}
}
