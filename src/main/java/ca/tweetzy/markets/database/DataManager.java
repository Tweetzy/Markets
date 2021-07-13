package ca.tweetzy.markets.database;

import ca.tweetzy.core.database.DataManagerAbstract;
import ca.tweetzy.core.database.DatabaseConnector;
import ca.tweetzy.markets.market.Market;
import org.bukkit.plugin.Plugin;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 12 2021
 * Time Created: 10:02 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class DataManager extends DataManagerAbstract {

    public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
        super(databaseConnector, plugin);
    }

}
