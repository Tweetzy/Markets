package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.database.annotations.Column;
import ca.tweetzy.flight.database.annotations.Id;
import ca.tweetzy.flight.database.annotations.Ignore;
import ca.tweetzy.flight.database.annotations.Nested;
import ca.tweetzy.flight.database.annotations.Table;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.MarketItem;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Table("category")
public final class MarketCategory implements Category {

	@Id
	@Column("id")
	private UUID id;
	
	@Column("name")
	private String name;
	
	@Column("owning_market")
	private UUID owningMarket;
	
	@Nested
	@Column("icon")
	private ItemStack icon;
	
	@Column("display_name")
	private String displayName;
	
	@Nested
	@Column("description")
	private List<String> description;
	
	@Ignore
	private List<MarketItem> items;

	@Column("created_at")
	private long createdAt;
	
	@Column("updated_at")
	private long updatedAt;

	@Ignore
	private List<Player> viewingUsers;

	public MarketCategory() {
		this.viewingUsers = new ArrayList<>();
	}

	public MarketCategory(
			@NonNull final UUID owningMarket,
			@NonNull final UUID id,
			@NonNull final ItemStack icon,
			@NonNull final String name,
			@NonNull final String displayName,
			@NonNull final List<String> description,
			@NonNull final List<MarketItem> items,
			final long createdAt,
			final long updatedAt
	) {
		this.id = id;
		this.name = name;
		this.owningMarket = owningMarket;
		this.icon = icon;
		this.displayName = displayName;
		this.description = description;
		this.items = items;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.viewingUsers = new ArrayList<>();
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

	@NotNull
	@Override
	public ItemStack getIcon() {
		return this.icon;
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

	@NotNull
	@Override
	public List<MarketItem> getItems() {
		if (this.items == null) {
			this.items = new ArrayList<>();
		}
		return this.items;
	}

	@Override
	public void setIcon(@NotNull ItemStack icon) {
		this.icon = icon;
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
	public void setItems(@NotNull List<MarketItem> items) {
		this.items = items;
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
	public List<Player> getViewingPlayers() {
		if (this.viewingUsers == null) {
			this.viewingUsers = new ArrayList<>();
		}
		return this.viewingUsers;
	}

	@Override
	public void store(@NonNull Consumer<Category> stored) {
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().createCategory(this, (error, created) -> {
			if (error == null && created != null)
				stored.accept(created);
			else if (error != null)
				stored.accept(null);
		});
	}

	@Override
	public void sync(@Nullable Consumer<SynchronizeResult> syncResult) {
		this.updatedAt = System.currentTimeMillis();
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().updateCategory(this, (error, success) -> {
			if (syncResult != null)
				syncResult.accept(error == null && success ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getCategoryRepository().deleteById(this.id, (error, deleted) -> {
			if (deleted != null && deleted) {
				Markets.getMarketManager().getByUUID(this.owningMarket).getCategories().removeIf(category -> category.getId().equals(this.id));
				Markets.getCategoryManager().remove(this);
			}

			if (syncResult != null)
				syncResult.accept(error == null && deleted != null && deleted ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}
}
