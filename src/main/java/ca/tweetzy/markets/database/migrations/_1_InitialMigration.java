package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _1_InitialMigration extends DataMigration {

	public _1_InitialMigration() {
		super(1);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			// market
			statement.execute("CREATE TABLE " + tablePrefix + "markets (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"owner VARCHAR(36) NOT NULL, " +
					"owner_name VARCHAR(16) NOT NULL, " +
					"display_name VARCHAR(72) NOT NULL, " +
					"description TEXT NOT NULL, " +
					"type VARCHAR(16) NOT NULL, " +
					"created_at BigInt NOT NULL, " +
					"updated_at BigInt NOT NULL " +
					")");

			// category
			statement.execute("CREATE TABLE " + tablePrefix + "category (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"owning_market VARCHAR(36) NOT NULL, " +
					"name VARCHAR(32) NOT NULL, " +
					"display_name VARCHAR(72) NOT NULL, " +
					"description TEXT NOT NULL, " +
					"created_at BigInt NOT NULL, " +
					"updated_at BigInt NOT NULL " +
					")");
		}
	}
}
