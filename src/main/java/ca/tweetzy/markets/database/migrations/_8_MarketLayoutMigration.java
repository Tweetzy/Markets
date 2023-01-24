package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _8_MarketLayoutMigration extends DataMigration {

	public _8_MarketLayoutMigration() {
		super(8);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "markets ADD home_layout TEXT;");
			statement.execute("ALTER TABLE " + tablePrefix + "markets ADD category_layout TEXT;");
		}
	}
}
