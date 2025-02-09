package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.gui.admin.MarketsAdminGUI;
import ca.tweetzy.markets.gui.shared.MarketsMainGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketCategoryViewGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketViewGUI;
import ca.tweetzy.markets.gui.user.BankGUI;
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
			if (sender instanceof final Player player)
				Markets.getGuiManager().showGUI(player, new MarketsAdminGUI(player));
			return ReturnType.SUCCESS;
		}// 0 1 2

		if (args.length == 1) {
			if (!(sender instanceof final Player player)) return ReturnType.FAIL;

			switch (args[0].toLowerCase()) {
				case "collecttax":
					Markets.getGuiManager().showGUI(player, new BankGUI(null, player, true));
			}
			return ReturnType.SUCCESS;
		}

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
				case "openserver":
					final Market market = Markets.getMarketManager().getServerMarket();
					if (market == null) {
						tell(sender, "&cThe server market is not setup, please create it in /markets admin");
						break;
					}

					if (market.isEmpty() || !market.isOpen()) {
						tell(sender, "&cThe server market is closed/has no items - cannot open for player.");
						break;
					}

					final Category locatedCategory = args.length == 3 ? market.getCategories().stream().filter(category -> category.getName().equalsIgnoreCase(args[2])).findFirst().orElse(null) : null;

					if (locatedCategory != null) {
						Markets.getGuiManager().showGUI(target, new MarketCategoryViewGUI(target, market, locatedCategory, false, true));
						return ReturnType.SUCCESS;
					}

					Markets.getGuiManager().showGUI(target, new MarketViewGUI(null, target, market, false));
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
