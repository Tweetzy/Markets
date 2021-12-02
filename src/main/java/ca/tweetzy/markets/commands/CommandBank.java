package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.CompatibleHand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.guis.GUIBank;
import ca.tweetzy.markets.utils.Common;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 06 2021
 * Time Created: 2:39 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandBank extends AbstractCommand {

	public CommandBank() {
		super(CommandType.PLAYER_ONLY, "bank");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		Player player = (Player) sender;
		if (args.length == 0) {
			Markets.getInstance().getGuiManager().showGUI(player, new GUIBank(player));
			return ReturnType.SUCCESS;
		}

		ItemStack heldItem = Common.getItemInHand(player).clone();

		boolean addAll = args.length == 2 && MarketsAPI.getInstance().getCommandFlags(args).contains("-a");

		if (args[0].equalsIgnoreCase("add")) {
			if (heldItem.getType() == XMaterial.AIR.parseMaterial()) {
				Markets.getInstance().getLocale().getMessage("nothing_in_hand").sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			if (addAll) {
				int total = MarketsAPI.getInstance().getItemCountInPlayerInventory(player, heldItem);
				Markets.getInstance().getCurrencyBank().addCurrency(player.getUniqueId(), heldItem, total);
				Markets.getInstance().getLocale().getMessage("added_currency_to_bank").processPlaceholder("amount", total).processPlaceholder("currency_item", Common.getItemName(heldItem)).sendPrefixedMessage(player);
				MarketsAPI.getInstance().removeSpecificItemQuantityFromPlayer(player, heldItem, total);
			} else {
				Markets.getInstance().getCurrencyBank().addCurrency(player.getUniqueId(), heldItem, heldItem.getAmount());
				Markets.getInstance().getLocale().getMessage("added_currency_to_bank").processPlaceholder("amount", heldItem.getAmount()).processPlaceholder("currency_item", Common.getItemName(heldItem)).sendPrefixedMessage(player);
				PlayerUtils.takeActiveItem(player, CompatibleHand.MAIN_HAND, heldItem.getAmount());
			}
		}
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1) return Collections.singletonList("add");
		if (args.length == 2) return Collections.singletonList("-a");
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.bank";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.bank").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.bank").getMessage();
	}
}
