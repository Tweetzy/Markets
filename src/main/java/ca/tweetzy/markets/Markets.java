package ca.tweetzy.markets;

import ca.tweetzy.core.TweetyCore;
import ca.tweetzy.core.TweetyPlugin;
import ca.tweetzy.core.commands.CommandManager;
import ca.tweetzy.core.compatibility.ServerProject;
import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.configuration.Config;
import ca.tweetzy.core.database.DataMigrationManager;
import ca.tweetzy.core.database.DatabaseConnector;
import ca.tweetzy.core.database.MySQLConnector;
import ca.tweetzy.core.gui.GuiManager;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.hooks.PluginHook;
import ca.tweetzy.core.hooks.economies.Economy;
import ca.tweetzy.core.utils.Metrics;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.api.UpdateChecker;
import ca.tweetzy.markets.commands.*;
import ca.tweetzy.markets.database.DataManager;
import ca.tweetzy.markets.database.migrations._1_InitialMigration;
import ca.tweetzy.markets.database.migrations._2_FeaturedMarketMigration;
import ca.tweetzy.markets.database.migrations._3_InfiniteItemMigration;
import ca.tweetzy.markets.economy.CurrencyBank;
import ca.tweetzy.markets.economy.UltraEconomyHook;
import ca.tweetzy.markets.listeners.PlayerListeners;
import ca.tweetzy.markets.listeners.SignListener;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.MarketManager;
import ca.tweetzy.markets.market.MarketPlayerManager;
import ca.tweetzy.markets.market.MarketRating;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.request.Request;
import ca.tweetzy.markets.request.RequestItem;
import ca.tweetzy.markets.request.RequestManager;
import ca.tweetzy.markets.settings.LocaleSettings;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.tasks.MarketCheckTask;
import ca.tweetzy.markets.transaction.TransactionManger;
import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 30 2021
 * Time Created: 3:28 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class Markets extends TweetyPlugin {

	private static TaskChainFactory taskChainFactory;

	@Getter
	private static Markets instance;

	@Getter
	private final Config data = new Config(this, "data.yml");

	@Getter
	private GuiManager guiManager;

	@Getter
	private CommandManager commandManager;

	@Getter
	private MarketManager marketManager;

	@Getter
	private TransactionManger transactionManger;

	@Getter
	private MarketPlayerManager marketPlayerManager;

	@Getter
	private RequestManager requestManager;

	@Getter
	private CurrencyBank currencyBank;

	@Getter
	private DataManager dataManager;

	@Getter
	protected DatabaseConnector databaseConnector;

	protected Metrics metrics;

	private PluginHook ultraEconomyHook;

	protected String IS_SONGODA_DOWNLOAD = "%%__SONGODA__%%";
	protected String SONGODA_NODE = "%%__SONGODA_NODE__%%";
	protected String TIMESTAMP = "%%__TIMESTAMP__%%";
	protected String USER = "%%__USER__%%";
	protected String USERNAME = "%%__USERNAME__%%";
	protected String RESOURCE = "%%__RESOURCE__%%";
	protected String NONCE = "%%__NONCE__%%";

	@Override
	public void onPluginLoad() {
		instance = this;
	}

	@Override
	public void onPluginEnable() {
		TweetyCore.registerPlugin(this, 101, XMaterial.CHEST.name());

		// Stop the plugin if the server version is not 1.8 or higher
		if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_7)) {
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// Check server type
		if (ServerProject.isServer(ServerProject.CRAFTBUKKIT, ServerProject.GLOWSTONE, ServerProject.TACO, ServerProject.UNKNOWN)) {
			// I can't remember if spigot allows me to disable a plugin if its running a specific jar so just tell them AGAIN
			// that they're running a non-supported server project even though the plugin page will state it....
			getLogger().severe("You're running Markets on a non-supported server project. There is no guarantee anything will work.");
		}

		taskChainFactory = BukkitTaskChainFactory.create(this);

		// Settings
		Settings.setup();

		this.ultraEconomyHook = PluginHook.addHook(Economy.class, "UltraEconomy", UltraEconomyHook.class);

		// Load Economy
		EconomyManager.load();

		// Setup the locale
		setLocale(Settings.LANG.getString());
		LocaleSettings.setup();

		// Setup Economy
		final String ECO_PLUGIN = Settings.ECONOMY_PLUGIN.getString();
		if (ECO_PLUGIN.startsWith("UltraEconomy")) {
			EconomyManager.getManager().setPreferredHook(this.ultraEconomyHook);
		} else {
			EconomyManager.getManager().setPreferredHook(ECO_PLUGIN);
		}

		if (!EconomyManager.getManager().isEnabled()) {
			getLogger().severe("Could not find a valid economy provider for shops");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		// Load the data file
		this.data.load();

		// Setup the database if enabled
		if (Settings.DATABASE_USE.getBoolean()) {
			this.databaseConnector = new MySQLConnector(this, Settings.DATABASE_HOST.getString(), Settings.DATABASE_PORT.getInt(), Settings.DATABASE_NAME.getString(), Settings.DATABASE_USERNAME.getString(), Settings.DATABASE_PASSWORD.getString());
			this.dataManager = new DataManager(this.databaseConnector, this);
			DataMigrationManager dataMigrationManager = new DataMigrationManager(this.databaseConnector, this.dataManager,
					new _1_InitialMigration(),
					new _2_FeaturedMarketMigration(),
					new _3_InfiniteItemMigration()
			);
			dataMigrationManager.runMigrations();
		}

		// Managers
		this.guiManager = new GuiManager(this);

		this.commandManager = new CommandManager(this);
		this.commandManager.setSyntaxErrorMessage(TextUtils.formatText(Settings.COMMAND_SYNTAX_ERROR.getStringList()));
		this.commandManager.setNoCommandMessage(TextUtils.formatText(Settings.COMMAND_INVALID_COMMAND.getString()));
		this.commandManager.setNoPermsMessage(TextUtils.formatText(Settings.COMMAND_NO_PERMISSION.getString()));

		this.marketManager = new MarketManager();
		this.marketPlayerManager = new MarketPlayerManager();
		this.transactionManger = new TransactionManger();
		this.requestManager = new RequestManager();
		this.currencyBank = new CurrencyBank();

		this.guiManager.init();

		newChain().async(() -> {
			this.marketManager.loadMarkets();
			this.marketManager.loadFeaturedMarkets();
			this.transactionManger.loadTransactions();
			this.transactionManger.loadPayments();
			this.requestManager.loadRequests();
			this.marketManager.loadBlockedItems();
			this.currencyBank.loadBank();
		}).execute();

		// Listeners
		Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
		Bukkit.getPluginManager().registerEvents(new SignListener(), this);


		// Commands
		this.commandManager.addCommand(new CommandMarkets()).addSubCommands(
				new CommandCreate(),
				new CommandRemove(),
				new CommandAddCategory(),
				new CommandAddItem(),
				new CommandRequest(),
				new CommandShowRequest(),
				new CommandPayments(),
				new CommandPayUpKeep(),
				new CommandBank(),
				new CommandSet(),
				new CommandSearch(),
				new CommandView(),
				new CommandList(),
				new CommandHelp(),
				new CommandSettings(),
				new CommandConfiscate(),
				new CommandReload(),
				new CommandsBlockItem(),
				new CommandForceSave(),
				new CommandInternal()
		);

		MarketCheckTask.startTask();

		// Perform the update check
		getServer().getScheduler().runTaskLaterAsynchronously(this, () -> new UpdateChecker(this, 92178, getConsole()).check(), 1L);
		if (Settings.AUTO_SAVE_ENABLED.getBoolean()) {
			getServer().getScheduler().runTaskTimerAsynchronously(this, () -> saveData(true, "Saving data due to auto save"), 20L * 5, (long) 20 * Settings.AUTO_SAVE_DELAY.getInt());
		}

		if (Settings.FILE_BACKUP_ON_START.getBoolean())
			getServer().getScheduler().runTaskLaterAsynchronously(this, this::forceBackup, 20L * Settings.FILE_BACKUP_ON_START_DELAY.getInt());

		this.metrics = new Metrics(this, 7689);
	}

	@Override
	public void onPluginDisable() {
		saveData(false, "Saving data due to plugin disable");
		instance = null;
	}

	public void saveData(boolean async, String saveType) {
		if (Settings.LOG_SAVE_MSG.getBoolean())
			getLogger().info(saveType);
		if (Settings.DATABASE_USE.getBoolean()) {
			newChain().async(() -> {
				this.dataManager.saveMarkets(this.marketManager.getMarkets(), async);
				this.dataManager.saveBlockedItems(this.marketManager.getBlockedItems(), async);
				this.dataManager.saveTransactions(this.transactionManger.getTransactions(), async);
				this.dataManager.savePayments(this.transactionManger.getPayments(), async);
				this.dataManager.saveRequests(this.requestManager.getRequests(), async);
				this.dataManager.saveBanks(this.currencyBank.getBank(), async);
				this.dataManager.saveUpKeeps(this.marketManager.getFeeLastChargedOn(), async);
				this.dataManager.saveFeaturedMarkets(this.marketManager.getFeaturedMarkets(), async);

				List<MarketCategory> categories = new ArrayList<>();
				this.marketManager.getMarkets().stream().map(Market::getCategories).forEach(categories::addAll);
				this.dataManager.saveCategories(categories, async);

				List<MarketItem> marketItems = new ArrayList<>();
				categories.stream().map(MarketCategory::getItems).forEach(marketItems::addAll);
				this.dataManager.saveItems(marketItems, async);

				List<MarketRating> marketRatings = new ArrayList<>();
				this.marketManager.getMarkets().stream().map(Market::getRatings).forEach(marketRatings::addAll);
				this.dataManager.saveRatings(marketRatings, async);

				List<RequestItem> requestItems = new ArrayList<>();
				this.requestManager.getRequests().stream().map(Request::getRequestedItems).forEach(requestItems::addAll);
				this.dataManager.saveRequestItems(requestItems, async);
			}).execute();
		} else {
			newChain().asyncFirst(() -> {
				this.data.set("up keep", this.marketManager.getFeeLastChargedOn());
				this.marketManager.setMarketsOnFile();
				this.marketManager.setBlockedItemsOnFile();
				this.marketManager.setFeaturedMarketsOnFile();
				this.transactionManger.setTransactionsOnFile();
				this.transactionManger.setPaymentsOnFile();
				this.requestManager.setRequestsOnFile();
				this.currencyBank.setBankOnFile();
				return null;
			}).asyncLast((rs) -> this.data.save()).execute();
		}

		if (Settings.LOG_SAVE_MSG.getBoolean())
			getLogger().info("Market data has been automatically saved");
	}

	private void forceBackup() {
		newChain().asyncFirst(() -> {
			this.data.set("up keep", this.marketManager.getFeeLastChargedOn());
			this.marketManager.setMarketsOnFile();
			this.marketManager.setBlockedItemsOnFile();
			this.marketManager.setFeaturedMarketsOnFile();
			this.transactionManger.setTransactionsOnFile();
			this.transactionManger.setPaymentsOnFile();
			this.requestManager.setRequestsOnFile();
			this.currencyBank.setBankOnFile();
			return null;
		}).asyncLast((rs) -> this.data.save()).execute();
	}

	@Override
	public void onConfigReload() {
		Settings.setup();
		setLocale(Settings.LANG.getString());
		LocaleSettings.setup();
	}

	@Override
	public List<Config> getExtraConfig() {
		return null;
	}

	public static <T> TaskChain<T> newChain() {
		return taskChainFactory.newChain();
	}
}
