package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.market.GUIMarketList;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:34 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandList extends AbstractCommand {

	public CommandList() {
		super(CommandType.PLAYER_ONLY, "list");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		Player player = (Player) sender;
		Markets.getInstance().getGuiManager().showGUI(player, new GUIMarketList());
		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.list";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.list").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.list").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}
}
