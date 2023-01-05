package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _4_CategoryItemCurrencyMigration extends DataMigration {

	public _4_CategoryItemCurrencyMigration() {
		super(4);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			// user profile
			statement.execute("ALTER TABLE " + tablePrefix + "category_item ADD currency_item TEXT;");
		}
	}
}
