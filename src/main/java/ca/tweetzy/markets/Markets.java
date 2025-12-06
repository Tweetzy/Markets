package ca.tweetzy.markets;

import ca.tweetzy.flight.FlightPlugin;
import ca.tweetzy.flight.command.CommandManager;
import ca.tweetzy.flight.database.DataMigrationManager;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.MySQLConnector;
import ca.tweetzy.flight.database.SQLiteConnector;
import ca.tweetzy.flight.gui.GuiManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.commands.*;
import ca.tweetzy.markets.database.DataManager;
import ca.tweetzy.markets.database.migrations.*;
import ca.tweetzy.markets.database.repository.*;
import ca.tweetzy.markets.impl.MarketsAPIImpl;
import ca.tweetzy.markets.listeners.MarketTransactionListener;
import ca.tweetzy.markets.listeners.PlayerJoinListener;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.model.manager.*;
import ca.tweetzy.flight.dependency.Dependency;
import ca.tweetzy.flight.dependency.Relocation;
import ca.tweetzy.markets.model.sync.CrossServerNotificationManager;
import ca.tweetzy.markets.model.sync.CrossServerSyncManager;
import ca.tweetzy.markets.model.sync.StockReservationManager;
import ca.tweetzy.markets.model.TransactionLogger;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class Markets extends FlightPlugin {

	private static TaskChainFactory taskChainFactory;

	@SuppressWarnings("FieldCanBeLocal")
	private DatabaseConnector databaseConnector;
	private DataManager dataManager;
	
	// Repositories
	private MarketRepository marketRepository;
	private CategoryRepository categoryRepository;
	private MarketItemRepository marketItemRepository;
	private MarketUserRepository marketUserRepository;
	private OfferRepository offerRepository;
	private RatingRepository ratingRepository;
	private RequestRepository requestRepository;
	private TransactionRepository transactionRepository;
	private BankEntryRepository bankEntryRepository;
	private PaymentRepository paymentRepository;

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
	private final PlayerTextureCache playerTextureCache = new PlayerTextureCache();
	
	private CrossServerSyncManager crossServerSyncManager;
	private StockReservationManager stockReservationManager;
	private CrossServerNotificationManager notificationManager;
	
	// Transaction logger
	private TransactionLogger transactionLogger;

	// default vault economy
	private Economy economy = null;

	private final MarketsAPI API = new MarketsAPIImpl();

	@Override
	protected Set<Dependency> getOptionalDependencies() {
		Set<Dependency> dependencies = new HashSet<>(super.getOptionalDependencies());
		
		dependencies.add(new Dependency(
				"https://repo1.maven.org/maven2",
				"redis.clients",
				"jedis",
				"5.1.0",
				true,
				new Relocation("redis.clients", "ca.tweetzy.flight.third_party.redis.clients")
		));
		
		dependencies.add(new Dependency(
				"https://repo1.maven.org/maven2",
				"org.apache.commons",
				"commons-pool2",
				"2.12.0",
				true,
				null 
		));
		
		return dependencies;
	}

	@Override
	protected void onFlight() {
		Settings.init();
		Translations.init();

		Common.setPrefix(Settings.PREFIX.getStringOr("&8[&EMarkets&8]"));
		taskChainFactory = BukkitTaskChainFactory.create(this);

		// Set up the database if enabled
		this.databaseConnector = Settings.DATABASE_USE.getBoolean() ? new MySQLConnector(
				this,
				Settings.DATABASE_HOST.getString(),
				Settings.DATABASE_PORT.getInt(),
				Settings.DATABASE_NAME.getString(),
				Settings.DATABASE_USERNAME.getString(),
				Settings.DATABASE_PASSWORD.getString(),
				Settings.DATABASE_CUSTOM_PARAMS.getString().equalsIgnoreCase("None") ? "" : Settings.DATABASE_CUSTOM_PARAMS.getString()
		) : new SQLiteConnector(this);

		this.dataManager = new DataManager(this.databaseConnector, this);
		
		// Initialize Redis sync if MySQL is enabled and Redis is enabled
		if (Settings.DATABASE_USE.getBoolean() && Settings.REDIS_ENABLED.getBoolean()) {
			String redisPassword = Settings.REDIS_PASSWORD.getString();
			if (redisPassword != null && redisPassword.isEmpty()) {
				redisPassword = null; // Convert empty string to null
			}
			
			boolean redisInitialized = this.dataManager.initializeRedisSync(
					Settings.REDIS_HOST.getString(),
					Settings.REDIS_PORT.getInt(),
					redisPassword,
					Settings.REDIS_CHANNEL.getString()
			);
			
			if (!redisInitialized) {
				Common.log("&cRedis sync initialization failed. Cross-server synchronization disabled.");
				Common.log("&cMarkets will continue to work in single-server mode.");
			}
		}
		
		// Initialize repositories
		final String tablePrefix = this.dataManager.getTablePrefix();
		this.marketRepository = new MarketRepository(this.databaseConnector, tablePrefix);
		this.categoryRepository = new CategoryRepository(this.databaseConnector, tablePrefix);
		this.marketItemRepository = new MarketItemRepository(this.databaseConnector, tablePrefix);
		this.marketUserRepository = new MarketUserRepository(this.databaseConnector, tablePrefix);
		this.offerRepository = new OfferRepository(this.databaseConnector, tablePrefix);
		this.ratingRepository = new RatingRepository(this.databaseConnector, tablePrefix);
		this.requestRepository = new RequestRepository(this.databaseConnector, tablePrefix);
		this.transactionRepository = new TransactionRepository(this.databaseConnector, tablePrefix);
		this.bankEntryRepository = new BankEntryRepository(this.databaseConnector, tablePrefix);
		this.paymentRepository = new PaymentRepository(this.databaseConnector, tablePrefix);

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
				new _16_InfiniteItemsMigration(),
				new _17_BankEntryPriceMigration()
		);

		// run migrations for tables
		dataMigrationManager.runMigrations();

		// setup vault
		setupEconomy();

		// gui system
		this.guiManager.init();
		
		// Initialize transaction logger
		if (Settings.TRANSACTION_LOGGING_ENABLED.getBoolean()) {
			this.transactionLogger = new TransactionLogger(this);
			this.transactionLogger.start();
			Common.log("&aTransaction logging enabled - logs stored in plugins/Markets/logs/");
		}

		// Initialize cross-server sync manager if Redis is enabled
		if (this.dataManager.getRedisSyncManager() != null && this.dataManager.getRedisSyncManager().isEnabled()) {
			this.crossServerSyncManager = new CrossServerSyncManager(this, this.dataManager);
			this.stockReservationManager = new StockReservationManager(this);
			this.notificationManager = new CrossServerNotificationManager(this, this.dataManager);
		}
		
		// managers
		this.marketManager.load();
		this.playerManager.load();
		this.currencyManager.load();
		this.offlineItemPaymentManager.load();
		this.bankManager.load();
		this.offerManager.load();
		this.requestManager.load();
		this.transactionManager.load();
		this.playerTextureCache.start();

		// Prefetch textures for market owners at startup (if enabled)
		if (Settings.PLAYER_TEXTURE_STARTUP_PREFETCH_ENABLED.getBoolean()) {
			// Get all market owners with open markets (most likely to be viewed)
			List<UUID> marketOwners = this.marketManager.getManagerContent().stream()
				.filter(Market::isOpen)
				.map(Market::getOwnerUUID)
				.distinct()
				.toList();
			
			int limit = Settings.PLAYER_TEXTURE_STARTUP_PREFETCH_LIMIT.getInt();
			if (limit > 0 && !marketOwners.isEmpty()) {
				Common.log("&aPrefetching textures for " + Math.min(marketOwners.size(), limit) + " market owners...");
				this.playerTextureCache.prefetchMarketOwnersAtStartup(marketOwners, limit);
			}
		}

		// listeners
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getServer().getPluginManager().registerEvents(new MarketTransactionListener(), this);
		
		// Note: Database sync events are handled by CrossServerSyncManager, which registers itself
		// No need for separate DatabaseSyncListener
		
		// Start periodic cleanup task for viewer tracking and stuck flags (every 5 minutes)
		getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
			// Clean up invalid viewers from all markets, categories, and items
			marketManager.getManagerContent().forEach(market -> {
				market.getCategories().forEach(category -> {
					category.getViewingPlayers().removeIf(player -> player == null || !player.isOnline());
					category.getItems().forEach(item -> {
						item.getViewingPlayers().removeIf(player -> player == null || !player.isOnline());
						// Check and clear stuck beingEdited flags
						item.isBeingEdited(); // This will auto-clear if stuck
					});
				});
			});
		}, 6000L, 6000L); // Every 5 minutes (6000 ticks)

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
				new CommandTransactions(),
				new CommandReload()
		);
	}

	@Override
	protected int getBStatsId() {
		return 7689;
	}

	@Override
	protected void onSleep() {
		// Shutdown transaction logger
		if (this.transactionLogger != null) {
			this.transactionLogger.stop();
		}
		
		// Shutdown notification manager before shutting down data manager
		if (this.notificationManager != null) {
			this.notificationManager.shutdown();
		}
		
		// Shutdown cross-server sync manager
		if (this.crossServerSyncManager != null) {
			this.crossServerSyncManager.shutdown();
		}
		
		// Shutdown stock reservation manager
		if (this.stockReservationManager != null) {
			this.stockReservationManager.shutdown();
		}
		
		// Shutdown player texture cache
		this.playerTextureCache.shutdown();
		
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
	
	public static PlayerTextureCache getPlayerTextureCache() {
		return getInstance().playerTextureCache;
	}
	
	public static StockReservationManager getStockReservationManager() {
		return getInstance().stockReservationManager;
	}
	
	public static CrossServerNotificationManager getNotificationManager() {
		return getInstance().notificationManager;
	}
	
	public static TransactionLogger getTransactionLogger() {
		return getInstance().transactionLogger;
	}
	
	public static MarketRepository getMarketRepository() {
		return getInstance().marketRepository;
	}
	
	public static CategoryRepository getCategoryRepository() {
		return getInstance().categoryRepository;
	}
	
	public static MarketItemRepository getMarketItemRepository() {
		return getInstance().marketItemRepository;
	}
	
	public static MarketUserRepository getMarketUserRepository() {
		return getInstance().marketUserRepository;
	}
	
	public static OfferRepository getOfferRepository() {
		return getInstance().offerRepository;
	}
	
	public static RatingRepository getRatingRepository() {
		return getInstance().ratingRepository;
	}
	
	public static RequestRepository getRequestRepository() {
		return getInstance().requestRepository;
	}
	
	public static TransactionRepository getTransactionRepository() {
		return getInstance().transactionRepository;
	}
	
	public static BankEntryRepository getBankEntryRepository() {
		return getInstance().bankEntryRepository;
	}
	
	public static PaymentRepository getPaymentRepository() {
		return getInstance().paymentRepository;
	}

	public static Economy getEconomy() {
		return getInstance().economy;
	}

	public static <T> TaskChain<T> newChain() {
		return taskChainFactory.newChain();
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
