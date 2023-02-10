package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _3_CategoryItemMigration extends DataMigration {

	public _3_CategoryItemMigration() {
		super(3);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			// user profile
			statement.execute("CREATE TABLE " + tablePrefix + "category_item (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"owning_category VARCHAR(36) NOT NULL, " +
					"item TEXT NOT NULL, " +
					"currency VARCHAR(72) NOT NULL, " +
					"price DOUBLE NOT NULL, " +
					"stock INT NOT NULL, " +
					"price_is_for_all BOOLEAN NOT NULL " +
					")");

		}
	}
}
