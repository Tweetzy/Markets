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
	public static TranslationEntry TAKE_OUT_ITEM_FIRST = create("take out item", "&cPlease remove your item from the menu first!");
	public static TranslationEntry AT_MAX_CATEGORY_LIMIT = create("at maximum category limit", "&cYou aren't allowed to create more categories.");


	// inputs
	public static TranslationEntry PROMPT_NEW_CATEGORY_TITLE = create("prompts.new category.title", "<GRADIENT:65B1B4>&LNew Category</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_NEW_CATEGORY_SUBTITLE = create("prompts.new category.subtitle", "&fEnter new category id into chat");

	public static TranslationEntry PROMPT_ITEM_PRICE_TITLE = create("prompts.item price.title", "<GRADIENT:65B1B4>&LItem Price</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_ITEM_PRICE_SUBTITLE = create("prompts.item price.subtitle", "&fEnter item price into chat");

	public static TranslationEntry PROMPT_MARKET_NAME_TITLE = create("prompts.market name.title", "<GRADIENT:65B1B4>&LMarket Name</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_MARKET_NAME_SUBTITLE = create("prompts.market name.subtitle", "&fEnter new market name into chat");

	public static TranslationEntry PROMPT_MARKET_DESC_TITLE = create("prompts.market description.title", "<GRADIENT:65B1B4>&LMarket Description</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_MARKET_DESC_SUBTITLE = create("prompts.market description.subtitle", "&fEnter new description into chat");



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
			"&a&l%left_click% &7to use selector",
			"&b&l%right_click% &7while holding the item",
			"&7you wish to use as the category icon."
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

	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_PRICE_NAME = create("gui.category add item.items.price.name", "<GRADIENT:65B1B4>&LItem Price</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_PRICE_LORE = create("gui.category add item.items.price.lore",
			"&7The current price is &f: &a%market_item_price%"
	);

	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_CURRENCY_NAME = create("gui.category add item.items.currency.name", "<GRADIENT:65B1B4>&lSwitch Currency</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_CURRENCY_LORE = create("gui.category add item.items.currency.lore",
			"&7Used to adjust which currency you will",
			"&7be accepting for this particular item.",
			"",
			"&7Current&f: &e%market_item_currency%",
			"",
			"&a&l%left_click% &7to edit currency"
	);

	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_OFFERS_NAME = create("gui.category add item.items.offers.name", "<GRADIENT:65B1B4>&lToggle Offers</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_OFFERS_LORE = create("gui.category add item.items.offers.lore",
			"&7By default offers are enabled, if enabled",
			"&7players can make an offer on an item they want.",
			"",
			"&7Current&f: %enabled%",
			"",
			"&a&l%left_click% &7to toggle offers"
	);
	


	public static void init() {
		new Translations(Markets.getInstance()).setup();
	}
}
