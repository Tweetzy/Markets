package ca.tweetzy.markets.commands;

import ca.tweetzy.flight.command.AllowedExecutor;
import ca.tweetzy.flight.command.Command;
import ca.tweetzy.flight.command.ReturnType;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.gui.user.OffersGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public final class CommandOffers extends Command {

	public CommandOffers() {
		super(AllowedExecutor.PLAYER, Settings.CMD_ALIAS_SUB_OFFERS.getStringList().toArray(new String[0]));
	}

	@Override
	protected ReturnType execute(CommandSender sender, String... args) {
		if (sender instanceof final Player player) {
			if (Settings.DISABLE_OFFERS.getBoolean()) {
				Common.tell(player, TranslationManager.string(player, Translations.OFFERS_DISABLED));
				return ReturnType.SUCCESS;
			}

			Markets.getGuiManager().showGUI(player, new OffersGUI(null, player));
		}
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> tab(CommandSender sender, String... args) {
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.command.offers";
	}

	@Override
	public String getSyntax() {
		return "offers";
	}

	@Override
	public String getDescription() {
		return "Opens your offers menu";
	}
}
