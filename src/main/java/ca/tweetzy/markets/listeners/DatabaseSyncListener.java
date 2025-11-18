package ca.tweetzy.markets.listeners;

import ca.tweetzy.flight.database.sync.DatabaseEvent;
import ca.tweetzy.flight.database.sync.DatabaseEventListener;
import ca.tweetzy.markets.Markets;
import lombok.NonNull;

/**
 * Listens for database events from Redis
 * Database events are handled by CrossServerSyncManager
 * This listener is registered to receive database sync events
 */
public class DatabaseSyncListener implements DatabaseEventListener {
	
	private final Markets plugin;
	
	public DatabaseSyncListener(@NonNull Markets plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onDatabaseEvent(@NonNull DatabaseEvent event) {
		// Database events are handled by CrossServerSyncManager
		// This listener is registered to ensure events are received
		// The actual handling is done in CrossServerSyncManager
	}
	
	@Override
	public String getTableName() {
		return null; // Listen to all tables
	}
	
	@Override
	public String getTablePrefix() {
		return Markets.getDataManager().getTablePrefix();
	}
}

