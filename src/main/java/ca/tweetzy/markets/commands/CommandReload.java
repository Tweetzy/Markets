package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class CommandReload extends Command {

	public CommandReload() {
		super(AllowedExecutor.BOTH, "reload");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		Settings.init();
		Translations.init();
		Common.setPrefix(Settings.PREFIX.getStringOr("&8[&EMarkets&8]"));
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.command.reload";
	}

	@Override
	public String getSyntax() {
		return "reload";
	}

	@Override
	public String getDescription() {
		return "Reloads all the configuration files";
	}
}
