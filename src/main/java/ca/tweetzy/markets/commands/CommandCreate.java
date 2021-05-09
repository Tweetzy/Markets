package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.events.MarketCreateEvent;
import ca.tweetzy.markets.market.Market;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 01 2021
 * Time Created: 4:08 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandCreate extends AbstractCommand {

    public CommandCreate() {
        super(CommandType.PLAYER_ONLY, "create");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);
        if (market != null) {
            Markets.getInstance().getLocale().getMessage("already_have_market").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        market = new Market(player.getUniqueId(), player.getName(), args.length == 1 ? args[0] : player.getName() + "'s Market");

        MarketCreateEvent marketCreateEvent = new MarketCreateEvent(player, market);
        Bukkit.getPluginManager().callEvent(marketCreateEvent);
        if (marketCreateEvent.isCancelled()) return ReturnType.FAILURE;

        // Create a new market for the player
        Markets.getInstance().getMarketManager().addMarket(market);
        Markets.getInstance().getLocale().getMessage("created_market").sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "markets.cmd.create";
    }

    @Override
    public String getSyntax() {
        return Markets.getInstance().getLocale().getMessage("command_syntax.create").getMessage();
    }

    @Override
    public String getDescription() {
        return Markets.getInstance().getLocale().getMessage("command_description.create").getMessage();
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

}
