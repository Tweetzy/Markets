package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _11_OfferRequestAmountMigration extends DataMigration {

	public _11_OfferRequestAmountMigration() {
		super(11);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "offer ADD request_amount INT NOT NULL;");

		}
	}
}
