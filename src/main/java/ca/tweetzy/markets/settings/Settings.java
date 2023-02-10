package ca.tweetzy.markets.settings;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.config.ConfigEntry;
import ca.tweetzy.flight.settings.FlightSettings;
import ca.tweetzy.markets.Markets;

public final class Settings extends FlightSettings {

	public static ConfigEntry PREFIX = create("prefix", "&8[&eMarkets&8]").withComment("The prefix for the plugin");
	public static ConfigEntry LANGUAGE = create("language", "en_us").withComment("The primary language of the plugin");
	public static ConfigEntry DEFAULT_MAX_ALLOWED_MARKET_ITEMS = create("settings.max allowed market items", 64).withComment("The maximum # of items a player can add to their market before special permissions.");
	public static ConfigEntry DEFAULT_MAX_ALLOWED_MARKET_CATEGORIES = create("settings.max allowed market categories", 20).withComment("The maximum # of categories a player can add to their market before special permissions.");
	public static ConfigEntry DEFAULT_MAX_ALLOWED_REQUESTS = create("settings.max allowed requests", 64).withComment("The maximum # of requests a player can make without further permission");
	public static ConfigEntry TAX_ENABLED = create("settings.tax.enabled", false).withComment("If true, will apply sales tax to the total when a user is buying an item");
	public static ConfigEntry TAX_AMOUNT = create("settings.tax.percentage", 13.0).withComment("The tax percentage. By default it's 13%");
	public static ConfigEntry CREATION_COST_ENABLED = create("settings.creation cost.enabled", true).withComment("If enabled, players will be charged to create their market");
	public static ConfigEntry CREATION_COST_COST = create("settings.creation cost.cost", 1000).withComment("How much should market's charge the player to create their market");
	public static ConfigEntry ALLOW_ANYONE_TO_CREATE_MARKET = create("settings.allow anyone to create market", true).withComment("If true, anyone can create a market. Otherwise they will need the permission: markets.createmarket");
	public static ConfigEntry CURRENCY_ALLOW_PICK = create("settings.currency.allow user to pick", true).withComment("If true, players will be able to select which currency they want to use.");
	public static ConfigEntry CURRENCY_DEFAULT_SELECTED = create("settings.currency.default selection", "Vault/Vault").withComment("The default currency selection, PluginName/CurrencyName -> Ex. Vault/Vault");
	public static ConfigEntry CURRENCY_ITEM_DEFAULT_SELECTED = create("settings.currency.default item selection", "DIAMOND").withComment("The default currency selection if using item only mode");
	public static ConfigEntry CURRENCY_VAULT_SYMBOL = create("settings.currency.vault symbol", "$").withComment("When using default/vault currency, what symbol should be used.");
	public static ConfigEntry CURRENCY_USE_ITEM_ONLY = create("settings.currency.use item only", false).withComment("If true, Markets will only allow the usage of another item for currency.");
	public static ConfigEntry TIME_BETWEEN_RATINGS = create("settings.time between ratings", 86400).withComment("How many seconds must a player wait before they can rate the same market?");

