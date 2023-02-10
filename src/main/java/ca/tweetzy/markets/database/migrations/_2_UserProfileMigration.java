package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _2_UserProfileMigration extends DataMigration {

	public _2_UserProfileMigration() {
		super(2);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			// user profile
			statement.execute("CREATE TABLE " + tablePrefix + "user (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"last_known_name VARCHAR(16) NOT NULL, " +
					"bio TEXT NOT NULL, " +
					"last_seen_at BigInt NOT NULL " +
					")");

		}
	}
}
