package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.market.Market;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 06 2021
 * Time Created: 7:03 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandSet extends AbstractCommand {

	public CommandSet() {
		super(CommandType.PLAYER_ONLY, "set");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (args.length < 2) return ReturnType.SUCCESS;
		Player player = (Player) sender;

		Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);
		if (market == null) {
			Markets.getInstance().getLocale().getMessage("market_required").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		StringBuilder builder;
		switch (args[0].toLowerCase()) {
			case "name":
				builder = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					builder.append(args[i]).append(" ");
				}
				market.setName(builder.toString().trim());
				market.setUpdatedAt(System.currentTimeMillis());
				Markets.getInstance().getLocale().getMessage("updated_market_name").sendPrefixedMessage(player);
				break;
			case "desc":
			case "description":
				builder = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					builder.append(args[i]).append(" ");
				}
				market.setDescription(builder.toString().trim());
				market.setUpdatedAt(System.currentTimeMillis());
				Markets.getInstance().getLocale().getMessage("updated_market_description").sendPrefixedMessage(player);
				break;
			case "open":
				switch (args[1].toLowerCase()) {
					case "yes":
					case "true":
						market.setOpen(true);
						market.setUpdatedAt(System.currentTimeMillis());
						Markets.getInstance().getLocale().getMessage("updated_market_to_open").sendPrefixedMessage(player);
						break;
					case "no":
					case "false":
						market.setOpen(false);
						market.setUpdatedAt(System.currentTimeMillis());
						Markets.getInstance().getLocale().getMessage("updated_market_to_close").sendPrefixedMessage(player);
						break;
				}
				break;
		}

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1) return Arrays.asList("name", "description", "open");
		if (args.length == 2 && args[0].equalsIgnoreCase("open")) return Arrays.asList("true", "false", "yes", "no");
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.set";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.set").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.set").getMessage();
	}
}
