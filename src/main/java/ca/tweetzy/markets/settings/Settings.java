package ca.tweetzy.markets.settings;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.config.ConfigEntry;
import ca.tweetzy.flight.settings.FlightSettings;
import ca.tweetzy.markets.Markets;

public final class Settings extends FlightSettings {

	public static ConfigEntry PREFIX = create("prefix", "&8[&eMarkets&8]").withComment("The prefix for the plugin");
	public static ConfigEntry LANGUAGE = create("language", "en_us").withComment("The primary language of the plugin");

	/*
	========================= GUI STUFF =========================
	 */
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

	public static void init() {
		Markets.getCoreConfig().init();
	}
}
