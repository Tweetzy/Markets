package ca.tweetzy.markets.database;

import ca.tweetzy.core.database.DataManagerAbstract;
import ca.tweetzy.core.database.DatabaseConnector;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.MarketRating;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.request.Request;
import ca.tweetzy.markets.request.RequestItem;
import ca.tweetzy.markets.transaction.Payment;
import ca.tweetzy.markets.transaction.Transaction;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
        String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "market SET market_id = ?, owner = ?, name = ?, description = ?, type = ?, open = ?, created_at = ?, updated_at = ?";
        String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "market";

        try (PreparedStatement statement = connection.prepareStatement(truncate)) {
            statement.execute();
        }

        PreparedStatement statement = connection.prepareStatement(saveMarket);
        markets.forEach(market -> {
            try {
                statement.setString(1, market.getId().toString());
                statement.setString(2, market.getOwner().toString());
                statement.setString(3, market.getName());
                statement.setString(4, market.getDescription());
                statement.setString(5, market.getMarketType().name());
                statement.setBoolean(6, market.isOpen());
                statement.setLong(7, market.getCreatedAt());
                statement.setLong(8, market.getUpdatedAt());
                statement.addBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        statement.executeBatch();
    }

    private void saveRating(List<MarketRating> ratings, Connection connection) throws SQLException {
        String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "ratings SET market_id = ?, rating_id = ?, rater = ?, stars = ?, message = ?";
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
                statement.addBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        statement.executeBatch();
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
                statement.setString(1,  request.getId().toString());
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
                statement.setString(1,  requestItem.getRequestId().toString());
                statement.setString(2, Arrays.toString(MarketsAPI.getInstance().serializeItem(requestItem.getItem())));
                statement.setString(3, Arrays.toString(MarketsAPI.getInstance().serializeItem(requestItem.getCurrency())));
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
                statement.setString(1,  payment.getTo().toString());
                statement.setString(2, Arrays.toString(MarketsAPI.getInstance().serializeItem(payment.getItem())));
                statement.addBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        statement.executeBatch();
    }

    private void saveItem(List<MarketItem> items, Connection connection) throws SQLException {
        String saveMarket = "INSERT IGNORE INTO " + this.getTablePrefix() + "items SET item_id = ?, category_id = ?, item = ?, currency_item = ?, use_item_currency = ?, price = ?, price_for_stack = ?";
        String truncate = "TRUNCATE TABLE " + this.getTablePrefix() + "items";

        try (PreparedStatement statement = connection.prepareStatement(truncate)) {
            statement.execute();
        }

        PreparedStatement statement = connection.prepareStatement(saveMarket);
        items.forEach(item -> {
            try {
                statement.setString(1,  item.getId().toString());
                statement.setString(2,  item.getCategoryId().toString());
                statement.setString(3, Arrays.toString(MarketsAPI.getInstance().serializeItem(item.getItemStack())));
                statement.setString(4, Arrays.toString(MarketsAPI.getInstance().serializeItem(item.getCurrencyItem())));
                statement.setBoolean(5, item.isUseItemCurrency());
                statement.setDouble(6, item.getPrice());
                statement.setBoolean(7, item.isPriceForStack());
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
                statement.setString(1,  transaction.getId().toString());
                statement.setString(2,  transaction.getMarketId().toString());
                statement.setString(3,  transaction.getPurchaser().toString());
                statement.setString(4, Arrays.toString(MarketsAPI.getInstance().serializeItem(transaction.getItemStack())));
                statement.setInt(5,  transaction.getPurchaseQty());
                statement.setDouble(6,  transaction.getFinalPrice());
                statement.setLong(7,  transaction.getTime());
                statement.addBatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        statement.executeBatch();
    }

    public void saveMarkets(List<Market> markets, boolean async) {
        if (async) {
            this.async(() -> this.databaseConnector.connect(connection -> saveMarket(markets, connection)));
        } else {
            this.sync(() -> this.databaseConnector.connect(connection -> saveMarket(markets, connection)));
        }
    }

    public void saveRatings(List<MarketRating> ratings, boolean async) {
        if (async) {
            this.async(() -> this.databaseConnector.connect(connection -> saveRating(ratings, connection)));
        } else {
            this.sync(() -> this.databaseConnector.connect(connection -> saveRating(ratings, connection)));
        }
    }

    public void saveCategories(List<MarketCategory> categories, boolean async) {
        if (async) {
            this.async(() -> this.databaseConnector.connect(connection -> saveCategory(categories, connection)));
        } else {
            this.sync(() -> this.databaseConnector.connect(connection -> saveCategory(categories, connection)));
        }
    }

    public void saveRequests(List<Request> requests, boolean async) {
        if (async) {
            this.async(() -> this.databaseConnector.connect(connection -> saveRequest(requests, connection)));
        } else {
            this.sync(() -> this.databaseConnector.connect(connection -> saveRequest(requests, connection)));
        }
    }

    public void saveRequestItems(List<RequestItem> requestItems, boolean async) {
        if (async) {
            this.async(() -> this.databaseConnector.connect(connection -> saveRequestItem(requestItems, connection)));
        } else {
            this.sync(() -> this.databaseConnector.connect(connection -> saveRequestItem(requestItems, connection)));
        }
    }

    public void savePayments(List<Payment> payments, boolean async) {
        if (async) {
            this.async(() -> this.databaseConnector.connect(connection -> savePayment(payments, connection)));
        } else {
            this.sync(() -> this.databaseConnector.connect(connection -> savePayment(payments, connection)));
        }
    }

    public void saveTransactions(List<Transaction> transactions, boolean async) {
        if (async) {
            this.async(() -> this.databaseConnector.connect(connection -> saveTransaction(transactions, connection)));
        } else {
            this.sync(() -> this.databaseConnector.connect(connection -> saveTransaction(transactions, connection)));
        }
    }

    public void saveItems(List<MarketItem> items, boolean async) {
        if (async) {
            this.async(() -> this.databaseConnector.connect(connection -> saveItem(items, connection)));
        } else {
            this.sync(() -> this.databaseConnector.connect(connection -> saveItem(items, connection)));
        }
    }
}
