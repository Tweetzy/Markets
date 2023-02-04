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

	public static TranslationEntry MARKET_NAME_TOO_LONG = create("error.market name too long", "&cMarket name too long, max is 72 characters.");
	public static TranslationEntry CATEGORY_NAME_TOO_LONG = create("error.category name too long", "&cCategory name too long, max is 32 characters.");
	public static TranslationEntry CATEGORY_NAME_USED = create("error.category name used", "&cYou already have a category named&F: &4%category_name%");
	public static TranslationEntry TAKE_OUT_ITEM_FIRST = create("error.take out item", "&cPlease remove your item from the menu first!");
	public static TranslationEntry AT_MAX_CATEGORY_LIMIT = create("error.at maximum category limit", "&cYou aren't allowed to create more categories.");
	public static TranslationEntry PLACE_ITEM_TO_ADD = create("error.placed item to add", "&cPlease put the item you wish to add into the empty slot");
	public static TranslationEntry MUST_BE_HIGHER_THAN_ZERO = create("error.must be higher than zero", "&cPlease enter a number that is higher than 0");
	public static TranslationEntry NO_MARKET_FOUND = create("error.no market found", "&cCould not find any market for&F: &4%player_name%");
	public static TranslationEntry ONE_FILL_SLOT_REQUIRED = create("error.one fill slot required", "&cThis layout requires at least one fill slot!");
	public static TranslationEntry ITEM_OUT_OF_STOCK = create("error.item out of stock", "&cSorry that item is now out of stock");

	public static TranslationEntry MARKET_ITEM_BOUGHT_SELLER = create("info.market item bought.seller", "&fx&a%purchase_quantity% &f%item_name% &ewas bought by &b%buyer_name%");
	public static TranslationEntry MARKET_ITEM_BOUGHT_BUYER = create("info.market item bought.buyer", "&eBought &fx&a%purchase_quantity% &f%item_name% &efrom &b%seller_name%");
	public static TranslationEntry MARKET_ITEM_OUT_OF_STOCK = create("info.market item bought.out of stock", "&EAll &f%item_name% &estock has been sold!");

	public static TranslationEntry OFFER_RECEIVED = create("info.offer.received", "&b%sender_name% &ehas sent you an offer on an item!");
	public static TranslationEntry OFFER_SENT = create("info.offer.sent", "&EYou offer has successfully be sent to &b%owner_name% ");


	// inputs
	public static TranslationEntry PROMPT_NEW_CATEGORY_TITLE = create("prompts.new category.title", "<GRADIENT:65B1B4>&LNew Category</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_NEW_CATEGORY_SUBTITLE = create("prompts.new category.subtitle", "&fEnter new category id into chat");

	public static TranslationEntry PROMPT_ITEM_PRICE_TITLE = create("prompts.item price.title", "<GRADIENT:65B1B4>&LItem Price</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_ITEM_PRICE_SUBTITLE = create("prompts.item price.subtitle", "&fEnter item price into chat");

	public static TranslationEntry PROMPT_MARKET_NAME_TITLE = create("prompts.market name.title", "<GRADIENT:65B1B4>&LMarket Name</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_MARKET_NAME_SUBTITLE = create("prompts.market name.subtitle", "&fEnter new market name into chat");

	public static TranslationEntry PROMPT_MARKET_DESC_TITLE = create("prompts.market description.title", "<GRADIENT:65B1B4>&LMarket Description</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_MARKET_DESC_SUBTITLE = create("prompts.market description.subtitle", "&fEnter new description into chat");

	public static TranslationEntry PROMPT_OFFER_PRICE_TITLE = create("prompts.offer price.title", "<GRADIENT:65B1B4>&LOffer Price</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_OFFER_PRICE_SUBTITLE = create("prompts.offer price.subtitle", "&fEnter how much you want to offer");


	// guis
	public static TranslationEntry GUI_MAIN_VIEW_TITLE = create("gui.main view.title", "&eMarkets &f- &7Home");

	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_NAME = create("gui.main view.items.your market.name", "&e&lYour Market");
	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_CREATE = create("gui.main view.items.your market.lore",
			"&7You currently don't have a market",
			"",
			"&a&lClick &7to create one."
	);
	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_VIEW = create("gui.main view.items.your market.view",
			"&a&lClick &7to view market"
	);

	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_GLOBAL_NAME = create("gui.main view.items.global.name", "<GRADIENT:65B1B4>&LAll Markets</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_GLOBAL_LORE = create("gui.main view.items.global.lore",
			"&7Used to view all open markets",
			"&7that are owned by other players",
			"",
			"&e&l%left_click% &7to view open markets"
	);

	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_PAYMENTS_NAME = create("gui.main view.items.payments.name", "<GRADIENT:65B1B4>&LCollect Payments</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_PAYMENTS_LORE = create("gui.main view.items.payments.lore",
			"&7Used to collect any payments that were",
			"&7made to your market while you were offline.",
			"",
			"&e&l%left_click% &7to collect payments"
	);

	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_BANK_NAME = create("gui.main view.items.bank.name", "<GRADIENT:65B1B4>&LYour Bank</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_BANK_LORE = create("gui.main view.items.bank.lore",
			"&7Used to store any physical (item) currency",
			"&7that you want to use as payment for offers.",
			"",
			"&e&l%left_click% &7to view bank"
	);

	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_OFFERS_NAME = create("gui.main view.items.offers.name", "<GRADIENT:65B1B4>&LOffers</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_OFFERS_LORE = create("gui.main view.items.offers.lore",
			"&7Used to view any offers that",
			"&7other users have sent regarding your items.",
			"",
			"&e&l%left_click% &7to view offers"
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
			"&e&l%left_click% &7to use selector",
			"&b&l%right_click% &7while holding the item",
			"&7you wish to use as the category icon."
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_MARKET_ITEM_LORE = create("gui.market category edit.items.market item.lore",
			"&7----------------------------",
			"&7Price&f: &a%market_item_price%",
			"&7Currency&f: &e%market_item_currency%",
			"&7Stock&f: &e%market_item_stock%",
			"",
			"&e&l%left_click% &7to edit price",
			"&b&l%right_click% &7to edit settings",
			"&c&l%drop_button% &7to remove item",
			"&7----------------------------"
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
			"&e&l%left_click% &7to edit currency"
	);

	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_OFFERS_NAME = create("gui.category add item.items.offers.name", "<GRADIENT:65B1B4>&lToggle Offers</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_OFFERS_LORE = create("gui.category add item.items.offers.lore",
			"&7By default offers are enabled, if enabled",
			"&7players can make an offer on an item they want.",
			"",
			"&7Current&f: %enabled%",
			"",
			"&e&l%left_click% &7to toggle offers"
	);

	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_PRICE_FOR_ALL_NAME = create("gui.category add item.items.price for all.name", "<GRADIENT:65B1B4>&lToggle 'Wholesale'</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_PRICE_FOR_ALL_LORE = create("gui.category add item.items.price for all.lore",
			"&7By default the price will be for a single item",
			"&7from the entire stack/stock. If enabled, the price will",
			"&7be for the entire stack/stock that is available.",
			"",
			"&7Current&f: %enabled%",
			"",
			"&e&l%left_click% &7to toggle price mode"
	);

	public static TranslationEntry GUI_CATEGORY_ADD_ITEM_ITEMS_CUSTOM_CURRENCY_LORE = create("gui.category add item.items.custom currency.lore",
			"&7----------------------------",
			"&eThis item is the custom currency",
			"&7----------------------------"
	);

	public static TranslationEntry GUI_USER_PICKER_TITLE = create("gui.user picker.title", "&eMarkets &f- &7Pick a user");


	public static TranslationEntry GUI_CURRENCY_PICKER_TITLE = create("gui.currency picker.title", "&eMarkets &f- &7Pick a currency");
	public static TranslationEntry GUI_CURRENCY_PICKER_ITEMS_CUSTOM_CURRENCY_NAME = create("gui.currency picker.items.custom currency.name", "<GRADIENT:65B1B4>&lCustom Item</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CURRENCY_PICKER_ITEMS_CUSTOM_CURRENCY_LORE = create("gui.currency picker.items.custom currency.lore",
			"&7If you want to use use a specific item for",
			"&7the currency, you can set that here.",
			"",
			"&b&l%right_click% &7to open a material picker &eor",
			"&e&l%left_click% &7with the item you want to use",
			"&7as the currency onto this icon."
	);

	public static TranslationEntry GUI_MARKET_SETTINGS_TITLE = create("gui.market settings.title", "&eMarkets &f- &7Settings");
	public static TranslationEntry GUI_MARKET_SETTINGS_ITEMS_TOGGLE_OPEN_NAME = create("gui.market settings.items.toggle open.name", "<GRADIENT:65B1B4>&lToggle Open</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_SETTINGS_ITEMS_TOGGLE_OPEN_LORE = create("gui.market settings.items.toggle open.lore",
			"&7Used to toggle whether or not your market",
			"&7is open/closed. If closed, player's will not see it.",
			"",
			"&7Current&F: %open%",
			"",
			"&e&l%left_click% &7to open/close market"
	);

	public static TranslationEntry GUI_MARKET_SETTINGS_ITEMS_TOGGLE_CLOSE_WHEN_OUT_OF_STOCK_NAME = create("gui.market settings.items.toggle close when out of stock.name", "<GRADIENT:65B1B4>&lToggle Auto Close</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_SETTINGS_ITEMS_TOGGLE_CLOSE_WHEN_OUT_OF_STOCK_LORE = create("gui.market settings.items.toggle close when out of stock.lore",
			"&7Used to toggle whether or not your market",
			"&7will auto close when you completely run out of stock.",
			"",
			"&7Current&F: %enabled%",
			"",
			"&e&l%left_click% &7to toggle auto close"
	);

	public static TranslationEntry GUI_MARKET_SETTINGS_ITEMS_BANNED_USERS_NAME = create("gui.market settings.items.banned users.name", "<GRADIENT:65B1B4>&lBanned Users</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_SETTINGS_ITEMS_BANNED_USERS_LORE = create("gui.market settings.items.banned users.lore",
			"&7Used to add and remove certain users from",
			"&7your market ban list, banned users cannot buy anything.",
			"",
			"&e&l%left_click% &7to edit banned users"
	);

	public static TranslationEntry GUI_MARKET_SETTINGS_ITEMS_HOME_LAYOUT_NAME = create("gui.market settings.items.home layout.name", "<GRADIENT:65B1B4>&lHome Layout</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_SETTINGS_ITEMS_HOME_LAYOUT_LORE = create("gui.market settings.items.home layout.lore",
			"&7The home layout is the main view",
			"&7of your market, it's where all",
			"&7of your will be shown",
			"",
			"&e&l%left_click% &7to edit home layout",
			"&b&l%right_click% &7to open item picker for bg",
			"&d&l%right_click% &7with an item on your cursor",
			"&7to set this layout's background"
	);

	public static TranslationEntry GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_NAME = create("gui.market settings.items.category layout.name", "<GRADIENT:65B1B4>&lCategory Layout</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_LORE = create("gui.market settings.items.category layout.lore",
			"&7The category layout is where all",
			"&7the items you add in a category",
			"&7will be shown to others.",
			"",
			"&e&l%left_click% &7to edit category layout",
			"&b&l%right_click% &7to open item picker for bg",
			"&d&l%right_click% &7with an item on your cursor",
			"&7to set this layout's background"
	);

	public static TranslationEntry GUI_MARKET_BANNED_USERS_TITLE = create("gui.market banned users.title", "&eMarkets &f- &7Settings &F- &cBans");
	public static TranslationEntry GUI_MARKET_BANNED_USERS_ITEMS_PLAYER_NAME = create("gui.market banned users.items.player.name", "<GRADIENT:65B1B4>&l%player_name%</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_BANNED_USERS_ITEMS_PLAYER_LORE = create("gui.market banned users.items.player.lore",
			"&7This user is currently banned from",
			"&7interacting with your market.",
			"",
			"&e&l%left_click% &7to unban them"
	);

	public static TranslationEntry GUI_MARKET_BANNED_USERS_ITEMS_NEW_BAN_NAME = create("gui.market banned users.items.new ban.name", "<GRADIENT:65B1B4>&lAdd Banned User</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_BANNED_USERS_ITEMS_NEW_BAN_LORE = create("gui.market banned users.items.new ban.lore",
			"&7Used to add a new user to the ban list",
			"&e&l%left_click% &7to ban a player"
	);


	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_TITLE = create("gui.layout control picker.title", "&eMarkets &f- &7Select Layout Control");
	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_EXIT_NAME = create("gui.layout control picker.items.exit.name", "<GRADIENT:65B1B4>&lExit Button</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_EXIT_LORE = create("gui.layout control picker.items.exit.lore",
			"&7Used to exit market menu",
			"&e&l%left_click% &7to select this control"
	);

	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_PROFILE_NAME = create("gui.layout control picker.items.profile.name", "<GRADIENT:65B1B4>&lProfile Button</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_PROFILE_LORE = create("gui.layout control picker.items.profile.lore",
			"&7Used to open your profile",
			"&e&l%left_click% &7to select this control"
	);

	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_BACK_NAME = create("gui.layout control picker.items.prev page.name", "<GRADIENT:65B1B4>&lBack Button</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_BACK_LORE = create("gui.layout control picker.items.prev page.lore",
			"&7Used to navigate back if theres multiple pages",
			"&e&l%left_click% &7to select this control"
	);

	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_NEXT_NAME = create("gui.layout control picker.items.next page.name", "<GRADIENT:65B1B4>&lNext Button</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_NEXT_LORE = create("gui.layout control picker.items.next page.lore",
			"&7Used to navigate forward if theres multiple pages",
			"&e&l%left_click% &7to select this control"
	);

	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_SEARCH_NAME = create("gui.layout control picker.items.search.name", "<GRADIENT:65B1B4>&lSearch Button</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_SEARCH_LORE = create("gui.layout control picker.items.search.lore",
			"&7Used to search your market for items",
			"&e&l%left_click% &7to select this control"
	);

	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_REVIEW_NAME = create("gui.layout control picker.items.review.name", "<GRADIENT:65B1B4>&lReview Button</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_CONTROL_PICKER_ITEMS_REVIEW_LORE = create("gui.layout control picker.items.review.lore",
			"&7Used to open market review",
			"&e&l%left_click% &7to select this control"
	);

	public static TranslationEntry GUI_LAYOUT_EDITOR_TITLE_HOME = create("gui.layout editor.title.home", "&eMarkets &f- &7Layout &F- &7Home");
	public static TranslationEntry GUI_LAYOUT_EDITOR_TITLE_CATEGORY = create("gui.layout editor.title.category", "&eMarkets &f- &7Layout &F- &7Category");

	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_FILL_SLOT_NAME = create("gui.layout editor.items.fill slot.name", "<GRADIENT:65B1B4>&lFill Slot</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_FILL_SLOT_LORE = create("gui.layout editor.items.fill slot.lore",
			"&7This slot will be populated by",
			"&7your market categories.",
			"",
			"&e&l%left_click% &7to disable this slot"
	);

	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_DECO_SLOT_NAME = create("gui.layout editor.items.decoration slot.name", "<GRADIENT:65B1B4>&lDecoration Slot</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_DECO_SLOT_LORE = create("gui.layout editor.items.decoration slot.lore",
			"&7This is a decoration slot",
			"",
			"&e&l%left_click% &7to make empty slot",
			"&d&l%right_click% &7with an item on your cursor",
			"&7to change the decoration item."
	);

	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_EMPTY_SLOT_NAME = create("gui.layout editor.items.empty slot.name", "<GRADIENT:65B1B4>&lEmpty Slot</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_EMPTY_SLOT_LORE = create("gui.layout editor.items.empty slot.lore",
			"&7This is a free slot, you can add",
			"&7decorations here or assign it as a ",
			"&7populated slot or even move controls.",
			"",
			"&e&l%left_click% &7to set as fill slot",
			"&b&l%right_click% &7to move a control here",
			"&d&l%right_click% &7with an item on your cursor",
			"&7to decorate this slot."
	);

	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_PROFILE_NAME = create("gui.layout editor.items.profile.name", "<GRADIENT:65B1B4>&lProfile</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_PROFILE_LORE = create("gui.layout editor.items.profile.lore",
			"&7Used to view the profile of the",
			"&7current market owner&F: &e%market_owner%"
	);

	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_REVIEW_NAME = create("gui.layout editor.items.review.name", "<GRADIENT:65B1B4>&LReview</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_REVIEW_LORE = create("gui.layout editor.items.review.lore",
			"&7Used to leave a review of the current market",
			"&7there's a delay between leaving reviews."
	);

	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_SEARCH_NAME = create("gui.layout editor.items.search.name", "<GRADIENT:65B1B4>&lSearch</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_LAYOUT_EDITOR_ITEMS_SEARCH_LORE = create("gui.layout editor.items.search.lore",
			"&7Used to search for items within",
			"&7all open categories of this market"
	);

	public static TranslationEntry GUI_MARKET_VIEW_TITLE = create("gui.market view.title", "%market_display_name%");
	public static TranslationEntry GUI_MARKET_VIEW_ITEMS_PROFILE_NAME = create("gui.market view.items.profile.name", "<GRADIENT:65B1B4>&lProfile</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_VIEW_ITEMS_PROFILE_LORE = create("gui.market view.items.profile.lore",
			"&7Used to view the profile of the",
			"&7current market owner&F: &e%market_owner%",
			"",
			"&e&l%left_click% &7to view owner profile"
	);

	public static TranslationEntry GUI_MARKET_VIEW_ITEMS_REVIEW_NAME = create("gui.market view.items.review.name", "<GRADIENT:65B1B4>&LReview</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_VIEW_ITEMS_REVIEW_LORE = create("gui.market view.items.review.lore",
			"&7Used to leave a review of the current market",
			"&7there's a delay between leaving reviews.",
			"",
			"&e&l%left_click% &7to leave a review",
			"&b&l%right_click% &7to view reviews"
	);

	public static TranslationEntry GUI_MARKET_VIEW_ITEMS_SEARCH_NAME = create("gui.market view.items.search.name", "<GRADIENT:65B1B4>&lSearch</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_VIEW_ITEMS_SEARCH_LORE = create("gui.market view.items.search.lore",
			"&7Used to search for items within",
			"&7all open categories of this market",
			"",
			"&e&l%left_click% &7to search market"
	);

	public static TranslationEntry GUI_MARKET_VIEW_ITEMS_CATEGORY_NAME = create("gui.market view.items.category.name", "%category_display_name%");
	public static TranslationEntry GUI_MARKET_VIEW_ITEMS_CATEGORY_LORE = create("gui.market view.items.category.lore",
			"",
			"&e&l%left_click% &7to open category"
	);


	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_TITLE = create("gui.market category view.title", "%market_display_name% &7- %category_display_name%");

	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_PROFILE_NAME = create("gui.market category view.items.profile.name", "<GRADIENT:65B1B4>&lProfile</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_PROFILE_LORE = create("gui.market category view.items.profile.lore",
			"&7Used to view the profile of the",
			"&7current market owner&F: &e%market_owner%",
			"",
			"&e&l%left_click% &7to view owner profile"
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_REVIEW_NAME = create("gui.market category view.items.review.name", "<GRADIENT:65B1B4>&LReview</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_REVIEW_LORE = create("gui.market category view.items.review.lore",
			"&7Used to leave a review of the current market",
			"&7there's a delay between leaving reviews.",
			"",
			"&e&l%left_click% &7to leave a review",
			"&b&l%right_click% &7to view reviews"
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_SEARCH_NAME = create("gui.market category view.items.search.name", "<GRADIENT:65B1B4>&lSearch</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_SEARCH_LORE = create("gui.market category view.items.search.lore",
			"&7Used to search for items within",
			"&7this specific category",
			"",
			"&e&l%left_click% &7to search category"
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_HEADER = create("gui.market category view.items.item.lore.header", "&7----------------------------");
	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_INFO = create("gui.market category view.items.item.lore.info",
			"&7Price&f: &a%market_item_price%",
			"&7Currency&f: &e%market_item_currency%",
			"&7Stock&f: &e%market_item_stock%",
			"&7Price for all&f: %true%",
			""
	);

	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_FOOTER = create("gui.market category view.items.item.lore.footer", "&7----------------------------");
	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_BUY = create("gui.market category view.items.item.lore.buy", "&e&l%left_click% &7to purchase item");
	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_MAKE_OFFER = create("gui.market category view.items.item.lore.make offer", "&b&l%right_click% &7to make offer");
	public static TranslationEntry GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_VIEW_CURRENCY = create("gui.market category view.items.item.lore.view currency", "&d&l%shift_right_click% &7to view currency");

	public static TranslationEntry GUI_PURCHASE_ITEM_TITLE = create("gui.purchase item.title", "%market_display_name% &7- &eCheckout");
	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_CUSTOM_CURRENCY_LORE = create("gui.purchase item.items.custom currency.lore",
			"&7----------------------------",
			"&eThis item is the required currency",
			"&7----------------------------"
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_PURCHASE_ITEM_LORE = create("gui.purchase item.items.purchasing item.lore",
			"&7----------------------------",
			"&7Available Stock&F: &e%market_item_stock%",
			"&7----------------------------"
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_INC1_NAME = create("gui.purchase item.items.increment one.name", "&a&l+1");
	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_INC1_LORE = create("gui.purchase item.items.increment one.lore",
			"",
			"&e&l%left_click% &7to increment by &a1"
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_INC5_NAME = create("gui.purchase item.items.increment five.name", "&a&l+5");
	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_INC5_LORE = create("gui.purchase item.items.increment five.lore",
			"",
			"&e&l%left_click% &7to increment by &a5"
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_INC10_NAME = create("gui.purchase item.items.increment ten.name", "&a&l+10");
	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_INC10_LORE = create("gui.purchase item.items.increment ten.lore",
			"",
			"&e&l%left_click% &7to increment by &a10"
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_DEC1_NAME = create("gui.purchase item.items.decrement one.name", "&a&l-1");
	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_DEC1_LORE = create("gui.purchase item.items.decrement one.lore",
			"",
			"&e&l%left_click% &7to decrement by &c1"
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_DEC5_NAME = create("gui.purchase item.items.decrement five.name", "&a&l-5");
	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_DEC5_LORE = create("gui.purchase item.items.decrement five.lore",
			"",
			"&e&l%left_click% &7to decrement by &c5"
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_DEC10_NAME = create("gui.purchase item.items.decrement ten.name", "&a&l-10");
	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_DEC10_LORE = create("gui.purchase item.items.decrement ten.lore",
			"",
			"&e&l%left_click% &7to decrement by &c10"
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_BUY_NAME = create("gui.purchase item.items.buy.name", "<GRADIENT:65B1B4>&lPurchase</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_BUY_LORE = create("gui.purchase item.items.buy.lore",
			"",
			"&e&l%left_click% &7to purchase item(s)"
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_NAME = create("gui.purchase item.items.price breakdown.name", "<GRADIENT:65B1B4>&lPrice Breakdown</GRADIENT:2B6F8A>");

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_INFO = create("gui.purchase item.items.price breakdown.lore.info",
			"&7Purchase Quantity&f: &E%purchase_quantity%",
			"&7Cost Per Item&f: &a%market_item_price%",
			"&7Currency&f: %market_item_currency%",
			""
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_SUBTOTAL = create("gui.purchase item.items.price breakdown.lore.subtotal",
			"&b&lSub Total",
			"&a%purchase_sub_total%",
			""
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_TAX = create("gui.purchase item.items.price breakdown.lore.tax",
			"&b&LTax",
			"&a%sales_tax%",
			""
	);

	public static TranslationEntry GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_TOTAL = create("gui.purchase item.items.price breakdown.lore.total",
			"&b&lTotal",
			"&a%purchase_total%"
	);

	public static TranslationEntry GUI_OFFLINE_PAYMENTS_TITLE = create("gui.offline payments.title", "&eMarkets &f- &7Collect Payments");
	public static TranslationEntry GUI_OFFLINE_PAYMENTS_ITEMS_PROFILE_LORE = create("gui.offline payments.items.payment.lore",
			"",
			"&7Quantity Received&f: &a%payment_total%",
			"",
			"&b&lReason",
			"%payment_reason%",
			"",
			"&d&lReceived On",
			"&e%payment_date%",
			"",
			"&e&l%left_click% &7to collect payment"
	);

	public static TranslationEntry GUI_OFFER_CREATE_TITLE = create("gui.offer creation.title", "&eMarkets &f- &7Send an Offer");
	public static TranslationEntry GUI_OFFER_CREATE_ITEMS_CREATE_NAME = create("gui.offer creation.items.create offer.name", "<GRADIENT:65B1B4>&LCreate Offer</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_OFFER_CREATE_ITEMS_CREATE_LORE = create("gui.offer creation.items.create offer.lore",
			"&e&l%left_click% &7to send the offer"
	);

	public static TranslationEntry GUI_OFFER_CREATE_ITEMS_BREAKDOWN_NAME = create("gui.offer creation.items.breakdown.name", "<GRADIENT:65B1B4>&LOffer Breakdown</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_OFFER_CREATE_ITEMS_BREAKDOWN_LORE = create("gui.offer creation.items.breakdown.lore",
			"",
			"&a&lYou &7(&f%sender_name%&7) &eis offering &b&lSeller &7(&f%seller_name%&7)",
			"&fx&a%offer_amount% &a%offer_currency% &efor &fx&a%requested_amount% &a%market_item_name%",
			"",
			"&c&oThis offer will be created assuming stock levels stay the",
			"&c&osame, if it changes this offer will be automatically voided."
	);

	public static TranslationEntry GUI_OFFER_CREATE_ITEMS_AMOUNT_NAME = create("gui.offer creation.items.offered amount.name", "<GRADIENT:65B1B4>&LOffer Amount</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_OFFER_CREATE_ITEMS_AMOUNT_LORE = create("gui.offer creation.items.offered amount.lore",
			"&7Current Offer &f: &a%offer_amount%",
			"",
			"&e&l%left_click% &7to change offer amount"

	);

	public static TranslationEntry GUI_OFFER_CREATE_ITEMS_CURRENCY_NAME = create("gui.offer creation.items.currency.name", "<GRADIENT:65B1B4>&lSwitch Currency</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_OFFER_CREATE_ITEMS_CURRENCY_LORE = create("gui.offer creation.items.currency.lore",
			"&7Used to adjust which currency you will",
			"&7be be offering for this market item.",
			"",
			"&7Current&f: &e%offer_currency%",
			"",
			"&e&l%left_click% &7to edit currency"
	);

	public static void init() {
		new Translations(Markets.getInstance()).setup();
	}
}
