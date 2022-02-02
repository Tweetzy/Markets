package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.CompatibleHand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.api.events.MarketItemAddEvent;
import ca.tweetzy.markets.guis.items.GUIAddItem;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.BlockedItem;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 04 2021
 * Time Created: 12:51 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandAddItem extends AbstractCommand {

	public CommandAddItem() {
		super(CommandType.PLAYER_ONLY, "add item");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (!CommandMiddleware.handle()) return ReturnType.FAILURE;

		Player player = (Player) sender;

		Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);

		if (market == null) {
			Markets.getInstance().getLocale().getMessage("market_required").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		if (market.isUnpaid()) {
			Markets.getInstance().getLocale().getMessage("upkeep_fee_not_paid").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		if (market.getCategories().isEmpty()) {
			Markets.getInstance().getLocale().getMessage("market_category_required").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		// check the max allowed items
		if (Settings.LIMIT_MARKET_ITEMS_BY_PERMISSION.getBoolean()) {
			int maxAllowedItems = MarketsAPI.getInstance().maxAllowedMarketItems(player);
			int totalItemsInMarket = market.getCategories().stream().mapToInt(cat -> cat.getItems().size()).sum();
			if (totalItemsInMarket >= maxAllowedItems) {
				Markets.getInstance().getLocale().getMessage("at_max_items_limit").sendPrefixedMessage(player);
				return ReturnType.FAILURE;
			}
		}

		if (args.length == 0) {
			// open the add menu
			Markets.getInstance().getGuiManager().showGUI(player, new GUIAddItem(player, market));
			return ReturnType.SUCCESS;
		}

		ItemStack heldItem = Common.getItemInHand(player).clone();
		if (heldItem.getType() == XMaterial.AIR.parseMaterial()) {
			Markets.getInstance().getLocale().getMessage("nothing_in_hand").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		if (Markets.getInstance().getMarketManager().getBlockedItems().size() != 0 && Markets.getInstance().getMarketManager().getBlockedItems().stream().map(BlockedItem::getItem).anyMatch(item -> item.isSimilar(heldItem))) {
			Markets.getInstance().getLocale().getMessage("item_is_blocked").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		List<String> arguments = new ArrayList<>(Arrays.asList(args));
		Iterator<String> argumentIterator = arguments.listIterator();

		boolean useCustomCurrency = !MarketsAPI.getInstance().getCommandFlags(args).isEmpty() && MarketsAPI.getInstance().getCommandFlags(args).contains("-c");
		boolean isPriceForStack = false;
		String category = "";
		double price = 0;

		while (argumentIterator.hasNext()) {
			String next = argumentIterator.next();
			if (NumberUtils.isDouble(next)) {
				price = Double.parseDouble(next);
				argumentIterator.remove();
			} else if (!NumberUtils.isDouble(next) && !Arrays.asList("yes", "true", "Yes", "True", "no", "No", "false", "False").contains(next) && MarketsAPI.getInstance().isAlphaNumeric(next)) {
				category = next;
				argumentIterator.remove();
			} else if (Arrays.asList("yes", "true", "Yes", "True").contains(next)) {
				isPriceForStack = true;
			}
		}

		final String finalCategory = category;
		MarketCategory marketCategory = market.getCategories().stream().filter(cat -> cat.getName().equalsIgnoreCase(finalCategory)).findFirst().orElse(null);

		if (marketCategory == null) {
			Markets.getInstance().getLocale().getMessage("market_category_not_found").processPlaceholder("market_category_name", finalCategory).sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		if (price <= 0) {
			Markets.getInstance().getLocale().getMessage("price_is_zero_or_less").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		if (heldItem.getAmount() == 1) isPriceForStack = true;

		MarketItem marketItem = new MarketItem(marketCategory, heldItem, price, isPriceForStack);
		marketItem.setInfinite(!MarketsAPI.getInstance().getCommandFlags(args).isEmpty() && MarketsAPI.getInstance().getCommandFlags(args).contains("-o") && player.hasPermission("markets.addinfiniteitems"));

		MarketItemAddEvent marketItemAddEvent = new MarketItemAddEvent(market, marketItem);
		Bukkit.getPluginManager().callEvent(marketItemAddEvent);
		if (marketItemAddEvent.isCancelled()) return ReturnType.FAILURE;

		if (useCustomCurrency) {
			Markets.getInstance().getMarketPlayerManager().addPlayerToCustomCurrencyItem(player.getUniqueId(), market, marketCategory, marketItem);
			Markets.getInstance().getLocale().getMessage("click_currency_item").sendPrefixedMessage(player);

			final String prefix = Markets.getInstance().getLocale().getMessage("general.prefix").getMessage();
			final String content = Markets.getInstance().getLocale().getMessage("currency_select_option").getMessage();

			MarketsAPI.getInstance().sendClickableCommand(player, prefix + " " + content, "markets internal marketsCustomCurrencySelectionGUI " + finalCategory);
			return ReturnType.SUCCESS;
		}

		market.setUpdatedAt(System.currentTimeMillis());
		Markets.getInstance().getMarketManager().addItemToCategory(marketCategory, marketItem);
		Markets.getInstance().getLocale().getMessage("added_item_to_category").processPlaceholder("item_name", Common.getItemName(heldItem)).processPlaceholder("market_category_name", marketCategory.getName()).sendPrefixedMessage(player);
		PlayerUtils.takeActiveItem(player, CompatibleHand.MAIN_HAND, heldItem.getAmount());
		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		Player player = (Player) sender;
		if (args.length == 1) {
			Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);
			if (market != null && market.getCategories().size() > 0) {
				return market.getCategories().stream().map(MarketCategory::getName).collect(Collectors.toList());
			}
		}
		if (args.length == 2) return Arrays.asList("1", "2", "3", "4", "5");
		if (args.length == 3) return Arrays.asList("true", "false");
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.additem";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.add_item").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.add_item").getMessage();
	}
}
