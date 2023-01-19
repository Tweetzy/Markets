package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _7_MarketSettingsInitialMigration extends DataMigration {

	public _7_MarketSettingsInitialMigration() {
		super(7);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "markets ADD open BOOLEAN DEFAULT 1 NOT NULL;");
			statement.execute("ALTER TABLE " + tablePrefix + "markets ADD close_when_out_of_stock BOOLEAN DEFAULT 0 NOT NULL;");
			statement.execute("ALTER TABLE " + tablePrefix + "markets ADD banned_users TEXT;");
		}
	}
}