	/*
	========================= GUI STUFF =========================
	 */
	public static ConfigEntry GUI_SHARED_ITEMS_BACK_BUTTON = create("gui.shared buttons.back button.item", CompMaterial.DARK_OAK_DOOR.name());
	public static ConfigEntry GUI_SHARED_ITEMS_EXIT_BUTTON = create("gui.shared buttons.exit button.item", CompMaterial.BARRIER.name());
	public static ConfigEntry GUI_SHARED_ITEMS_PREVIOUS_BUTTON = create("gui.shared buttons.previous button.item", CompMaterial.ARROW.name());
	public static ConfigEntry GUI_SHARED_ITEMS_NEXT_BUTTON = create("gui.shared buttons.next button.item", CompMaterial.ARROW.name());


	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_ALL_MARKETS = create("gui.main view.items.global.item", "https://textures.minecraft.net/texture/fc1e73023352cbc77b896fe7ea242b43143e013bec5bf314d41e5f26548fb2d2");
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_PAYMENTS = create("gui.main view.items.payments.item", CompMaterial.GOLD_INGOT.name());
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_REQUESTS = create("gui.main view.items.requests.item", CompMaterial.PAPER.name());
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_BANK = create("gui.main view.items.bank.item", CompMaterial.ENDER_CHEST.name());
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_OFFERS = create("gui.main view.items.offers.item", CompMaterial.CREEPER_BANNER_PATTERN.name());


	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DPN_ITEM = create("gui.market overview.items.display name.item", CompMaterial.NAME_TAG.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DESC_ITEM = create("gui.market overview.items.description.item", CompMaterial.ENCHANTED_BOOK.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_SETTINGS_ITEM = create("gui.market overview.items.settings.item", CompMaterial.REPEATER.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_NEW_CAT_ITEM = create("gui.market overview.items.new category.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DELETE_ITEM = create("gui.market overview.items.unStore market.item", CompMaterial.LAVA_BUCKET.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_ITEM = create("gui.market category edit.items.display name.item", CompMaterial.NAME_TAG.name());


	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_ITEM = create("gui.market category edit.items.description.item", CompMaterial.ENCHANTED_BOOK.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_ITEM = create("gui.market category edit.items.settings.item", CompMaterial.REPEATER.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_ITEM = create("gui.market overview.items.new item.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_ITEM = create("gui.market category edit.items.unStore category.item", CompMaterial.LAVA_BUCKET.name());

	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_ITEMS_NEW_ITEM_ITEM = create("gui.category add item.items.new item.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_ITEMS_CURRENCY_ITEM = create("gui.category add item.items.currency.item", CompMaterial.GOLD_INGOT.name());
	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_ITEMS_OFFERS_ITEM = create("gui.category add item.items.offers.item", CompMaterial.FLOWER_BANNER_PATTERN.name());
	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_ITEMS_PRICE_ITEM = create("gui.category add item.items.price.item", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_CATEGORY_ADD_ITEM_ITEMS_PRICE_FOR_ALL_ITEM = create("gui.category add item.items.price for all.item", CompMaterial.RED_SHULKER_BOX.name());


	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_OPEN_ITEM = create("gui.market settings.items.open.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_CLOSE_ITEM = create("gui.market settings.items.closed.item", CompMaterial.RED_STAINED_GLASS_PANE.name());

	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_CLOSE_WHEN_OUT_OF_STOCK_ENABLED_ITEM = create("gui.market settings.items.close when out of stock.enabled.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_CLOSE_WHEN_OUT_OF_STOCK_DISABLED_ITEM = create("gui.market settings.items.close when out of stock.disabled.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_BANNED_USERS_ITEM = create("gui.market settings.items.banned users.item", CompMaterial.SHIELD.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_HOME_LAYOUT_ITEM = create("gui.market settings.items.home layout.item", CompMaterial.PINK_BED.name());
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_ITEM = create("gui.market settings.items.category layout.item.item", CompMaterial.CHEST.name());
	public static ConfigEntry GUI_MARKET_BANNED_USERS_ITEMS_NEW_BAN = create("gui.market banned users.items.new ban.item", CompMaterial.LIME_DYE.name());

	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_EXIT = create("gui.layout control picker.items.exit.item", CompMaterial.BARRIER.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_PREV_PAGE = create("gui.layout control picker.items.prev page.item", CompMaterial.ARROW.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_NEXT_PAGE = create("gui.layout control picker.items.next page.item", CompMaterial.ARROW.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_SEARCH = create("gui.layout control picker.items.search.item", CompMaterial.DARK_OAK_SIGN.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_REVIEW = create("gui.layout control picker.items.review.item", CompMaterial.NETHER_STAR.name());

	public static ConfigEntry GUI_LAYOUT_EDITOR_ITEMS_REVIEW = create("gui.layout editor.items.review.item", CompMaterial.NETHER_STAR.name());
	public static ConfigEntry GUI_LAYOUT_EDITOR_ITEMS_SEARCH = create("gui.layout editor.items.search.item", CompMaterial.DARK_OAK_SIGN.name());

	public static ConfigEntry GUI_MARKET_VIEW_ITEMS_SEARCH = create("gui.market view.items.search.item", CompMaterial.DARK_OAK_SIGN.name());
	public static ConfigEntry GUI_MARKET_VIEW_ITEMS_REVIEW = create("gui.market view.items.reviews.item", CompMaterial.NETHER_STAR.name());

	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_BUY = create("gui.purchase item.items.buy.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_INCREMENT = create("gui.purchase item.items.increment.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_DECREMENT = create("gui.purchase item.items.decrement.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN = create("gui.purchase item.items.price breakdown.item", CompMaterial.PAPER.name());

	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_CREATE_OFFER = create("gui.offer creation.items.create offer.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_BREAKDOWN = create("gui.offer creation.items.breakdown.item", CompMaterial.PAPER.name());
	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_AMOUNT = create("gui.offer creation.items.offered amount.item", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_CURRENCY = create("gui.offer creation.items.currency.item", CompMaterial.GOLD_INGOT.name());
	public static ConfigEntry GUI_BANK_ITEMS_ADD = create("gui.bank.items.add.item", CompMaterial.LIME_DYE.name());

	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_ACCEPTING_OFFERS_ITEM = create("gui.edit market item.items.accepting offers.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_REJECTING_OFFERS_ITEM = create("gui.edit market item.items.rejecting offers.item", CompMaterial.RED_STAINED_GLASS_PANE.name());

	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_IS_WHOLESALE_ITEM = create("gui.edit market item.items.wholesale enabled.item", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_NOT_WHOLESALE_ITEM = create("gui.edit market item.items.wholesale disabled.item", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_STOCK_ITEM = create("gui.edit market item.items.stock.item", CompMaterial.HOPPER.name());
	public static ConfigEntry GUI_EDIT_ITEM_ITEMS_CURRENCY_ITEM = create("gui.edit market item.items.currency.item", CompMaterial.GOLD_INGOT.name());

	public static ConfigEntry GUI_NEW_RATING_ITEMS_CREATE_ITEM = create("gui.new rating.items.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_NEW_RATING_ITEMS_STAR_ITEM = create("gui.new rating.star.item", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_NEW_RATING_ITEMS_MSG_ITEM = create("gui.new rating.message.item", CompMaterial.DARK_OAK_SIGN.name());

	public static ConfigEntry GUI_ALL_MARKETS_ITEMS_SEARCH_ITEM = create("gui.all markets.items.search.item", CompMaterial.DARK_OAK_SIGN.name());
	public static ConfigEntry GUI_REQUEST_ITEMS_TOGGLE_ITEM = create("gui.request.items.toggle.item", CompMaterial.LEVER.name());
	public static ConfigEntry GUI_REQUEST_ITEMS_CREATE_ITEM = create("gui.request.items.create.item", CompMaterial.LIME_DYE.name());

	public static ConfigEntry GUI_CREATE_REQUEST_ITEMS_CURRENCY_ITEM = create("gui.create request.items.currency.item", CompMaterial.GOLD_INGOT.name());
	public static ConfigEntry GUI_CREATE_REQUEST_ITEMS_AMOUNT_ITEM = create("gui.create request.items.amount.item", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_CREATE_REQUEST_ITEMS_PRICE_ITEM = create("gui.create request.items.price.item", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_CREATE_REQUEST_ITEMS_CREATE_ITEM = create("gui.create request.items.create.item", CompMaterial.LIME_DYE.name());


	public static void init() {
		Markets.getCoreConfig().init();
	}
}
