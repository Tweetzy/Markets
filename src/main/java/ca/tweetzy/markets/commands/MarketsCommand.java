package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.gui.shared.MarketsMainGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class MarketsCommand extends Command {

	public MarketsCommand() {
		super(AllowedExecutor.BOTH, Settings.CMD_ALIAS_MAIN.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (sender instanceof final Player player) {

			if (Settings.MAIN_COMMAND_REQUIRES_PERM.getBoolean() && !player.hasPermission("markets.command")) {
				tell(player, TranslationManager.string(player, Translations.NO_PERMISSION));
				return ReturnType.FAIL;
			}

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
