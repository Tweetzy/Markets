package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _12_BankEntryMigration extends DataMigration {

	public _12_BankEntryMigration() {
		super(12);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			statement.execute("CREATE TABLE " + tablePrefix + "bank_entry (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"owner VARCHAR(36) NOT NULL, " +
					"item TEXT, " +
					"quantity INT NOT NULL" +
					")");

		}
	}
}
