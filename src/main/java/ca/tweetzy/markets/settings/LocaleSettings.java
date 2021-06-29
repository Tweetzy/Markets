package ca.tweetzy.markets.settings;

import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.markets.Markets;

import java.util.HashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 17 2021
 * Time Created: 2:14 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class LocaleSettings {

    static final HashMap<String, String> languageNodes = new HashMap<>();

    static {
        languageNodes.put("general.prefix", "&8[&eMarkets&8]");
        languageNodes.put("general.reloaded", "&aMarkets has been reloaded (%value%ms)");

        languageNodes.put("nothing_in_hand", "&cYou aren't holding anything in your hand!");
        languageNodes.put("not_a_number", "&cPlease enter a valid number");
        languageNodes.put("price_is_zero_or_less", "&cPlease enter a price greater than 0");
        languageNodes.put("not_enough_money", "&cYou do not have enough money!");
        languageNodes.put("not_enough_money_create", "&cYou do not have enough money to create a market!");
        languageNodes.put("player_does_not_have_funds", "&4%player% &cdoes not have enough money to pay!");
        languageNodes.put("not_enough_items", "&cYou do not have enough items to fulfill the request.");
        languageNodes.put("money_remove", "&c&l- $%price%");
        languageNodes.put("money_add", "&a&l+ $%price%");
        languageNodes.put("added_blocked_item", "&aAdded &2%item_name%&a to blacklist");
        languageNodes.put("item_is_blocked", "&cYou cannot request/sell this item");
        languageNodes.put("cannot_buy_from_own_market", "&cYou cannot buy from your own market!");
        languageNodes.put("market_category_not_found", "&cCould not find a category in your market named&f: &4%market_category_name%");
        languageNodes.put("market_required", "&cYou need to create a market to do that!");
        languageNodes.put("market_category_required", "&cYou need at least 1 category in your market!");
        languageNodes.put("already_have_market", "&cYou cannot create a market since you already have one!");
        languageNodes.put("category_already_created", "&cYou already have a market category by the name&f: &4%market_category_name%");
        languageNodes.put("market_not_found", "&cCould not find a market owned by that player.");
        languageNodes.put("market_closed", "&cThat player's market is currently closed");
        languageNodes.put("removed_market", "&cYour market has been deleted!");
        languageNodes.put("removed_player_market", "&cSuccessfully removed &4%player%&c's market!");
        languageNodes.put("removed_category", "&cRemoved category &4%market_category_name% &cfrom your market!");
        languageNodes.put("confiscated_market", "&cSuccessfully confiscated market items.");
        languageNodes.put("created_market", "&aYou're now the owner of your very own market!");
        languageNodes.put("created_category", "&aYou created a new category for your market called&f: &2%market_category_name%");
        languageNodes.put("added_item_to_category", "&aAdded &2%item_name% &ato market category&f: &2%market_category_name%");
        languageNodes.put("at_max_items_limit", "&cYou are not allowed to add any more items to your market");

        languageNodes.put("created_request", "&aCreated a new request for x&2%request_amount% %request_item_name%");
        languageNodes.put("max_request_amount", "&cYou can only request &4%request_max_amount% &citems per request.");
        languageNodes.put("cannot_fulfill_own ", "&cYou cannot fulfill your own request");
        languageNodes.put("updated_market_name", "&aSuccessfully updated market name");
        languageNodes.put("updated_market_description", "&aSuccessfully updated market description");
        languageNodes.put("updated_market_to_close", "&aSuccessfully closed your market");
        languageNodes.put("updated_market_to_open", "&aSuccessfully opened your market");
        languageNodes.put("updated_category_name", "&aSuccessfully updated category display name.");
        languageNodes.put("updated_category_description", "&aSuccessfully updated category description.");
        languageNodes.put("updated_category_icon", "&aSuccessfully updated category icon.");

        languageNodes.put("misc.price is for stack.true", "True");
        languageNodes.put("misc.price is for stack.false", "False");
        languageNodes.put("misc.default category description", "Default Category Description");

        languageNodes.put("command_syntax.add_category", "add category <name> [description]");
        languageNodes.put("command_syntax.add_item", "add item <category> <price> [priceIsForStack]");
        languageNodes.put("command_syntax.create", "create [name]");
        languageNodes.put("command_syntax.help", "help");
        languageNodes.put("command_syntax.list", "list");
        languageNodes.put("command_syntax.markets", "/markets");
        languageNodes.put("command_syntax.reload", "reload");
        languageNodes.put("command_syntax.set", "set <name|desc|open> <value>");
        languageNodes.put("command_syntax.request", "request <amount> <price>");
        languageNodes.put("command_syntax.settings", "settings");
        languageNodes.put("command_syntax.view", "view <player>");
        languageNodes.put("command_syntax.remove", "remove");
        languageNodes.put("command_syntax.confiscate", "confiscate <player>");
        languageNodes.put("command_syntax.block_item", "block item [list]");

        languageNodes.put("command_description.add_category", "Create a new category inside your market.");
        languageNodes.put("command_description.add_item", "Add a new item to your market");
        languageNodes.put("command_description.create", "Create a market if you don't have one.");
        languageNodes.put("command_description.help", "Shows the help page");
        languageNodes.put("command_description.list", "View all available player markets.");
        languageNodes.put("command_description.markets", "Open the market menu");
        languageNodes.put("command_description.reload", "Reload lang and configuration files");
        languageNodes.put("command_description.set", "Update a specific market setting");
        languageNodes.put("command_description.request", "Open a new item request");
        languageNodes.put("command_description.settings", "Open the in game config editor");
        languageNodes.put("command_description.view", "Open a specific player's market");
        languageNodes.put("command_description.remove", "Delete your market");
        languageNodes.put("command_description.confiscate", "Confiscate an entire market's items");
        languageNodes.put("command_description.block_item", "Blacklist an item or view all blocked items.");

        languageNodes.put("prompt.enter_market_name", "&aEnter the new market name:");
        languageNodes.put("prompt.enter_category_name", "&aEnter name for new category:");
        languageNodes.put("prompt.enter_category_display_name", "&aEnter a new display name for the category:");
        languageNodes.put("prompt.enter_category_description", "&aEnter the new description for the category:");
        languageNodes.put("prompt.enter_market_item_price", "&aEnter the new price:");
     }

    public static void setup() {
        Config config = Markets.getInstance().getLocale().getConfig();

        languageNodes.keySet().forEach(key -> {
            config.setDefault(key, languageNodes.get(key));
        });

        config.setAutoremove(true).setAutosave(true);
        config.saveChanges();
    }
}
