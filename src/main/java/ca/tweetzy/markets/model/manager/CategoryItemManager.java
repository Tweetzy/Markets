package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.MarketItem;
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

	public void create(@NonNull final Category category, @NonNull final ItemStack item, @NonNull final String currency, @NonNull final ItemStack currencyItem, final double price, final boolean priceIsForAll, final boolean acceptingOffers, final boolean infinite, @NonNull final Consumer<Boolean> created) {
		final MarketItem marketItem = new CategoryItem(
				UUID.randomUUID(),
				category.getId(),
				item,
				currency,
				currencyItem,
				price,
				item.getAmount(),
				priceIsForAll,
				acceptingOffers,
				infinite
		);

		marketItem.store(storedItem -> {
			if (storedItem != null) {
				// Ensure item is added to cache and category immediately
				// This prevents items from not appearing on the server where they were added
				add(storedItem);
				category.getItems().add(storedItem);
				created.accept(true);
			} else {
				// Log the initial store failure
				Markets.getInstance().getLogger().warning("CategoryItemManager.create() - Initial store() returned null:");
				Markets.getInstance().getLogger().warning("  Item ID: " + marketItem.getId());
				Markets.getInstance().getLogger().warning("  Category ID: " + category.getId());
				Markets.getInstance().getLogger().warning("  Category Name: " + category.getName());
				Markets.getInstance().getLogger().warning("  Item Type: " + (item != null ? item.getType().name() : "null"));
				Markets.getInstance().getLogger().warning("  Price: " + price);
				Markets.getInstance().getLogger().warning("  Stock: " + item.getAmount());
				Markets.getInstance().getLogger().warning("  Currency: " + currency);
				
				// If store failed, check if item might have been saved anyway (connection issue during fetch)
				// Try to reload from DB as fallback
				Markets.getDataManager().getMarketItemsByCategory(category.getId(), (error, items) -> {
					if (error == null && items != null) {
						// Check if our item is in the list
						MarketItem foundItem = items.stream()
							.filter(i -> i.getId().equals(marketItem.getId()))
							.findFirst()
							.orElse(null);
						
						if (foundItem != null) {
							// Item was saved, add to cache
							Markets.getInstance().getLogger().info("CategoryItemManager.create() - Item found in fallback query, adding to cache");
							add(foundItem);
							category.getItems().add(foundItem);
							created.accept(true);
						} else {
							// Fallback also failed - log final failure
							Markets.getInstance().getLogger().severe("CategoryItemManager.create() - Final failure: Item not found in fallback query");
							Markets.getInstance().getLogger().severe("  Item ID: " + marketItem.getId());
							Markets.getInstance().getLogger().severe("  Category ID: " + category.getId());
							Markets.getInstance().getLogger().severe("  Total items in category: " + items.size());
							created.accept(false);
						}
					} else {
						// Fallback query failed - log the error
						Markets.getInstance().getLogger().severe("CategoryItemManager.create() - Fallback query failed:");
						Markets.getInstance().getLogger().severe("  Item ID: " + marketItem.getId());
						Markets.getInstance().getLogger().severe("  Category ID: " + category.getId());
						if (error != null) {
							Markets.getInstance().getLogger().severe("  Error: " + error.getMessage());
							error.printStackTrace();
						} else {
							Markets.getInstance().getLogger().severe("  Error: getMarketItemsByCategory returned null items without error");
						}
						created.accept(false);
					}
				});
			}
		});
	}

	@Override
	public void load() {
		clear();

		Markets.getDataManager().getMarketItems((error, found) -> {
			if (error != null) return;

			found.forEach(marketItem -> {
				final Category locatedCategory = Markets.getCategoryManager().getByUUID(marketItem.getOwningCategory());
				if (locatedCategory == null) {
					return;
				}

				locatedCategory.getItems().add(marketItem);
				add(marketItem);
			});
		});
	}
}
