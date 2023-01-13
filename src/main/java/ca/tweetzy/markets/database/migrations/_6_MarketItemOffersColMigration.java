package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _6_MarketItemOffersColMigration extends DataMigration {

	public _6_MarketItemOffersColMigration() {
		super(6);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "category_item ADD accepting_offers BOOLEAN DEFAULT 1 NOT NULL;");
		}
	}
}
