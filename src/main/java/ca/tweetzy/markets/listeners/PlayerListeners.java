package ca.tweetzy.markets.listeners;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.market.Market;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 06 2021
 * Time Created: 6:11 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class PlayerListeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Markets.newChain().async(() -> {
            Player player = e.getPlayer();
            Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);
            if (market == null) return;
            if (market.getOwnerName().equals(player.getName())) return;
            market.setOwnerName(player.getName());
        }).execute();
    }
}
