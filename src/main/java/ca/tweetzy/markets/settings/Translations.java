package ca.tweetzy.markets.settings;

import ca.tweetzy.flight.settings.TranslationEntry;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.markets.Markets;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

public final class Translations extends TranslationManager {

	public Translations(@NonNull JavaPlugin plugin) {
		super(plugin);
		this.mainLanguage = Settings.LANGUAGE.getString();
	}

	public static TranslationEntry ITEM_IS_AIR = create("error.item is air", "&cYou cannot add air to your market...");
	public static TranslationEntry NO_CATEGORIES = create("error.no categories", "&cYou need to create at least 1 category first.");
	public static TranslationEntry INVALID_CATEGORY = create("error.invalid category", "&cCould not find a category with id&f: &4%category_id%");
	public static TranslationEntry MARKET_NAME_TOO_LONG = create("error.market name too long", "&cMarket name too long, max is 72 characters.");
	public static TranslationEntry CATEGORY_NAME_TOO_LONG = create("error.category name too long", "&cCategory name too long, max is 32 characters.");
	public static TranslationEntry CANNOT_PAY_CREATION_FEE = create("error.cannot pay creation fee", "&cInsufficient funds to pay the creation fee");
	public static TranslationEntry NOT_ALLOWED_TO_CREATE = create("error.not allowed to create", "&cYou are not allowed to create a market");
	public static TranslationEntry NOT_ALLOWED_TO_REVIEW = create("error.not allowed to review", "&cYou need to wait longer to review again.");
	public static TranslationEntry MUST_BUY_ITEM_TO_REVIEW = create("error.must buy item to review", "&cYou must buy at least 1 item first.");
	public static TranslationEntry REVIEW_TOO_LONG = create("error.review feedback too long", "&cReview too long, max is 128 characters.");
	public static TranslationEntry CATEGORY_NAME_USED = create("error.category name used", "&cYou already have a category named&F: &4%category_name%");
	public static TranslationEntry REQUESTER_CANT_PAY = create("error.requester cannot pay", "&4%requester_name% &cdoes not have enough money to for you!");
	public static TranslationEntry NOT_ENOUGH_ITEMS = create("error.not enough items", "&cYou do not have enough items to fulfill that request");
	public static TranslationEntry TAKE_OUT_ITEM_FIRST = create("error.take out item", "&cPlease remove your item from the menu first!");
	public static TranslationEntry AT_MAX_CATEGORY_LIMIT = create("error.at maximum category limit", "&cYou aren't allowed to create more categories.");
	public static TranslationEntry AT_MAX_ITEM_LIMIT = create("error.at maximum item limit", "&cYou aren't allowed to add more items!");
	public static TranslationEntry AT_MAX_REQUEST_LIMIT = create("error.at maximum request limit", "&cYou aren't allowed to create more requests!");
	public static TranslationEntry PLACE_REQUEST_ITEM = create("error.place request item", "&cPlease select/provide the item you want to request");
	public static TranslationEntry PLACE_ITEM_TO_ADD = create("error.placed item to add", "&cPlease put the item you wish to add into the empty slot");
	public static TranslationEntry MUST_BE_HIGHER_THAN_ZERO = create("error.must be higher than zero", "&cPlease enter a number that is higher than 0");
	public static TranslationEntry INSUFFICIENT_ENTRY_AMOUNT = create("error.insufficient bank entry balance", "&cWithdrawal amount exceeds your stored total!");
	public static TranslationEntry NO_MARKET_FOUND = create("error.no market found", "&cCould not find any market for&F: &4%player_name%");
	public static TranslationEntry ONE_FILL_SLOT_REQUIRED = create("error.one fill slot required", "&cThis layout requires at least one fill slot!");
	public static TranslationEntry ITEM_OUT_OF_STOCK = create("error.item out of stock", "&cSorry that item is now out of stock");
	public static TranslationEntry ITEM_NO_LONGER_AVAILABLE = create("error.item no longer available", "&cSorry that item is no longer available");
	public static TranslationEntry BANNED_FROM_MARKET = create("error.banned from market", "&4%market_owner% &chas banned you from viewing their market!");
	public static TranslationEntry MARKET_IS_CLOSED = create("error.market is closed", "&4%market_owner%&c's market is currently closed!");
	public static TranslationEntry PROVIDE_REQUESTED_ITEM = create("error.provide requested item", "&cYou need to place/select a requested item first");
	public static TranslationEntry MAX_STACK_SIZE = create("error.max stack size", "&cCannot request that many, max stack size is &4%max_stack_size%");
	public static TranslationEntry REVIEWS_DISABLED = create("error.ratings disabled", "&cReviews are disabled");
	public static TranslationEntry OFFERS_DISABLED = create("error.offers disabled", "&cOffers are disabled");
	public static TranslationEntry NO_REVIEWS = create("error.no reviews", "&cThere are no reviews currently.");

