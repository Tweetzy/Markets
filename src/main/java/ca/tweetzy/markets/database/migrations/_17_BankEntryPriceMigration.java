package ca.tweetzy.markets.database.migrations;

import ca.tweetzy.flight.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class _17_BankEntryPriceMigration extends DataMigration {

	public _17_BankEntryPriceMigration() {
		super(17);
	}

	@Override
	public void migrate(Connection connection, String tablePrefix) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute("ALTER TABLE " + tablePrefix + "bank_entry ADD price DOUBLE NOT NULL default 0;");
			statement.execute("ALTER TABLE " + tablePrefix + "bank_entry ADD currency VARCHAR(72) NOT NULL DEFAULT 'Vault/Vault'");
			statement.execute("ALTER TABLE " + tablePrefix + "bank_entry ADD currency_item TEXT NULL");

		}
	}
}
