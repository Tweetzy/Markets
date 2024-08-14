package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.gui.shared.MarketsMainGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public final class CommandAdmin extends Command {

	public CommandAdmin() {
		super(AllowedExecutor.BOTH, Settings.CMD_ALIAS_SUB_ADMIN.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (args.length == 0) {
			return ReturnType.SUCCESS;
		}// 0 1 2


		if (args.length >= 2) {
			final Player target = Bukkit.getPlayerExact(args[0]);

			if (target == null) {
				tell(sender, TranslationManager.string(Translations.PLAYER_NOT_FOUND, "value", args[0]));
				return ReturnType.FAIL;
			}


			// open main menu
			switch (args[1].toLowerCase()) {
				case "openmain":
					Markets.getGuiManager().showGUI(target, new MarketsMainGUI(target));
					break;
			}
		}


		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		if (args.length == 1)
			return Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toList());

		if (args.length == 2)
			return List.of("openmain");

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
