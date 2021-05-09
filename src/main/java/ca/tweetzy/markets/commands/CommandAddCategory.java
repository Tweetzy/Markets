package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.events.MarketCategoryCreateEvent;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 03 2021
 * Time Created: 12:30 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandAddCategory extends AbstractCommand {

    public CommandAddCategory() {
        super(CommandType.PLAYER_ONLY, "add category");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 1) return ReturnType.SYNTAX_ERROR;
        Player player = (Player) sender;

        Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);
        if (market == null) {
            Markets.getInstance().getLocale().getMessage("market_required").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        String categoryName = args[0].toLowerCase();

        StringBuilder description = null;
        if (args.length > 1) {
            description = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                description.append(args[i]).append(" ");
            }
        }

        if (market.getCategories().stream().anyMatch(category -> category.getName().equalsIgnoreCase(categoryName))) {
            Markets.getInstance().getLocale().getMessage("category_already_created").processPlaceholder("market_category_name", categoryName).sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        MarketCategory marketCategory = description == null ? new MarketCategory(categoryName) : new MarketCategory(categoryName, description.toString().trim());

        MarketCategoryCreateEvent marketCategoryCreateEvent = new MarketCategoryCreateEvent(market, marketCategory);
        Bukkit.getPluginManager().callEvent(marketCategoryCreateEvent);
        if (marketCategoryCreateEvent.isCancelled()) return ReturnType.FAILURE;

        Markets.getInstance().getMarketManager().addCategoryToMarket(market, marketCategory);
        market.setUpdatedAt(System.currentTimeMillis());
        Markets.getInstance().getLocale().getMessage("created_category").processPlaceholder("market_category_name", categoryName).sendPrefixedMessage(player);
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "markets.cmd.addcategory";
    }

    @Override
    public String getSyntax() {
        return Markets.getInstance().getLocale().getMessage("command_syntax.add_category").getMessage();
    }

    @Override
    public String getDescription() {
        return Markets.getInstance().getLocale().getMessage("command_description.add_category").getMessage();
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
