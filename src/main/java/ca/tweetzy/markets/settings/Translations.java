package ca.tweetzy.markets.settings;

import ca.tweetzy.flight.settings.TranslationEntry;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.markets.Markets;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

public final class Translations extends TranslationManager {

	public Translations(@NonNull JavaPlugin plugin) {
		super(plugin);
	}

	public static TranslationEntry MARKET_NAME_TOO_LONG = create("market name too long", "&cMarket name too long, max is 72 characters.");
	public static TranslationEntry CATEGORY_NAME_TOO_LONG = create("category name too long", "&cCategory name too long, max is 32 characters.");
	public static TranslationEntry CATEGORY_NAME_USED = create("category name used", "&cYou already have a category named&F: &4%category_name%");


	public static TranslationEntry COLOURS_SUCCESS = create("colours.success", "#009933,#66FF99");


	// guis
	public static TranslationEntry GUI_MAIN_VIEW_TITLE = create("gui.main view.title", "&eMarkets &f- &7Home");

	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_NAME = create("gui.main view.items.your market.name", "&e&lYour Market");
	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_CREATE = create("gui.main view.items.your market.create",
			"&7You currently don't have a market",
			"",
			"&a&lClick &7to create one."
	);
	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_VIEW = create("gui.main view.items.your market.view",
			"&a&lClick &7to view market"
	);

	public static TranslationEntry GUI_MARKET_OVERVIEW_TITLE = create("gui.market overview.title", "&eMarkets &f- &7Your Market");
	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_DPN_NAME = create("gui.market overview.items.display name.name", "<GRADIENT:65B1B4>&LMarket Name</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_DPN_LORE = create("gui.market overview.items.display name.lore",
			"&7The display name of your market, this",
			"&7is what others will see in the search.",
			"",
			"&7Current&f: %market_display_name%",
			"",
			"&a&lClick &7to change display name"
	);

	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_DESC_NAME = create("gui.market overview.items.description.name", "<GRADIENT:65B1B4>&LMarket Description</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_DESC_LORE = create("gui.market overview.items.description.lore",
			"&7A brief description of market, something",
			"&7to help catch someone's attention",
			"",
			"&7Current&f: ",
			"%market_description%",
			"",
			"&a&lClick &7to change description"
	);

	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_SETTINGS_NAME = create("gui.market overview.items.settings.name", "<GRADIENT:65B1B4>&LMarket Settings</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_SETTINGS_LORE = create("gui.market overview.items.settings.lore",
			"&7This is used to configure market details.",
			"",
			"&a&lClick &7to adjust settings"
	);

	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_NEW_CAT_NAME = create("gui.market overview.items.new category.name", "<GRADIENT:65B1B4>&LNew Category</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_NEW_CAT_LORE = create("gui.market overview.items.new category.lore",
			"&7Used to make a new category",
			"&a&lClick &7to create new category"
	);

	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_DELETE_NAME = create("gui.market overview.items.unStore market.name", "&c&lDelete Market");
	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_DELETE_LORE = create("gui.market overview.items.unStore market.lore",
			"&7This action &4&lCANNOT &7be undone!",
			"",
			"&a&lClick &7to unStore market"
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_TITLE = create("gui.market category edit.title", "&eMarkets &f- &7Edit &f- &7%category_name%");
	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_ICON_NAME = create("gui.market category edit.items.icon.name", "<GRADIENT:65B1B4>&lCategory Icon</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_ICON_LORE = create("gui.market category edit.items.icon.lore",
			"&7The icon of your category, this",
			"&7is what is shown to other players",
			"",
			"&7Current&f: %category_icon%",
			"",
			"&a&lClick &7to change icon"
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_NAME = create("gui.market category edit.items.display name.name", "<GRADIENT:65B1B4>&lCategory Name</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_LORE = create("gui.market category edit.items.display name.lore",
			"&7The display name of your category, this",
			"&7is what others will see in the search.",
			"",
			"&7Current&f: %category_display_name%",
			"",
			"&a&lClick &7to change display name"
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_NAME = create("gui.market category edit.items.description.name", "<GRADIENT:65B1B4>&lCategory Description</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_LORE = create("gui.market category edit.items.description.lore",
			"&7A brief description of category, something",
			"&7to describe what the category contains.",
			"",
			"&7Current&f: ",
			"%category_description%",
			"",
			"&a&lClick &7to change description"
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_NAME = create("gui.market category edit.items.settings.name", "<GRADIENT:65B1B4>&lCategory Settings</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_LORE = create("gui.market category edit.items.settings.lore",
			"&7This is used to configure market details.",
			"",
			"&a&lClick &7to adjust settings"
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_NAME = create("gui.market overview.items.new item.name", "<GRADIENT:65B1B4>&LNew Item</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_LORE = create("gui.market overview.items.new item.lore",
			"&7Used to add a new item",
			"&a&lClick &7to add item to category"
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_NAME = create("gui.market category edit.items.unStore category.name", "&c&lDelete Category");
	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_LORE = create("gui.market category edit.items.unStore category.lore",
			"&7This action &4&lCANNOT &7be undone!",
			"",
			"&a&lClick &7to unStore this category"
	);

	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_TITLE = create("gui.category add item.title", "&eCategory &f- &7%category_name% &f- &7New Item");
	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_NEW_ITEM_NAME = create("gui.category add item.items.new item.name", "<GRADIENT:65B1B4>&lAdd Item</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_NEW_ITEM_LORE = create("gui.category add item.items.new item.lore",
			"&a&lClick &7to add item to category"
	);

	public static void init() {
		new Translations(Markets.getInstance()).setup();
	}
}
