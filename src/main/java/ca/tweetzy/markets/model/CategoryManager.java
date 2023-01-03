package ca.tweetzy.markets.model;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.impl.MarketCategory;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class CategoryManager extends ListManager<Category> {

	public CategoryManager() {
		super("Category");
	}

	public void create(@NonNull final Market market, @NonNull final String name, @NonNull final Consumer<Boolean> created) {
		final Category category = new MarketCategory(
				market.getId(),
				UUID.randomUUID(),
				name.toLowerCase(),
				"&e" + name,
				List.of("&7Market category"),
				System.currentTimeMillis(),
				System.currentTimeMillis()
		);

		category.store(storedCategory -> {
			if (storedCategory != null) {
				add(storedCategory);
				market.getCategories().add(storedCategory);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
	}

	@Override
	public void load() {
		clear();

		Markets.getDataManager().getCategories((error, found) -> {
			if (error != null) return;
			found.forEach(category -> {
				final Market locatedMarket = Markets.getMarketManager().getByUUID(category.getOwningMarket());
				if (locatedMarket == null) return;

				locatedMarket.getCategories().add(category);
				// After categories have been added to corresponding markets, load the items

			});
		});
	}
}
