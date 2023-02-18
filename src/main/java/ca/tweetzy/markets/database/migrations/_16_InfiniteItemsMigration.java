package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _16_InfiniteItemsMigration extends DataMigration {

	public _16_InfiniteItemsMigration() {
		super(16);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "category_item ADD infinite BOOLEAN NOT NULL default 'false';");

		}
	}
}
