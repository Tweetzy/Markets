package ca.tweetzy.markets.settings;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.config.ConfigEntry;
import ca.tweetzy.flight.settings.FlightSettings;
import ca.tweetzy.markets.Markets;

import java.util.List;

public final class Settings extends FlightSettings {

	public static ConfigEntry PREFIX = create("prefix", "&8[&eMarkets&8]").withComment("The prefix for the plugin");
	public static ConfigEntry LANGUAGE = create("language", "en_us").withComment("The primary language of the plugin");
	public static ConfigEntry DATETIME_FORMAT = create("date time format", "MMM dd, yyyy hh:mm:ss a").withComment("How should timestamps be formatted");
	public static ConfigEntry MAIN_COMMAND_REQUIRES_PERM = create("settings.main command requires permission", false).withComment("If true, players will need the permission: 'markets.command' to use /markets");
	public static ConfigEntry DEFAULT_MAX_ALLOWED_MARKET_ITEMS = create("settings.max allowed market items", 64).withComment("The maximum # of items a player can add to their market before special permissions.");
	public static ConfigEntry DEFAULT_MAX_ALLOWED_MARKET_CATEGORIES = create("settings.max allowed market categories", 20).withComment("The maximum # of categories a player can add to their market before special permissions.");
	public static ConfigEntry DEFAULT_MAX_ALLOWED_REQUESTS = create("settings.max allowed requests", 64).withComment("The maximum # of requests a player can make without further permission");
	public static ConfigEntry TAX_ENABLED = create("settings.tax.enabled", false).withComment("If true, will apply sales tax to the total when a user is buying an item");
	public static ConfigEntry TAX_AMOUNT = create("settings.tax.percentage", 13.0).withComment("The tax percentage. By default it's 13%");
	public static ConfigEntry CREATION_COST_ENABLED = create("settings.creation cost.enabled", true).withComment("If enabled, players will be charged to create their market");
	public static ConfigEntry CREATION_COST_COST = create("settings.creation cost.cost", 1000).withComment("How much should market's charge the player to create their market");
	public static ConfigEntry ALLOW_ANYONE_TO_CREATE_MARKET = create("settings.allow anyone to create market", true).withComment("If true, anyone can create a market. Otherwise they will need the permission: markets.createmarket");
	public static ConfigEntry ALLOW_BANK = create("settings.allow usage of bank", true).withComment("If true, players can use the bank. If not, offline payment for things requiring items as payment will not work!");
	public static ConfigEntry ALLOW_REQUESTS = create("settings.allow usage of request system", true).withComment("If true, players can use the request system");
	public static ConfigEntry CURRENCY_ALLOW_PICK = create("settings.currency.allow user to pick", true).withComment("If true, players will be able to select which currency they want to use.");
	public static ConfigEntry CURRENCY_DEFAULT_SELECTED = create("settings.currency.default selection", "Vault/Vault").withComment("The default currency selection, PluginName/CurrencyName -> Ex. Vault/Vault");
	public static ConfigEntry CURRENCY_ITEM_DEFAULT_SELECTED = create("settings.currency.default item selection", "DIAMOND").withComment("The default currency selection if using item only mode");
	public static ConfigEntry CURRENCY_VAULT_SYMBOL = create("settings.currency.vault symbol", "$").withComment("When using default/vault currency, what symbol should be used.");
	public static ConfigEntry CURRENCY_USE_ITEM_ONLY = create("settings.currency.use item only", false).withComment("If true, Markets will only allow the usage of another item for currency.");
	public static ConfigEntry CURRENCY_BLACKLISTED = create("settings.currency.black listed", List.of("UltraEconomy:Gems")).withComment("A list of owning plugins & the currency to be blacklisted. Ex. UltraEconomy:Gems");
	public static ConfigEntry TIME_BETWEEN_RATINGS = create("settings.time between ratings", 86400).withComment("How many seconds must a player wait before they can rate the same market?");
	public static ConfigEntry OPEN_CATEGORY_SETTINGS_AFTER_ITEM_ADD = create("settings.open category after item add", false).withComment("If true, when adding an item using the command, it will open the category after");
	public static ConfigEntry AUTO_REMOVE_ITEM_WHEN_OUT_OF_STOCK = create("settings.remove market item when out of stock", false).withComment("If true, then when an item's stock count hits zero, it will be removed from the market.");
	public static ConfigEntry MIN_PURCHASES_BEFORE_REVIEW = create("settings.minimum purchases before review", 1).withComment("How many items must a player buy from a market before they can leave a review?");
	public static ConfigEntry ENABLE_SEARCH_IN_MARKETS = create("settings.enable search in markets", true).withComment("If true, the search button will be shown in the market content view/search");
	public static ConfigEntry ITEMS_ARE_WHOLESALE_BY_DEFAULT = create("settings.default new items to wholesale", false).withComment("If true, any new items added to a market will be set as a wholesale item.");
	public static ConfigEntry DISABLE_REVIEWS = create("settings.disable reviews", false).withComment("If true, ratings/reviews will be disabled");
	public static ConfigEntry DISABLE_OFFERS = create("settings.disable offers", false).withComment("If true, offers will be disabled");
	public static ConfigEntry DISABLE_WHOLESALE = create("settings.disable wholesale", false).withComment("If true, wholesale will be disabled");
	public static ConfigEntry DISABLE_LAYOUT_EDITING = create("settings.disable layout editing", false).withComment("If true, users will not be able to change the layout of their markets");

