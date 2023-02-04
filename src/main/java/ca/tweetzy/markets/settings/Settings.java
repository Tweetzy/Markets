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
	public static ConfigEntry PURCHASE_REQUIRES_CONFIRMATION = create("settings.confirmations.purchase", true).withComment("If true, markets will ask the player to confirm in a gui before the purchase is made.");
	public static ConfigEntry TAX_ENABLED = create("settings.tax.enabled", false).withComment("If true, will apply sales tax to the total when a user is buying an item");
	public static ConfigEntry TAX_AMOUNT = create("settings.tax.percentage", 13.0).withComment("The tax percentage. By default it's 13%");
	public static ConfigEntry CURRENCY_ALLOW_PICK = create("settings.currency.allow user to pick", true).withComment("If true, players will be able to select which currency they want to use.");
	public static ConfigEntry CURRENCY_DEFAULT_SELECTED = create("settings.currency.default selection", "Vault/Vault").withComment("The default currency selection, PluginName/CurrencyName -> Ex. Vault/Vault");
	public static ConfigEntry CURRENCY_VAULT_SYMBOL = create("settings.currency.vault symbol", "$").withComment("When using default/vault currency, what symbol should be used.");
	public static ConfigEntry CURRENCY_USE_ITEM_ONLY = create("settings.currency.use item only", false).withComment("If true, Markets will only allow the usage of another item for currency.");

	/*
	========================= GUI STUFF =========================
	 */
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_ALL_MARKETS = create("gui.main view.items.global", "https://textures.minecraft.net/texture/fc1e73023352cbc77b896fe7ea242b43143e013bec5bf314d41e5f26548fb2d2");
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_PAYMENTS = create("gui.main view.items.payments", CompMaterial.GOLD_INGOT.name());
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_BANK = create("gui.main view.items.bank", CompMaterial.ENDER_CHEST.name());
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_OFFERS = create("gui.main view.items.offers", CompMaterial.CREEPER_BANNER_PATTERN.name());


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
	public static ConfigEntry GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_ITEM = create("gui.market settings.items.category layout.item", CompMaterial.CHEST.name());
	public static ConfigEntry GUI_MARKET_BANNED_USERS_ITEMS_NEW_BAN = create("gui.market banned users.items.new ban", CompMaterial.LIME_DYE.name());

	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_EXIT = create("gui.layout control picker.items.exit", CompMaterial.BARRIER.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_PREV_PAGE = create("gui.layout control picker.items.prev page", CompMaterial.ARROW.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_NEXT_PAGE = create("gui.layout control picker.items.next page", CompMaterial.ARROW.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_SEARCH = create("gui.layout control picker.items.search", CompMaterial.DARK_OAK_SIGN.name());
	public static ConfigEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_REVIEW = create("gui.layout control picker.items.review", CompMaterial.NETHER_STAR.name());

	public static ConfigEntry GUI_LAYOUT_EDITOR_ITEMS_REVIEW = create("gui.layout editor.items.review", CompMaterial.NETHER_STAR.name());
	public static ConfigEntry GUI_LAYOUT_EDITOR_ITEMS_SEARCH = create("gui.layout editor.items.search", CompMaterial.DARK_OAK_SIGN.name());

	public static ConfigEntry GUI_MARKET_VIEW_ITEMS_SEARCH = create("gui.market view.items.search", CompMaterial.DARK_OAK_SIGN.name());
	public static ConfigEntry GUI_MARKET_VIEW_ITEMS_REVIEW = create("gui.market view.items.reviews", CompMaterial.NETHER_STAR.name());

	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_BUY = create("gui.purchase item.items.buy", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_INCREMENT = create("gui.purchase item.items.increment", CompMaterial.LIME_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_DECREMENT = create("gui.purchase item.items.decrement", CompMaterial.RED_STAINED_GLASS_PANE.name());
	public static ConfigEntry GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN = create("gui.purchase item.items.price breakdown", CompMaterial.PAPER.name());

	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_CREATE_OFFER = create("gui.offer creation.items.create offer", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_BREAKDOWN = create("gui.offer creation.items.breakdown", CompMaterial.PAPER.name());
	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_AMOUNT = create("gui.offer creation.items.offered amount", CompMaterial.SUNFLOWER.name());
	public static ConfigEntry GUI_OFFER_CREATE_ITEMS_CURRENCY = create("gui.offer creation.items.currency", CompMaterial.GOLD_INGOT.name());

	public static void init() {
		Markets.getCoreConfig().init();
	}
}
