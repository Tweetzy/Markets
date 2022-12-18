package ca.tweetzy.markets.impl.market;

import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.*;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class PlayerMarket extends AbstractMarket {

	private final UUID id;

	private String displayName;
	private List<String> description;

	private final UUID ownerUUID;
	private String ownerName;

	private final List<Category> categories;
	private final List<Rating> ratings;

	private final long createdAt;
	private long updatedAt;


	public PlayerMarket(
			@NonNull final UUID id,
			@NonNull final UUID ownerUUID,
			@NonNull final String ownerName,
			@NonNull final String displayName,
			@NonNull final List<String> description,
			@NonNull final List<Category> categories,
			@NonNull final List<Rating> ratings,
			final long createdAt,
			final long updatedAt
	) {
		super(MarketType.PLAYER);
		this.id = id;
		this.ownerUUID = ownerUUID;
		this.ownerName = ownerName;
		this.displayName = displayName;
		this.description = description;
		this.categories = categories;
		this.ratings = ratings;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@Override
	public @NonNull UUID getOwnerUUID() {
		return this.ownerUUID;
	}

	@Override
	public @NonNull String getOwnerName() {
		return this.ownerName;
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
	public @NonNull List<Category> getCategories() {
		return this.categories;
	}

	@Override
	public @NonNull List<Rating> getRatings() {
		return this.ratings;
	}

	@Override
	public void setOwnerName(@NonNull String ownerName) {
		this.ownerName = ownerName;
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
	public void store(@NonNull Consumer<Market> stored) {

	}

	@Override
	public void sync(@Nullable Consumer<SynchronizeResult> syncResult) {

	}
}
