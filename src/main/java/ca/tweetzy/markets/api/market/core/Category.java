package ca.tweetzy.markets.api.market.core;

import ca.tweetzy.markets.api.*;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public interface Category extends Identifiable, UserIdentifiable, Displayable, Trackable, Synchronize, UserViewable, Storeable<Category> {

	@NonNull UUID getOwningMarket();

	@NonNull ItemStack getIcon();

	@NonNull List<MarketItem> getItems();

	void setIcon(@NonNull final ItemStack icon);

	void setItems(@NonNull final List<MarketItem> items);

}
