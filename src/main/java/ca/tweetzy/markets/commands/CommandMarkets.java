package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.GUIMain;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 30 2021
 * Time Created: 3:58 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandMarkets extends AbstractCommand {

	public CommandMarkets() {
		super(CommandType.CONSOLE_OK, "markets");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			Markets.getInstance().getGuiManager().showGUI(player, new GUIMain(player));
		}
		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.markets").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.markets").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}

}
