package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.market.Market;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 08 2021
 * Time Created: 11:45 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandConfiscate extends AbstractCommand {

    public CommandConfiscate() {
        super(CommandType.PLAYER_ONLY, "confiscate");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 1) return ReturnType.SYNTAX_ERROR;
        Player player = (Player) sender;

        Market market = Markets.getInstance().getMarketManager().getMarketByPlayerName(args[0]);
        if (market == null) {
            Markets.getInstance().getLocale().getMessage("market_not_found").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        boolean deleteItems = args.length == 2 && Arrays.asList("yes", "true", "Yes", "True").contains(args[1]);

        if (!deleteItems) {
            List<ItemStack> items = new ArrayList<>();
            Markets.newChain().async(() -> market.getCategories().forEach(category -> category.getItems().forEach(item -> items.add(item.getItemStack())))).sync(() -> PlayerUtils.giveItem(player, items)).execute();
        }

        market.getCategories().clear();
        market.setUpdatedAt(System.currentTimeMillis());
        Markets.getInstance().getLocale().getMessage("confiscated_market").sendPrefixedMessage(player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1)
            return Markets.getInstance().getMarketManager().getMarkets().stream().map(Market::getOwnerName).collect(Collectors.toList());
        if (args.length == 2)
            return Arrays.asList("true", "yes", "false", "no");
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "markets.cmd.confiscate";
    }

    @Override
    public String getSyntax() {
        return Markets.getInstance().getLocale().getMessage("command_syntax.confiscate").getMessage();
    }

    @Override
    public String getDescription() {
        return Markets.getInstance().getLocale().getMessage("command_description.confiscate").getMessage();
    }
}
