package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.events.MarketDeleteEvent;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 08 2021
 * Time Created: 11:24 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandRemove extends AbstractCommand {

	public CommandRemove() {
		super(CommandType.CONSOLE_OK, "remove");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (!CommandMiddleware.handle()) return ReturnType.FAILURE;

//		if (args.length == 0) {
//			Player player = (Player) sender;
//
//			Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);
//			if (market == null) {
//				Markets.getInstance().getLocale().getMessage("market_required").sendPrefixedMessage(player);
//				return ReturnType.FAILURE;
//			}
//
//			if (handleDeleteEvent(player, market)) return ReturnType.FAILURE;
//			deleteMarket(player, market, false);
//		}
//
//		if (args.length == 1 && sender.hasPermission("markets.admin")) {
//			Market market = Markets.getInstance().getMarketManager().getMarketByPlayerName(args[0]);
//			if (market == null) {
//				Markets.getInstance().getLocale().getMessage("market_not_found").sendPrefixedMessage(sender);
//				return ReturnType.FAILURE;
//			}
//
////			if (handleDeleteEvent(sender, market)) return ReturnType.FAILURE;
//			deleteMarket(sender, market, true);
//		}

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.remove";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.remove").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.remove").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1 && sender.hasPermission("markets.admin"))
			return Markets.getInstance().getMarketManager().getMarkets().stream().map(Market::getOwnerName).collect(Collectors.toList());
		return null;
	}

	private boolean handleDeleteEvent(Player player, Market market) {
		MarketDeleteEvent marketDeleteEvent = new MarketDeleteEvent(player, market);
		Bukkit.getPluginManager().callEvent(marketDeleteEvent);
		return marketDeleteEvent.isCancelled();
	}

	private void deleteMarket(CommandSender player, Market market, boolean admin) {
		if (Settings.GIVE_ITEMS_ON_MARKET_DELETE.getBoolean() && !market.getCategories().isEmpty()) {
			List<ItemStack> items = new ArrayList<>();
			if (player instanceof Player) {
				Player target = (Player) player;
				Markets.newChain().async(() -> market.getCategories().forEach(category -> category.getItems().forEach(item -> items.add(item.getItemStack())))).sync(() -> PlayerUtils.giveItem(target, items)).execute();
			}
		}
		Markets.getInstance().getMarketManager().deleteMarket(market);
		if (admin) {
			Markets.getInstance().getLocale().getMessage("removed_player_market").processPlaceholder("player", market.getOwnerName()).sendPrefixedMessage(player);
		} else {
			Markets.getInstance().getLocale().getMessage("removed_market").sendPrefixedMessage(player);
		}
	}
}
