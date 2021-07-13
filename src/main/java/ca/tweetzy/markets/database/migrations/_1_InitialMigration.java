package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.core.database.DataMigration;
import ca.tweetzy.core.database.MySQLConnector;
import ca.tweetzy.markets.Markets;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 12 2021
 * Time Created: 10:03 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class _1_InitialMigration extends DataMigration {

    public _1_InitialMigration() {
        super(1);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        String autoIncrement = Markets.getInstance().getDatabaseConnector() instanceof MySQLConnector ? " AUTO_INCREMENT" : "";

        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "market (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "market_id VARCHAR(32) NOT NULL, " +
                    "owner VARCHAR(32) NOT NULL, " +
                    "name VARCHAR(64) NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "type VARCHAR(12) NOT NULL, " +
                    "open BOOLEAN NOT NULL, " +
                    "created_at BigInt NOT NULL, " +
                    "updated_at BigInt NOT NULL )");

            statement.execute("CREATE TABLE " + tablePrefix + "ratings (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "market_id VARCHAR(32) NOT NULL, " +
                    "rating_id VARCHAR(32) NOT NULL, " +
                    "rater VARCHAR(32) NOT NULL, " +
                    "stars VARCHAR(32) NOT NULL, " +
                    "message TEXT NOT NULL )");

            statement.execute("CREATE TABLE " + tablePrefix + "categories (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "market_id VARCHAR(32) NOT NULL, " +
                    "category_id VARCHAR(32) NOT NULL, " +
                    "name VARCHAR(64) NOT NULL, " +
                    "display_name VARCHAR(92) NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "icon VARCHAR(28) NOT NULL, " +
                    "sale_active BOOLEAN NOT NULL, " +
                    "sale_discount DOUBLE NOT NULL )");

            statement.execute("CREATE TABLE " + tablePrefix + "items (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "item_id VARCHAR(32) NOT NULL, " +
                    "category_id VARCHAR(32) NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "currency_item TEXT NULL, " +
                    "use_item_currency BOOLEAN NOT NULL, " +
                    "price DOUBLE NOT NULL, " +
                    "price_for_stack BOOLEAN NOT NULL )");

            statement.execute("CREATE TABLE " + tablePrefix + "requests (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "request_id VARCHAR(32) NOT NULL, " +
                    "requester VARCHAR(32) NOT NULL, " +
                    "date BigInt NOT NULL )");

            statement.execute("CREATE TABLE " + tablePrefix + "request_item (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "request_id VARCHAR(32) NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "currency TEXT NULL, " +
                    "amount INTEGER NOT NULL, " +
                    "price DOUBLE NOT NULL, " +
                    "fulfilled BOOLEAN NOT NULL, " +
                    "use_custom_currency BOOLEAN NOT NULL )");

            statement.execute("CREATE TABLE " + tablePrefix + "payments (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "is_for VARCHAR(32) NOT NULL, " +
                    "item TEXT NOT NULL )");

            statement.execute("CREATE TABLE " + tablePrefix + "transactions (" +
                    "id INTEGER PRIMARY KEY" + autoIncrement + ", " +
                    "transaction_id VARCHAR(32) NOT NULL, " +
                    "market_id VARCHAR(32) NOT NULL, " +
                    "purchaser VARCHAR(32) NOT NULL, " +
                    "item TEXT NOT NULL, " +
                    "quantity INTEGER NOT NULL, " +
                    "price DOUBLE NOT NULL, " +
                    "time BigInt NOT NULL )");
        }
    }
}
