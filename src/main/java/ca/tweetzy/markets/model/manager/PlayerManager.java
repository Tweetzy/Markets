package ca.tweetzy.markets.model.manager;

import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.KeyValueManager;
import ca.tweetzy.markets.api.market.MarketUser;
import ca.tweetzy.markets.impl.MarketPlayer;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class PlayerManager extends KeyValueManager<UUID, MarketUser> {

	public PlayerManager() {
		super("Player");
	}

	public void create(@NonNull final Player player, @NonNull final Consumer<Boolean> created) {
		final MarketUser marketUser = new MarketPlayer(
				player.getUniqueId(),
				player,
				player.getName(),
				List.of("Hi there, welcome to my profile"),
				"english",
				"us",
				System.currentTimeMillis()
		);

		marketUser.store(storedUser -> {
			if (storedUser != null) {
				add(storedUser.getUUID(), storedUser);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
	}

	@Override
	public void load() {
		clear();

		Markets.getDataManager().getMarketUsers((error, found) -> {
			if (error != null) return;
			Common.log("&aLoading Players");
			found.forEach(user -> add(user.getUUID(), user));
		});
	}
}
