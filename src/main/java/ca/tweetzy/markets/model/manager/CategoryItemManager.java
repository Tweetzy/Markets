package ca.tweetzy.markets.model.manager;

import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.impl.CategoryItem;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public final class CategoryItemManager extends ListManager<MarketItem> {

	public CategoryItemManager() {
		super("Category Item");
	}

	public MarketItem getByUUID(@NonNull final UUID id) {
		return getManagerContent().stream().filter(marketItem -> marketItem.getId().equals(id)).findFirst().orElse(null);
	}

	public void create(@NonNull final Category category, @NonNull final ItemStack item, @NonNull final String currency, final double price, final boolean priceIsForAll, @NonNull final Consumer<Boolean> created) {
		final MarketItem marketItem = new CategoryItem(
				UUID.randomUUID(),
				category.getId(),
				item,
				currency,
				price,
				item.getAmount(),
				priceIsForAll
		);

		marketItem.store(storedItem -> {
			if (storedItem != null) {
				add(storedItem);
				category.getItems().add(storedItem);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
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