	public static TranslationEntry DELETED_MARKET = create("info.deleted market", "&eSuccessfully deleted your market!");
	public static TranslationEntry REMOVED_PLAYER_MARKET = create("info.admin.removed market", "&eSuccessfully removed market owned by &b%player_name%");
	public static TranslationEntry MARKET_ITEM_BOUGHT_SELLER = create("info.market item bought.seller", "&fx&a%purchase_quantity% &f%item_name% &ewas bought by &b%buyer_name%");
	public static TranslationEntry MARKET_ITEM_BOUGHT_BUYER = create("info.market item bought.buyer", "&eBought &fx&a%purchase_quantity% &f%item_name% &efrom &b%seller_name%");
	public static TranslationEntry MARKET_ITEM_OUT_OF_STOCK = create("info.market item bought.out of stock", "&EAll &f%item_name% &estock has been sold!");
	public static TranslationEntry MARKET_ITEM_ADDED_TO_CATEGORY = create("info.added item to category", "&fx&a%item_quantity% &f%item_name% &ewas added to &b%category_display_name%");
	public static TranslationEntry OFFLINE_SALES_INFO = create("info.offline sales messages",
			"<center>&e&lMarkets",
			"",
			"<center>&7Hey, you had &a%offline_sales_amount% &7sales while offline.",
			"<center>&7Use &f/&emarkets transactions &7to view",
			""
	);

	public static TranslationEntry OFFER_RECEIVED = create("info.offer.received", "&b%sender_name% &ehas sent you an offer on an item!");
	public static TranslationEntry OFFER_SENT = create("info.offer.sent", "&EYou offer has successfully be sent to &b%owner_name%");

	public static TranslationEntry OFFER_REJECT_NOT_ACCEPTED = create("info.offer.rejected.not accepted", "&b%owner_name% &crejected your offer on &e%market_item_name%!");
	public static TranslationEntry OFFER_REJECT_NOT_AVAILABLE = create("info.offer.rejected.item not available", "&b%owner_name% &crejected your offer (item no longer available)");
	public static TranslationEntry OFFER_REJECT_INSUFFICIENT_STOCK = create("info.offer.rejected.insufficient stock", "&b%owner_name% &crejected your offer on &e%market_item_name% &c(insufficient stock)");
	public static TranslationEntry OFFER_REJECT_NO_MONEY = create("info.offer.rejected.no money", "&b%owner_name% &crejected your offer on &e%market_item_name% &c(insufficient balance)");
	public static TranslationEntry OFFER_ACCEPTED = create("info.offer.accepted", "&b%owner_name% &eaccepted your offer on &e%market_item_name%");
	public static TranslationEntry OFFER_ACCEPTED_PAYMENT = create("info.offer.accepted payment", "&eThe offer request was accepted");
	public static TranslationEntry REQUEST_PAYMENT = create("info.request.payment", "&eA request was completed");
	public static TranslationEntry REQUEST_FULFILLED = create("info.request.fulfilled", "&b%fulfill_name% &efulfilled your request for &e%request_item_name%");


	public static TranslationEntry DEFAULTS_MARKET_DISPLAY_NAME = create("info.defaults.market name", "&e%player_name%'s Market");
	public static TranslationEntry DEFAULTS_MARKET_DESCRIPTION = create("info.defaults.market description", "&aWelcome to my market");
	public static TranslationEntry DEFAULTS_MARKET_CATEGORY_DISPLAY_NAME = create("info.defaults.category name", "&e%market_category_name%");
	public static TranslationEntry DEFAULTS_MARKET_CATEGORY_DESCRIPTION = create("info.defaults.category description", "&7Market category");
	public static TranslationEntry DEFAULTS_REVIEW_MSG = create("info.defaults.review message", "&bAn awesome market");

