package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _13_MarketReviewMigration extends DataMigration {

	public _13_MarketReviewMigration() {
		super(13);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {

			statement.execute("CREATE TABLE " + tablePrefix + "review (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"market VARCHAR(36) NOT NULL, " +
					"rater VARCHAR(36) NOT NULL, " +
					"rater_name VARCHAR(36) NOT NULL, " +
					"feedback VARCHAR(128), " +
					"stars INT NOT NULL" +
					")");

		}
	}
}
