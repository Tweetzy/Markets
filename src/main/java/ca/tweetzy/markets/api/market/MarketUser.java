package ca.tweetzy.markets.api.market;

import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Synchronize;
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

	long getLastSeenAt();

	void setLastKnownName(@NonNull final String name);

	void setPlayer(@NonNull final Player player);

	void setBio(@NonNull final List<String> bio);

	void setLastSeenAt(final long lastSeenAt);
}
