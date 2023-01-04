package ca.tweetzy.markets.model;

import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.MarketItem;

public final class CategoryItemManager extends ListManager<MarketItem> {

	public CategoryItemManager() {
		super("Category Item");
	}


	@Override
	public void load() {
		clear();

		Markets.getDataManager().getMarketItems((error, found) -> {
			if (error != null) return;
			Common.log("&aLoading Category Items");

			found.forEach(marketItem -> {
				final Category locatedCategory = Markets.getCategoryManager().getByUUID(marketItem.getOwningCategory());
				if (locatedCategory == null) return;

				locatedCategory.getItems().add(marketItem);
			});
		});
	}
}
