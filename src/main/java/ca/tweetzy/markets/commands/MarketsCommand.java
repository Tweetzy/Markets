package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.gui.shared.MarketsMainGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class MarketsCommand extends Command {

	public MarketsCommand() {
		super(AllowedExecutor.BOTH, "markets");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (sender instanceof final Player player) {
			Markets.getGuiManager().showGUI(player, new MarketsMainGUI(player));
			return ReturnType.SUCCESS;
		}

		return ReturnType.SUCCESS;
	}

	@Override
	public String getSyntax() {
		return "/markets";
	}

	@Override
	public String getDescription() {
		return "Main command for Markets";
	}

	@Override
	public String getPermissionNode() {
		return null;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}
}
