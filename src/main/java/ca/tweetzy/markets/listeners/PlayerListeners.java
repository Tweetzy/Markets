package ca.tweetzy.markets.listeners;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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

    @EventHandler
    public void onQuickAddChestClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!Markets.getInstance().getUsingQuickAdd().containsKey(player.getUniqueId())) return;
        if (e.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getType() != XMaterial.CHEST.parseMaterial()) return;
        if (!(e.getClickedBlock().getState() instanceof Chest)) return;

        Chest chest = (Chest) e.getClickedBlock().getState();
        if (chest.getInventory().isEmpty()) {
            Markets.getInstance().getLocale().getMessage("chest_empty").sendPrefixedMessage(player);
            return;
        }

    }

    @EventHandler
    public void onPlayerChatUsingQuickAdd(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (!Markets.getInstance().getUsingQuickAdd().containsKey(player.getUniqueId())) return;
        if (e.getMessage().length() != 0 && e.getMessage().equalsIgnoreCase(Settings.QUICK_ADD_CANCEL_WORD.getString())) {
            Markets.getInstance().getUsingQuickAdd().remove(player.getUniqueId());
            Markets.getInstance().getLocale().getMessage("exited_quick_add").sendPrefixedMessage(player);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreakUsingQuickAdd(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (Markets.getInstance().getUsingQuickAdd().containsKey(player.getUniqueId())) e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlaceUsingQuickAdd(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (Markets.getInstance().getUsingQuickAdd().containsKey(player.getUniqueId())) e.setCancelled(true);
    }
}
