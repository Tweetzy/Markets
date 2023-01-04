package ca.tweetzy.markets.settings;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.config.ConfigEntry;
import ca.tweetzy.flight.settings.FlightSettings;
import ca.tweetzy.markets.Markets;

import java.util.List;

public final class Settings extends FlightSettings {

	public static ConfigEntry PREFIX = create("prefix", "&8[&eMarkets&8]").withComment("The prefix for the plugin");
	public static ConfigEntry LANGUAGE = create("language", "en_us").withComment("The primary language of the plugin");

	/*
	========================= GUI STUFF =========================
	 */

	public static ConfigEntry GUI_MAIN_VIEW_TITLE = create("gui.main view.title", "&eMarkets &f- &7Home").withComment("The tile of the menu");

	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_NAME = create("gui.main view.items.your market.name", "&e&lYour Market");
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_CREATE = create("gui.main view.items.your market.create", List.of(
			"&7You currently don't have a market",
			"",
			"&a&lClick &7to create one."
	));
	public static ConfigEntry GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_VIEW = create("gui.main view.items.your market.view", List.of(
			"&a&lClick &7to view market"
	));


	public static ConfigEntry GUI_MARKET_OVERVIEW_TITLE = create("gui.market overview.title", "&eMarkets &f- &7Your Market").withComment("The tile of the menu");
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DPN_ITEM = create("gui.market overview.items.display name.item", CompMaterial.NAME_TAG.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DPN_NAME = create("gui.market overview.items.display name.name", "<GRADIENT:65B1B4>&LMarket Name</GRADIENT:2B6F8A>");
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DPN_LORE = create("gui.market overview.items.display name.lore", List.of(
			"&7The display name of your market, this",
			"&7is what others will see in the search.",
			"",
			"&7Current&f: %market_display_name%",
			"",
			"&a&lClick &7to change display name"
	));

	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DESC_ITEM = create("gui.market overview.items.description.item", CompMaterial.ENCHANTED_BOOK.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DESC_NAME = create("gui.market overview.items.description.name", "<GRADIENT:65B1B4>&LMarket Description</GRADIENT:2B6F8A>");
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DESC_LORE = create("gui.market overview.items.description.lore", List.of(
			"&7A brief description of market, something",
			"&7to help catch someone's attention",
			"",
			"&7Current&f: ",
			"%market_description%",
			"",
			"&a&lClick &7to change description"
	));

	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_SETTINGS_ITEM = create("gui.market overview.items.settings.item", CompMaterial.REPEATER.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_SETTINGS_NAME = create("gui.market overview.items.settings.name", "<GRADIENT:65B1B4>&LMarket Settings</GRADIENT:2B6F8A>");
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_SETTINGS_LORE = create("gui.market overview.items.settings.lore", List.of(
			"&7This is used to configure market details.",
			"",
			"&a&lClick &7to adjust settings"
	));

	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_NEW_CAT_ITEM = create("gui.market overview.items.new category.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_NEW_CAT_NAME = create("gui.market overview.items.new category.name", "<GRADIENT:65B1B4>&LNew Category</GRADIENT:2B6F8A>");
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_NEW_CAT_LORE = create("gui.market overview.items.new category.lore", List.of(
			"&7Used to make a new category",
			"&a&lClick &7to create new category"
	));

	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DELETE_ITEM = create("gui.market overview.items.delete market.item", CompMaterial.LAVA_BUCKET.name());
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DELETE_NAME = create("gui.market overview.items.delete market.name", "&c&lDelete Market");
	public static ConfigEntry GUI_MARKET_OVERVIEW_ITEMS_DELETE_LORE = create("gui.market overview.items.delete market.lore", List.of(
			"&7This action &4&lCANNOT &7be undone!",
			"",
			"&a&lClick &7to delete market"
	));

	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_TITLE = create("gui.market category edit.title", "&eMarkets &f- &7Edit &f- &7%category_name%").withComment("The tile of the menu");

	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_ICON_NAME = create("gui.market category edit.items.icon.name", "<GRADIENT:65B1B4>&lCategory Icon</GRADIENT:2B6F8A>");
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_ICON_LORE = create("gui.market category edit.items.icon.lore", List.of(
			"&7The icon of your category, this",
			"&7is what is shown to other players",
			"",
			"&7Current&f: %category_icon%",
			"",
			"&a&lClick &7to change icon"
	));

	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_ITEM = create("gui.market category edit.items.display name.item", CompMaterial.NAME_TAG.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_NAME = create("gui.market category edit.items.display name.name", "<GRADIENT:65B1B4>&lCategory Name</GRADIENT:2B6F8A>");
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_LORE = create("gui.market category edit.items.display name.lore", List.of(
			"&7The display name of your category, this",
			"&7is what others will see in the search.",
			"",
			"&7Current&f: %category_display_name%",
			"",
			"&a&lClick &7to change display name"
	));

	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_ITEM = create("gui.market category edit.items.description.item", CompMaterial.ENCHANTED_BOOK.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_NAME = create("gui.market category edit.items.description.name", "<GRADIENT:65B1B4>&lCategory Description</GRADIENT:2B6F8A>");
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_LORE = create("gui.market category edit.items.description.lore", List.of(
			"&7A brief description of category, something",
			"&7to describe what the category contains.",
			"",
			"&7Current&f: ",
			"%category_description%",
			"",
			"&a&lClick &7to change description"
	));

	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_ITEM = create("gui.market category edit.items.settings.item", CompMaterial.REPEATER.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_NAME = create("gui.market category edit.items.settings.name", "<GRADIENT:65B1B4>&lCategory Settings</GRADIENT:2B6F8A>");
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_LORE = create("gui.market category edit.items.settings.lore", List.of(
			"&7This is used to configure market details.",
			"",
			"&a&lClick &7to adjust settings"
	));

	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_ITEM = create("gui.market overview.items.new item.item", CompMaterial.LIME_DYE.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_NAME = create("gui.market overview.items.new item.name", "<GRADIENT:65B1B4>&LNew Item</GRADIENT:2B6F8A>");
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_LORE = create("gui.market overview.items.new item.lore", List.of(
			"&7Used to add a new item",
			"&a&lClick &7to add item to category"
	));


	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_ITEM = create("gui.market category edit.items.delete category.item", CompMaterial.LAVA_BUCKET.name());
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_NAME = create("gui.market category edit.items.delete category.name", "&c&lDelete Category");
	public static ConfigEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_LORE = create("gui.market category edit.items.delete category.lore", List.of(
			"&7This action &4&lCANNOT &7be undone!",
			"",
			"&a&lClick &7to delete this category"
	));

	public static void init() {
		Markets.getCoreConfig().init();
	}
}
