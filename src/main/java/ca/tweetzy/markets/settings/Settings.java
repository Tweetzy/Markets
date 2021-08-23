package ca.tweetzy.markets.settings;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.core.configuration.ConfigSetting;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.markets.Markets;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 30 2021
 * Time Created: 3:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class Settings {

    private static final Config config = Markets.getInstance().getCoreConfig();

    public static final ConfigSetting LANG = new ConfigSetting(config, "lang", "en_US", "Default language file");
    public static final ConfigSetting ECONOMY_PLUGIN = new ConfigSetting(config, "economy provider", EconomyManager.getEconomy() == null ? "Vault" : EconomyManager.getEconomy().getName(),
            "Which economy should auction house use?",
            "You have the following supported economy plugins installed: \"" + EconomyManager.getManager().getPossiblePlugins().stream().collect(Collectors.joining("\", \"")) + "\"."
    );

    public static final ConfigSetting GIVE_ITEMS_ON_CATEGORY_DELETE = new ConfigSetting(config, "setting.give back items on category delete", true, "Should Markets give the player all their items from the category", "back when they click the delete button?");
    public static final ConfigSetting GIVE_ITEMS_ON_MARKET_DELETE = new ConfigSetting(config, "setting.give back items on market delete", true, "Should markets give the player all their items in all categories", "when they decide to delete their market?");
    public static final ConfigSetting LOG_TRANSACTIONS = new ConfigSetting(config, "setting.log transactions", true, "Should transactions be logged?");
    public static final ConfigSetting MAX_REQUEST_AMOUNT = new ConfigSetting(config, "setting.max amount per request", 256, "How many items can a player request per request creation?");
    public static final ConfigSetting AUTO_CREATE_MARKET = new ConfigSetting(config, "setting.auto create market", true, "If a player tries to access a the market menu without owning a market", "should markets automatically create a market for them?");
    public static final ConfigSetting AUTO_SAVE_ENABLED = new ConfigSetting(config, "setting.auto save.enabled", true, "Should markets automatically save market data?");
    public static final ConfigSetting AUTO_SAVE_DELAY = new ConfigSetting(config, "setting.auto save.delay", 900, "How often (in seconds) should markets save data?");
    public static final ConfigSetting LIMIT_MARKET_ITEMS_BY_PERMISSION = new ConfigSetting(config, "setting.limit total market items by permission", true, "If enabled, markets will limit the total amount of items a player can have in their market based on their permission");
    public static final ConfigSetting LIMIT_REQUESTS_BY_PERMISSION = new ConfigSetting(config, "setting.limit total requests by permission", true, "If enabled, markets will limit the total amount of requests a player can have active based on their permission");
    public static final ConfigSetting DEFAULT_MAX_ALLOWED_MARKET_ITEMS = new ConfigSetting(config, "setting.default max allowed market items", 64, "If limit total market items by permission is enabled, this is will be the default max allowed amount of items (individual stacks)");
    public static final ConfigSetting DEFAULT_MAX_ALLOWED_REQUESTS_ITEMS = new ConfigSetting(config, "setting.default max allowed requests", 12, "If limit total requests by permission is enabled, this is will be default allowed request amount");
    public static final ConfigSetting ALLOW_OWNER_TO_BUY_OWN_ITEMS = new ConfigSetting(config, "setting.allow owner to buy own items", false, "For whatever reason, if you want the market owner to buy their own items, enable this.");
    public static final ConfigSetting ALLOW_OWNER_FULFILL_REQUEST = new ConfigSetting(config, "setting.allow owner to fulfill requests", false, "For whatever reason, if you want the requester to fulfill their own request, enable this.");
    public static final ConfigSetting ALLOW_RATING_CHANGE = new ConfigSetting(config, "setting.ratings.allow change", true, "Should players be allowed to change their rating on a market?");
    public static final ConfigSetting RATING_CHANGE_DELAY = new ConfigSetting(config, "setting.ratings.change delay", 86400, "How many seconds must a player wait before they can change their rating?");
    public static final ConfigSetting RATING_MAX_MESSAGE_LENGTH = new ConfigSetting(config, "setting.ratings.max message length", 41, "What is the max length a rating message can be?");
    public static final ConfigSetting MARKET_CHECK_DELAY = new ConfigSetting(config, "setting.run market check delay", 10, "How often (in seconds) should markets check for things like the upkeep charge?");
    public static final ConfigSetting BROADCAST_REQUEST_CREATION = new ConfigSetting(config, "setting.broadcast request creation", true, "Should clickable request messages be sent to everyone upon a new request creation?");
    public static final ConfigSetting FEATURE_COST = new ConfigSetting(config, "setting.feature.cost", 20000, "How much should it cost to feature a market?");
    public static final ConfigSetting FEATURE_TIME = new ConfigSetting(config, "setting.feature.time", 60 * 60, "How long should a market feature last (in seconds)");

    public static final ConfigSetting USE_CREATION_FEE = new ConfigSetting(config, "setting.creation fee.enabled", true, "Should markets charge players a fee to create their market?");
    public static final ConfigSetting CREATION_FEE_AMOUNT = new ConfigSetting(config, "setting.creation fee.amount", 1000, "How much should the market creation fee be?");

    public static final ConfigSetting UPKEEP_FEE_USE = new ConfigSetting(config, "setting.upkeep fee.use", false, "Should markets charge players after x amount of time to keep their market open?");
    public static final ConfigSetting UPKEEP_FEE_FEE = new ConfigSetting(config, "setting.upkeep fee.fee", 2500, "How much should the upkeep fee cost?");
    public static final ConfigSetting UPKEEP_FEE_FEE_PER_ITEM = new ConfigSetting(config, "setting.upkeep fee.additional item fee", 5, "Adds to the total upkeep fee (ex. if they have 20 items, $ 20 * 5 would be added to the base fee)");
    public static final ConfigSetting UPKEEP_FEE_CHARGE_EVERY = new ConfigSetting(config, "setting.upkeep fee.delay", 604800, "How many seconds should pass before upkeep fees are collected?");

    public static final ConfigSetting TAX_ENABLED = new ConfigSetting(config, "setting.tax.enabled", false, "If enabled, when a sale is made, percentage of the sale will be removed / added");
    public static final ConfigSetting TAX_BUYER_INSTEAD_OF_SELLER = new ConfigSetting(config, "setting.tax.tax buyer instead of seller", true, "If enabled, markets will charge the buyer tax instead of the seller");
    public static final ConfigSetting TAX_AMOUNT = new ConfigSetting(config, "setting.tax.amount", 13, "This is a percentage, ex. 13 -> 13% tax");

    public static final ConfigSetting INCREMENT_NUMBER_ONE = new ConfigSetting(config, "setting.increment one", 1, "How much should the item qty be increased by (btn 1)");
    public static final ConfigSetting INCREMENT_NUMBER_TWO = new ConfigSetting(config, "setting.increment two", 5, "How much should the item qty be increased by (btn 2)");
    public static final ConfigSetting DECREMENT_NUMBER_ONE = new ConfigSetting(config, "setting.decrement one", 1, "How much should the item qty be decreased by (btn 1)");
    public static final ConfigSetting DECREMENT_NUMBER_TWO = new ConfigSetting(config, "setting.decrement two", 5, "How much should the item qty be decreased by (btn 2)");

    /*  ===============================
     *         DATABASE OPTIONS
     *  ===============================*/
    public static final ConfigSetting DATABASE_USE = new ConfigSetting(config, "database.use database", false, "Should the plugin use a database to store shop data?");
    public static final ConfigSetting DATABASE_HOST = new ConfigSetting(config, "database.host", "kiranhart.com", "What is the connection url/host");
    public static final ConfigSetting DATABASE_PORT = new ConfigSetting(config, "database.port", 3306, "What is the port to database (default is 3306)");
    public static final ConfigSetting DATABASE_NAME = new ConfigSetting(config, "database.name", "test_user", "What is the name of the database?");
    public static final ConfigSetting DATABASE_USERNAME = new ConfigSetting(config, "database.username", "test_database", "What is the name of the user connecting?");
    public static final ConfigSetting DATABASE_PASSWORD = new ConfigSetting(config, "database.password", "Password123.", "What is the password to the user connecting?");
    public static final ConfigSetting DATABASE_USE_SSL = new ConfigSetting(config, "database.use ssl", true, "Should the database connection use ssl?");

    /*
    =========== GLOBAL BUTTONS FOR GUIS ===========
    */
    public static final ConfigSetting GUI_BACK_BTN_ITEM = new ConfigSetting(config, "guis.global items.back button.item", "ARROW", "Settings for the back button");
    public static final ConfigSetting GUI_BACK_BTN_NAME = new ConfigSetting(config, "guis.global items.back button.name", "&e<< Back");
    public static final ConfigSetting GUI_BACK_BTN_LORE = new ConfigSetting(config, "guis.global items.back button.lore", Arrays.asList("&7Click the button to go", "&7back to the previous page."));

    public static final ConfigSetting GUI_CLOSE_BTN_ITEM = new ConfigSetting(config, "guis.global items.close button.item", "BARRIER", "Settings for the close button");
    public static final ConfigSetting GUI_CLOSE_BTN_NAME = new ConfigSetting(config, "guis.global items.close button.name", "&cClose");
    public static final ConfigSetting GUI_CLOSE_BTN_LORE = new ConfigSetting(config, "guis.global items.close button.lore", Collections.singletonList("&7Click to close this menu."));

    public static final ConfigSetting GUI_NEXT_BTN_ITEM = new ConfigSetting(config, "guis.global items.next button.item", "ARROW", "Settings for the next button");
    public static final ConfigSetting GUI_NEXT_BTN_NAME = new ConfigSetting(config, "guis.global items.next button.name", "&eNext >>");
    public static final ConfigSetting GUI_NEXT_BTN_LORE = new ConfigSetting(config, "guis.global items.next button.lore", Arrays.asList("&7Click the button to go", "&7to the next page."));

    public static final ConfigSetting GUI_MAIN_TITLE = new ConfigSetting(config, "guis.main.title", "&eMarkets");
    public static final ConfigSetting GUI_MAIN_USE_BORDER = new ConfigSetting(config, "guis.main.use border", true);
    public static final ConfigSetting GUI_MAIN_FILL_SLOTS = new ConfigSetting(config, "guis.main.fill slots", true);
    public static final ConfigSetting GUI_MAIN_GLOW_BORDER = new ConfigSetting(config, "guis.main.glow border", true);
    public static final ConfigSetting GUI_MAIN_FILL_ITEM = new ConfigSetting(config, "guis.main.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_MAIN_BORDER_ITEM = new ConfigSetting(config, "guis.main.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());

    public static final ConfigSetting GUI_MAIN_ITEMS_ALL_MARKETS_ITEM_USE_CUSTOM_HEAD = new ConfigSetting(config, "guis.main.items.all markets.item.use custom head", true);
    public static final ConfigSetting GUI_MAIN_ITEMS_ALL_MARKETS_ITEM_CUSTOM_HEAD_LINK = new ConfigSetting(config, "guis.main.items.all markets.item.custom head link", "1de9a8e5e6303cec956e37321ca0f7f7f9738211b8f570c3e76c929df5da18");
    public static final ConfigSetting GUI_MAIN_ITEMS_ALL_MARKETS_ITEM = new ConfigSetting(config, "guis.main.items.all markets.item.item", XMaterial.NETHER_STAR.name());
    public static final ConfigSetting GUI_MAIN_ITEMS_ALL_MARKETS_NAME = new ConfigSetting(config, "guis.main.items.all markets.name", "&eView all player markets");
    public static final ConfigSetting GUI_MAIN_ITEMS_ALL_MARKETS_LORE = new ConfigSetting(config, "guis.main.items.all markets.lore", Arrays.asList(
            "&7Click to view the markets of",
            "&7player's who have one."
    ));

    public static final ConfigSetting GUI_MAIN_ITEMS_YOUR_MARKET_NAME = new ConfigSetting(config, "guis.main.items.your market.name", "&eView your market");
    public static final ConfigSetting GUI_MAIN_ITEMS_YOUR_MARKET_LORE = new ConfigSetting(config, "guis.main.items.your market.lore", Arrays.asList(
            "&7Click to edit or view information",
            "&7about your market if you have one."
    ));

    public static final ConfigSetting GUI_MAIN_ITEMS_TRANSACTIONS_ITEM = new ConfigSetting(config, "guis.main.items.transactions.item", XMaterial.PAPER.name());
    public static final ConfigSetting GUI_MAIN_ITEMS_TRANSACTIONS_NAME = new ConfigSetting(config, "guis.main.items.transactions.name", "&eTransaction History");
    public static final ConfigSetting GUI_MAIN_ITEMS_TRANSACTIONS_LORE = new ConfigSetting(config, "guis.main.items.transactions.lore", Arrays.asList(
            "&7Click to view the transaction history",
            "&7for items you bought or sold."
    ));

    public static final ConfigSetting GUI_MAIN_ITEMS_REQUESTS_ITEM = new ConfigSetting(config, "guis.main.items.requests.item", XMaterial.CHEST_MINECART.name());
    public static final ConfigSetting GUI_MAIN_ITEMS_REQUESTS_NAME = new ConfigSetting(config, "guis.main.items.requests.name", "&eYour Requests");
    public static final ConfigSetting GUI_MAIN_ITEMS_REQUESTS_LORE = new ConfigSetting(config, "guis.main.items.requests.lore", Arrays.asList(
            "&7Click to view any item requests",
            "&7that you have made."
    ));

    public static final ConfigSetting GUI_MAIN_ITEMS_OPEN_REQUESTS_ITEM = new ConfigSetting(config, "guis.main.items.open requests.item", XMaterial.CHEST.name());
    public static final ConfigSetting GUI_MAIN_ITEMS_OPEN_REQUESTS_NAME = new ConfigSetting(config, "guis.main.items.open requests.name", "&eOpen Requests");
    public static final ConfigSetting GUI_MAIN_ITEMS_OPEN_REQUESTS_LORE = new ConfigSetting(config, "guis.main.items.open requests.lore", Arrays.asList(
            "&7Click to view any open requests",
            "&7that player's have made."
    ));

    /*
    ==================================
            MARKET EDIT GUI
    ==================================
     */

    public static final ConfigSetting GUI_MARKET_EDIT_TITLE = new ConfigSetting(config, "guis.market edit.title", "&eEditing your market");
    public static final ConfigSetting GUI_MARKET_EDIT_GLOW_BORDER = new ConfigSetting(config, "guis.market edit.glow border", true);
    public static final ConfigSetting GUI_MARKET_EDIT_FILL_ITEM = new ConfigSetting(config, "guis.market edit.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_MARKET_EDIT_BORDER_ITEM = new ConfigSetting(config, "guis.market edit.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());

    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_NAME_ITEM = new ConfigSetting(config, "guis.market edit.items.edit name.item", XMaterial.PAPER.name());
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_NAME_NAME = new ConfigSetting(config, "guis.market edit.items.edit name.name", "&eDisplay Name");
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_NAME_LORE = new ConfigSetting(config, "guis.market edit.items.edit name.lore", Arrays.asList(
            "&7Current name&F: %market_name%",
            "&7Click to change your market name."
    ));

    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_OPEN_ENABLE_ITEM = new ConfigSetting(config, "guis.market edit.items.open enabled.item", XMaterial.LIME_DYE.name());
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_OPEN_ENABLE_NAME = new ConfigSetting(config, "guis.market edit.items.open enabled.name", "&aMarket Open");
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_OPEN_ENABLE_LORE = new ConfigSetting(config, "guis.market edit.items.open enabled.lore", Collections.singletonList("&7Click to &cclose &7market"));

    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_OPEN_DISABLE_ITEM = new ConfigSetting(config, "guis.market edit.items.open disabled.item", XMaterial.RED_DYE.name());
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_OPEN_DISABLE_NAME = new ConfigSetting(config, "guis.market edit.items.open disabled.name", "&cMarket Closed");
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_OPEN_DISABLE_LORE = new ConfigSetting(config, "guis.market edit.items.open disabled.lore", Collections.singletonList("&7Click to &aOpen &7market"));

    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_ADD_CATEGORY_ITEM = new ConfigSetting(config, "guis.market edit.items.add category.item", XMaterial.NETHER_STAR.name());
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_ADD_CATEGORY_NAME = new ConfigSetting(config, "guis.market edit.items.add category.name", "&eNew Category");
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_ADD_CATEGORY_LORE = new ConfigSetting(config, "guis.market edit.items.add category.lore", Arrays.asList(
            "&7Click to add a new category",
            "&7to your market."
    ));

    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_FEATURE_ITEM = new ConfigSetting(config, "guis.market edit.items.feature market.item", XMaterial.FIREWORK_ROCKET.name());
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_FEATURE_NAME = new ConfigSetting(config, "guis.market edit.items.feature market.name", "&eFeature Market");
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_FEATURE_LORE = new ConfigSetting(config, "guis.market edit.items.feature market.lore", Arrays.asList(
            "&7Click to feature your market",
            "&7for &a$%feature_cost%"
    ));

    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_FEATURE_LORE_ALREADY = new ConfigSetting(config, "guis.market edit.items.feature market.lore already featured", Arrays.asList(
            "&7Your market is already being featured"
    ));

    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_ALL_ITEMS_ITEM = new ConfigSetting(config, "guis.market edit.items.all items.item", XMaterial.CHEST.name());
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_ALL_ITEMS_NAME = new ConfigSetting(config, "guis.market edit.items.all items.name", "&eView All Items");
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_ALL_ITEMS_LORE = new ConfigSetting(config, "guis.market edit.items.all items.lore", Collections.singletonList("&7Click to view all your market items."));

    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_DELETE_MARKET_ITEM = new ConfigSetting(config, "guis.market edit.items.delete market.item", XMaterial.FIRE_CHARGE.name());
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_DELETE_MARKET_NAME = new ConfigSetting(config, "guis.market edit.items.delete market.name", "&4Delete Market");
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_DELETE_MARKET_LORE = new ConfigSetting(config, "guis.market edit.items.delete market.lore", Arrays.asList(
            "&cRight-Click to delete your market,",
            "&cthis action &c&lCANNOT &cbe undone!"
    ));

    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_ADD_ITEM_ITEM = new ConfigSetting(config, "guis.market edit.items.add item.item", XMaterial.ENDER_EYE.name());
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_ADD_ITEM_NAME = new ConfigSetting(config, "guis.market edit.items.add item.name", "&eAdd Item");
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_ADD_ITEM_LORE = new ConfigSetting(config, "guis.market edit.items.add item.lore", Collections.singletonList(
            "&7Click to add an item to your market"
    ));

    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_CATEGORY_NAME = new ConfigSetting(config, "guis.market edit.items.category.name", "%category_display_name%");
    public static final ConfigSetting GUI_MARKET_EDIT_ITEMS_CATEGORY_LORE = new ConfigSetting(config, "guis.market edit.items.category.lore", Collections.singletonList("&7Click to edit this category"));

    /*
    ==================================
          CATEGORY SETTINGS GUI
    ==================================
     */
    public static final ConfigSetting GUI_CATEGORY_EDIT_TITLE = new ConfigSetting(config, "guis.category edit.title", "&eYour Market > %category_name%");
    public static final ConfigSetting GUI_CATEGORY_EDIT_GLOW_BORDER = new ConfigSetting(config, "guis.category edit.glow border", true);
    public static final ConfigSetting GUI_CATEGORY_EDIT_FILL_ITEM = new ConfigSetting(config, "guis.category edit.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_CATEGORY_EDIT_BORDER_ITEM = new ConfigSetting(config, "guis.category edit.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());

    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_NAME_ITEM = new ConfigSetting(config, "guis.category edit.items.edit name.item", XMaterial.PAPER.name());
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_NAME_NAME = new ConfigSetting(config, "guis.category edit.items.edit name.name", "&eDisplay Name");
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_NAME_LORE = new ConfigSetting(config, "guis.category edit.items.edit name.lore", Arrays.asList(
            "&7Current name&F: %category_display_name%",
            "&7Click to change this category's display name."
    ));

    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_DESCRIPTION_ITEM = new ConfigSetting(config, "guis.category edit.items.edit description.item", XMaterial.WRITABLE_BOOK.name());
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_DESCRIPTION_NAME = new ConfigSetting(config, "guis.category edit.items.edit description.name", "&eDescription");
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_DESCRIPTION_LORE = new ConfigSetting(config, "guis.category edit.items.edit description.lore", Arrays.asList(
            "&7Current Description&F:",
            "%category_description%",
            "&7Click to change this category's description."
    ));

    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_ICON_NAME = new ConfigSetting(config, "guis.category edit.items.edit icon.name", "&eIcon");
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_ICON_LORE = new ConfigSetting(config, "guis.category edit.items.edit icon.lore", Collections.singletonList(
            "&7Click to change this category's icon"
    ));

    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_ADD_ITEM_ITEM = new ConfigSetting(config, "guis.category edit.items.add item.item", XMaterial.ENDER_EYE.name());
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_ADD_ITEM_NAME = new ConfigSetting(config, "guis.category edit.items.add item.name", "&eAdd Item");
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_ADD_ITEM_LORE = new ConfigSetting(config, "guis.category edit.items.add item.lore", Collections.singletonList(
            "&7Click to add an item to this category"
    ));

    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_EMPTY_ITEM = new ConfigSetting(config, "guis.category edit.items.empty.item", XMaterial.HOPPER.name());
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_EMPTY_NAME = new ConfigSetting(config, "guis.category edit.items.empty.name", "&eEmpty Items");
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_EMPTY_LORE = new ConfigSetting(config, "guis.category edit.items.empty.lore", Collections.singletonList("&7Click to empty all items from this category"));

    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_DELETE_ITEM = new ConfigSetting(config, "guis.category edit.items.delete.item", XMaterial.FIRE_CHARGE.name());
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_DELETE_NAME = new ConfigSetting(config, "guis.category edit.items.delete.name", "&4Delete Category");
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_DELETE_LORE = new ConfigSetting(config, "guis.category edit.items.delete.lore", Arrays.asList(
            "&cRight-Click to delete this category,",
            "&cthis action &c&lCANNOT &cbe undone!"
    ));

    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_ITEM_NAME = new ConfigSetting(config, "guis.category edit.items.market item.name", "&e%item_name%");
    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_ITEM_LORE = new ConfigSetting(config, "guis.category edit.items.market item.lore", Arrays.asList(
            "&7Price&f: &a$%market_item_price%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "",
            "&7Left-Click to edit price",
            "&7Middle-Click to remove item from market",
            "&7Right-Click to toggle price per stack"
    ));

    public static final ConfigSetting GUI_CATEGORY_EDIT_ITEMS_ITEM_LORE_CUSTOM_CURRENCY = new ConfigSetting(config, "guis.category edit.items.market item.lore custom currency", Arrays.asList(
            "&7Price&f: &fx%market_item_price% %market_item_currency%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "",
            "&7Left-Click to edit price",
            "&7Middle-Click to remove item from market",
            "&7Right-Click to toggle price per stack",
            "&7Shift-Right Click to edit currency item."
    ));

    /*
   ==================================
        ALL ITEMS VIEW GUI
   ==================================
    */
    public static final ConfigSetting GUI_ALL_ITEMS_TITLE = new ConfigSetting(config, "guis.all items.title", "&e%market_name%&f > All Items");
    public static final ConfigSetting GUI_ALL_ITEMS_TITLE_EDIT = new ConfigSetting(config, "guis.all items.title edit", "&eYour Market > All Items");
    public static final ConfigSetting GUI_ALL_ITEMS_GLOW_BORDER = new ConfigSetting(config, "guis.all items.glow border", true);
    public static final ConfigSetting GUI_ALL_ITEMS_FILL_ITEM = new ConfigSetting(config, "guis.all items.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_ALL_ITEMS_BORDER_ITEM = new ConfigSetting(config, "guis.all items.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_ALL_ITEMS_ITEMS_ITEM_NAME = new ConfigSetting(config, "guis.all items.items.market item.name", "&e%item_name%");
    public static final ConfigSetting GUI_ALL_ITEMS_ITEMS_ITEM_EDIT_LORE = new ConfigSetting(config, "guis.all items.items.market item.edit lore", Arrays.asList(
            "&7Price&f: &a$%market_item_price%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "",
            "&7Left-Click to edit price",
            "&7Middle-Click to remove item from market",
            "&7Right-Click to toggle price per stack"
    ));

    public static final ConfigSetting GUI_ALL_ITEMS_ITEMS_ITEM_EDIT_LORE_CUSTOM_CURRENCY = new ConfigSetting(config, "guis.all items.items.market item.edit lore custom currency", Arrays.asList(
            "&7Price&f: &a$%market_item_price% %market_item_currency%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "",
            "&7Left-Click to edit price",
            "&7Middle-Click to remove item from market",
            "&7Right-Click to toggle price per stack",
            "&7Shift-Right Click to edit currency item."
    ));

    public static final ConfigSetting GUI_ALL_ITEMS_ITEMS_ITEM_LORE = new ConfigSetting(config, "guis.all items.items.market item.lore", Arrays.asList(
            "&7Price&f: &a$%market_item_price%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "",
            "&7Click to purchase this item."
    ));

    public static final ConfigSetting GUI_ALL_ITEMS_ITEMS_ITEM_LORE_CUSTOM_CURRENCY = new ConfigSetting(config, "guis.all items.items.market item.lore custom currency", Arrays.asList(
            "&7Price&f: &fx%market_item_price% %market_item_currency%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "",
            "&7Click to purchase this item.",
            "&e(!) This is item has a custom currency",
            "&eRight-Click to view the required currency."
    ));

    /*
   ==================================
           MARKET VIEW GUI
   ==================================
    */
    public static final ConfigSetting GUI_MARKET_VIEW_TITLE = new ConfigSetting(config, "guis.market view.title", "&e%market_name%");
    public static final ConfigSetting GUI_MARKET_VIEW_GLOW_BORDER = new ConfigSetting(config, "guis.market view.glow border", true);
    public static final ConfigSetting GUI_MARKET_VIEW_FILL_ITEM = new ConfigSetting(config, "guis.market view.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_MARKET_VIEW_BORDER_ITEM = new ConfigSetting(config, "guis.market view.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_MARKET_VIEW_ITEMS_ALL_ITEMS_ITEM = new ConfigSetting(config, "guis.market view.items.all items.item", XMaterial.CHEST.name());
    public static final ConfigSetting GUI_MARKET_VIEW_ITEMS_ALL_ITEMS_NAME = new ConfigSetting(config, "guis.market view.items.all items.name", "&eAll Items");
    public static final ConfigSetting GUI_MARKET_VIEW_ITEMS_ALL_ITEMS_LORE = new ConfigSetting(config, "guis.market view.items.all items.lore", Collections.singletonList("&7Click to view all items."));
    public static final ConfigSetting GUI_MARKET_VIEW_ITEMS_ALL_CATEGORY_NAME = new ConfigSetting(config, "guis.market view.items.category.name", "&e%category_display_name%");
    public static final ConfigSetting GUI_MARKET_VIEW_ITEMS_ALL_CATEGORY_LORE = new ConfigSetting(config, "guis.market view.items.category.lore", Arrays.asList(
            "%category_description%",
            "",
            "&7Click to view items this in category"
    ));

    public static final ConfigSetting GUI_MARKET_VIEW_ITEMS_RATINGS_USE_CUSTOM_SKULL = new ConfigSetting(config, "guis.market view.items.ratings.use custom skull", true, "If enabled, markets will use a custom textured skull (star)");
    public static final ConfigSetting GUI_MARKET_VIEW_ITEMS_RATINGS_CUSTOM_SKULL_LINK = new ConfigSetting(config, "guis.market view.items.ratings.custom skull link", "1c8e0cfebc7f9c7e16fbaaae025d1b1d19d5ee633666bcf25fa0b40d5bd21bcd");
    public static final ConfigSetting GUI_MARKET_VIEW_ITEMS_RATINGS_ITEM = new ConfigSetting(config, "guis.market view.items.ratings.item", XMaterial.PAPER.name());
    public static final ConfigSetting GUI_MARKET_VIEW_ITEMS_RATINGS_NAME = new ConfigSetting(config, "guis.market view.items.ratings.name", "&eMarket Ratings");
    public static final ConfigSetting GUI_MARKET_VIEW_ITEMS_RATINGS_LORE = new ConfigSetting(config, "guis.market view.items.ratings.lore", Arrays.asList(
            "&7Average Rating&f: &e%average_rating_stars%",
            "",
            "&7Left-Click to view all ratings",
            "&7Right-Click to leave a rating"
    ), "%average_rating_number% can be used instead of %average_rating_stars%");

    public static final ConfigSetting GUI_MARKET_LIST_TITLE = new ConfigSetting(config, "guis.market list.title", "&eOpen Markets");
    public static final ConfigSetting GUI_MARKET_LIST_GLOW_BORDER = new ConfigSetting(config, "guis.market list.glow border", true);
    public static final ConfigSetting GUI_MARKET_LIST_FILL_ITEM = new ConfigSetting(config, "guis.market list.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_MARKET_LIST_BORDER_ITEM = new ConfigSetting(config, "guis.market list.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_MARKET_LIST_MARKET_NAME = new ConfigSetting(config, "guis.market list.market name", "%market_name%");
    public static final ConfigSetting GUI_MARKET_LIST_MARKET_LORE = new ConfigSetting(config, "guis.market list.market lore", Arrays.asList(
            "%market_description%",
            "&7Owner&f: &e%market_owner%",
            "",
            "&7Click to view this market"
    ));

    public static final ConfigSetting GUI_MARKET_LIST_MARKET_LORE_FEATURED = new ConfigSetting(config, "guis.market list.market lore featured", Arrays.asList(
            "%market_description%",
            "&7Owner&f: &e%market_owner%",
            "",
            "&7Click to view this market",
            "",
            "&e&lFEATURED"
    ));

    /*
    ==================================
           MARKET VIEW GUI
    ==================================
    */
    public static final ConfigSetting GUI_MARKET_CATEGORY_TITLE = new ConfigSetting(config, "guis.market category.title", "&e%market_name% > %category_display_name%");
    public static final ConfigSetting GUI_MARKET_CATEGORY_GLOW_BORDER = new ConfigSetting(config, "guis.market category.glow border", true);
    public static final ConfigSetting GUI_MARKET_CATEGORY_FILL_ITEM = new ConfigSetting(config, "guis.market category.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_MARKET_CATEGORY_BORDER_ITEM = new ConfigSetting(config, "guis.market category.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_MARKET_CATEGORY_ITEM_NAME = new ConfigSetting(config, "guis.market category.item name", "%item_name%");
    public static final ConfigSetting GUI_MARKET_CATEGORY_ITEM_LORE = new ConfigSetting(config, "guis.market category.item lore", Arrays.asList(
            "&7Price&f: &a$%market_item_price%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "",
            "&7Click to purchase this item."
    ));

    public static final ConfigSetting GUI_MARKET_CATEGORY_ITEM_LORE_CUSTOM_CURRENCY = new ConfigSetting(config, "guis.market category.item lore custom currency", Arrays.asList(
            "&7Price&f: &fx%market_item_price% %market_item_currency%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "",
            "&7Click to purchase this item.",
            "&e(!) This is item has a custom currency",
            "&eRight-Click to view the required currency."
    ));

    /*
    ==================================
           ITEM PRE PURCHASE GUI
    ==================================
    */
    public static final ConfigSetting GUI_ITEM_PURCHASE_TITLE = new ConfigSetting(config, "guis.item purchase.title", "&ePurchasing Item");
    public static final ConfigSetting GUI_ITEM_PURCHASE_GLOW_BORDER = new ConfigSetting(config, "guis.item purchase.glow border", true);
    public static final ConfigSetting GUI_ITEM_PURCHASE_FILL_ITEM = new ConfigSetting(config, "guis.item purchase.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_ITEM_PURCHASE_BORDER_ITEM = new ConfigSetting(config, "guis.item purchase.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());

    public static final ConfigSetting GUI_ITEM_PURCHASE_ITEMS_INC_ITEM = new ConfigSetting(config, "guis.item purchase.items.increment.item", XMaterial.LIME_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_ITEM_PURCHASE_ITEMS_INC_NAME = new ConfigSetting(config, "guis.item purchase.items.increment.name", "&a+%increment_amount%");
    public static final ConfigSetting GUI_ITEM_PURCHASE_ITEMS_INC_LORE = new ConfigSetting(config, "guis.item purchase.items.increment.lore", Arrays.asList(
            "&7Click to increase the purchase",
            "&7quantity by &a+%increment_amount%"
    ));

    public static final ConfigSetting GUI_ITEM_PURCHASE_ITEMS_DECR_ITEM = new ConfigSetting(config, "guis.item purchase.items.decrement.item", XMaterial.RED_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_ITEM_PURCHASE_ITEMS_DECR_NAME = new ConfigSetting(config, "guis.item purchase.items.decrement.name", "&c-%decrement_amount%");
    public static final ConfigSetting GUI_ITEM_PURCHASE_ITEMS_DECR_LORE = new ConfigSetting(config, "guis.item purchase.items.decrement.lore", Arrays.asList(
            "&7Click to decrease the purchase",
            "&7quantity by &c-%decrement_amount%"
    ));

    public static final ConfigSetting GUI_ITEM_PURCHASE_ITEMS_PURCHASE_ITEM = new ConfigSetting(config, "guis.item purchase.items.purchase.item", XMaterial.SUNFLOWER.name());
    public static final ConfigSetting GUI_ITEM_PURCHASE_ITEMS_PURCHASE_NAME = new ConfigSetting(config, "guis.item purchase.items.purchase.name", "&ePurchase Information");
    public static final ConfigSetting GUI_ITEM_PURCHASE_ITEMS_PURCHASE_LORE = new ConfigSetting(config, "guis.item purchase.items.purchase.lore", Arrays.asList(
            "&7Quantity&f: &e%purchase_quantity%",
            "&7Stack Price&f: &a$%stack_price%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "&7Final Price&f: &a$%purchase_price%",
            "",
            "&7Click to make purchase."
    ));

    public static final ConfigSetting GUI_ITEM_PURCHASE_ITEMS_PURCHASE_LORE_CUSTOM_CURRENCY = new ConfigSetting(config, "guis.item purchase.items.purchase.lore custom currency", Arrays.asList(
            "&7Quantity&f: &e%purchase_quantity%",
            "&7Stack Price&f: x%stack_price% %market_item_currency%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "&7Final Price&f: x%purchase_price% %market_item_currency%",
            "",
            "&7Click to make purchase."
    ));

    /*
    ==================================
           TRANSACTIONS GUI
    ==================================
    */
    public static final ConfigSetting GUI_TRANSACTIONS_TITLE = new ConfigSetting(config, "guis.transactions.title", "&eTransaction History");
    public static final ConfigSetting GUI_TRANSACTIONS_GLOW_BORDER = new ConfigSetting(config, "guis.transactions.glow border", true);
    public static final ConfigSetting GUI_TRANSACTIONS_FILL_ITEM = new ConfigSetting(config, "guis.transactions.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_TRANSACTIONS_BORDER_ITEM = new ConfigSetting(config, "guis.transactions.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_TRANSACTIONS_TRANSACTION_ITEM = new ConfigSetting(config, "guis.transactions.transaction item", XMaterial.PAPER.name());
    public static final ConfigSetting GUI_TRANSACTIONS_TRANSACTION_NAME = new ConfigSetting(config, "guis.transactions.transaction name", "&eTransaction");
    public static final ConfigSetting GUI_TRANSACTIONS_TRANSACTION_LORE = new ConfigSetting(config, "guis.transactions.transaction lore", Arrays.asList(
            "&7ID: &e%transaction_id%",
            "&7Buyer: &e%transaction_buyer%",
            "&7Market: &e%transaction_market%",
            "&7Price: &e%transaction_price%",
            "&7Date: &e%transaction_date%"
    ));

    /*
    ==================================
            OPEN REQUEST GUI
    ==================================
     */

    public static final ConfigSetting GUI_OPEN_REQUEST_TITLE = new ConfigSetting(config, "guis.open request.title", "&eYour Requests");
    public static final ConfigSetting GUI_OPEN_REQUEST_TITLE_ALL = new ConfigSetting(config, "guis.open request.title all", "&eAll Open Requests");
    public static final ConfigSetting GUI_OPEN_REQUEST_GLOW_BORDER = new ConfigSetting(config, "guis.open request.glow border", true);
    public static final ConfigSetting GUI_OPEN_REQUEST_FILL_ITEM = new ConfigSetting(config, "guis.open request.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_OPEN_REQUEST_BORDER_ITEM = new ConfigSetting(config, "guis.open request.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());

    public static final ConfigSetting GUI_OPEN_REQUEST_ITEMS_EMPTY_ITEM = new ConfigSetting(config, "guis.open request.items.empty.item", XMaterial.HOPPER.name());
    public static final ConfigSetting GUI_OPEN_REQUEST_ITEMS_EMPTY_NAME = new ConfigSetting(config, "guis.open request.items.empty.name", "&eCancel Requests");
    public static final ConfigSetting GUI_OPEN_REQUEST_ITEMS_EMPTY_LORE = new ConfigSetting(config, "guis.open request.items.empty.lore", Collections.singletonList("&7Click to cancel all open requests"));

    public static final ConfigSetting GUI_OPEN_REQUEST_ITEMS_COLLECTION_ITEM = new ConfigSetting(config, "guis.open request.items.collection.item", XMaterial.CHEST.name());
    public static final ConfigSetting GUI_OPEN_REQUEST_ITEMS_COLLECTION_NAME = new ConfigSetting(config, "guis.open request.items.collection.name", "&eRequest Collection");
    public static final ConfigSetting GUI_OPEN_REQUEST_ITEMS_COLLECTION_LORE = new ConfigSetting(config, "guis.open request.items.collection.lore", Collections.singletonList("&7Click to view fulfilled requests"));

    public static final ConfigSetting GUI_OPEN_REQUEST_ITEMS_REQUEST_NAME = new ConfigSetting(config, "guis.open request.items.request.name", "%request_item_name%");
    public static final ConfigSetting GUI_OPEN_REQUEST_ITEMS_REQUEST_LORE = new ConfigSetting(config, "guis.open request.items.request.lore", Arrays.asList(
            "",
            "&7Item Requested&f: %request_item%",
            "&7Amount Requested&f: %request_amount%",
            "",
            "&7Middle-Click to cancel request"
    ));

    public static final ConfigSetting GUI_OPEN_REQUEST_ITEMS_REQUEST_LORE_ALL = new ConfigSetting(config, "guis.open request.items.request.lore all", Arrays.asList(
            "",
            "&7Requester&f: &e%request_requesting_player%",
            "&7Item Requested&f: %request_item%",
            "&7Amount Requested&f: %request_amount%",
            "",
            "&7Click to view individual fulfillments"
    ));

    /*
    ==================================
         REQUEST FULFILLMENT GUI
    ==================================
     */
    public static final ConfigSetting GUI_REQUEST_FULFILLMENT_TITLE = new ConfigSetting(config, "guis.request fulfillment.title", "&eRequest &f> &7Fulfillment Items");
    public static final ConfigSetting GUI_REQUEST_FULFILLMENT_GLOW_BORDER = new ConfigSetting(config, "guis.request fulfillment.glow border", true);
    public static final ConfigSetting GUI_REQUEST_FULFILLMENT_FILL_ITEM = new ConfigSetting(config, "guis.request fulfillment.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_REQUEST_FULFILLMENT_BORDER_ITEM = new ConfigSetting(config, "guis.request fulfillment.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());

    public static final ConfigSetting GUI_REQUEST_FULFILLMENT_REQUEST_ITEM_LORE = new ConfigSetting(config, "guis.request fulfillment.requested item lore", Arrays.asList(
            "",
            "&7Requester&f: &e%request_requesting_player%",
            "&7Quantity&f: &e%request_amount%",
            "&7Price&f: &a$%request_price%",
            "",
            "&7Click to fulfill this request"
    ));

    public static final ConfigSetting GUI_REQUEST_FULFILLMENT_REQUEST_ITEM_LORE_CUSTOM_CURRENCY = new ConfigSetting(config, "guis.request fulfillment.requested item lore custom currency", Arrays.asList(
            "",
            "&7Requester&f: &e%request_requesting_player%",
            "&7Quantity&f: &e%request_amount%",
            "&7Price&f: &a%request_price%",
            "&7Currency&f: %market_item_currency%",
            "",
            "&eRight-Click to view the required currency.",
            "&7Click to fulfill this request."
    ));

    /*
    ==================================
           BLOCKED ITEMS GUI
    ==================================
    */
    public static final ConfigSetting GUI_BLOCKED_ITEMS_TITLE = new ConfigSetting(config, "guis.blocked items.title", "&eBlocked Items");
    public static final ConfigSetting GUI_BLOCKED_ITEMS_GLOW_BORDER = new ConfigSetting(config, "guis.blocked items.glow border", true);
    public static final ConfigSetting GUI_BLOCKED_ITEMS_FILL_ITEM = new ConfigSetting(config, "guis.blocked items.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_BLOCKED_ITEMS_BORDER_ITEM = new ConfigSetting(config, "guis.blocked items.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_BLOCKED_ITEMS_ITEM_NAME = new ConfigSetting(config, "guis.blocked items.item name", "%item_name%");
    public static final ConfigSetting GUI_BLOCKED_ITEMS_ITEM_LORE = new ConfigSetting(config, "guis.blocked items.item lore", Arrays.asList(
            "",
            "&7Click to remove item from blacklist"
    ));

    /*
   ==================================
        CUSTOM CURRENCY VIEW GUI
   ==================================
    */
    public static final ConfigSetting GUI_CUSTOM_CURRENCY_VIEW_TITLE = new ConfigSetting(config, "guis.custom currency view.title", "&eRequired Currency");
    public static final ConfigSetting GUI_CUSTOM_CURRENCY_VIEW_FILL_ITEM = new ConfigSetting(config, "guis.custom currency view.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());

    /*
   ==================================
        PAYMENT COLLECTION GUI
   ==================================
    */
    public static final ConfigSetting GUI_PAYMENT_COLLECTION_TITLE = new ConfigSetting(config, "guis.payment collection.title", "&ePayment Collection");
    public static final ConfigSetting GUI_PAYMENT_COLLECTION_FILL_ITEM = new ConfigSetting(config, "guis.payment collection.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());

    /*
    ==================================
        PAYMENT COLLECTION GUI
    ==================================
    */
    public static final ConfigSetting GUI_CATEGORY_SELECT_TITLE = new ConfigSetting(config, "guis.category select.title", "&eSelect Category");
    public static final ConfigSetting GUI_CATEGORY_SELECT_FILL_ITEM = new ConfigSetting(config, "guis.category select.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_CATEGORY_SELECT_CATEGORY_NAME = new ConfigSetting(config, "guis.category select.category name", "%category_name%");
    public static final ConfigSetting GUI_CATEGORY_SELECT_CATEGORY_LORE = new ConfigSetting(config, "guis.category select.category lore", Arrays.asList(
            "%category_description%",
            "&7Click to select this category"
    ));

    /*
    ==================================
              ADD ITEM GUI
     ==================================
     */
    public static final ConfigSetting GUI_ADD_ITEM_TITLE = new ConfigSetting(config, "guis.add item.title", "&eAdding Market Item");
    public static final ConfigSetting GUI_ADD_ITEM_GLOW_BORDER = new ConfigSetting(config, "guis.add item.glow border", true);
    public static final ConfigSetting GUI_ADD_ITEM_FILL_ITEM = new ConfigSetting(config, "guis.add item.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_ADD_ITEM_BORDER_ITEM = new ConfigSetting(config, "guis.add item.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());

    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_PRICE_ITEM = new ConfigSetting(config, "guis.add item.items.price.item", XMaterial.SUNFLOWER.name());
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_PRICE_NAME = new ConfigSetting(config, "guis.add item.items.price.name", "&e&LItem Price");
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_PRICE_LORE = new ConfigSetting(config, "guis.add item.items.price.lore", Arrays.asList(
            "&7Current Price&f: &a$%market_item_price%",
            "&7Click to change the price"
    ));

    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_PRICE_LORE_CUSTOM_CURRENCY = new ConfigSetting(config, "guis.add item.items.price.lore custom currency", Arrays.asList(
            "&7Current Price&f: x%market_item_price% %market_item_currency%",
            "&7Click to change the price"
    ));

    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_ENABLED_ITEM = new ConfigSetting(config, "guis.add item.items.price for stack.enabled.item", XMaterial.LIME_DYE.name());
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_ENABLED_NAME = new ConfigSetting(config, "guis.add item.items.price for stack.enabled.name", "&a&LPrice if for stack");
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_ENABLED_LORE = new ConfigSetting(config, "guis.add item.items.price for stack.enabled.lore", Collections.singletonList("&7Click to set price for stack to &Cfalse"));

    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_DISABLED_ITEM = new ConfigSetting(config, "guis.add item.items.price for stack.disabled.item", XMaterial.RED_DYE.name());
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_DISABLED_NAME = new ConfigSetting(config, "guis.add item.items.price for stack.disabled.name", "&c&LPrice isn't for stack");
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_DISABLED_LORE = new ConfigSetting(config, "guis.add item.items.price for stack.disabled.lore", Collections.singletonList("&7Click to set price for stack to &Atrue"));

    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_SELECTED_CATEGORY_ITEM = new ConfigSetting(config, "guis.add item.items.selected category.item", XMaterial.NETHER_STAR.name());
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_SELECTED_CATEGORY_NAME = new ConfigSetting(config, "guis.add item.items.selected category.name", "&e&LSelected Category");
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_SELECTED_CATEGORY_LORE = new ConfigSetting(config, "guis.add item.items.selected category.lore", Arrays.asList(
            "&7Current category&f: &e%selected_market_category%",
            "&7Click to change the category"
    ));

    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_ENABLED_ITEM = new ConfigSetting(config, "guis.add item.items.use custom currency.enabled.item", XMaterial.LIME_DYE.name());
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_ENABLED_NAME = new ConfigSetting(config, "guis.add item.items.use custom currency.enabled.name", "&a&LCustom Currency Enabled");
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_ENABLED_LORE = new ConfigSetting(config, "guis.add item.items.use custom currency.enabled.lore", Collections.singletonList("&7Click to &cdisable &7custom currency"));

    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_DISABLED_ITEM = new ConfigSetting(config, "guis.add item.items.use custom currency.disabled.item", XMaterial.RED_DYE.name());
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_DISABLED_NAME = new ConfigSetting(config, "guis.add item.items.use custom currency.disabled.name", "&c&LCustom Currency Disabled");
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_DISABLED_LORE = new ConfigSetting(config, "guis.add item.items.use custom currency.disabled.lore", Collections.singletonList("&7Click to &aenable &7custom currency"));

    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_CANCEL_ADD_ITEM = new ConfigSetting(config, "guis.add item.items.cancel add.item", XMaterial.RED_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_CANCEL_ADD_NAME = new ConfigSetting(config, "guis.add item.items.cancel add.name", "&c&LCancel");
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_CANCEL_ADD_LORE = new ConfigSetting(config, "guis.add item.items.cancel add.lore", Collections.singletonList("&7Click to cancel adding a market item."));

    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_CONFIRM_ADD_ITEM = new ConfigSetting(config, "guis.add item.items.confirm add.item", XMaterial.LIME_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_CONFIRM_ADD_NAME = new ConfigSetting(config, "guis.add item.items.confirm add.name", "&a&lConfirm");
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_CONFIRM_ADD_LORE = new ConfigSetting(config, "guis.add item.items.confirm add.lore", Collections.singletonList("&7Click to add the item to the market."));

    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_CURRENCY_HOLDER_ITEM = new ConfigSetting(config, "guis.add item.items.currency holder.item", XMaterial.BARRIER.name());
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_CURRENCY_HOLDER_NAME = new ConfigSetting(config, "guis.add item.items.currency holder.name", "&c&lDisabled");
    public static final ConfigSetting GUI_ADD_ITEM_ITEMS_CURRENCY_HOLDER_LORE = new ConfigSetting(config, "guis.add item.items.currency holder.lore", Collections.singletonList("&7You must &aenable &7custom currency to place an item here."));

    /*
    ==================================
                 BANK GUI
    ==================================
    */
    public static final ConfigSetting GUI_BANK_TITLE = new ConfigSetting(config, "guis.bank.title", "&eYour Bank");
    public static final ConfigSetting GUI_BANK_GLOW_BORDER = new ConfigSetting(config, "guis.bank.glow border", true);
    public static final ConfigSetting GUI_BANK_FILL_ITEM = new ConfigSetting(config, "guis.bank.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_BANK_BORDER_ITEM = new ConfigSetting(config, "guis.bank.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_BANK_CURRENCY_NAME = new ConfigSetting(config, "guis.bank.item name", "%item_name%");
    public static final ConfigSetting GUI_BANK_CURRENCY_LORE = new ConfigSetting(config, "guis.bank.item lore", Arrays.asList(
            "&7Total Stored&f: &e%currency_amount%",
            "&7Left-Click to withdraw"
    ));

    /*
    ==================================
           RATINGS LIST GUI
    ==================================
    */
    public static final ConfigSetting GUI_RATINGS_LIST_TITLE = new ConfigSetting(config, "guis.ratings list.title", "&eMarket &f- &7Ratings");
    public static final ConfigSetting GUI_RATINGS_LIST_GLOW_BORDER = new ConfigSetting(config, "guis.ratings list.glow border", true);
    public static final ConfigSetting GUI_RATINGS_LIST_FILL_ITEM = new ConfigSetting(config, "guis.ratings list.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_RATINGS_LIST_BORDER_ITEM = new ConfigSetting(config, "guis.ratings list.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_RATINGS_LIST_RATING_ITEM = new ConfigSetting(config, "guis.ratings list.rating.item", XMaterial.PAPER.name());
    public static final ConfigSetting GUI_RATINGS_LIST_RATING_NAME = new ConfigSetting(config, "guis.ratings list.rating.name", "&eRating");
    public static final ConfigSetting GUI_RATINGS_LIST_RATING_LORE = new ConfigSetting(config, "guis.ratings list.rating.lore", Arrays.asList(
            "&7Rater&f: %rating_rater%",
            "&7Stars&f: %rating_stars%",
            "&7Message&f:",
            "&e%rating_message%"
    ));

    /*
    ==================================
            SEARCH ITEM GUI
    ==================================
    */
    public static final ConfigSetting GUI_SEARCH_TITLE = new ConfigSetting(config, "guis.search.title", "&eMarket &f- &7Search Results");
    public static final ConfigSetting GUI_SEARCH_GLOW_BORDER = new ConfigSetting(config, "guis.search.glow border", true);
    public static final ConfigSetting GUI_SEARCH_FILL_ITEM = new ConfigSetting(config, "guis.search.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_SEARCH_BORDER_ITEM = new ConfigSetting(config, "guis.search.border item", XMaterial.ORANGE_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_SEARCH_ITEM_LORE = new ConfigSetting(config, "guis.search.item lore", Arrays.asList(
            "&7Market&f: &e%market_name%",
            "&7Price&f: &a$%market_item_price%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "",
            "&7Click to purchase this item."
    ));

    public static final ConfigSetting GUI_SEARCH_ITEM_LORE_CUSTOM_CURRENCY = new ConfigSetting(config, "guis.search.item lore custom currency", Arrays.asList(
            "&7Market&f: &e%market_name%",
            "&7Price&f: &fx%market_item_price% %market_item_currency%",
            "&7Price is for stack&f: &e%market_item_price_for_stack%",
            "",
            "&7Click to purchase this item.",
            "&e(!) This is item has a custom currency",
            "&eRight-Click to view the required currency."
    ));

    /*
    ==================================
         RATINGS STAR SELECT GUI
    ==================================
    */
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_TITLE = new ConfigSetting(config, "guis.rating star select.title", "&eSelect Star Rating");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_FILL_ITEM = new ConfigSetting(config, "guis.rating star select.fill item", XMaterial.BLACK_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_USE_CUSTOM_SKULL = new ConfigSetting(config, "guis.rating star select.star.use custom skull", true);

    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_ON_1 = new ConfigSetting(config, "guis.rating star select.star.textures.selected.one", "http://textures.minecraft.net/texture/88991697469653c9af8352fdf18d0cc9c67763cfe66175c1556aed33246c7");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_ON_2 = new ConfigSetting(config, "guis.rating star select.star.textures.selected.two", "http://textures.minecraft.net/texture/5496c162d7c9e1bc8cf363f1bfa6f4b2ee5dec6226c228f52eb65d96a4635c");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_ON_3 = new ConfigSetting(config, "guis.rating star select.star.textures.selected.three", "http://textures.minecraft.net/texture/c4226f2eb64abc86b38b61d1497764cba03d178afc33b7b8023cf48b49311");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_ON_4 = new ConfigSetting(config, "guis.rating star select.star.textures.selected.four", "http://textures.minecraft.net/texture/f920ecce1c8cde5dbca5938c5384f714e55bee4cca866b7283b95d69eed3d2c");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_ON_5 = new ConfigSetting(config, "guis.rating star select.star.textures.selected.five", "http://textures.minecraft.net/texture/a2c142af59f29eb35ab29c6a45e33635dcfc2a956dbd4d2e5572b0d38891b354");

    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_OFF_1 = new ConfigSetting(config, "guis.rating star select.star.textures.not selected.one", "http://textures.minecraft.net/texture/ca516fbae16058f251aef9a68d3078549f48f6d5b683f19cf5a1745217d72cc");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_OFF_2 = new ConfigSetting(config, "guis.rating star select.star.textures.not selected.two", "http://textures.minecraft.net/texture/4698add39cf9e4ea92d42fadefdec3be8a7dafa11fb359de752e9f54aecedc9a");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_OFF_3 = new ConfigSetting(config, "guis.rating star select.star.textures.not selected.three", "http://textures.minecraft.net/texture/fd9e4cd5e1b9f3c8d6ca5a1bf45d86edd1d51e535dbf855fe9d2f5d4cffcd2");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_OFF_4 = new ConfigSetting(config, "guis.rating star select.star.textures.not selected.four", "http://textures.minecraft.net/texture/f2a3d53898141c58d5acbcfc87469a87d48c5c1fc82fb4e72f7015a3648058");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_OFF_5 = new ConfigSetting(config, "guis.rating star select.star.textures.not selected.five", "http://textures.minecraft.net/texture/d1fe36c4104247c87ebfd358ae6ca7809b61affd6245fa984069275d1cba763");

    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_ITEM = new ConfigSetting(config, "guis.rating star select.star.item", XMaterial.NETHER_STAR.name());
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_NAME = new ConfigSetting(config, "guis.rating star select.star.name", "&e%star_number% Stars");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_RATING_LORE = new ConfigSetting(config, "guis.rating star select.star.lore", Collections.singletonList("&7Click to rate %star_number% stars"));

    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_MESSAGE_ITEM = new ConfigSetting(config, "guis.rating star select.message.item", XMaterial.PAPER.name());
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_MESSAGE_NAME = new ConfigSetting(config, "guis.rating star select.message.name", "&eMessage");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_MESSAGE_LORE = new ConfigSetting(config, "guis.rating star select.message.lore", Arrays.asList(
            "&7Message&f: &e%rating_message%",
            "",
            "&7Click to change your message"
    ));

    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_CONFIRM_ITEM = new ConfigSetting(config, "guis.rating star select.confirm.item", XMaterial.LIME_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_CONFIRM_NAME = new ConfigSetting(config, "guis.rating star select.confirm.name", "&AConfirm Rating");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_CONFIRM_LORE = new ConfigSetting(config, "guis.rating star select.confirm.lore", Collections.singletonList("&7Click to post rating"));

    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_CANCEL_ITEM = new ConfigSetting(config, "guis.rating star select.cancel.item", XMaterial.RED_STAINED_GLASS_PANE.name());
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_CANCEL_NAME = new ConfigSetting(config, "guis.rating star select.cancel.name", "&cCancel Rating");
    public static final ConfigSetting GUI_RATINGS_STAR_SELECT_CANCEL_LORE = new ConfigSetting(config, "guis.rating star select.cancel.lore", Collections.singletonList("&7Click to cancel rating"));


    public static void setup() {
        config.load();
        config.setAutoremove(true).setAutosave(true);
        config.saveChanges();
    }
}
