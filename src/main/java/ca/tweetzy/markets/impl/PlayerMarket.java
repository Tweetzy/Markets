package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.database.annotations.Column;
import ca.tweetzy.flight.database.annotations.Id;
import ca.tweetzy.flight.database.annotations.Ignore;
import ca.tweetzy.flight.database.annotations.Nested;
import ca.tweetzy.flight.database.annotations.Table;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.core.*;
import ca.tweetzy.markets.api.market.layout.Layout;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Table("markets")
public class PlayerMarket extends AbstractMarket {

	@Id
	@Column("id")
	private UUID id;

	@Column("display_name")
	private String displayName;
	
	@Nested
	@Column("description")
	private List<String> description;

	@Column("owner")
	private UUID ownerUUID;
	
	@Column("owner_name")
	private String ownerName;

	@Ignore
	private List<Category> categories;
	
	@Ignore
	private List<Rating> ratings;
	
	@Nested
	@Column("banned_users")
	private List<UUID> bannedUsers;
	
	@Column("open")
	private boolean open;
	
	@Column("close_when_out_of_stock")
	private boolean closeWhenOutOfStock;
	
	@Nested
	@Column("home_layout")
	private Layout homeLayout;
	
	@Nested
	@Column("category_layout")
	private Layout categoryLayout;
	
	@Column("created_at")
	private long createdAt;
	
	@Column("updated_at")
	private long updatedAt;

	@Column("type")
	protected MarketType marketType;

	public PlayerMarket() {
		super(MarketType.PLAYER);
		this.marketType = MarketType.PLAYER;
	}

	public PlayerMarket(
			@NonNull final UUID id,
			@NonNull final UUID ownerUUID,
			@NonNull final String ownerName,
			@NonNull final String displayName,
			@NonNull final List<String> description,
			@NonNull final List<Category> categories,
			@NonNull final List<Rating> ratings,
			@NonNull final List<UUID> bannedUsers,
			final boolean open,
			final boolean closeWhenOutOfStock,
			final Layout homeLayout,
			final Layout categoryLayout,
			final long createdAt,
			final long updatedAt
	) {
		super(MarketType.PLAYER);
		this.marketType = MarketType.PLAYER;
		this.id = id;
		this.ownerUUID = ownerUUID;
		this.ownerName = ownerName;
		this.displayName = displayName;
		this.description = description;
		this.categories = categories;
		this.ratings = ratings;
		this.bannedUsers = bannedUsers;
		this.open = open;
		this.closeWhenOutOfStock = closeWhenOutOfStock;
		this.homeLayout = homeLayout;
		this.categoryLayout = categoryLayout;
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
		if (this.description == null) {
			this.description = new ArrayList<>();
		}
		return this.description;
	}

	@Override
	public @NonNull List<Category> getCategories() {
		if (this.categories == null) {
			this.categories = new ArrayList<>();
		}
		return this.categories;
	}

	@Override
	public @NonNull List<Rating> getRatings() {
		if (this.ratings == null) {
			this.ratings = new ArrayList<>();
		}
		return this.ratings;
	}

	@Override
	public boolean isOpen() {
		return this.open;
	}

	@Override
	public List<UUID> getBannedUsers() {
		if (this.bannedUsers == null) {
			this.bannedUsers = new ArrayList<>();
		}
		return this.bannedUsers;
	}

	@Override
	public boolean isCloseWhenOutOfStock() {
		return this.closeWhenOutOfStock;
	}

	@Override
	public Layout getHomeLayout() {
		return this.homeLayout;
	}

	@Override
	public Layout getCategoryLayout() {
		return this.categoryLayout;
	}

	@Override
	public void setOpen(boolean open) {
		this.open = open;
	}

	@Override
	public void setCloseWhenOutOfStock(boolean closeWhenOutOfStock) {
		this.closeWhenOutOfStock = closeWhenOutOfStock;
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
		// Ensure marketType is never null before saving
		if (this.marketType == null) {
			this.marketType = MarketType.PLAYER;
		}
		
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().createMarket(this, (error, created) -> {
			if (error == null && created != null) {
				stored.accept(created);
			} else {
				// Log error and still call callback with null to indicate failure
				if (error != null) {
					Markets.getInstance().getLogger().severe("Failed to store market for player " + this.ownerName + " (UUID: " + this.ownerUUID + "): " + error.getMessage());
					error.printStackTrace();
				}
				stored.accept(null);
			}
		});
	}

	@Override
	public void sync(@Nullable Consumer<SynchronizeResult> syncResult) {
		// Ensure marketType is never null before saving
		if (this.marketType == null) {
			this.marketType = MarketType.PLAYER;
		}
		
		this.updatedAt = System.currentTimeMillis();
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().updateMarket(this, (error, success) -> {
			if (syncResult != null)
				syncResult.accept(error == null && success ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getMarketRepository().deleteById(this.id, (error, deleted) -> {
			if (deleted != null && deleted) {
				Markets.getMarketManager().remove(this);
			}

			if (syncResult != null)
				syncResult.accept(error == null && deleted != null && deleted ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}
}
