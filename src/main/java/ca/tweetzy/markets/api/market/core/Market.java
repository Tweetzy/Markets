package ca.tweetzy.markets.api.market.core;

import ca.tweetzy.markets.api.*;
import ca.tweetzy.markets.api.market.layout.Layout;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface Market extends Identifiable, Displayable, Trackable, Synchronize, Storeable<Market> {

	@NonNull UUID getOwnerUUID();

	@NonNull String getOwnerName();

	void setOwnerName(@NonNull String ownerName);

	@NonNull List<Category> getCategories();

	@NonNull List<Rating> getRatings();

	boolean isOpen();

	List<UUID> getBannedUsers();

	boolean isCloseWhenOutOfStock();

	Layout getHomeLayout();

	Layout getCategoryLayout();

	void setOpen(final boolean open);

	void setCloseWhenOutOfStock(final boolean closeWhenOutOfStock);

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
