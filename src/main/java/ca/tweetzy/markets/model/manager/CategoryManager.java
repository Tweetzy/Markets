package ca.tweetzy.markets.model.manager;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.impl.MarketCategory;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;

public final class CategoryManager extends ListManager<Category> {

	public CategoryManager() {
		super("Category");
	}

	public Category getByUUID(@NonNull final UUID id) {
		return getManagerContent().stream().filter(category -> category.getId().equals(id)).findFirst().orElse(null);
	}

	public Category getByName(@NonNull final UUID owningMarket, @NonNull final String name) {
		return getManagerContent().stream().filter(category -> category.getOwningMarket().equals(owningMarket) && category.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public Category getByName(@NonNull final Market owningMarket, @NonNull final String name) {
		return getByName(owningMarket.getId(), name.toLowerCase());
	}

	public boolean doesCategoryExistAlready(@NonNull final UUID owningMarket, @NonNull final String name) {
		return getByName(owningMarket, name.toLowerCase()) != null;
	}

	public boolean doesCategoryExistAlready(@NonNull final Market owningMarket, @NonNull final String name) {
		return doesCategoryExistAlready(owningMarket.getId(), name.toLowerCase());
	}


	public void create(@NonNull final Market market, @NonNull final String name, @NonNull final Consumer<Boolean> created) {
		final Category category = new MarketCategory(
				market.getId(),
				UUID.randomUUID(),
				CompMaterial.CHEST.parseItem(),
				name.toLowerCase(),
				TranslationManager.string(Translations.DEFAULTS_MARKET_CATEGORY_DISPLAY_NAME, "market_category_name", name),
				TranslationManager.list(Translations.DEFAULTS_MARKET_CATEGORY_DESCRIPTION),
				new ArrayList<>(),
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
			Common.log("&aLoading Market Categories");

			found.forEach(category -> {
				final Market locatedMarket = Markets.getMarketManager().getByUUID(category.getOwningMarket());
				if (locatedMarket == null) return;

				// load items for the category, this is likely going to create way too many tasks when there's hundreds of categories... but we will see.
				Markets.getDataManager().getMarketItemsByCategory(category.getId(), (itemsError, foundItems) -> {
					if (itemsError != null) return;

					category.getItems().addAll(foundItems);
					Markets.getCategoryItemManager().addAll(foundItems);
					locatedMarket.getCategories().add(category);
					add(category);
				});
			});
		});
	}
}
