package ca.tweetzy.markets.impl;

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

public final class MarketCategory implements Category {

	private final UUID id;
	private final String name;
	private final UUID owningMarket;
	private ItemStack icon;
	private String displayName;
	private List<String> description;
	private List<MarketItem> items;

	private final long createdAt;
	private long updatedAt;

	private final List<Player> viewingUsers;

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
		return this.description;
	}

	@NotNull
	@Override
	public List<MarketItem> getItems() {
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
		return this.viewingUsers;
	}

	@Override
	public void store(@NonNull Consumer<Category> stored) {
		Markets.getDataManager().createCategory(this, (error, created) -> {
			if (error == null)
				stored.accept(created);
		});
	}

	@Override
	public void sync(@Nullable Consumer<SynchronizeResult> syncResult) {
		this.updatedAt = System.currentTimeMillis();
		Markets.getDataManager().updateCategory(this, (error, updateStatus) -> {
			if (syncResult != null)
				syncResult.accept(error == null ? updateStatus ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE : SynchronizeResult.FAILURE);
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getDataManager().deleteCategory(this, (error, updateStatus) -> {
			if (updateStatus) {
				Markets.getMarketManager().getByUUID(this.owningMarket).getCategories().removeIf(category -> category.getId().equals(this.id));
				Markets.getCategoryManager().remove(this);
			}

			if (syncResult != null)
				syncResult.accept(error == null ? updateStatus ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE : SynchronizeResult.FAILURE);
		});
	}
}
