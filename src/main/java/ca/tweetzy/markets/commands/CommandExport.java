package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.Markets;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 03 2021
 * Time Created: 12:30 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandExport extends AbstractCommand {

	public CommandExport() {
		super(CommandType.PLAYER_ONLY, "export");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {

		Markets.newSharedChain("export").async(() -> {
			Markets.getInstance().getMarketManager().export();
			sender.sendMessage(TextUtils.formatText("&aExported markets to markets-export.yml"));
		}).execute();

		Markets.newSharedChain("export").async(() -> {
			Markets.getInstance().getTransactionManger().export();
			sender.sendMessage(TextUtils.formatText("&aExported transactions to markets-export.yml"));
		}).execute();

		Markets.newSharedChain("export").async(() -> {
			Markets.getInstance().getRequestManager().export();
			sender.sendMessage(TextUtils.formatText("&aExported requests to markets-export.yml"));
		}).execute();

		Markets.newSharedChain("export").async(() -> {
			Markets.getInstance().getCurrencyBank().export();
			sender.sendMessage(TextUtils.formatText("&aExported bank to markets-export.yml"));
		}).execute();

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.export";
	}

	@Override
	public String getSyntax() {
		return "export";
	}

	@Override
	public String getDescription() {
		return "Exports v1 data in a format to be imported into v2";
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}
}
