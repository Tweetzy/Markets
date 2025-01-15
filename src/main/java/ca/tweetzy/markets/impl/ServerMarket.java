package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.MarketType;
import ca.tweetzy.markets.api.market.core.Rating;
import ca.tweetzy.markets.api.market.layout.Layout;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public final class ServerMarket extends PlayerMarket{
	
	public ServerMarket(@NonNull UUID id, @NonNull UUID ownerUUID, @NonNull String ownerName, @NonNull String displayName, @NonNull List<String> description, @NonNull List<Category> categories, @NonNull List<Rating> ratings, @NonNull List<UUID> bannedUsers, boolean open, boolean closeWhenOutOfStock, Layout homeLayout, Layout categoryLayout, long createdAt, long updatedAt) {
		super(id, ownerUUID, ownerName, displayName, description, categories, ratings, bannedUsers, open, closeWhenOutOfStock, homeLayout, categoryLayout, createdAt, updatedAt);
		this.marketType = MarketType.SERVER;
	}
}
