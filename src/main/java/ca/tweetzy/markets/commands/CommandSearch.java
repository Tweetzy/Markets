package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.gui.shared.view.MarketSearchGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class CommandSearch extends Command {

	public CommandSearch() {
		super(AllowedExecutor.PLAYER, "search");
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (args.length < 1) {
			return ReturnType.FAIL;
		}

		if (sender instanceof final Player player) {
			final StringBuilder builder = new StringBuilder();

			for (int i = 0; i < args.length; i++) {
				builder.append(args[i]).append(" ");
			}

			Markets.getGuiManager().showGUI(player, new MarketSearchGUI(null, player, builder.toString().trim()));
		}
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.command.search";
	}

	@Override
	public String getSyntax() {
		return "search <keywords>";
	}

	@Override
	public String getDescription() {
		return "Search all open markets for items";
	}
}
