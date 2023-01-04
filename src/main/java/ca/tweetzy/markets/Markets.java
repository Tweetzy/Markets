package ca.tweetzy.markets;

import ca.tweetzy.flight.FlightPlugin;
import ca.tweetzy.flight.command.CommandManager;
import ca.tweetzy.flight.database.DataMigrationManager;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.SQLiteConnector;
import ca.tweetzy.flight.gui.GuiManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.commands.CommandAdmin;
import ca.tweetzy.markets.commands.MarketsCommand;
import ca.tweetzy.markets.database.DataManager;
import ca.tweetzy.markets.database.migrations._1_InitialMigration;
import ca.tweetzy.markets.database.migrations._2_UserProfileMigration;
import ca.tweetzy.markets.database.migrations._3_CategoryItemMigration;
import ca.tweetzy.markets.impl.MarketsAPIImpl;
import ca.tweetzy.markets.model.CategoryItemManager;
import ca.tweetzy.markets.model.CategoryManager;
import ca.tweetzy.markets.model.MarketManager;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;

public final class Markets extends FlightPlugin {

	@SuppressWarnings("FieldCanBeLocal")
	private DatabaseConnector databaseConnector;
	private DataManager dataManager;

	private final CommandManager commandManager = new CommandManager(this);
	private final GuiManager guiManager = new GuiManager(this);

	private final MarketManager marketManager = new MarketManager();
	private final CategoryManager categoryManager = new CategoryManager();
	private final CategoryItemManager categoryItemManager = new CategoryItemManager();

	private final MarketsAPI API = new MarketsAPIImpl();

	@Override
	protected void onFlight() {
		Settings.init();
		Translations.init();

		Common.setPrefix(Settings.PREFIX.getStringOr("&8[&EMarkets&8]"));

		// Set up the database if enabled
		this.databaseConnector = new SQLiteConnector(this);
		this.dataManager = new DataManager(this.databaseConnector, this);

		final DataMigrationManager dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager,
				new _1_InitialMigration(),
				new _2_UserProfileMigration(),
				new _3_CategoryItemMigration()
		);

		// run migrations for tables
		dataMigrationManager.runMigrations();

		// gui system
		this.guiManager.init();
		this.marketManager.load();

		// setup commands
		this.commandManager.registerCommandDynamically(new MarketsCommand()).addSubCommands(
				new CommandAdmin()
		);
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

	public static GuiManager getGuiManager() {
		return getInstance().guiManager;
	}

	public static DataManager getDataManager() {
		return getInstance().dataManager;
	}

	public static MarketManager getMarketManager() {
		return getInstance().marketManager;
	}

	public static CategoryManager getCategoryManager() {
		return getInstance().categoryManager;
	}

	public static CategoryItemManager getCategoryItemManager() {
		return getInstance().categoryItemManager;
	}
}