	/*
	========================= COMMAND ALIASES =========================
	 */
	public static ConfigEntry CMD_ALIAS_MAIN = create("command aliases.main", List.of("markets", "market")).withComment("Aliases for the main command, please ensure this doesn't conflict with other plugin commands.");
	public static ConfigEntry CMD_ALIAS_SUB_ADD = create("command aliases.subcommands.add", List.of("add")).withComment("Aliases for the add command");
	public static ConfigEntry CMD_ALIAS_SUB_ADMIN = create("command aliases.subcommands.admin", List.of("admin")).withComment("Aliases for the add command");
	public static ConfigEntry CMD_ALIAS_SUB_DELETE = create("command aliases.subcommands.delete", List.of("delete")).withComment("Aliases for the delete command");
	public static ConfigEntry CMD_ALIAS_SUB_BANK = create("command aliases.subcommands.bank", List.of("bank")).withComment("Aliases for the bank command");
	public static ConfigEntry CMD_ALIAS_SUB_OFFERS = create("command aliases.subcommands.offers", List.of("offers")).withComment("Aliases for the offers command");
	public static ConfigEntry CMD_ALIAS_SUB_SEARCH = create("command aliases.subcommands.search", List.of("search", "lookup")).withComment("Aliases for the search command");
	public static ConfigEntry CMD_ALIAS_SUB_TRANSACTIONS = create("command aliases.subcommands.transactions", List.of("transactions")).withComment("Aliases for the transactions command");
	public static ConfigEntry CMD_ALIAS_SUB_VIEW = create("command aliases.subcommands.view", List.of("view")).withComment("Aliases for the view command");
	public static ConfigEntry CMD_ALIAS_SUB_PAYMENTS = create("command aliases.subcommands.payments", List.of("payments")).withComment("Aliases for the payments command");


	/*
	========================= BLACKLISTED ITEMS =========================
	 */

	/*
	========================= CLICKS =========================
	 */
	public static ConfigEntry CLICK_LAYOUT_BG_APPLY = create("settings.clicks.layout change background", "RIGHT").withComment("https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html");


	/*
	========================= INTERNAL TIMINGS =========================
	 */
	public static ConfigEntry INTERNAL_ADD_ITEM_DELAY = create("settings.internal.add item delay", 3).withComment("In ticks, how long should markets wait before calling the method to create an item after the button is clicked.");


	/*
	========================= GUI STUFF =========================
	 */
	public static ConfigEntry GUI_SHARED_ITEMS_BACK_BUTTON = create("gui.shared buttons.back button.item", CompMaterial.DARK_OAK_DOOR.name());
	public static ConfigEntry GUI_SHARED_ITEMS_EXIT_BUTTON = create("gui.shared buttons.exit button.item", CompMaterial.BARRIER.name());
	public static ConfigEntry GUI_SHARED_ITEMS_PREVIOUS_BUTTON = create("gui.shared buttons.previous button.item", CompMaterial.ARROW.name());
	public static ConfigEntry GUI_SHARED_ITEMS_NEXT_BUTTON = create("gui.shared buttons.next button.item", CompMaterial.ARROW.name());

