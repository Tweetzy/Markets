package ca.tweetzy.markets.api;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.utils.nms.NBTEditor;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:42 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MarketsAPI {

    private MarketsAPI() {
    }

    private static MarketsAPI instance;
    private final Pattern maxAllowedMarketPattern = Pattern.compile("markets\\.maxalloweditems\\.(\\d+)");
    private final Pattern alphaNumericPattern = Pattern.compile("^[a-zA-Z0-9]*$");

    public static MarketsAPI getInstance() {
        if (instance == null) {
            instance = new MarketsAPI();
        }
        return instance;
    }

    /**
     * Get the total amount of an item in the player's inventory
     *
     * @param player is the player being checked
     * @param stack  is the item you want to find
     * @return the total count of the item(s)
     */
    public int getItemCountInPlayerInventory(Player player, ItemStack stack) {
        int total = 0;
        if (stack.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || item.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) continue;
                if (NBTEditor.getTexture(item).equals(NBTEditor.getTexture(stack))) total += item.getAmount();
            }
        } else {
            for (ItemStack item : player.getInventory().getContents()) {
                if (item == null || !item.isSimilar(stack)) continue;
                total += item.getAmount();
            }
        }
        return total;
    }

    /**
     * Removes a set amount of a specific item from the player inventory
     *
     * @param player is the player you want to remove the item from
     * @param stack  is the item that you want to remove
     * @param amount is the amount of items you want to remove.
     */
    public void removeSpecificItemQuantityFromPlayer(Player player, ItemStack stack, int amount) {
        int i = amount;
        for (int j = 0; j < player.getInventory().getSize(); j++) {
            ItemStack item = player.getInventory().getItem(j);
            if (item == null) continue;
            if (stack.getType() == XMaterial.PLAYER_HEAD.parseMaterial() && item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
                if (!NBTEditor.getTexture(item).equals(NBTEditor.getTexture(stack))) continue;
            } else {
                if (!item.isSimilar(stack)) continue;

            }

            if (i >= item.getAmount()) {
                player.getInventory().clear(j);
                i -= item.getAmount();
            } else if (i > 0) {
                item.setAmount(item.getAmount() - i);
                i = 0;
            } else {
                break;
            }
        }
    }

    /**
     * Used to get command flags (ex. -h, -f, -t, etc)
     *
     * @param args is the arguments passed when running a command
     * @return any command flags if any
     */
    public List<String> getCommandFlags(String... args) {
        List<String> flags = new ArrayList<>();
        for (String arg : args) {
            if (arg.startsWith("-") && arg.length() >= 2) {
                flags.add(arg.substring(0, 2));
            }
        }
        return flags;
    }

    /**
     * Get the max allowed items based on the permission
     *
     * @param player is the player being checked
     * @return the max allowed items from the permission node.
     */
    public int maxAllowedMarketItems(Player player) {
        int maxAllowedItems = Settings.DEFAULT_MAX_ALLOWED_MARKET_ITEMS.getInt();
        int max = player.getEffectivePermissions().stream().map(i -> {
            Matcher matcher = maxAllowedMarketPattern.matcher(i.getPermission());
            if (matcher.matches()) {
                return Integer.parseInt(matcher.group(1));
            }
            return 0;
        }).max(Integer::compareTo).orElse(0);

        if (player.hasPermission("markets.maxalloweditems.*")) {
            maxAllowedItems = Integer.MAX_VALUE;
        }

        if (max > maxAllowedItems) {
            maxAllowedItems = max;
        }

        return maxAllowedItems;
    }

    /**
     * Check if a string is alphanumeric
     *
     * @param string is the phrase being checked
     * @return true if the provided string is alphanumeric
     */
    public boolean isAlphaNumeric(String string) {
        Matcher matcher = alphaNumericPattern.matcher(string);
        return matcher.matches();
    }
}
