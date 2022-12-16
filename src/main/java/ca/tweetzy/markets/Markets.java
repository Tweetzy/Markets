package ca.tweetzy.markets;

import ca.tweetzy.flight.FlightPlugin;
import ca.tweetzy.flight.config.tweetzy.TweetzyYamlConfig;
import ca.tweetzy.flight.database.DataMigrationManager;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.SQLiteConnector;
import ca.tweetzy.markets.database.DataManager;

public final class Markets extends FlightPlugin {

	private final TweetzyYamlConfig coreConfig = new TweetzyYamlConfig(this, "config.yml");

	@SuppressWarnings("FieldCanBeLocal")
	private DatabaseConnector databaseConnector;
	private DataManager dataManager;

	@Override
	protected void onFlight() {

		// Set up the database if enabled
		this.databaseConnector = new SQLiteConnector(this);
		this.dataManager = new DataManager(this.databaseConnector, this);

		final DataMigrationManager dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager);

		// run migrations for tables
		dataMigrationManager.runMigrations();
	}

	@Override
	protected int getBStatsId() {
		return 7689;
	}

	@Override
	protected void onSleep() {
		shutdownDataManager(this.dataManager);
	}

	public static Markets getInstance() {
		return (Markets) FlightPlugin.getInstance();
	}

	public static DataManager getDataManager() {
		return getInstance().dataManager;
	}
}