	public static TranslationEntry MARKET_SORT_NAME = create("info.market sort type.name", "&eName");
	public static TranslationEntry MARKET_SORT_REVIEWS = create("info.market sort type.reviews", "&eReviews");
	public static TranslationEntry MARKET_SORT_ITEMS = create("info.market sort type.items", "&eTotal Items");
	public static TranslationEntry MARKET_SORT_LAST_UPDATED = create("info.market sort type.last updated", "&eLast Updated");

	// inputs
	public static TranslationEntry PROMPT_SEARCH_TITLE = create("prompts.search.title", "<GRADIENT:65B1B4>&LSearch</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_SEARCH_SUBTITLE = create("prompts.search.subtitle", "&fEnter search keywords into chat");

	public static TranslationEntry PROMPT_REQUEST_AMOUNT_TITLE = create("prompts.request amount.title", "<GRADIENT:65B1B4>&lRequest Amount</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_REQUEST_AMOUNT_SUBTITLE = create("prompts.request amount.subtitle", "&fEnter how many of that item you want");
	public static TranslationEntry PROMPT_REQUEST_PRICE_TITLE = create("prompts.price.title", "<GRADIENT:65B1B4>&lRequest Price</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_REQUEST_PRICE_SUBTITLE = create("prompts.price.subtitle", "&fEnter how much you'll pay for the request");

	public static TranslationEntry PROMPT_NEW_REVIEW_TITLE = create("prompts.new review.title", "<GRADIENT:65B1B4>&LReview Message</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_NEW_REVIEW_SUBTITLE = create("prompts.new review.subtitle", "&fEnter your feedback into chat");

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
	public static TranslationEntry PROMPT_WITHDRAW_ENTRY_TITLE = create("prompts.withdraw bank entry.title", "<GRADIENT:65B1B4>&LEnter Amount</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_WITHDRAW_ENTRY_SUBTITLE = create("prompts.withdraw bank entry.subtitle", "&fEnter how much you want to withdraw");

	public static TranslationEntry PROMPT_CATEGORY_NAME_TITLE = create("prompts.category name.title", "<GRADIENT:65B1B4>&LCategory Name</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_CATEGORY_NAME_SUBTITLE = create("prompts.category name.subtitle", "&fEnter new name for category into chat");

	public static TranslationEntry PROMPT_CATEGORY_DESC_TITLE = create("prompts.category description.title", "<GRADIENT:65B1B4>&LCategory Description</GRADIENT:2B6F8A>");
	public static TranslationEntry PROMPT_CATEGORY_DESC_SUBTITLE = create("prompts.category description.subtitle", "&fEnter new category description");

	// guis
	public static TranslationEntry GUI_SHARED_ITEMS_BACK_BUTTON_NAME = create("gui.shared buttons.back button.name", "<GRADIENT:65B1B4>&LGo Back</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_SHARED_ITEMS_BACK_BUTTON_LORE = create("gui.shared buttons.back button.lore",
			"&e&l%left_click% &7to go back"
	);

	public static TranslationEntry GUI_SHARED_ITEMS_EXIT_BUTTON_NAME = create("gui.shared buttons.exit button.name", "<GRADIENT:65B1B4>&LExit</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_SHARED_ITEMS_EXIT_BUTTON_LORE = create("gui.shared buttons.exit button.lore",
			"&e&l%left_click% &7to exit menu"
	);

	public static TranslationEntry GUI_SHARED_ITEMS_PREVIOUS_BUTTON_NAME = create("gui.shared buttons.previous button.name", "<GRADIENT:65B1B4>&lPrevious Page</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_SHARED_ITEMS_PREVIOUS_BUTTON_LORE = create("gui.shared buttons.previous button.lore",
			"&e&l%left_click% &7to go back a page"
	);

	public static TranslationEntry GUI_SHARED_ITEMS_NEXT_BUTTON_NAME = create("gui.shared buttons.next button.name", "<GRADIENT:65B1B4>&lNext Page</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_SHARED_ITEMS_NEXT_BUTTON_LORE = create("gui.shared buttons.next button.lore",
			"&e&l%left_click% &7to go to next page"
	);

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

	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_REQUESTS_NAME = create("gui.main view.items.requests.name", "<GRADIENT:65B1B4>&LRequests</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MAIN_VIEW_ITEMS_REQUESTS_LORE = create("gui.main view.items.requests.lore",
			"&7Used to view all open player",
			"&7requests or to create your own.",
			"",
			"&e&l%left_click% &7to view requests"
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

	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_DELETE_NAME = create("gui.market overview.items.delete market.name", "&c&lDelete Market");
	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_DELETE_LORE = create("gui.market overview.items.delete market.lore",
			"&7This action &4&lCANNOT &7be undone!",
			"&8Any items that cannot fit into your inventory",
			"&8will be dropped to the floor!",
			"",
			"&a&lClick &7to delete market"
	);

	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_REVIEWS_NAME = create("gui.market overview.items.reviews.name", "<GRADIENT:65B1B4>&LReviews</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_OVERVIEW_ITEMS_REVIEWS_LORE = create("gui.market overview.items.reviews.lore",
			"&7Used to view any reviews that",
			"&7were left by other players",
			"",
			"&7Rating&f: &6%market_ratings_stars% &f(&7%market_ratings_total%&f)",
			"",
			"&a&lClick &7to view reviews"
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
			"&7Wholesale&f: &e%market_item_wholesale%",
			"&7Accept Offers&f: &e%market_item_accepting_offers%",
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

	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_NAME_MOBILE = create("gui.market overview.items.new item mobile.name", "<GRADIENT:65B1B4>&LNew Item</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_LORE_MOBILE = create("gui.market overview.items.new item mobile.lore",
			"&7To create a new item on mobile.",
			"",
			"&f/&emarkets add &b%category_id% &a<price>",
			"&7Additionally, you can add &e-nooffers &7and/or &e-wholesale &7after price",
			"",
			"&7Example&f: &e/markets add %category_id% 25",
			"&7Example&f: &e/markets add %category_id% 95 -nooffers",
			"&7Example&f: &e/markets add %category_id% 500 -wholesale",
			"&7Example&f: &e/markets add %category_id% 35 -wholesale -nooffers",
			""
	);


	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_NAME = create("gui.market category edit.items.delete category.name", "&c&lDelete Category");
	public static TranslationEntry GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_LORE = create("gui.market category edit.items.delete category.lore",
			"&7This action &4&lCANNOT &7be undone!",
			"",
			"&a&lClick &7to delete this category"
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
			"&7Wholesale&f: %market_item_wholesale%",
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
			"&7be offering for this market item.",
			"",
			"&7Current&f: &e%offer_currency%",
			"",
			"&e&l%left_click% &7to edit currency"
	);

	public static TranslationEntry GUI_TRANSACTIONS_TITLE = create("gui.transactions.title", "&eMarkets &f- &7Transactions");
	public static TranslationEntry GUI_TRANSACTIONS_ITEMS_ENTRY_LORE = create("gui.transactions.items.entry.lore",
			"&7----------------------------",
			"&7Quantity&f: &E%item_quantity%",
			"&7Price&F: &a%market_item_price%",
			"&7Currency&f: &a%market_item_currency%",
			"",
			"&7Buyer&f: &e%buyer_name%",
			"&7Date&f: &e%transaction_date%",
			""
	);

	public static TranslationEntry GUI_BANK_TITLE = create("gui.bank.title", "&eMarkets &f- &7Bank");
	public static TranslationEntry GUI_BANK_ITEMS_ADD_NAME = create("gui.bank.items.add.name", "<GRADIENT:65B1B4>&LDeposit</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_BANK_ITEMS_ADD_LORE = create("gui.bank.items.add.lore",
			"&eDrop items on here to deposit"
	);

	public static TranslationEntry GUI_BANK_ITEMS_ENTRY_LORE = create("gui.bank.items.entry.lore",
			"&7Quantity&f: &E%entry_quantity%",
			"",
			"&e&l%left_click% &7to withdraw all",
			"&b&l%right_click% &7to withdraw qty"
	);

	public static TranslationEntry GUI_OFFERS_TITLE = create("gui.offers.title", "&eMarkets &f- &7Offers");
	public static TranslationEntry GUI_OFFERS_ITEMS_OFFER_LORE_INFO = create("gui.offers.items.offer.lore.info",
			"&b&lOriginal",
			"&7Price&F: &a%market_item_price%",
			"&7Currency&f: &a%market_item_currency%",
			"&7Quantity&f: &a%offer_requested_amount%",
			"",
			"&d&lOffered",
			"&7Price&F: &a%offer_amount%",
			"&7Currency&F: &a%offer_currency%",
			""
	);

	public static TranslationEntry GUI_OFFERS_ITEMS_OFFER_LORE_ACCEPT = create("gui.offers.items.offer.lore.accept", "&e&l%left_click% &7To accept this offer");
	public static TranslationEntry GUI_OFFERS_ITEMS_OFFER_LORE_REJECT = create("gui.offers.items.offer.lore.reject", "&d&l%right_click% &7To reject this offer");
	public static TranslationEntry GUI_OFFERS_ITEMS_OFFER_LORE_REJECT_STOCK = create("gui.offers.items.offer.lore.reject stock", "&C&OYou can only reject this item since it is no", "&c&olonger available or the stock count is too low.", "");

	public static TranslationEntry GUI_OFFERS_ITEMS_OFFER_LORE_HEADER = create("gui.offers.items.offer.lore.header", "&7----------------------------");
	public static TranslationEntry GUI_OFFERS_ITEMS_OFFER_LORE_FOOTER = create("gui.offers.items.offer.lore.footer", "&7----------------------------");

	public static TranslationEntry GUI_ALL_MARKETS_TITLE = create("gui.all markets.title", "&eMarkets &f- &7Open Markets");
	public static TranslationEntry GUI_ALL_MARKETS_ITEMS_MARKET_LORE = create("gui.all markets.items.market.lore",
			"",
			"&7Ratings&f: &6%market_ratings_stars% &f(&7%market_ratings_total%&f)",
			"",
			"&e&L%left_click% &7To enter market"
	);

	public static TranslationEntry GUI_ALL_MARKETS_ITEMS_FILTER_NAME = create("gui.all markets.items.filter.name", "<GRADIENT:65B1B4>&LFilter</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_ALL_MARKETS_ITEMS_FILTER_LORE = create("gui.all markets.items.filter.lore",
			"&7Used to filter markets by",
			"&7name,items,ratings and last updated",
			"",
			"&7Current&F: &e%market_sort_type%",
			"",
			"&e&lClick &7to change filter."
	);

	public static TranslationEntry GUI_ALL_MARKETS_ITEMS_SEARCH_NAME = create("gui.all markets.items.search.name", "<GRADIENT:65B1B4>&lSearch</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_ALL_MARKETS_ITEMS_SEARCH_LORE = create("gui.all markets.items.search.lore",
			"&7Used to search for items in",
			"&7every market that is open currently.",
			"",
			"&e&l%left_click% &7to search markets."
	);

	public static TranslationEntry GUI_EDIT_ITEM_TITLE = create("gui.edit market item.title", "&eMarkets &f- &7Item Settings");
	public static TranslationEntry GUI_EDIT_ITEM_ITEMS_OFFERS_NAME = create("gui.edit market item.items.offers.name", "<GRADIENT:65B1B4>&LOffers</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_EDIT_ITEM_ITEMS_OFFERS_LORE = create("gui.edit market item.items.offers.lore",
			"If enabled, other players will be",
			"&7able to send you offers om this item",
			"",
			"&7Current&F: %enabled%",
			"",
			"&e&l%left_click% &7to toggle offers"
	);

	public static TranslationEntry GUI_EDIT_ITEM_ITEMS_WHOLESALE_NAME = create("gui.edit market item.items.wholesale.name", "<GRADIENT:65B1B4>&LWholesale</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_EDIT_ITEM_ITEMS_WHOLESALE_LORE = create("gui.edit market item.items.wholesale.lore",
			"If enabled, the price will be for",
			"&7the entire stack/stock that is available.",
			"",
			"&7Current&F: %enabled%",
			"",
			"&e&l%left_click% &7to toggle wholesale"
	);

	public static TranslationEntry GUI_EDIT_ITEM_ITEMS_STOCK_NAME = create("gui.edit market item.items.stock.name", "<GRADIENT:65B1B4>&lStock</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_EDIT_ITEM_ITEMS_STOCK_LORE = create("gui.edit market item.items.stock.lore",
			"&7If you have any of this item in your",
			"&7inventory you can &edrop &7it here to add to stock.",
			"",
			"&7Current Stock&F: &e%market_item_stock%"
	);

	public static TranslationEntry GUI_EDIT_ITEM_ITEMS_CURRENCY_NAME = create("gui.edit market item.items.currency.name", "<GRADIENT:65B1B4>&lSwitch Currency</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_EDIT_ITEM_ITEMS_CURRENCY_LORE = create("gui.edit market item.items.currency.lore",
			"&7Used to adjust which currency you will",
			"&7be accepting for this particular item.",
			"",
			"&7Current&f: &e%market_item_currency%",
			"",
			"&e&l%left_click% &7to edit currency"
	);

	public static TranslationEntry GUI_NEW_RATING_TITLE = create("gui.new rating.title", "%market_display_name% &f- &7Review");
	public static TranslationEntry GUI_NEW_RATING_ITEMS_CREATE_NAME = create("gui.new rating.items.create.name", "<GRADIENT:65B1B4>&lLeave Review</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_NEW_RATING_ITEMS_CREATE_LORE = create("gui.new rating.items.create.lore",
			"&7If you are happy with your review",
			"&7then you can use this to confirm it.",
			"",
			"&e&l%left_click% &7to leave market review."
	);

	public static TranslationEntry GUI_NEW_RATING_ITEMS_MSG_NAME = create("gui.new rating.items.message.name", "<GRADIENT:65B1B4>&lReview Message</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_NEW_RATING_ITEMS_MSG_LORE = create("gui.new rating.items.message.lore",
			"&7The message content of the review,",
			"&7a brief statement why you gave the rating.",
			"",
			"&7Current Message&f:",
			"&e%review_message%",
			"",
			"&e&l%left_click% &7to edit message"
	);

	public static TranslationEntry GUI_NEW_RATING_ITEMS_STAR_NAME = create("gui.new rating.items.star.name", "<GRADIENT:65B1B4>&l%star_level% Star</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_NEW_RATING_ITEMS_STAR_LORE = create("gui.new rating.items.star.lore",
			"&e&l%left_click% &7to rate &e%star_level% &7star"
	);

	public static TranslationEntry GUI_RATINGS_TITLE = create("gui.ratings.title", "%market_display_name% &f- &7Ratings");
	public static TranslationEntry GUI_RATINGS_ITEMS_RATING_NAME = create("gui.ratings.items.rating.name", "<GRADIENT:65B1B4>&l%rater_name%</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_RATINGS_ITEMS_RATING_LORE = create("gui.ratings.items.rating.lore",
			"",
			"&e&lFeedback",
			"&7%rating_feedback%",
			"",
			"&7Stars&f: &6%rating_stars%",
			"",
			"&e%rating_date%"
	);

	public static TranslationEntry GUI_USER_PROFILE_TITLE = create("gui.user profile.title", "&e%player_name%&f'&7s Profile");
	public static TranslationEntry GUI_USER_PROFILE_ITEMS_USER_NAME = create("gui.user profile.items.user.name", "<GRADIENT:65B1B4>&l%player_name%</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_USER_PROFILE_ITEMS_USER_LORE = create("gui.user profile.items.user.lore",
			"&7Last Login&F: ",
			"&e%user_last_seen%",
			"",
			"&7Online&F: %true%"
	);

	public static TranslationEntry GUI_USER_PROFILE_ITEMS_RATING_NAME = create("gui.user profile.items.rating.name", "<GRADIENT:65B1B4>&l%rater_name%</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_USER_PROFILE_ITEMS_RATING_LORE = create("gui.user profile.items.rating.lore",
			"",
			"&e&lFeedback",
			"&7%rating_feedback%",
			"",
			"&7Stars&f: &6%rating_stars%",
			"",
			"&e%rating_date%"
	);

	public static TranslationEntry GUI_SEARCH_TITLE = create("gui.search.title", "&eMarkets &f- &7Search &f- &7%search_keywords%");
	public static TranslationEntry GUI_SEARCH_ITEMS_ITEM_LORE_INFO = create("gui.search.items.item.lore.info",
			"&7Price&f: &a%market_item_price%",
			"&7Currency&f: &e%market_item_currency%",
			"&7Stock&f: &e%market_item_stock%",
			"&7Wholesale&f: %market_item_wholesale%",
			"&7Owner&F: &e%market_item_owner%",
			"&7Shop&F: %market_item_market_name%",
			""
	);

	public static TranslationEntry GUI_REQUEST_TITLE_ALL = create("gui.request.title.all", "&eMarkets &f- &7All Requests");
	public static TranslationEntry GUI_REQUEST_TITLE_YOURS = create("gui.request.title.yours", "&eMarkets &f- &7Your Requests");

	public static TranslationEntry GUI_REQUEST_ITEMS_TOGGLE_NAME = create("gui.request.items.toggle.name", "<GRADIENT:65B1B4>&lSwitch View</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_REQUEST_ITEMS_TOGGLE_LORE = create("gui.request.items.toggle.lore",
			"&7Used to toggle between viewing all open",
			"&7requests and just requests you've made",
			"",
			"&e&l%left_click% &7to toggle view"
	);

	public static TranslationEntry GUI_REQUEST_ITEMS_REQUEST_LORE_OTHER = create("gui.request.items.request.lore.others",
			"&7Price&F: &a%request_price%",
			"&7Currency&f: &a%request_currency%",
			"&7Quantity&f: &a%request_amount%",
			"",
			"&e&l%left_click% &7to fulfill request"
	);

	public static TranslationEntry GUI_REQUEST_ITEMS_REQUEST_LORE_SELF = create("gui.request.items.request.lore.self",
			"&7Price&F: &a%request_price%",
			"&7Currency&f: &a%request_currency%",
			"&7Quantity&f: &a%request_amount%",
			"",
			"&e&l%left_click% &7to cancel request"
	);

	public static TranslationEntry GUI_REQUEST_ITEMS_CREATE_NAME = create("gui.request.items.create.name", "<GRADIENT:65B1B4>&lCreate Request</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_REQUEST_ITEMS_CREATE_LORE = create("gui.request.items.create.lore",
			"&7Used to create a new request for a",
			"&7particular item you are looking for.",
			"",
			"&e&l%left_click% &7to make a request"
	);


	public static TranslationEntry GUI_CREATE_REQUEST_TITLE = create("gui.create request.title", "&eMarkets &f- &7New Request");

	public static TranslationEntry GUI_CREATE_REQUEST_ITEMS_CURRENCY_NAME = create("gui.create request.items.currency.name", "<GRADIENT:65B1B4>&lSwitch Currency</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CREATE_REQUEST_ITEMS_CURRENCY_LORE = create("gui.create request.items.currency.lore",
			"&7Used to adjust which currency you will",
			"&7be offering for the requested item(s).",
			"",
			"&7Current&f: &e%request_currency%",
			"",
			"&e&l%left_click% &7to edit currency"
	);

	public static TranslationEntry GUI_CREATE_REQUEST_ITEMS_REQUESTED_ITEM_NAME = create("gui.create request.items.requested item.name", "<GRADIENT:65B1B4>&lNo Item Selected</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CREATE_REQUEST_ITEMS_REQUESTED_ITEM_LORE = create("gui.create request.items.requested item.lore",
			"&7----------------------------",
			"&e&l%left_click% &7to open a material picker &eor",
			"&b&l%right_click% &7with the item you want",
			"&7to request more of.",
			"&7----------------------------"
	);

	public static TranslationEntry GUI_CREATE_REQUEST_ITEMS_AMOUNT_NAME = create("gui.create request.items.amount.name", "<GRADIENT:65B1B4>&lRequest Amount</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CREATE_REQUEST_ITEMS_AMOUNT_LORE = create("gui.create request.items.amount.lore",
			"&7Current&f: &a%request_amount%",
			"",
			"&e&l%left_click% &7to change request amount"
	);

	public static TranslationEntry GUI_CREATE_REQUEST_ITEMS_CREATE_NAME = create("gui.create request.items.create.name", "<GRADIENT:65B1B4>&lCreate Request</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CREATE_REQUEST_ITEMS_CREATE_LORE = create("gui.create request.items.create.lore",
			"&e&l%left_click% &7to make request"
	);

	public static TranslationEntry GUI_CREATE_REQUEST_ITEMS_PRICE_NAME = create("gui.create request.items.price.name", "<GRADIENT:65B1B4>&lOffered Amount</GRADIENT:2B6F8A>");
	public static TranslationEntry GUI_CREATE_REQUEST_ITEMS_PRICE_LORE = create("gui.create request.items.price.lore",
			"&7Current&f: &a%request_price%",
			"",
			"&e&l%left_click% &7to change request pay"
	);


	public static void init() {
		new Translations(Markets.getInstance()).setup();
	}
}
