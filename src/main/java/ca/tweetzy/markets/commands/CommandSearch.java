package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.Inflector;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.guis.items.GUIItemSearch;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.structures.Pair;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 27 2021
 * Time Created: 12:46 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandSearch extends AbstractCommand {

	public CommandSearch() {
		super(CommandType.PLAYER_ONLY, "search");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (args.length < 1) return ReturnType.SYNTAX_ERROR;
		Player player = (Player) sender;

		StringBuilder builder = new StringBuilder();
		for (String arg : args) {
			builder.append(arg).append(" ");
		}

		String phrase = builder.toString().trim();
		if (phrase.length() == 0) {
			Markets.getInstance().getLocale().getMessage("search_phrase_empty").sendPrefixedMessage(player);
			return ReturnType.FAILURE;
		}

		Markets.newChain().asyncFirst(() -> {
			List<Pair<Market, MarketItem>> items = new ArrayList<>();
			Markets.getInstance().getMarketManager().getMarkets().stream().filter(market -> market.isOpen() && !market.isUnpaid() && !market.getOwner().equals(player.getUniqueId())).collect(Collectors.toList()).forEach(market -> {
				market.getCategories().forEach(category -> category.getItems().forEach(item -> items.add(new Pair<>(market, item))));
			});

			return items.stream().filter(marketItem -> checkSearchCriteria(phrase, marketItem.getSecond().getItemStack())).collect(Collectors.toList());
		}).syncLast(data -> Markets.getInstance().getGuiManager().showGUI(player, new GUIItemSearch(data))).execute();

		return ReturnType.SUCCESS;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.search";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.search").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.search").getMessage();
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		return null;
	}

	private boolean checkSearchCriteria(String phrase, ItemStack stack) {

		return MarketsAPI.getInstance().match(phrase, MarketsAPI.getInstance().getItemName(stack)) ||
				MarketsAPI.getInstance().match(phrase, Inflector.getInstance().pluralize(stack.getType().name())) ||
				MarketsAPI.getInstance().match(phrase, Inflector.getInstance().singularize(stack.getType().name())) ||
				MarketsAPI.getInstance().match(phrase, MarketsAPI.getInstance().getItemLore(stack)) ||
				MarketsAPI.getInstance().match(phrase, MarketsAPI.getInstance().getItemEnchantments(stack));
	}
}
