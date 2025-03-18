package ca.tweetzy.markets.api.market;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.markets.api.Navigable;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum MarketSortType implements Navigable<MarketSortType> {

	NAME(Settings.MARKET_SORT_FILTER_NAME_ENABLED.getBoolean()),
	REVIEWS(!Settings.DISABLE_REVIEWS.getBoolean()),
	ITEMS(Settings.MARKET_SORT_FILTER_ITEMS_ENABLED.getBoolean()),
	LAST_UPDATED(Settings.MARKET_SORT_FILTER_LAST_UPDATED_ENABLED.getBoolean());



	@Getter
	private final boolean enabled;

	@Override
	public MarketSortType next() {
		MarketSortType[] values = enumClass().getEnumConstants();
		MarketSortType current = this;
		int ordinal = current.ordinal();

		// Loop through the values starting from the next one
		for (int i = 1; i < values.length; i++) {
			int nextOrdinal = (ordinal + i) % values.length;
			MarketSortType next = values[nextOrdinal];

			// Return the first enabled value found
			if (next.enabled) {
				return next;
			}
		}

		return null;
	}

	@Override
	public MarketSortType previous() {
		MarketSortType[] values = enumClass().getEnumConstants();
		MarketSortType current = this;
		int ordinal = current.ordinal();

		// Loop through the values starting from the previous one
		for (int i = 1; i <= values.length; i++) {
			int previousOrdinal = (ordinal - i + values.length) % values.length;
			MarketSortType previous = values[previousOrdinal];

			// Return the first enabled value found
			if (previous.enabled) {
				return previous;
			}
		}

		// If no enabled values are found, return null or throw an exception
		// For this example, we'll return null
		return null;
	}


	@Override
	public Class<MarketSortType> enumClass() {
		return MarketSortType.class;
	}

	public String getTranslatedName() {
		return switch (this) {
			case NAME -> TranslationManager.string(Translations.MARKET_SORT_NAME);
			case REVIEWS -> TranslationManager.string(Translations.MARKET_SORT_REVIEWS);
			case ITEMS -> TranslationManager.string(Translations.MARKET_SORT_ITEMS);
			case LAST_UPDATED -> TranslationManager.string(Translations.MARKET_SORT_LAST_UPDATED);
		};
	}
}
