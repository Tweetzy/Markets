package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import org.bukkit.command.CommandSender;

import java.util.List;

public final class CommandAdmin extends Command {

	public CommandAdmin() {
		super(AllowedExecutor.BOTH, "admin");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		return null;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.command.admin";
	}

	@Override
	public String getSyntax() {
		return "admin";
	}

	@Override
	public String getDescription() {
		return "Used to open administrative gui";
	}
}
