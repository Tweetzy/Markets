package ca.tweetzy.markets.api.market;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.markets.api.Navigable;
import ca.tweetzy.markets.settings.Translations;

public enum MarketSortType implements Navigable<MarketSortType> {

	NAME,
	REVIEWS,
	ITEMS,
	LAST_UPDATED;

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
