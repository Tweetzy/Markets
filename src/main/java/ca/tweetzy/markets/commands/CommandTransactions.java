package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.gui.user.TransactionsGUI;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class CommandTransactions extends Command {

	public CommandTransactions() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_TRANSACTIONS.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (sender instanceof final Player player) {
			Markets.getGuiManager().showGUI(player, new TransactionsGUI(null, player));
		}
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.command.transactions";
	}

	@Override
	public String getSyntax() {
		return "transactions";
	}

	@Override
	public String getDescription() {
		return "Opens your offline transactions";
	}
}
