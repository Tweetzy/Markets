package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 13 2021
 * Time Created: 3:17 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandForceSave extends AbstractCommand {

    public CommandForceSave() {
        super(CommandType.CONSOLE_OK, "force save");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Markets.newChain().async(() -> Markets.getInstance().saveData(true)).execute();
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "marekts.cmd.forcesave";
    }

    @Override
    public String getSyntax() {
        return Markets.getInstance().getLocale().getMessage("command_syntax.force_save").getMessage();
    }

    @Override
    public String getDescription() {
        return Markets.getInstance().getLocale().getMessage("command_description.force_save").getMessage();
    }
}
