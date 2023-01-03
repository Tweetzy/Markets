package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Category;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class MarketCategory implements Category {

	private final UUID id;
	private final String name;
	private final UUID owningMarket;
	private String displayName;
	private List<String> description;

	private final long createdAt;
	private long updatedAt;

	public MarketCategory(
			@NonNull final UUID owningMarket,
			@NonNull final UUID id,
			@NonNull final String name,
			@NonNull final String displayName,
			@NonNull final List<String> description,
			final long createdAt,
			final long updatedAt
	) {
		this.id = id;
		this.name = name;
		this.owningMarket = owningMarket;
		this.displayName = displayName;
		this.description = description;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@NotNull
	@Override
	public String getName() {
		return this.name;
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
		Markets.getDataManager().createCategory(this, (error, created) -> {
			if (error == null)
				stored.accept(created);
		});
	}
}
