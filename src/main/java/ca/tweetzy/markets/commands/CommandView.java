package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.market.GUIMarketView;
import ca.tweetzy.markets.market.Market;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 01 2021
 * Time Created: 6:16 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandView extends AbstractCommand {

	public CommandView() {
		super(CommandType.PLAYER_ONLY, "view");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (args.length < 1) return ReturnType.SYNTAX_ERROR;

		Player player = (Player) sender;
		Market market = Markets.getInstance().getMarketManager().getMarketByPlayerName(args[0]);
		if (market == null) {
			Markets.getInstance().getLocale().getMessage("market_not_found").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		if (market.isUnpaid() && market.getOwner().equals(player.getUniqueId())) {
			Markets.getInstance().getLocale().getMessage("upkeep_fee_not_paid").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		if (!market.isOpen()) {
			if (!market.getOwner().equals(player.getUniqueId())) {
				Markets.getInstance().getLocale().getMessage("market_closed").sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}
		}

		Markets.getInstance().getGuiManager().showGUI(player, new GUIMarketView(market));
		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.view";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.view").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.view").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1 && Markets.getInstance().getMarketManager().getMarkets().size() != 0)
			return Markets.getInstance().getMarketManager().getMarkets().stream().map(Market::getOwnerName).collect(Collectors.toList());
		return null;
	}
}
