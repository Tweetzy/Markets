package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.gui.shared.view.content.MarketCategoryViewGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketViewGUI;
import ca.tweetzy.markets.gui.user.category.MarketCategoryEditGUI;
import ca.tweetzy.markets.gui.user.market.MarketOverviewGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class CommandView extends Command {

	public CommandView() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_VIEW.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (args.length < 1) return ReturnType.INVALID_SYNTAX;

		if (sender instanceof final Player player) {
			final OfflinePlayer target = Bukkit.getPlayerExact(args[0]);
			Market market = null;

			if (target == null) {
				market = Markets.getMarketManager().getByOwnerName(args[0]);

				if (market == null) {
					Common.tell(player, TranslationManager.string(player, Translations.PLAYER_OFFLINE, "value", args[0]));
					return ReturnType.FAIL;
				}
			}

			if (market == null)
				market = Markets.getMarketManager().getByOwner(target.getUniqueId());

			if (market == null) {
				Common.tell(player, TranslationManager.string(player, Translations.NO_MARKET_FOUND, "player_name", args[0]));
				return ReturnType.FAIL;
			}

			if (args.length == 2) {
				final Category locatedCategory = market.getCategories().stream().filter(category -> category.getName().equalsIgnoreCase(args[1])).findFirst().orElse(null);
				if (locatedCategory == null) {
					handle(market, player, target);
				} else {
					if (market.getOwnerUUID().equals(target.getUniqueId())) {
						Markets.getGuiManager().showGUI(player, new MarketCategoryViewGUI(player, market, locatedCategory));
					} else {
						Markets.getGuiManager().showGUI(player, new MarketCategoryEditGUI(player, market, locatedCategory));
					}
				}

			} else {
				handle(market, player, target);
			}
		}

		return ReturnType.SUCCESS;
	}

	private void handle(Market market, Player player, OfflinePlayer target) {
		if (market.getOwnerUUID().equals(target.getUniqueId())) {
			Markets.getGuiManager().showGUI(player, new MarketOverviewGUI(player, market));
		} else {
			Markets.getGuiManager().showGUI(player, new MarketViewGUI(player, market));
		}
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.command.view";
	}

	@Override
	public String getSyntax() {
		return "view <player>";
	}

	@Override
	public String getDescription() {
		return "Used to open another player's market";
	}
}
