package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.Markets;
import org.bukkit.command.CommandSender;

import java.util.Comparator;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 07 2021
 * Time Created: 11:12 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandHelp extends AbstractCommand {

    public CommandHelp() {
        super(CommandType.CONSOLE_OK, "help");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        sender.sendMessage(TextUtils.formatText("&6&lMarkets"));
        sender.sendMessage(TextUtils.formatText("&8- &e/markets &7- Open the Market Menu"));
        Markets.getInstance().getCommandManager().getAllCommands().stream().sorted(Comparator.comparing(AbstractCommand::getSyntax)).forEach(command -> {
            if (!command.getSyntax().equalsIgnoreCase("/markets")) {
                if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
                    sender.sendMessage(TextUtils.formatText(String.format("&8- &e/markets %s &7- %s", command.getSyntax(), command.getDescription())));
                }
            }

        });
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "markets.cmd.help";
    }

    @Override
    public String getSyntax() {
        return Markets.getInstance().getLocale().getMessage("command_syntax.help").getMessage();
    }

    @Override
    public String getDescription() {
        return Markets.getInstance().getLocale().getMessage("command_description.help").getMessage();
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }
}
