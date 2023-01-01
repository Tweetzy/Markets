package ca.tweetzy.markets.settings;

import ca.tweetzy.flight.config.ConfigEntry;
import ca.tweetzy.flight.settings.FlightSettings;
import ca.tweetzy.markets.Markets;

public final class Settings extends FlightSettings {

	public static ConfigEntry PREFIX = create("prefix", "&8[&eMarkets&8]").withComment("The prefix for the plugin");
	public static ConfigEntry LANGUAGE = create("language", "en_us").withComment("The primary language of the plugin");

	public static void init() {
		Markets.getCoreConfig().init();
	}
}
