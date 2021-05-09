package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.configuration.editor.PluginConfigGui;
import ca.tweetzy.markets.Markets;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 01 2021
 * Time Created: 1:28 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandSettings extends AbstractCommand {

    public CommandSettings() {
        super(CommandType.PLAYER_ONLY, "settings");
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;
        Markets.getInstance().getGuiManager().showGUI(player, new PluginConfigGui(Markets.getInstance(), Markets.getInstance().getLocale().getMessage("general.prefix").getMessage()));
        return ReturnType.SUCCESS;
    }

    @Override
    public String getPermissionNode() {
        return "markets.cmd.settings";
    }

    @Override
    public String getSyntax() {
        return Markets.getInstance().getLocale().getMessage("command_syntax.settings").getMessage();
    }

    @Override
    public String getDescription() {
        return Markets.getInstance().getLocale().getMessage("command_description.settings").getMessage();
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

}
