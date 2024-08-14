package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public final class CommandDelete extends Command {

	public CommandDelete() {
		super(AllowedExecutor.BOTH, Settings.CMD_ALIAS_SUB_DELETE.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (args.length == 0) {
			return ReturnType.SUCCESS;
		}// 0 1 2


		OfflinePlayer target = Bukkit.getPlayerExact(args[0]);

		if (target == null) {
			// try again with offline user list
			target = Bukkit.getOfflinePlayer(args[0]);
			if (target == null) {
				tell(sender, TranslationManager.string(Translations.PLAYER_NOT_FOUND, "value", args[0]));
				return ReturnType.FAIL;

			}
		}

		// check if they have a market
		final Market market = Markets.getMarketManager().getByOwner(target.getUniqueId());

		if (market == null) {
			tell(sender, TranslationManager.string(Translations.NO_MARKET_FOUND, "player_name", args[0]));
			return ReturnType.FAIL;
		}

		market.getCategories().forEach(category -> {
			category.getItems().forEach(item -> item.getViewingPlayers().clear());

			Markets.getDataManager().deleteMarketItems(category, (error, itemResult) -> {
				if (error == null && itemResult) {
					category.getItems().forEach(item -> Markets.getCategoryItemManager().remove(item));
				}
			});
		});

		// kill categories
		market.getCategories().forEach(category -> category.unStore(categoryRemoveResult -> {
		}));

		// remove market
		market.unStore(result -> {
			if (result == SynchronizeResult.SUCCESS) {
				tell(sender, TranslationManager.string(Translations.REMOVED_PLAYER_MARKET, "player_name", args[0]));
			}
		});


		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		if (args.length == 1)
			return Bukkit.getOnlinePlayers().stream().map(OfflinePlayer::getName).collect(Collectors.toList());

		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.command.delete";
	}

	@Override
	public String getSyntax() {
		return "delete <player>";
	}

	@Override
	public String getDescription() {
		return "Used to delete a player's market completely";
	}
}
