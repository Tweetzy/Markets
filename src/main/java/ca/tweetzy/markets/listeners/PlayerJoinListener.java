package ca.tweetzy.markets.listeners;

import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.MarketUser;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

	@EventHandler // TODO possibly adjust priority
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
			return;
		}

		// player is not found, so call create
		Markets.getPlayerManager().create(player, success -> {
			if (!success)
				Common.log("&cSomething went wrong while creating a new profile for&F: &e" + player.getName());

		});
	}
}
