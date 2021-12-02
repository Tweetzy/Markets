package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 07 2021
 * Time Created: 11:08 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandReload extends AbstractCommand {

	public CommandReload() {
		super(CommandType.CONSOLE_OK, "reload");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		long start = System.currentTimeMillis();
		Markets.getInstance().reloadConfig();
		Markets.getInstance().getLocale().getMessage("general.reloaded").processPlaceholder("value", System.currentTimeMillis() - start).sendPrefixedMessage(sender);

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.reload";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.reload").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.reload").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}
}
