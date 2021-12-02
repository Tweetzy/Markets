package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 16 2021
 * Time Created: 10:41 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class _2_FeaturedMarketMigration extends DataMigration {

	public _2_FeaturedMarketMigration() {
		super(2);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE " + tablePrefix + "featured_markets (" +
					"id VARCHAR(36) PRIMARY KEY, " +
					"expires_at BigInt(20) NULL )");
		}
	}
}
