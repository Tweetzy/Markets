package ca.tweetzy.markets.api.market.core;

import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.api.*;
import ca.tweetzy.markets.api.market.layout.Layout;
import ca.tweetzy.markets.impl.ServerMarket;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public interface Market extends Identifiable, Displayable, Trackable, Synchronize, Storeable<Market> {

	@NonNull
	UUID getOwnerUUID();

	@NonNull
	String getOwnerName();

	void setOwnerName(@NonNull String ownerName);

	@NonNull
	List<Category> getCategories();

	@NonNull
	List<Rating> getRatings();

	boolean isOpen();

	List<UUID> getBannedUsers();

	boolean isCloseWhenOutOfStock();

	Layout getHomeLayout();

	Layout getCategoryLayout();

	void setOpen(final boolean open);

	void setCloseWhenOutOfStock(final boolean closeWhenOutOfStock);

	default ItemStack getDynamicIcon() {
		return getOwnerUUID().equals(UUID.fromString(Settings.SERVER_MARKET_UUID.getString())) ? QuickItem.of(Settings.SERVER_MARKET_TEXTURE.getString()).make() : QuickItem.of(Bukkit.getOfflinePlayer(getOwnerUUID())).make();
	}

	default boolean isServerMarket() {
		return this instanceof ServerMarket || getOwnerUUID().equals(UUID.fromString(Settings.SERVER_MARKET_UUID.getString())) ;
	}

	default boolean isEmpty() {
		boolean isEmpty = true;
		for (Category category : getCategories()) {
			for (MarketItem item : category.getItems()) {
				if (item.getStock() > 0) {
					isEmpty = false;
					break;
				}
			}
		}
		return isEmpty;
	}

	default int getItemCount() {
		int count = 0;
		for (Category category : getCategories()) {
			count += category.getItems().size();
		}

		return count;
	}

	default double getReviewAvg() {
		return getRatings().stream().mapToInt(Rating::getStars).average().orElse(0);
	}
}
