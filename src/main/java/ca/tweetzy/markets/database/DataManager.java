package ca.tweetzy.markets.database;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.database.DataManagerAbstract;
import ca.tweetzy.core.database.DatabaseConnector;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.economy.Currency;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.MarketRating;
import ca.tweetzy.markets.market.MarketType;
import ca.tweetzy.markets.market.contents.BlockedItem;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.request.Request;
import ca.tweetzy.markets.request.RequestItem;
import ca.tweetzy.markets.structures.Triple;
import ca.tweetzy.markets.transaction.Payment;
import ca.tweetzy.markets.transaction.Transaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 12 2021
 * Time Created: 10:02 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class DataManager extends DataManagerAbstract {

	public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
		super(databaseConnector, plugin);
	}

	private void saveMarket(List<Market> markets, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "market SET market_id = ?, owner = ?, owner_name = ?, name = ?, description = ?, type = ?, open = ?, created_at = ?, updated_at = ?, unpaid = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "market";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		markets.forEach(market -> {
			try {
				statement.setString(1, market.getId().toString());
				statement.setString(2, market.getOwner().toString());
				statement.setString(3, market.getOwnerName());
				statement.setString(4, market.getName());
				statement.setString(5, market.getDescription());
				statement.setString(6, market.getMarketType().name());
				statement.setBoolean(7, market.isOpen());
				statement.setLong(8, market.getCreatedAt());
				statement.setLong(9, market.getUpdatedAt());
				statement.setBoolean(10, market.isUnpaid());
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		statement.executeBatch();
	}

	private void saveBlockedItem(List<BlockedItem> items, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "blocked_items SET blocked_id = ?, item = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "blocked_items";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		items.forEach(item -> {
			try {
				statement.setString(1, item.getId().toString());
				statement.setString(2, MarketsAPI.getInstance().convertToBase64(new DatabaseItem(item.getItem())));
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		statement.executeBatch();
	}

	public void saveBank(HashMap<UUID, ArrayList<Currency>> banks, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "bank SET owner_id = ?, item = ?, total = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "bank";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		banks.entrySet().forEach(entry -> entry.getValue().forEach(currency -> {
			try {
				statement.setString(1, entry.getKey().toString());
				statement.setString(2, MarketsAPI.getInstance().convertToBase64(new DatabaseItem(currency.getItem())));
				statement.setInt(3, currency.getAmount());
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}));

		statement.executeBatch();
	}

	private void saveRating(List<MarketRating> ratings, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "ratings SET market_id = ?, rating_id = ?, rater = ?, stars = ?, message = ?, time = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "ratings";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		ratings.forEach(rating -> {
			try {
				statement.setString(1, rating.getMarketId().toString());
				statement.setString(2, rating.getId().toString());
				statement.setString(3, rating.getRater().toString());
				statement.setInt(4, rating.getStars());
				statement.setString(5, rating.getMessage());
				statement.setLong(6, rating.getTime());
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		statement.executeBatch();
	}

	private void saveUpkeepDate(long lastPaid, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "upkeep SET last_paid = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "upkeep";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		try {
			statement.setLong(1, lastPaid);
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void saveCategory(List<MarketCategory> categories, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "categories SET market_id = ?, category_id = ?, name = ?, display_name = ?, description = ?, icon = ?, sale_active = ?, sale_discount = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "categories";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		categories.forEach(category -> {
			try {
				statement.setString(1, category.getMarketId().toString());
				statement.setString(2, category.getId().toString());
				statement.setString(3, category.getName());
				statement.setString(4, category.getDisplayName());
				statement.setString(5, category.getDescription());
				statement.setString(6, category.getIcon().name());
				statement.setBoolean(7, category.isSaleActive());
				statement.setDouble(8, category.getSaleDiscount());
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		statement.executeBatch();
	}

	private void saveRequest(List<Request> requests, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "requests SET request_id = ?, requester = ?, date = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "requests";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		requests.forEach(request -> {
			try {
				statement.setString(1, request.getId().toString());
				statement.setString(2, request.getRequester().toString());
				statement.setLong(3, request.getDate());
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		statement.executeBatch();
	}

	private void saveRequestItem(List<RequestItem> requestItems, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "request_item SET request_id = ?, item = ?, currency = ?, amount = ?, price = ?, fulfilled = ?, use_custom_currency = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "request_item";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		requestItems.forEach(requestItem -> {
			try {
				statement.setString(1, requestItem.getRequestId().toString());
				statement.setString(2, MarketsAPI.getInstance().convertToBase64(new DatabaseItem(requestItem.getItem())));
				statement.setString(3, MarketsAPI.getInstance().convertToBase64(new DatabaseItem(requestItem.getCurrency())));
				statement.setInt(4, requestItem.getAmount());
				statement.setDouble(5, requestItem.getPrice());
				statement.setBoolean(6, requestItem.isFulfilled());
				statement.setBoolean(7, requestItem.isUseCustomCurrency());
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		statement.executeBatch();
	}

	private void savePayment(List<Payment> payments, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "payments SET is_for = ?, item = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "payments";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		payments.forEach(payment -> {
			try {
				statement.setString(1, payment.getTo().toString());
				statement.setString(2, MarketsAPI.getInstance().convertToBase64(new DatabaseItem(payment.getItem())));
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		statement.executeBatch();
	}

	private void saveFeaturedMarket(Map<UUID, Long> featuredMarkets, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "featured_markets SET id = ?, expires_at = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "featured_markets";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		featuredMarkets.forEach((marketId, expiresAt) -> {
			try {
				statement.setString(1, marketId.toString());
				statement.setLong(2, expiresAt);
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		statement.executeBatch();
	}

	private void saveItem(List<MarketItem> items, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "items SET item_id = ?, category_id = ?, item = ?, currency_item = ?, use_item_currency = ?, price = ?, price_for_stack = ?, infinite = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "items";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		items.forEach(item -> {
			try {
				statement.setString(1, item.getId().toString());
				statement.setString(2, item.getCategoryId().toString());
				statement.setString(3, MarketsAPI.getInstance().convertToBase64(new DatabaseItem(item.getItemStack())));
				statement.setString(4, MarketsAPI.getInstance().convertToBase64(new DatabaseItem(item.getCurrencyItem())));
				statement.setBoolean(5, item.isUseItemCurrency());
				statement.setDouble(6, item.getPrice());
				statement.setBoolean(7, item.isPriceForStack());
				statement.setBoolean(8, item.isInfinite());
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		statement.executeBatch();
	}

	private void saveTransaction(List<Transaction> transactions, Connection connection) throws SQLException {
		String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "transactions SET transaction_id = ?, market_id = ?, purchaser = ?, item = ?, quantity = ?, price = ?, time = ?";
		String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "transactions";

		try (PreparedStatement statement = connection.prepareStatement(truncate)) {
			statement.execute();
		}

		PreparedStatement statement = connection.prepareStatement(saveMarket);
		transactions.forEach(transaction -> {
			try {
				statement.setString(1, transaction.getId().toString());
				statement.setString(2, transaction.getMarketId().toString());
				statement.setString(3, transaction.getPurchaser().toString());
				statement.setString(4, MarketsAPI.getInstance().convertToBase64(new DatabaseItem(transaction.getItemStack())));
				statement.setInt(5, transaction.getPurchaseQty());
				statement.setDouble(6, transaction.getFinalPrice());
				statement.setLong(7, transaction.getTime());
				statement.addBatch();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		statement.executeBatch();
	}

	public void saveBanks(HashMap<UUID, ArrayList<Currency>> banks, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						saveBank(banks, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				saveBank(banks, connection);
			}
		});
	}

	public void getBanks(Consumer<List<Triple<UUID, ItemStack, Integer>>> callback) {
		List<Triple<UUID, ItemStack, Integer>> banks = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			String select = "SELECT * FROM " + this.getTablePrefix() + "bank";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					banks.add(new Triple<>(
							UUID.fromString(result.getString("owner_id")),
							((DatabaseItem) MarketsAPI.getInstance().convertBase64ToObject(result.getString("item"))).getItem(),
							result.getInt("total")
					));
				}
			}

			this.sync(() -> callback.accept(banks));
		}));
	}

	public void saveBlockedItems(List<BlockedItem> items, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						saveBlockedItem(items, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				saveBlockedItem(items, connection);
			}
		});
	}

	public void getBlockedItems(Consumer<List<BlockedItem>> callback) {
		List<BlockedItem> blockedItems = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			String select = "SELECT * FROM " + this.getTablePrefix() + "blocked_items";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					blockedItems.add(new BlockedItem(
							UUID.fromString(result.getString("blocked_id")),
							((DatabaseItem) MarketsAPI.getInstance().convertBase64ToObject(result.getString("item"))).getItem()
					));
				}
			}

			this.sync(() -> callback.accept(blockedItems));
		}));
	}

	public void saveMarkets(List<Market> markets, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						saveMarket(markets, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				saveMarket(markets, connection);
			}
		});
	}

	public void getMarkets(Consumer<List<Market>> callback) {
		List<Market> markets = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			String select = "SELECT * FROM " + this.getTablePrefix() + "market";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					Market market = new Market(
							UUID.fromString(result.getString("market_id")),
							UUID.fromString(result.getString("owner")),
							result.getString("owner_name"),
							result.getString("name"),
							MarketType.valueOf(result.getString("type"))
					);

					market.setDescription(result.getString("description"));
					market.setCreatedAt(result.getLong("created_at"));
					market.setUpdatedAt(result.getLong("updated_at"));
					market.setOpen(result.getBoolean("open"));
					market.setUnpaid(result.getBoolean("unpaid"));

					markets.add(market);
				}
			}

			this.sync(() -> callback.accept(markets));
		}));
	}

	public void saveFeaturedMarkets(Map<UUID, Long> featuredMarkets, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						saveFeaturedMarket(featuredMarkets, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				saveFeaturedMarket(featuredMarkets, connection);
			}
		});
	}

	public void getFeaturedMarkets(Consumer<Map<UUID, Long>> callback) {
		this.async(() -> this.databaseConnector.connect(connection -> {
			HashMap<UUID, Long> map = new HashMap();
			String select = "SELECT * FROM " + this.getTablePrefix() + "featured_markets";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					map.put(UUID.fromString(result.getString("id")), result.getLong("expires_at"));
				}
			}
			this.sync(() -> callback.accept(map));
		}));
	}

	public void saveUpKeeps(long lastPaid, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						saveUpkeepDate(lastPaid, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				saveUpkeepDate(lastPaid, connection);
			}
		});
	}

	public void getUpKeepPaidDate(Consumer<Long> callback) {
		this.async(() -> this.databaseConnector.connect(connection -> {
			Long date = null;
			String select = "SELECT * FROM " + this.getTablePrefix() + "upkeep";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					if (hasColumn(result, "last_paid"))
						date = result.getLong("last_paid");
					else date = -1L;
				}
			}
			Long finalDate = date;
			this.sync(() -> callback.accept(finalDate));
		}));
	}

	public void saveRatings(List<MarketRating> ratings, boolean async) {
		if (async) {
			this.async(() -> this.databaseConnector.connect(connection -> saveRating(ratings, connection)));
		} else {
			this.databaseConnector.connect(connection -> saveRating(ratings, connection));
		}
	}

	public void getRatings(Consumer<List<MarketRating>> callback) {
		List<MarketRating> ratings = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			String select = "SELECT * FROM " + this.getTablePrefix() + "ratings";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					MarketRating rating = new MarketRating(
							UUID.fromString(result.getString("rating_id")),
							UUID.fromString(result.getString("rater")),
							result.getInt("stars"),
							result.getString("message"),
							result.getLong("time")
					);
					rating.setMarketId(UUID.fromString(result.getString("market_id")));
					ratings.add(rating);
				}
			}

			this.sync(() -> callback.accept(ratings));
		}));
	}

	public void saveCategories(List<MarketCategory> categories, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						saveCategory(categories, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				saveCategory(categories, connection);
			}
		});
	}

	public void getCategories(Consumer<List<MarketCategory>> callback) {
		List<MarketCategory> categories = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			String select = "SELECT * FROM " + this.getTablePrefix() + "categories";
			String selectItems = "SELECT * FROM " + this.getTablePrefix() + "items";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					MarketCategory marketCategory = new MarketCategory(
							UUID.fromString(result.getString("category_id")),
							result.getString("name"),
							result.getString("display_name"),
							result.getString("description"),
							Objects.requireNonNull(XMaterial.matchXMaterial(result.getString("icon")).orElse(XMaterial.CHEST).parseItem()),
							new ArrayList<>(),
							result.getBoolean("sale_active"),
							result.getDouble("sale_discount")
					);
					marketCategory.setMarketId(UUID.fromString(result.getString("market_id")));
					categories.add(marketCategory);
				}

				ResultSet itemResult = statement.executeQuery(selectItems);
				while (itemResult.next()) {
					MarketItem marketItem = new MarketItem(
							UUID.fromString(itemResult.getString("item_id")),
							((DatabaseItem) MarketsAPI.getInstance().convertBase64ToObject(itemResult.getString("item"))).getItem(),
							((DatabaseItem) MarketsAPI.getInstance().convertBase64ToObject(itemResult.getString("currency_item"))).getItem(),
							itemResult.getDouble("price"),
							itemResult.getBoolean("use_item_currency"),
							itemResult.getBoolean("price_for_stack"),
							UUID.fromString(itemResult.getString("category_id"))
					);

					if (hasColumn(itemResult, "infinite"))
						marketItem.setInfinite(itemResult.getBoolean("infinite"));

					categories.stream().filter(category -> category.getId().equals(marketItem.getCategoryId())).findFirst().get().getItems().add(marketItem);
				}
			}

			this.sync(() -> callback.accept(categories));
		}));
	}

	public void saveRequests(List<Request> requests, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						saveRequest(requests, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				saveRequest(requests, connection);
			}
		});
	}

	public void saveRequestItems(List<RequestItem> requestItems, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						saveRequestItem(requestItems, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				saveRequestItem(requestItems, connection);
			}
		});
	}

	public void getRequests(Consumer<List<Request>> callback) {
		List<Request> requests = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			String select = "SELECT * FROM " + this.getTablePrefix() + "requests";
			String selectItems = "SELECT * FROM " + this.getTablePrefix() + "request_item";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					requests.add(new Request(
							UUID.fromString(result.getString("request_id")),
							UUID.fromString(result.getString("requester")),
							result.getLong("date"),
							new ArrayList<>()
					));
				}

				ResultSet itemResult = statement.executeQuery(selectItems);
				while (itemResult.next()) {
					RequestItem requestItem = new RequestItem(
							UUID.fromString(itemResult.getString("request_id")),
							((DatabaseItem) MarketsAPI.getInstance().convertBase64ToObject(itemResult.getString("item"))).getItem(),
							((DatabaseItem) MarketsAPI.getInstance().convertBase64ToObject(itemResult.getString("currency"))).getItem(),
							itemResult.getInt("amount"),
							itemResult.getDouble("price"),
							itemResult.getBoolean("fulfilled"),
							itemResult.getBoolean("use_custom_currency")
					);

					requests.stream().filter(request -> request.getId().equals(requestItem.getRequestId())).findFirst().get().getRequestedItems().add(requestItem);
				}
			}

			this.sync(() -> callback.accept(requests));
		}));
	}

	public void savePayments(List<Payment> payments, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						savePayment(payments, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				savePayment(payments, connection);
			}
		});
	}

	public void getPayments(Consumer<List<Payment>> callback) {
		List<Payment> payments = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			String select = "SELECT * FROM " + this.getTablePrefix() + "payments";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					payments.add(new Payment(
							UUID.fromString(result.getString("is_for")),
							((DatabaseItem) MarketsAPI.getInstance().convertBase64ToObject(result.getString("item"))).getItem()
					));
				}
			}

			this.sync(() -> callback.accept(payments));
		}));
	}

	public void saveTransactions(List<Transaction> transactions, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						saveTransaction(transactions, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				saveTransaction(transactions, connection);
			}
		});
	}

	public void getTransactions(Consumer<List<Transaction>> callback) {
		List<Transaction> transactions = new ArrayList<>();
		this.async(() -> this.databaseConnector.connect(connection -> {
			String select = "SELECT * FROM " + this.getTablePrefix() + "transactions";

			try (Statement statement = connection.createStatement()) {
				ResultSet result = statement.executeQuery(select);
				while (result.next()) {
					Transaction transaction = new Transaction(
							UUID.fromString(result.getString("transaction_id")),
							UUID.fromString(result.getString("market_id")),
							UUID.fromString(result.getString("purchaser")),
							((DatabaseItem) MarketsAPI.getInstance().convertBase64ToObject(result.getString("item"))).getItem(),
							result.getInt("quantity"),
							result.getDouble("price")
					);

					transaction.setTime(result.getLong("time"));
					transactions.add(transaction);
				}
			}

			this.sync(() -> callback.accept(transactions));
		}));
	}

	public void saveItems(List<MarketItem> items, boolean async) {
		this.databaseConnector.connect(connection -> {
			if (async) {
				Markets.newChain().async(() -> {
					try {
						saveItem(items, connection);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}).execute();
			} else {
				saveItem(items, connection);
			}
		});
	}

	private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columns = rsmd.getColumnCount();
		for (int x = 1; x <= columns; x++) {
			if (columnName.equals(rsmd.getColumnName(x))) {
				return true;
			}
		}
		return false;
	}
}
