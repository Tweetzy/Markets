package ca.tweetzy.markets.listeners;

import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.market.GUIMarketView;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * The current file has been created by Kiran Hart
 * Date Created: November 03 2021
 * Time Created: 9:08 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class SignListener implements Listener {

	@EventHandler
	public void onSignEdit(final SignChangeEvent event) {
		final Block block = event.getBlock();
		final Player player = event.getPlayer();

		if (!player.hasPermission("markets.createsign")) {
			return;
		}

		if (!(block.getState() instanceof Sign)) {
			return;
		}

		final Sign sign = (Sign) block.getState();

		if (event.getLines().length < 2) return;
		if (!event.getLine(0).equalsIgnoreCase(Settings.SIGN_ACTIVATE_HEADER.getString())) return;

		final Market market = Markets.getInstance().getMarketManager().getMarketByPlayerName(event.getLine(1));

		if (market == null) {
			sign.setLine(1, TextUtils.formatText("&cMarket not found"));
		} else {
			sign.setLine(0, TextUtils.formatText(Settings.SIGN_ACTIVATED_HEADER.getString()));
			sign.setLine(1, TextUtils.formatText(Settings.SIGN_OWNER_NAME_COLOR.getString() + market.getOwnerName()));
		}

		Bukkit.getServer().getScheduler().runTaskLater(Markets.getInstance(), () -> sign.update(true), 1L);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSignClick(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final Block block = event.getClickedBlock();

		if (block == null) return;
		if (!(block.getState() instanceof Sign)) return;

		final Sign sign = (Sign) block.getState();
		if (!TextUtils.formatText(Settings.SIGN_ACTIVATED_HEADER.getString()).equalsIgnoreCase(sign.getLine(0))) return;

		final Market market = Markets.getInstance().getMarketManager().getMarketByPlayerName(ChatColor.stripColor(sign.getLine(1)));

		if (market != null)
			Markets.getInstance().getGuiManager().showGUI(player, new GUIMarketView(market));
	}
}
