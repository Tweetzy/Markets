package ca.tweetzy.markets.model.manager;

import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.KeyValueManager;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.api.market.core.MarketUser;
import ca.tweetzy.markets.impl.MarketPlayer;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlayerManager extends KeyValueManager<UUID, MarketUser> {

	private final Pattern maximumAllowedItemsPattern = Pattern.compile("markets\\.maxalloweditems\\.(\\d+)");
	private final Pattern maximumAllowedCategoriesPattern = Pattern.compile("markets\\.maxllowedcategories\\.(\\d+)");
	private final Pattern maximumAllowedRequestsPattern = Pattern.compile("markets\\.maxllowedrequests\\.(\\d+)");

	public enum MarketLimitPermission {
		ITEMS,
		CATEGORIES,
		REQUESTS
	}

	public PlayerManager() {
		super("Player");
	}

	public int getMaxLimitOf(@NonNull final Player player, @NonNull final MarketLimitPermission limitPermission) {
		return switch (limitPermission) {
			case ITEMS -> getMaxAllowedMarketItems(player);
			case CATEGORIES -> getMaxAllowedMarketCategories(player);
			case REQUESTS -> getMaxAllowedRequests(player);
		};
	}

	public boolean isAtLimitOf(@NonNull final Player player, @NonNull final MarketLimitPermission limitPermission) {
		return switch (limitPermission) {
			case ITEMS -> isAtMarketItemLimit(player);
			case CATEGORIES -> isAtMarketCategoryLimit(player);
			case REQUESTS -> isAtRequestLimit(player);
		};
	}

	public int getMaxAllowedMarketItems(@NonNull final Player player) {
		int maxAllowedItems = Settings.DEFAULT_MAX_ALLOWED_MARKET_ITEMS.getInt();
		int max = player.getEffectivePermissions().stream().map(i -> {
			final Matcher matcher = maximumAllowedItemsPattern.matcher(i.getPermission());
			if (matcher.matches()) {
				return Integer.parseInt(matcher.group(1));
			}
			return 0;
		}).max(Integer::compareTo).orElse(0);

		if (player.hasPermission("markets.maxalloweditems.*")) {
			maxAllowedItems = Integer.MAX_VALUE;
		}

		if (max > maxAllowedItems) {
			maxAllowedItems = max;
		}

		return maxAllowedItems;
	}

	public boolean isAtMarketItemLimit(@NonNull final Player player) {
		final int maxAllowedItems = getMaxAllowedMarketItems(player);
		final Market playerMarket = Markets.getMarketManager().getByOwner(player.getUniqueId());

		if (playerMarket == null)
			return false;

		int categoryItemTotal = 0;
		for (List<MarketItem> marketItems : playerMarket.getCategories().stream().map(Category::getItems).toList()) {
			categoryItemTotal += marketItems.size();
		}

		return categoryItemTotal >= maxAllowedItems;
	}

	public int getMaxAllowedMarketCategories(@NonNull final Player player) {
		int maxAllowedCategories = Settings.DEFAULT_MAX_ALLOWED_MARKET_CATEGORIES.getIntOr(64);
		int max = player.getEffectivePermissions().stream().map(i -> {
			final Matcher matcher = maximumAllowedCategoriesPattern.matcher(i.getPermission());
			if (matcher.matches()) {
				return Integer.parseInt(matcher.group(1));
			}
			return 0;
		}).max(Integer::compareTo).orElse(0);

		if (player.hasPermission("markets.maxllowedcategories.*")) {
			maxAllowedCategories = Integer.MAX_VALUE;
		}

		if (max > maxAllowedCategories) {
			maxAllowedCategories = max;
		}

		return maxAllowedCategories;
	}

	public boolean isAtMarketCategoryLimit(@NonNull final Player player) {
		final int maxAllowedCategories = getMaxAllowedMarketCategories(player);
		final Market playerMarket = Markets.getMarketManager().getByOwner(player.getUniqueId());

		if (playerMarket == null)
			return false;

		return playerMarket.getCategories().size() >= maxAllowedCategories;
	}

	public int getMaxAllowedRequests(@NonNull final Player player) {
		int maxAllowedRequests = Settings.DEFAULT_MAX_ALLOWED_REQUESTS.getIntOr(64);
		int max = player.getEffectivePermissions().stream().map(i -> {
			final Matcher matcher = maximumAllowedRequestsPattern.matcher(i.getPermission());
			if (matcher.matches()) {
				return Integer.parseInt(matcher.group(1));
			}
			return 0;
		}).max(Integer::compareTo).orElse(0);

		if (player.hasPermission("markets.maxllowedrequests.*")) {
			maxAllowedRequests = Integer.MAX_VALUE;
		}

		if (max > maxAllowedRequests) {
			maxAllowedRequests = max;
		}

		return maxAllowedRequests;
	}

	public boolean isAtRequestLimit(@NonNull final Player player) {
		final int maxAllowedRequests = getMaxAllowedRequests(player);
		return Markets.getRequestManager().getRequestsBy(player.getUniqueId()).size() >= maxAllowedRequests;
	}


	public void create(@NonNull final Player player, @NonNull final Consumer<Boolean> created) {
		final MarketUser marketUser = createRawPlayer(player);

		marketUser.store(storedUser -> {
			if (storedUser != null) {
				add(storedUser.getUUID(), storedUser);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
	}

	public MarketUser createRawPlayer(@NonNull final Player player) {
		return new MarketPlayer(
				player.getUniqueId(),
				player,
				player.getName(),
				List.of("Hi there, welcome to my profile"),
				"english",
				"us",
				System.currentTimeMillis()
		);
	}

	@Override
	public void load() {
		clear();

		Markets.getDataManager().getMarketUsers((error, found) -> {
			if (error != null) return;
			Common.log("&aLoading Players");
			found.forEach(user -> add(user.getUUID(), user));
		});
	}
}
