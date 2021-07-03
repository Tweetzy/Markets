package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.transaction.Payment;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

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
        Markets.newChain().asyncFirst(() -> Markets.getInstance().getTransactionManger().getPayments(player.getUniqueId())).syncLast((data) -> {
            for (Payment payment : data) {
                PlayerUtils.giveItem(player, payment.getItem());
                Markets.getInstance().getTransactionManger().removePayment(payment);
            }
        }).execute();

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "markets.cmd.payments";
    }

    @Override
    public String getSyntax() {
        return "payments";
    }

    @Override
    public String getDescription() {
        return "Collect any custom currency payments";
    }
}
