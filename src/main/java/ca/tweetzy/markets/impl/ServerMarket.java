package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.MarketType;
import ca.tweetzy.markets.api.market.core.Rating;
import ca.tweetzy.markets.api.market.layout.Layout;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class ServerMarket extends PlayerMarket{
	
	public ServerMarket() {
		super();
		this.marketType = MarketType.SERVER;
	}
	
	public ServerMarket(@NonNull UUID id, @NonNull UUID ownerUUID, @NonNull String ownerName, @NonNull String displayName, @NonNull List<String> description, @NonNull List<Category> categories, @NonNull List<Rating> ratings, @NonNull List<UUID> bannedUsers, boolean open, boolean closeWhenOutOfStock, Layout homeLayout, Layout categoryLayout, long createdAt, long updatedAt) {
		super(id, ownerUUID, ownerName, displayName, description, categories, ratings, bannedUsers, open, closeWhenOutOfStock, homeLayout, categoryLayout, createdAt, updatedAt);
		this.marketType = MarketType.SERVER;
	}
	
	@Override
	public void store(@NonNull Consumer<ca.tweetzy.markets.api.market.core.Market> stored) {
		// Ensure marketType is never null before saving - ServerMarket should always be SERVER
		if (this.marketType == null) {
			this.marketType = MarketType.SERVER;
		}
		
		super.store(stored);
	}
	
	@Override
	public void sync(@Nullable Consumer<SynchronizeResult> syncResult) {
		// Ensure marketType is never null before saving - ServerMarket should always be SERVER
		if (this.marketType == null) {
			this.marketType = MarketType.SERVER;
		}
		
		super.sync(syncResult);
	}
}
