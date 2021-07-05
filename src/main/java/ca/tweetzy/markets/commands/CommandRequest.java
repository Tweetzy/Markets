package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.market.contents.BlockedItem;
import ca.tweetzy.markets.request.Request;
import ca.tweetzy.markets.request.RequestItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 07 2021
 * Time Created: 2:37 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandRequest extends AbstractCommand {

    public CommandRequest() {
        super(CommandType.PLAYER_ONLY, "request");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 2) return ReturnType.SYNTAX_ERROR;
        Player player = (Player) sender;

        ItemStack heldItem = Common.getItemInHand(player).clone();
        if (heldItem.getType() == XMaterial.AIR.parseMaterial()) {
            Markets.getInstance().getLocale().getMessage("nothing_in_hand").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (Markets.getInstance().getMarketManager().getBlockedItems().size() != 0 && Markets.getInstance().getMarketManager().getBlockedItems().stream().map(BlockedItem::getItem).anyMatch(item -> item.isSimilar(heldItem))) {
            Markets.getInstance().getLocale().getMessage("item_is_blocked").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (!NumberUtils.isInt(args[0]) || !NumberUtils.isDouble(args[1])) {
            Markets.getInstance().getLocale().getMessage("not_a_number").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (Double.parseDouble(args[1]) <= 0) {
            Markets.getInstance().getLocale().getMessage("price_is_zero_or_less").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        int requestedAmount = Integer.parseInt(args[0]);
        if (requestedAmount > Settings.MAX_REQUEST_AMOUNT.getInt()) {
            Markets.getInstance().getLocale().getMessage("max_request_amount").processPlaceholder("request_max_amount", Settings.MAX_REQUEST_AMOUNT.getInt()).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        boolean useCustomCurrency  = args.length == 3 && MarketsAPI.getInstance().getCommandFlags(args).contains("-c");

        double priceForAll = Double.parseDouble(args[1]);
        double pricePerItem = priceForAll / requestedAmount;
        int maxStackSize = heldItem.getMaxStackSize();
        int fullStacks = requestedAmount / maxStackSize;
        int remainder = requestedAmount % maxStackSize;

        if (useCustomCurrency) {
            Markets.getInstance().getMarketPlayerManager().addPlayerToRequestCustomCurrencyItem(player.getUniqueId(), heldItem, requestedAmount, priceForAll);
            Markets.getInstance().getLocale().getMessage("click_currency_item_request").sendPrefixedMessage(player);
            return ReturnType.SUCCESS;
        }

        List<RequestItem> requestItems = new ArrayList<>();

        for (int i = 0; i < fullStacks; i++) {
            requestItems.add(new RequestItem(heldItem, XMaterial.AIR.parseItem(), maxStackSize, pricePerItem * maxStackSize, false, false));
        }

        if (remainder != 0) {
            requestItems.add(new RequestItem(heldItem, XMaterial.AIR.parseItem(), remainder, pricePerItem * remainder, false, false));
        }

        Markets.getInstance().getRequestManager().addRequest(new Request(player.getUniqueId(), requestItems));
        Markets.getInstance().getLocale().getMessage("created_request").processPlaceholder("request_amount", requestedAmount).processPlaceholder("request_item_name", Common.getItemName(heldItem)).processPlaceholder("request_price", String.format("%,.2f", priceForAll)).sendPrefixedMessage(player);
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1 || args.length == 2) return Arrays.asList("1", "2", "3", "4", "5");
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "markets.cmd.request";
    }

    @Override
    public String getSyntax() {
        return Markets.getInstance().getLocale().getMessage("command_syntax.request").getMessage();
    }

    @Override
    public String getDescription() {
        return Markets.getInstance().getLocale().getMessage("command_description.request").getMessage();
    }
}
