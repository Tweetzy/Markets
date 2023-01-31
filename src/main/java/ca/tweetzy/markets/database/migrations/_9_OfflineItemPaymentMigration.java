package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _9_OfflineItemPaymentMigration extends DataMigration {

	public _9_OfflineItemPaymentMigration() {
		super(9);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			// user profile
			statement.execute("CREATE TABLE " + tablePrefix + "offline_payment (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"payment_for VARCHAR(36) NOT NULL, " +
					"currency TEXT NOT NULL, " +
					"amount VARCHAR(72) NOT NULL, " +
					"reason VARCHAR(128) NOT NULL, " +
					"received_at BigInt NOT NULL " +
					")");

		}
	}
}
