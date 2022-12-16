package ca.tweetzy.markets.database;

import ca.tweetzy.flight.database.DataManagerAbstract;
import ca.tweetzy.flight.database.DatabaseConnector;
import org.bukkit.plugin.Plugin;

public final class DataManager extends DataManagerAbstract {

	public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
		super(databaseConnector, plugin);
	}
}
