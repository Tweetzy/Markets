package ca.tweetzy.markets;

import ca.tweetzy.flight.FlightPlugin;
import ca.tweetzy.flight.command.CommandManager;
import ca.tweetzy.flight.database.DataMigrationManager;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.SQLiteConnector;
import ca.tweetzy.flight.gui.GuiManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.commands.*;
import ca.tweetzy.markets.database.DataManager;
import ca.tweetzy.markets.database.migrations.*;
import ca.tweetzy.markets.impl.MarketsAPIImpl;
import ca.tweetzy.markets.listeners.MarketTransactionListener;
import ca.tweetzy.markets.listeners.PlayerJoinListener;
import ca.tweetzy.markets.model.manager.*;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class Markets extends FlightPlugin {

	@SuppressWarnings("FieldCanBeLocal")
	private DatabaseConnector databaseConnector;
	private DataManager dataManager;

	private final CommandManager commandManager = new CommandManager(this);
	private final GuiManager guiManager = new GuiManager(this);

	private final MarketManager marketManager = new MarketManager();
	private final PlayerManager playerManager = new PlayerManager();
	private final CategoryManager categoryManager = new CategoryManager();
	private final CategoryItemManager categoryItemManager = new CategoryItemManager();
	private final OfferManager offerManager = new OfferManager();
	private final CurrencyManager currencyManager = new CurrencyManager();
	private final BankManager bankManager = new BankManager();
	private final RatingManager ratingManager = new RatingManager();
	private final RequestManager requestManager = new RequestManager();
	private final OfflineItemPaymentManager offlineItemPaymentManager = new OfflineItemPaymentManager();
	private final TransactionManager transactionManager = new TransactionManager();

	// default vault economy
	private Economy economy = null;

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
				new _3_CategoryItemMigration(),
				new _4_CategoryItemCurrencyMigration(),
				new _5_UserProfilePrefsMigration(),
				new _6_MarketItemOffersColMigration(),
				new _7_MarketSettingsInitialMigration(),
				new _8_MarketLayoutMigration(),
				new _9_OfflineItemPaymentMigration(),
				new _10_MarketOfferMigration(),
				new _11_OfferRequestAmountMigration(),
				new _12_BankEntryMigration(),
				new _13_MarketReviewMigration(),
				new _14_MarketRequestMigration(),
				new _15_TransactionsMigration(),
				new _16_InfiniteItemsMigration()
		);

		// run migrations for tables
		dataMigrationManager.runMigrations();

		// setup vault
		setupEconomy();

		// gui system
		this.guiManager.init();

		// managers
		this.marketManager.load();
		this.playerManager.load();
		this.currencyManager.load();
		this.offlineItemPaymentManager.load();
		this.bankManager.load();
		this.offerManager.load();
		this.requestManager.load();
		this.transactionManager.load();

		// listeners
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new MarketTransactionListener(), this);

		// setup commands
		this.commandManager.registerCommandDynamically(new MarketsCommand()).addSubCommands(
				new CommandAdd(),
				new CommandBank(),
				new CommandPayments(),
				new CommandOffers(),
				new CommandView(),
				new CommandSearch(),
				new CommandAdmin(),
				new CommandDelete(),
				new CommandReload()
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

	public static MarketsAPI getAPI() {
		return getInstance().API;
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

	public static BankManager getBankManager() {
		return getInstance().bankManager;
	}

	public static TransactionManager getTransactionManager() {
		return getInstance().transactionManager;
	}

	public static OfferManager getOfferManager() {
		return getInstance().offerManager;
	}

	public static PlayerManager getPlayerManager() {
		return getInstance().playerManager;
	}

	public static CurrencyManager getCurrencyManager() {
		return getInstance().currencyManager;
	}

	public static OfflineItemPaymentManager getOfflineItemPaymentManager() {
		return getInstance().offlineItemPaymentManager;
	}

	public static RatingManager getRatingManager() {
		return getInstance().ratingManager;
	}

	public static RequestManager getRequestManager() {
		return getInstance().requestManager;
	}

	public static Economy getEconomy() {
		return getInstance().economy;
	}

	// helpers
	private void setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return;
		}

		final RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

		if (rsp == null) {
			return;
		}

		this.economy = rsp.getProvider();
	}
}
