package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _10_MarketOfferMigration extends DataMigration {

	public _10_MarketOfferMigration() {
		super(10);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			// user profile
			statement.execute("CREATE TABLE " + tablePrefix + "offer (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"sender VARCHAR(36) NOT NULL, " +
					"sender_name VARCHAR(16) NOT NULL, " +
					"offer_to VARCHAR(36) NOT NULL, " +
					"market_item VARCHAR(36) NOT NULL, " +
					"currency VARCHAR(72) NOT NULL, " +
					"currency_item TEXT, " +
					"offered_amount DOUBLE NOT NULL, " +
					"offered_at BigInt NOT NULL " +
					")");

		}
	}
}
