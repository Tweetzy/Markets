package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The current file has been created by Kiran Hart
 * Date Created: November 02 2021
 * Time Created: 2:15 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class _3_InfiniteItemMigration extends DataMigration {

	public _3_InfiniteItemMigration() {
		super(3);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "items ADD COLUMN infinite BOOLEAN NULL");
		}
	}
}
