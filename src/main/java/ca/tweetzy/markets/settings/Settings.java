package ca.tweetzy.markets.settings;

import ca.tweetzy.flight.config.ConfigEntry;
import ca.tweetzy.flight.settings.FlightSettings;
import ca.tweetzy.markets.Markets;
import lombok.NonNull;

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


	public static void init() {
		Markets.getCoreConfig().init();
	}
}
