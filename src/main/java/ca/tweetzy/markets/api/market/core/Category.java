package ca.tweetzy.markets.api.market.core;

import ca.tweetzy.markets.api.*;
import ca.tweetzy.markets.Markets;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface Category extends Identifiable, UserIdentifiable, Displayable, Trackable, Synchronize, UserViewable, Storeable<Category> {

	@NonNull UUID getOwningMarket();

	@NonNull ItemStack getIcon();

	@NonNull List<MarketItem> getItems();

	void setIcon(@NonNull final ItemStack icon);

	void setItems(@NonNull final List<MarketItem> items);

	default List<MarketItem> getInStockItems() {
		return getInStockItems(null);
	}

	default List<MarketItem> getInStockItems(@NonNull Player viewer) {
		// Get the market to check ownership
		Market market = Markets.getMarketManager().getByUUID(getOwningMarket());
		boolean isOwner = market != null && viewer != null && market.getOwnerUUID().equals(viewer.getUniqueId());
		
		return getItems().stream()
			.filter(item -> {
				// Show items with stock > 0 to everyone
				if (item.getStock() > 0) {
					return true;
				}
				// Show items with stock = 0 only to the owner
				return isOwner;
			})
			.collect(Collectors.toList());
	}
}
