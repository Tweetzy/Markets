package ca.tweetzy.markets.model.manager;

import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.impl.market.PlayerMarket;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class MarketManager extends ListManager<Market> {

	public MarketManager() {
		super("Market");
	}

	public Market getByOwner(@NonNull final UUID uuid) {
		return getManagerContent().stream().filter(market -> market.getOwnerUUID().equals(uuid)).findFirst().orElse(null);
	}

	public Market getByUUID(@NonNull final UUID uuid) {
		return getManagerContent().stream().filter(market -> market.getId().equals(uuid)).findFirst().orElse(null);
	}

	public void create(@NonNull final Player player, @NonNull final Consumer<Boolean> created) {
		final Market market = new PlayerMarket(
				UUID.randomUUID(),
				player.getUniqueId(),
				player.getName(),
				"&e" + player.getName() + "'s Market",
				List.of("&aWelcome to my market"),
				new ArrayList<>(),
				new ArrayList<>(),
				System.currentTimeMillis(),
				System.currentTimeMillis()
		);

		market.store(storedMarket -> {
			if (storedMarket != null) {
				add(storedMarket);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
	}

	@Override
	public void load() {
		clear();

		Markets.getDataManager().getMarkets((error, found) -> {
			if (error != null) return;
			Common.log("&aLoading Markets");
			found.forEach(this::add);

			// after markets have been added let's load categories
			Markets.getCategoryManager().load();
		});
	}
}
