package ca.tweetzy.markets.api.market.core;

import ca.tweetzy.markets.api.*;
import ca.tweetzy.markets.Markets;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
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

	default List<MarketItem> getInStockItems(@Nullable Player viewer) {
		// Null viewer = non-owner view (hide zero-stock items). Must not throw.
		Market market = Markets.getMarketManager().getByUUID(getOwningMarket());
		final boolean isOwner = (market != null && viewer != null && market.getOwnerUUID().equals(viewer.getUniqueId()));
		return getItems().stream()
			.filter(item -> item.getStock() > 0 || isOwner)
			.collect(Collectors.toList());
	}
}
