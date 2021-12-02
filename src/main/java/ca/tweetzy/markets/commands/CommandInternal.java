package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.items.GUIIconSelect;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 28 2021
 * Time Created: 11:45 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class CommandInternal extends AbstractCommand {

	public CommandInternal() {
		super(CommandType.PLAYER_ONLY, "internal");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (args.length == 0) return ReturnType.FAILURE;
		final Player player = (Player) sender;
		final Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);

		switch (args[0]) {
			case "marketsCustomCurrencySelectionGUI":
				MarketCategory marketCategory = market.getCategories().stream().filter(cat -> cat.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);
				Markets.getInstance().getGuiManager().showGUI(player, new GUIIconSelect(market, marketCategory, true));
				break;
		}
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return null;
	}

	@Override
	public String getSyntax() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}
}
