package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.payment.GUIPaymentCollection;
import ca.tweetzy.markets.transaction.Payment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 03 2021
 * Time Created: 2:37 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandPayments extends AbstractCommand {

    public CommandPayments() {
        super(CommandType.PLAYER_ONLY, "payments");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            Markets.getInstance().getGuiManager().showGUI(player, new GUIPaymentCollection(player));
            return ReturnType.SUCCESS;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("collect")) {
            Markets.newChain().asyncFirst(() -> Markets.getInstance().getTransactionManger().getPayments(player.getUniqueId())).syncLast((data) -> {
                if (data.isEmpty()) {
                    Markets.getInstance().getLocale().getMessage("no_payments_to_collect").sendPrefixedMessage(player);
                    return;
                }

                for (Payment payment : data) {
                    PlayerUtils.giveItem(player, payment.getItem());
                    Markets.getInstance().getTransactionManger().removePayment(payment);
                }
            }).execute();
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) return Collections.singletonList("collect");
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "markets.cmd.payments";
    }

    @Override
    public String getSyntax() {
        return Markets.getInstance().getLocale().getMessage("command_syntax.payments").getMessage();
    }

    @Override
    public String getDescription() {
        return Markets.getInstance().getLocale().getMessage("command_description.payments").getMessage();
    }
}
