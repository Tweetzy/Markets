package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 18 2021
 * Time Created: 11:53 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandPayUpKeep extends AbstractCommand {

    public CommandPayUpKeep() {
        super(CommandType.PLAYER_ONLY, "pay upkeep");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);
        if (market == null) {
            Markets.getInstance().getLocale().getMessage("market_required").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (!market.isUnpaid()) {
            Markets.getInstance().getLocale().getMessage("upkeep_already_paid").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        if (!EconomyManager.hasBalance(player, Settings.UPKEEP_FEE_FEE.getDouble())) {
            Markets.getInstance().getLocale().getMessage("not_enough_money").sendPrefixedMessage(player);
            return ReturnType.FAILURE;
        }

        EconomyManager.withdrawBalance(player, Settings.UPKEEP_FEE_FEE.getDouble());
        Markets.getInstance().getLocale().getMessage("money_remove").processPlaceholder("price", String.format("%,.2f", Settings.UPKEEP_FEE_FEE.getDouble())).sendPrefixedMessage(player);
        Markets.getInstance().getLocale().getMessage("upkeep_fee_paid").processPlaceholder("upkeep_fee", String.format("%,.2f", Settings.UPKEEP_FEE_FEE.getDouble())).sendPrefixedMessage(player);
        market.setUnpaid(false);

        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "markets.cmd.payupkeep";
    }

    @Override
    public String getSyntax() {
        return Markets.getInstance().getLocale().getMessage("command_syntax.pay_upkeep").getMessage();
    }

    @Override
    public String getDescription() {
        return Markets.getInstance().getLocale().getMessage("command_description.pay_upkeep").getMessage();
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
