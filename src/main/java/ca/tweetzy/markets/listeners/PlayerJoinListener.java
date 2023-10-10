package ca.tweetzy.markets.listeners;

import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		final MarketUser marketUser = Markets.getPlayerManager().get(player.getUniqueId());

		if (marketUser != null) {
			// update name if changed
			if (marketUser.getLastKnownName().equalsIgnoreCase(player.getName()))
				marketUser.setLastKnownName(player.getName());

			marketUser.setLastSeenAt(System.currentTimeMillis());
			// perform sync
			marketUser.sync(result -> {
				if (result == SynchronizeResult.FAILURE)
					Common.log("&cSomething went wrong while updating the market profile for&F: &e" + player.getName());
			});

			// update the market if the player has one
			final Market market = Markets.getMarketManager().getByOwner(player.getUniqueId());
			if (market != null) {
				if (!market.getOwnerName().equalsIgnoreCase(player.getName())) {
					market.setOwnerName(player.getName());
					market.sync(result -> {
						if (result == SynchronizeResult.FAILURE)
							Common.log("&cSomething went wrong while updating the market owner name for&F: &e" + player.getName());
					});
				}
			}

			return;
		}

		// player is not found, so call create
		Markets.getPlayerManager().create(player, success -> {
			if (!success)
				Common.log("&cSomething went wrong while creating a new profile for&F: &e" + player.getName());

		});
	}
}