	public static ConfigEntry GUI_USER_PROFILE_BACKGROUND = create("gui.user profile.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_OFFLINE_PAYMENTS_BACKGROUND = create("gui.offline payments.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_OFFERS_BACKGROUND = create("gui.offers.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_RATINGS_BACKGROUND = create("gui.ratings.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_SEARCH_BACKGROUND = create("gui.search.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_CURRENCY_PICKER_BACKGROUND = create("gui.currency picker.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_PLAYER_PICKER_BACKGROUND = create("gui.player picker.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());


	public static ConfigEntry GUI_MAIN_VIEW_BACKGROUND = create("gui.main view.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MAIN_VIEW_ROWS = create("gui.main view.rows", 6);
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_ALL_MARKETS = create("gui.main view.items.global.item", "https://textures.minecraft.net/texture/fc1e73023352cbc77b896fe7ea242b43143e013bec5bf314d41e5f26548fb2d2");
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_ALL_MARKETS_SLOT = create("gui.main view.items.global.slot", 13, "Set to -1 to disable icon");
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_SLOT = create("gui.main view.items.your market.slot", 20, "Set to -1 to disable icon");
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_PAYMENTS = create("gui.main view.items.payments.item", CompMaterial.GOLD_INGOT.name());
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_PAYMENTS_SLOT = create("gui.main view.items.payments.slot", 37, "Set to -1 to disable icon");
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_REQUESTS = create("gui.main view.items.requests.item", CompMaterial.PAPER.name());
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_REQUESTS_SLOT = create("gui.main view.items.requests.slot", 24, "Set to -1 to disable icon");
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_BANK = create("gui.main view.items.bank.item", CompMaterial.ENDER_CHEST.name());
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_BANK_SLOT = create("gui.main view.items.bank.slot", 40, "Set to -1 to disable icon");
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_OFFERS = create("gui.main view.items.offers.item", CompMaterial.CREEPER_BANNER_PATTERN.name());
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_OFFERS_SLOT = create("gui.main view.items.offers.slot", 43, "Set to -1 to disable icon");


	public static ConfigEntry GUI_MARKET_OVERVIEW_BACKGROUND = create("gui.market overview.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DPN_ITEM = create("gui.market overview.items.display name.item", CompMaterial.NAME_TAG.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DESC_ITEM = create("gui.market overview.items.description.item", CompMaterial.ENCHANTED_BOOK.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_SETTINGS_ITEM = create("gui.market overview.items.settings.item", CompMaterial.REPEATER.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_NEW_CAT_ITEM = create("gui.market overview.items.new category.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DELETE_ITEM = create("gui.market overview.items.unStore market.item", CompMaterial.LAVA_BUCKET.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_REVIEWS_ITEM = create("gui.market overview.items.reviews.item", CompMaterial.NETHER_STAR.name());

	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_BACKGROUND = create("gui.market category edit.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_ITEM = create("gui.market category edit.items.display name.item", CompMaterial.NAME_TAG.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_ITEM = create("gui.market category edit.items.description.item", CompMaterial.ENCHANTED_BOOK.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_ITEM = create("gui.market category edit.items.settings.item", CompMaterial.REPEATER.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_ITEM = create("gui.market overview.items.new item.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_ITEM = create("gui.market category edit.items.unStore category.item", CompMaterial.LAVA_BUCKET.name());

	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_BACKGROUND = create("gui.category add item.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_ITEMS_NEW_ITEM_ITEM = create("gui.category add item.items.new item.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_ITEMS_CURRENCY_ITEM = create("gui.category add item.items.currency.item", CompMaterial.GOLD_INGOT.name());
	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_ITEMS_OFFERS_ITEM = create("gui.category add item.items.offers.item", CompMaterial.FLOWER_BANNER_PATTERN.name());
	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_ITEMS_PRICE_ITEM = create("gui.category add item.items.price.item", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_ITEMS_PRICE_FOR_ALL_ITEM = create("gui.category add item.items.price for all.item", CompMaterial.RED_SHULKER_BOX.name());


	public static ConfigEntry GUI_MARKET_SETTINGS_BACKGROUND = create("gui.market settings.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_OPEN_ITEM = create("gui.market settings.items.open.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_CLOSE_ITEM = create("gui.market settings.items.closed.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_CLOSE_WHEN_OUT_OF_STOCK_ENABLED_ITEM = create("gui.market settings.items.close when out of stock.enabled.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_CLOSE_WHEN_OUT_OF_STOCK_DISABLED_ITEM = create("gui.market settings.items.close when out of stock.disabled.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_BANNED_USERS_ITEM = create("gui.market settings.items.banned users.item", CompMaterial.SHIELD.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_HOME_LAYOUT_ITEM = create("gui.market settings.items.home layout.item", CompMaterial.PINK_BED.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_ITEM = create("gui.market settings.items.category layout.item.item", CompMaterial.CHEST.name());
	public static ConfigEntry GUI_MARKET_BANNED_USERS_BACKGROUND = create("gui.market banned users.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_BANNED_USERS_ITEMS_NEW_BAN = create("gui.market banned users.items.new ban.item", CompMaterial.LIME_DYE.name());

	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_BACKGROUND = create("gui.layout control picker.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_EXIT = create("gui.layout control picker.items.exit.item", CompMaterial.BARRIER.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_PREV_PAGE = create("gui.layout control picker.items.prev page.item", CompMaterial.ARROW.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_NEXT_PAGE = create("gui.layout control picker.items.next page.item", CompMaterial.ARROW.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_SEARCH = create("gui.layout control picker.items.search.item", CompMaterial.DARK_OAK_SIGN.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_REVIEW = create("gui.layout control picker.items.review.item", CompMaterial.NETHER_STAR.name());

	public static ConfigEntry GUI_LAYOUT_EDITOR_BACKGROUND = create("gui.layout editor.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_LAYOUT_EDITOR_ITEMS_REVIEW = create("gui.layout editor.items.review.item", CompMaterial.NETHER_STAR.name());
	public static ConfigEntry GUI_LAYOUT_EDITOR_ITEMS_SEARCH = create("gui.layout editor.items.search.item", CompMaterial.DARK_OAK_SIGN.name());

	public static ConfigEntry GUI_MARKET_VIEW_ITEMS_SEARCH = create("gui.market view.items.search.item", CompMaterial.DARK_OAK_SIGN.name());
	public static ConfigEntry GUI_MARKET_VIEW_ITEMS_REVIEW = create("gui.market view.items.reviews.item", CompMaterial.NETHER_STAR.name());

	public static ConfigEntry GUI_PURCHASE_ITEM_BACKGROUND = create("gui.purchase item.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_BUY = create("gui.purchase item.items.buy.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_INCREMENT = create("gui.purchase item.items.increment.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_DECREMENT = create("gui.purchase item.items.decrement.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN = create("gui.purchase item.items.price breakdown.item", CompMaterial.PAPER.name());

	public static ConfigEntry GUI_OFFER_CREATE_BACKGROUND = create("gui.offer creation.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_CREATE_OFFER = create("gui.offer creation.items.create offer.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_BREAKDOWN = create("gui.offer creation.items.breakdown.item", CompMaterial.PAPER.name());
	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_AMOUNT = create("gui.offer creation.items.offered amount.item", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_CURRENCY = create("gui.offer creation.items.currency.item", CompMaterial.GOLD_INGOT.name());
	public static ConfigEntry GUI_TRANSACTIONS_BACKGROUND = create("gui.transactions.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_BANK_BACKGROUND = create("gui.bank.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_BANK_ITEMS_ADD = create("gui.bank.items.add.item", CompMaterial.LIME_DYE.name());

	public static ConfigEntry GUI_EDIT_ITEM_BACKGROUND = create("gui.edit market item.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_ACCEPTING_OFFERS_ITEM = create("gui.edit market item.items.accepting offers.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_REJECTING_OFFERS_ITEM = create("gui.edit market item.items.rejecting offers.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_IS_WHOLESALE_ITEM = create("gui.edit market item.items.wholesale enabled.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_NOT_WHOLESALE_ITEM = create("gui.edit market item.items.wholesale disabled.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_STOCK_ITEM = create("gui.edit market item.items.stock.item", CompMaterial.HOPPER.name());
	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_CURRENCY_ITEM = create("gui.edit market item.items.currency.item", CompMaterial.GOLD_INGOT.name());

	public static ConfigEntry GUI_NEW_RATING_BACKGROUND = create("gui.new rating.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_NEW_RATING_ITEMS_CREATE_ITEM = create("gui.new rating.items.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_NEW_RATING_ITEMS_STAR_ITEM = create("gui.new rating.star.item", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_NEW_RATING_ITEMS_MSG_ITEM = create("gui.new rating.message.item", CompMaterial.DARK_OAK_SIGN.name());

	public static ConfigEntry GUI_ALL_MARKETS_BACKGROUND = create("gui.all markets.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_ALL_MARKETS_ITEMS_SEARCH_ITEM = create("gui.all markets.items.search.item", CompMaterial.DARK_OAK_SIGN.name());
	public static ConfigEntry GUI_ALL_MARKETS_ITEMS_FILTER_ITEM = create("gui.all markets.items.filter.item", CompMaterial.REPEATER.name());
	public static ConfigEntry GUI_REQUEST_BACKGROUND = create("gui.request.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_REQUEST_ITEMS_TOGGLE_ITEM = create("gui.request.items.toggle.item", CompMaterial.LEVER.name());
	public static ConfigEntry GUI_REQUEST_ITEMS_CREATE_ITEM = create("gui.request.items.create.item", CompMaterial.LIME_DYE.name());

	public static ConfigEntry GUI_CREATE_REQUEST_BACKGROUND = create("gui.create request.items.background", CompMaterial.BLACK_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_CREATE_REQUEST_ITEMS_CURRENCY_ITEM = create("gui.create request.items.currency.item", CompMaterial.GOLD_INGOT.name());
	public static ConfigEntry GUI_CREATE_REQUEST_ITEMS_AMOUNT_ITEM = create("gui.create request.items.amount.item", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_CREATE_REQUEST_ITEMS_PRICE_ITEM = create("gui.create request.items.price.item", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_CREATE_REQUEST_ITEMS_CREATE_ITEM = create("gui.create request.items.create.item", CompMaterial.LIME_DYE.name());


	public static void init() {
		Markets.getCoreConfig().init();
	}
}
