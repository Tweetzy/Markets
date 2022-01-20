package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.GUIBlockedItems;
import ca.tweetzy.markets.market.contents.BlockedItem;
import ca.tweetzy.markets.utils.Common;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 27 2021
 * Time Created: 4:09 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandsBlockItem extends AbstractCommand {

	public CommandsBlockItem() {
		super(CommandType.PLAYER_ONLY, "block item");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		Player player = (Player) sender;

		if (args.length == 0) {
			ItemStack heldItem = Common.getItemInHand(player).clone();

			if (heldItem.getType() == XMaterial.AIR.parseMaterial()) {
				Markets.getInstance().getLocale().getMessage("nothing_in_hand").sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}

			Markets.getInstance().getMarketManager().addBlockedItem(new BlockedItem(
					UUID.randomUUID(),
					heldItem
			));

			Markets.getInstance().getLocale().getMessage("added_blocked_item").processPlaceholder("item_name", Common.getItemName(heldItem)).sendPrefixedMessage(player);
			return ReturnType.SUCCESS;
		}

		if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
			Markets.getInstance().getGuiManager().showGUI(player, new GUIBlockedItems());
		}

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.blockitem";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.block_item").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.block_item").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1)
			return Collections.singletonList("list");
		return null;
	}
}
