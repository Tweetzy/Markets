package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _5_UserProfilePrefsMigration extends DataMigration {

	public _5_UserProfilePrefsMigration() {
		super(5);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "user ADD preferred_language VARCHAR(48) NOT NULL, currency_format_country VARCHAR(3) NOT NULL;");
		}
	}
}
