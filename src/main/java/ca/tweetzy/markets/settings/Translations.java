package ca.tweetzy.markets.settings;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.markets.Markets;
import lombok.NonNull;
import org.bukkit.plugin.java.JavaPlugin;

public final class Translations extends TranslationManager {

	public Translations(@NonNull JavaPlugin plugin) {
		super(plugin);
	}


	public static void init() {
		new Translations(Markets.getInstance()).setup();
	}
}
