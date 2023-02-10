package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _15_TransactionsMigration extends DataMigration {

	public _15_TransactionsMigration() {
		super(15);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			// user profile
			statement.execute("CREATE TABLE " + tablePrefix + "transaction (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"buyer VARCHAR(36) NOT NULL, " +
					"buyer_name VARCHAR(16) NOT NULL, " +
					"seller VARCHAR(36) NOT NULL, " +
					"seller_name VARCHAR(16) NOT NULL, " +
					"type VARCHAR(32) NOT NULL, " +
					"item TEXT NOT NULL, " +
					"currency TEXT NOT NULL, " +
					"quantity INT NOT NULL, " +
					"price DOUBLE NOT NULL, " +
					"created_at BigInt NOT NULL " +
					")");

		}
	}
}
