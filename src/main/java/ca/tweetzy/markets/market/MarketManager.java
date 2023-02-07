package ca.tweetzy.markets.market;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.market.contents.BlockedItem;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 30 2021
 * Time Created: 3:35 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MarketManager {

	private final List<Market> markets = Collections.synchronizedList(new ArrayList<>());
	private final List<BlockedItem> blockedItems = Collections.synchronizedList(new ArrayList<>());

	@Getter
	private final ConcurrentHashMap<UUID, Long> featuredMarkets = new ConcurrentHashMap<>();

	@Setter
	@Getter
	private long feeLastChargedOn = -1L;

	public void addMarket(Market market) {
		Objects.requireNonNull(market, "Cannot add a null Market to market list");
		this.markets.add(market);
	}

	public void addCategoryToMarket(Market market, MarketCategory marketCategory) {
		Objects.requireNonNull(market, "Cannot add a category to a null market");
		Objects.requireNonNull(marketCategory, "Cannot add a null category to a market");
		market.getCategories().add(marketCategory);
	}

	public void addItemToCategory(MarketCategory marketCategory, MarketItem marketItem) {
		Objects.requireNonNull(marketCategory, "Market category cannot be null when adding market item.");
		Objects.requireNonNull(marketItem, "Cannot add a null market item to category");
		marketCategory.getItems().add(marketItem);
	}

	public void deleteMarket(Market market) {
		Objects.requireNonNull(market, "Market cannot be null when deleting market");
		this.markets.removeIf(theMarket -> theMarket.getId().equals(market.getId()));
		this.featuredMarkets.remove(market.getId());
		Markets.newChain().async(() -> {
			Markets.getInstance().getData().set("markets." + market.getId().toString(), null);
			Markets.getInstance().getData().save();
		}).execute();
	}

	public Market getMarketByPlayer(Player player) {
		return this.markets.stream().filter(market -> market.getOwner().equals(player.getUniqueId())).findFirst().orElse(null);
	}

	public Market getMarketByPlayerName(String player) {
		return this.markets.stream().filter(market -> market.getOwnerName().equalsIgnoreCase(player)).findFirst().orElse(null);
	}

	public void addBlockedItem(BlockedItem blockedItem) {
		this.blockedItems.add(blockedItem);
	}

	public void removeBlockedItem(BlockedItem blockedItem) {
		this.blockedItems.remove(blockedItem);
	}

	public Market getMarketById(UUID id) {
		return this.markets.stream().filter(market -> market.getId().equals(id)).findFirst().orElse(null);
	}

	public List<Market> getMarkets() {
		return Collections.unmodifiableList(this.markets);
	}

	public List<Market> getNonEmptyMarkets() {
		final List<Market> markets = new ArrayList<>();


		this.markets.forEach(market -> {
			List<MarketItem> marketItemList = new ArrayList<>();
			market.getCategories().forEach(category -> marketItemList.addAll(category.getItems()));

			if (!marketItemList.isEmpty()) {
				markets.add(market);
				marketItemList.clear();
			}
		});

		return markets;
	}

	public List<BlockedItem> getBlockedItems() {
		return Collections.unmodifiableList(this.blockedItems);
	}

	public void setBlockedItemsOnFile() {
		Markets.getInstance().getData().set("blocked items", null);
		this.blockedItems.forEach(blockedItem -> {
			Markets.getInstance().getData().set("blocked items." + blockedItem.getId().toString(), blockedItem.getItem());
		});
	}

	public void saveBlockedItems() {
		Markets.newChain().sync(() -> {
			Markets.getInstance().getData().set("blocked items", null);
			this.blockedItems.forEach(blockedItem -> {
				Markets.getInstance().getData().set("blocked items." + blockedItem.getId().toString(), blockedItem.getItem());
			});
			Markets.getInstance().getData().save();
		}).execute();
	}

	public void loadBlockedItems() {
		if (Settings.DATABASE_USE.getBoolean()) {
			Markets.getInstance().getDataManager().getBlockedItems(callback -> callback.forEach(this::addBlockedItem));
		} else {
			Markets.newChain().async(() -> {
				ConfigurationSection section = Markets.getInstance().getData().getConfigurationSection("blocked items");
				if (section == null || section.getKeys(false).size() == 0) return;
				Markets.getInstance().getData().getConfigurationSection("blocked items").getKeys(false).forEach(item -> addBlockedItem(new BlockedItem(UUID.fromString(item), Markets.getInstance().getData().getItemStack("blocked items." + item))));
			}).execute();
		}
	}

	public void loadFeaturedMarkets() {
		if (Settings.DATABASE_USE.getBoolean()) {
			Markets.getInstance().getDataManager().getFeaturedMarkets(this.featuredMarkets::putAll);
		} else {
			Markets.newChain().async(() -> {
				ConfigurationSection section = Markets.getInstance().getData().getConfigurationSection("featured markets");
				if (section == null || section.getKeys(false).size() == 0) return;
				Markets.getInstance().getData().getConfigurationSection("featured markets").getKeys(false).forEach(item -> {
					this.featuredMarkets.put(UUID.fromString(item), Markets.getInstance().getData().getLong("featured markets." + item));
				});
			}).execute();
		}
	}

	public void setFeaturedMarketsOnFile() {
		Markets.getInstance().getData().set("featured markets", null);
		this.featuredMarkets.forEach((marketId, expiresAt) -> {
			Markets.getInstance().getData().set("featured markets." + marketId.toString(), expiresAt);
		});
	}

	public void saveFeaturedMarkets() {
		Markets.newChain().sync(() -> {
			Markets.getInstance().getData().set("featured markets", null);
			this.featuredMarkets.forEach((marketId, expiresAt) -> {
				Markets.getInstance().getData().set("featured markets." + marketId.toString(), expiresAt);
			});
			Markets.getInstance().getData().save();
		}).execute();

	}

	public void setMarketsOnFile() {
		for (Market market : this.markets) {
			saveMarket(market);
		}
	}

	public void export() {
		for (Market market : this.markets) {
			exportMarket(market);
		}

		Markets.getInstance().getExport().save();
	}

	public void saveMarkets(Market... markets) {
		Markets.newChain().sync(() -> {
			for (Market market : markets) {
				saveMarket(market);
			}
			Markets.getInstance().getData().save();
		}).execute();
	}

	public void exportMarket(Market market) {
		Objects.requireNonNull(market, "Cannot save a null market");
		String node = "markets." + market.getId().toString();
		Markets.getInstance().getExport().set(node + ".owner", market.getOwner().toString());
		Markets.getInstance().getExport().set(node + ".owner name", market.getOwnerName());
		Markets.getInstance().getExport().set(node + ".name", market.getName());
		Markets.getInstance().getExport().set(node + ".description", market.getDescription());
		Markets.getInstance().getExport().set(node + ".type", market.getMarketType().name());
		Markets.getInstance().getExport().set(node + ".open", market.isOpen());
		Markets.getInstance().getExport().set(node + ".unpaid", market.isUnpaid());
		Markets.getInstance().getExport().set(node + ".created at", Markets.getInstance().getExport().isSet(node + ".created at") ? Markets.getInstance().getExport().get(node + ".created at") : System.currentTimeMillis());
		Markets.getInstance().getExport().set(node + ".updated at", Markets.getInstance().getExport().isSet(node + ".updated at") ? Markets.getInstance().getExport().get(node + ".updated at") : System.currentTimeMillis());

		Markets.getInstance().getExport().set(node + ".categories", null);
		Markets.getInstance().getExport().set(node + ".items", null);

		if (!market.getRatings().isEmpty()) {
			market.getRatings().forEach(rating -> {
				Markets.getInstance().getExport().set(node + ".ratings." + rating.getId().toString() + ".rater", rating.getRater().toString());
				Markets.getInstance().getExport().set(node + ".ratings." + rating.getId().toString() + ".stars", rating.getStars());
				Markets.getInstance().getExport().set(node + ".ratings." + rating.getId().toString() + ".message", rating.getMessage());
				Markets.getInstance().getExport().set(node + ".ratings." + rating.getId().toString() + ".time", rating.getTime());
			});
		}

		if (!market.getCategories().isEmpty()) {
			market.getCategories().forEach(category -> {
				Markets.getInstance().getExport().set(node + ".categories." + category.getId().toString() + ".name", category.getName());
				Markets.getInstance().getExport().set(node + ".categories." + category.getId().toString() + ".display name", category.getDisplayName());
				Markets.getInstance().getExport().set(node + ".categories." + category.getId().toString() + ".description", category.getDescription());
				Markets.getInstance().getExport().set(node + ".categories." + category.getId().toString() + ".icon", category.getIcon().name());
				Markets.getInstance().getExport().set(node + ".categories." + category.getId().toString() + ".sale.active", category.isSaleActive());
				Markets.getInstance().getExport().set(node + ".categories." + category.getId().toString() + ".sale.amount", category.getSaleDiscount());

				category.getItems().forEach(item -> {
					if (item != null) {
						Markets.getInstance().getExport().set(node + ".items." + item.getId().toString() + ".category", category.getId().toString());
						Markets.getInstance().getExport().set(node + ".items." + item.getId().toString() + ".item", item.getItemStack());
						Markets.getInstance().getExport().set(node + ".items." + item.getId().toString() + ".price", item.getPrice());
						Markets.getInstance().getExport().set(node + ".items." + item.getId().toString() + ".price for stack", item.isPriceForStack());
						Markets.getInstance().getExport().set(node + ".items." + item.getId().toString() + ".currency item", item.getCurrencyItem());
						Markets.getInstance().getExport().set(node + ".items." + item.getId().toString() + ".use item currency", item.isUseItemCurrency());
						Markets.getInstance().getExport().set(node + ".items." + item.getId().toString() + ".infinite", item.isInfinite());
					}
				});

			});
		}
	}

	public void saveMarket(Market market) {
		Objects.requireNonNull(market, "Cannot save a null market");
		String node = "markets." + market.getId().toString();
		Markets.getInstance().getData().set(node + ".owner", market.getOwner().toString());
		Markets.getInstance().getData().set(node + ".owner name", market.getOwnerName());
		Markets.getInstance().getData().set(node + ".name", market.getName());
		Markets.getInstance().getData().set(node + ".description", market.getDescription());
		Markets.getInstance().getData().set(node + ".type", market.getMarketType().name());
		Markets.getInstance().getData().set(node + ".open", market.isOpen());
		Markets.getInstance().getData().set(node + ".unpaid", market.isUnpaid());
		Markets.getInstance().getData().set(node + ".created at", Markets.getInstance().getData().isSet(node + ".created at") ? Markets.getInstance().getData().get(node + ".created at") : System.currentTimeMillis());
		Markets.getInstance().getData().set(node + ".updated at", Markets.getInstance().getData().isSet(node + ".updated at") ? Markets.getInstance().getData().get(node + ".updated at") : System.currentTimeMillis());

		Markets.getInstance().getData().set(node + ".categories", null);
		Markets.getInstance().getData().set(node + ".items", null);

		if (!market.getRatings().isEmpty()) {
			market.getRatings().forEach(rating -> {
				Markets.getInstance().getData().set(node + ".ratings." + rating.getId().toString() + ".rater", rating.getRater().toString());
				Markets.getInstance().getData().set(node + ".ratings." + rating.getId().toString() + ".stars", rating.getStars());
				Markets.getInstance().getData().set(node + ".ratings." + rating.getId().toString() + ".message", rating.getMessage());
				Markets.getInstance().getData().set(node + ".ratings." + rating.getId().toString() + ".time", rating.getTime());
			});
		}

		if (!market.getCategories().isEmpty()) {
			market.getCategories().forEach(category -> {
				Markets.getInstance().getData().set(node + ".categories." + category.getId().toString() + ".name", category.getName());
				Markets.getInstance().getData().set(node + ".categories." + category.getId().toString() + ".display name", category.getDisplayName());
				Markets.getInstance().getData().set(node + ".categories." + category.getId().toString() + ".description", category.getDescription());
				Markets.getInstance().getData().set(node + ".categories." + category.getId().toString() + ".icon", category.getIcon().name());
				Markets.getInstance().getData().set(node + ".categories." + category.getId().toString() + ".sale.active", category.isSaleActive());
				Markets.getInstance().getData().set(node + ".categories." + category.getId().toString() + ".sale.amount", category.getSaleDiscount());

				category.getItems().forEach(item -> {
					if (item != null) {
						Markets.getInstance().getData().set(node + ".items." + item.getId().toString() + ".category", category.getId().toString());
						Markets.getInstance().getData().set(node + ".items." + item.getId().toString() + ".item", item.getItemStack());
						Markets.getInstance().getData().set(node + ".items." + item.getId().toString() + ".price", item.getPrice());
						Markets.getInstance().getData().set(node + ".items." + item.getId().toString() + ".price for stack", item.isPriceForStack());
						Markets.getInstance().getData().set(node + ".items." + item.getId().toString() + ".currency item", item.getCurrencyItem());
						Markets.getInstance().getData().set(node + ".items." + item.getId().toString() + ".use item currency", item.isUseItemCurrency());
						Markets.getInstance().getData().set(node + ".items." + item.getId().toString() + ".infinite", item.isInfinite());
					}
				});

			});
		}
	}

	public void loadMarkets() {
		if (Settings.DATABASE_USE.getBoolean()) {
			Markets.getInstance().getDataManager().getMarkets(callback -> {
				callback.forEach(this::addMarket);
				Markets.getInstance().getDataManager().getCategories(categories -> categories.forEach(category -> {
					this.markets.stream().filter(market -> market.getId().equals(category.getMarketId())).findFirst().get().getCategories().add(category);
				}));

				Markets.getInstance().getDataManager().getRatings(ratings -> ratings.forEach(rating -> {
					this.markets.stream().filter(market -> market.getId().equals(rating.getMarketId())).findFirst().get().getRatings().add(rating);
				}));
			});

			Markets.getInstance().getDataManager().getUpKeepPaidDate(callback -> this.feeLastChargedOn = callback);
		} else {
			Markets.newChain().async(() -> {
				if (Markets.getInstance().getData().isSet("up keep")) {
					this.feeLastChargedOn = Markets.getInstance().getData().getLong("up keep");
				}

				ConfigurationSection section = Markets.getInstance().getData().getConfigurationSection("markets");
				if (section == null || section.getKeys(false).size() == 0) return;

				Markets.getInstance().getData().getConfigurationSection("markets").getKeys(false).forEach(marketId -> {
					Market market = new Market(UUID.fromString(marketId), UUID.fromString(Markets.getInstance().getData().getString("markets." + marketId + ".owner")), Markets.getInstance().getData().getString("markets." + marketId + ".owner name"), Markets.getInstance().getData().getString("markets." + marketId + ".name"), MarketType.valueOf(Markets.getInstance().getData().getString("markets." + marketId + ".type")));
					market.setCreatedAt(Markets.getInstance().getData().getLong("markets." + marketId + ".created at"));
					market.setUpdatedAt(Markets.getInstance().getData().getLong("markets." + marketId + ".updated at"));
					market.setOpen(Markets.getInstance().getData().getBoolean("markets." + marketId + ".open"));
					market.setUnpaid(Markets.getInstance().getData().getBoolean("markets." + marketId + ".unpaid"));
					market.setDescription(Markets.getInstance().getData().getString("markets." + marketId + ".description"));

					ConfigurationSection categories = Markets.getInstance().getData().getConfigurationSection("markets." + marketId + ".categories");
					ConfigurationSection items = Markets.getInstance().getData().getConfigurationSection("markets." + marketId + ".items");
					ConfigurationSection ratings = Markets.getInstance().getData().getConfigurationSection("markets." + marketId + ".ratings");

					if (ratings != null && ratings.getKeys(false).size() != 0) {
						List<MarketRating> marketRatings = new ArrayList<>();
						Markets.getInstance().getData().getConfigurationSection("markets." + marketId + ".ratings").getKeys(false).forEach(rating -> {
							MarketRating marketRating = new MarketRating(
									UUID.fromString(rating),
									UUID.fromString(Markets.getInstance().getData().getString("markets." + marketId + ".ratings." + rating + ".rater")),
									Markets.getInstance().getData().getInt("markets." + marketId + ".ratings." + rating + ".stars"),
									Markets.getInstance().getData().getString("markets." + marketId + ".ratings." + rating + ".message"),
									Markets.getInstance().getData().getLong("markets." + marketId + ".ratings." + rating + ".time")
							);

							marketRating.setMarketId(market.getId());
							marketRatings.add(marketRating);
						});

						market.setRatings(marketRatings);
					}

					if (categories != null && categories.getKeys(false).size() != 0) {
						List<MarketItem> marketItems = new ArrayList<>();
						if (items != null && items.getKeys(false).size() != 0) {
							Markets.getInstance().getData().getConfigurationSection("markets." + marketId + ".items").getKeys(false).forEach(marketItem -> {
								MarketItem itemToAdd = new MarketItem(
										UUID.fromString(marketItem),
										Markets.getInstance().getData().getItemStack("markets." + marketId + ".items." + marketItem + ".item"),
										Markets.getInstance().getData().getItemStack("markets." + marketId + ".items." + marketItem + ".currency item"),
										Markets.getInstance().getData().getDouble("markets." + marketId + ".items." + marketItem + ".price"),
										Markets.getInstance().getData().getBoolean("markets." + marketId + ".items." + marketItem + ".use item currency"),
										Markets.getInstance().getData().getBoolean("markets." + marketId + ".items." + marketItem + ".price for stack"),
										UUID.fromString(Markets.getInstance().getData().getString("markets." + marketId + ".items." + marketItem + ".category"))
								);

								itemToAdd.setInfinite(Markets.getInstance().getData().getBoolean("markets." + marketId + ".items." + marketItem + ".infinite", false));

								marketItems.add(itemToAdd);
							});
						}

						List<MarketCategory> marketCategories = new ArrayList<>();
						Markets.getInstance().getData().getConfigurationSection("markets." + marketId + ".categories").getKeys(false).forEach(categoryNode -> {
							MarketCategory marketCategory = new MarketCategory(
									UUID.fromString(categoryNode),
									Markets.getInstance().getData().getString("markets." + marketId + ".categories." + categoryNode + ".name"),
									Markets.getInstance().getData().getString("markets." + marketId + ".categories." + categoryNode + ".display name"),
									Markets.getInstance().getData().getString("markets." + marketId + ".categories." + categoryNode + ".description"),
									XMaterial.matchXMaterial(Markets.getInstance().getData().getString("markets." + marketId + ".categories." + categoryNode + ".icon")).get().parseItem(),
									marketItems.stream().filter(item -> item.getCategoryId().equals(UUID.fromString(categoryNode))).collect(Collectors.toList()),
									Markets.getInstance().getData().getBoolean("markets." + marketId + ".categories." + categoryNode + ".sale.active"),
									Markets.getInstance().getData().getDouble("markets." + marketId + ".categories." + categoryNode + ".sale.amount")
							);

							marketCategory.setMarketId(market.getId());
							marketCategories.add(marketCategory);
						});
						market.setCategories(marketCategories);
					}

					addMarket(market);
				});
			}).execute();
		}
	}
}
