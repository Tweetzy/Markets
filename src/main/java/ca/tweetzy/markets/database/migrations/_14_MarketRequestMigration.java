package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _14_MarketRequestMigration extends DataMigration {

	public _14_MarketRequestMigration() {
		super(14);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			// user profile
			statement.execute("CREATE TABLE " + tablePrefix + "request (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"owner VARCHAR(36) NOT NULL, " +
					"owner_name VARCHAR(16) NOT NULL, " +
					"requested_item TEXT NOT NULL, " +
					"currency VARCHAR(72) NOT NULL, " +
					"currency_item TEXT, " +
					"price DOUBLE NOT NULL, " +
					"requested_amount INT NOT NULL, " +
					"requested_at BigInt NOT NULL " +
					")");

		}
	}
}
