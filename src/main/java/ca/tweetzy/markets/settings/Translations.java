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
	public static TranslationEntry CATEGORY_NAME_USED= create("category name used", "&cYou already have a category named&F: &4%category_name%");


	public static void init() {
		new Translations(Markets.getInstance()).setup();
	}
}
