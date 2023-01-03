package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.api.market.Category;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class MarketCategory implements Category {

	private final String id;
	private final UUID owningMarket;
	private String displayName;
	private List<String> description;

	private final long createdAt;
	private long updatedAt;

	public MarketCategory(
			@NonNull final UUID owningMarket,
			@NonNull final String id,
			@NonNull final String displayName,
			@NonNull final List<String> description,
			final long createdAt,
			final long updatedAt
	) {
		this.id = id;
		this.owningMarket = owningMarket;
		this.displayName = displayName;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public @NonNull String getId() {
		return this.id;
	}

	@Override
	public @NonNull UUID getOwningMarket() {
		return this.owningMarket;
	}

	@Override
	public @NonNull String getDisplayName() {
		return this.displayName;
	}

	@Override
	public @NonNull List<String> getDescription() {
		return this.description;
	}

	@Override
	public void setDisplayName(@NonNull String displayName) {
		this.displayName = displayName;
	}

	@Override
	public void setDescription(@NonNull List<String> description) {
		this.description = description;
	}

	@Override
	public long getTimeCreated() {
		return this.createdAt;
	}

	@Override
	public long getLastUpdated() {
		return this.updatedAt;
	}

	@Override
	public void store(@NonNull Consumer<Category> stored) {

	}
}
