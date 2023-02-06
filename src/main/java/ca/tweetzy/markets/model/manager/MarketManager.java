package ca.tweetzy.markets.model.manager;

import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.Filterer;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.impl.PlayerMarket;
import ca.tweetzy.markets.impl.layout.HomeLayout;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class MarketManager extends ListManager<Market> {

	public MarketManager() {
		super("Market");
	}

	public List<MarketItem> getSearchResults(@NonNull final Player searcher, @NonNull final String keywords) {
		final List<MarketItem> marketItems = new ArrayList<>();
		final List<Market> possibleSearchMarkets = getOpenMarketsExclusive(searcher).stream().filter(market -> !market.getBannedUsers().contains(searcher.getUniqueId())).toList();


		// populate items into search list
		possibleSearchMarkets.forEach(market -> market.getCategories().forEach(category -> marketItems.addAll(category.getItems())));
		return marketItems.stream().filter(marketItem -> Filterer.searchByItemInfo(keywords, marketItem.getItem())).collect(Collectors.toList());
	}

	public List<MarketItem> getSearchResults(@NonNull final Player searcher, @NonNull final Market market, @NonNull final String keywords) {
		final List<MarketItem> marketItems = new ArrayList<>();
		market.getCategories().forEach(category -> marketItems.addAll(category.getItems()));
		return marketItems.stream().filter(marketItem -> Filterer.searchByItemInfo(keywords, marketItem.getItem())).collect(Collectors.toList());
	}

	public List<MarketItem> getSearchResults(@NonNull final Category category, @NonNull final String keywords) {
		return category.getItems().stream().filter(marketItem -> Filterer.searchByItemInfo(keywords, marketItem.getItem())).collect(Collectors.toList());
	}

	public List<Market> getOpenMarketsExclusive(@NonNull final OfflinePlayer ignoredUser) {
		return getManagerContent().stream().filter(market -> !market.getOwnerUUID().equals(ignoredUser.getUniqueId()) && market.isOpen()).collect(Collectors.toList());
	}

	public List<Market> getOpenMarketsInclusive() {
		return getManagerContent().stream().filter(Market::isOpen).collect(Collectors.toList());
	}

	public Market getByOwner(@NonNull final UUID uuid) {
		return getManagerContent().stream().filter(market -> market.getOwnerUUID().equals(uuid)).findFirst().orElse(null);
	}

	public Market getByOwnerName(@NonNull final String ownerName) {
		return getManagerContent().stream().filter(market -> market.getOwnerName().equalsIgnoreCase(ownerName)).findFirst().orElse(null);
	}

	public Market getByUUID(@NonNull final UUID uuid) {
		return getManagerContent().stream().filter(market -> market.getId().equals(uuid)).findFirst().orElse(null);
	}

	public boolean isBannedFrom(@NonNull final Market market, @NonNull final UUID user) {
		return market.getBannedUsers().contains(user);
	}

	public boolean isBannedFrom(@NonNull final Market market, @NonNull final OfflinePlayer user) {
		return isBannedFrom(market, user.getUniqueId());
	}

	public void create(@NonNull final Player player, @NonNull final Consumer<Boolean> created) {//todo add layout
		final Market market = new PlayerMarket(
				UUID.randomUUID(),
				player.getUniqueId(),
				player.getName(),
				"&e" + player.getName() + "'s Market",
				List.of("&aWelcome to my market"),
				new ArrayList<>(),
				new ArrayList<>(),
				new ArrayList<>(),
				true,
				false,
				new HomeLayout(),
				new HomeLayout(),
				System.currentTimeMillis(),
				System.currentTimeMillis()
		);

		market.store(storedMarket -> {
			if (storedMarket != null) {
				add(storedMarket);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
	}

	@Override
	public void load() {
		clear();

		Markets.getDataManager().getMarkets((error, found) -> {
			if (error != null) return;
			Common.log("&aLoading Markets");
			found.forEach(market -> {
				Markets.getDataManager().getRatingsByMarket(market.getId(), (ratingError, foundRatings) -> {
					if (ratingError != null) return;
					market.getRatings().addAll(foundRatings);
					add(market);
				});
			});

			// after markets have been added let's load categories
			Markets.getCategoryManager().load();
		});
	}
}
