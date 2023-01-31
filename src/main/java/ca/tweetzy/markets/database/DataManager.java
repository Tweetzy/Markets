package ca.tweetzy.markets.database;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.database.Callback;
import ca.tweetzy.flight.database.DataManagerAbstract;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.UpdateCallback;
import ca.tweetzy.flight.utils.SerializeUtil;
import ca.tweetzy.markets.api.currency.Payment;
import ca.tweetzy.markets.api.market.*;
import ca.tweetzy.markets.impl.*;
import ca.tweetzy.markets.impl.layout.HomeLayout;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class DataManager extends DataManagerAbstract {

	public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
		super(databaseConnector, plugin);
	}

	public void createMarket(@NonNull final AbstractMarket market, final Callback<AbstractMarket> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {

			final String query = "INSERT INTO " + this.getTablePrefix() + "markets (id, type, display_name, description, owner, owner_name, created_at, updated_at, banned_users, open, close_when_out_of_stock, home_layout, category_layout) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			final String fetchQuery = "SELECT * FROM " + this.getTablePrefix() + "markets WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				final PreparedStatement fetch = connection.prepareStatement(fetchQuery);

				fetch.setString(1, market.getId().toString());

				preparedStatement.setString(1, market.getId().toString());
				preparedStatement.setString(2, market.getMarketType().name());
				preparedStatement.setString(3, market.getDisplayName());
				preparedStatement.setString(4, String.join(";;;", market.getDescription()));
				preparedStatement.setString(5, market.getOwnerUUID().toString());
				preparedStatement.setString(6, market.getOwnerName());
				preparedStatement.setLong(7, market.getTimeCreated());
				preparedStatement.setLong(8, market.getLastUpdated());
				preparedStatement.setString(9, market.getBannedUsers().stream().map(UUID::toString).collect(Collectors.joining(",")));
				preparedStatement.setBoolean(10, market.isOpen());
				preparedStatement.setBoolean(11, market.isCloseWhenOutOfStock());
				preparedStatement.setString(12, market.getHomeLayout().getJSONString());
				preparedStatement.setString(13, market.getCategoryLayout().getJSONString());

				preparedStatement.executeUpdate();

				if (callback != null) {
					final ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractMarket(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void updateMarket(@NonNull final AbstractMarket market, final Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			final String query = "UPDATE " + this.getTablePrefix() + "markets SET display_name = ?, description = ?, owner_name = ?, updated_at = ?, banned_users = ?, open = ?, close_when_out_of_stock = ?, home_layout = ?, category_layout = ? WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

				preparedStatement.setString(1, market.getDisplayName());
				preparedStatement.setString(2, String.join(";;;", market.getDescription()));
				preparedStatement.setString(3, market.getOwnerName());
				preparedStatement.setLong(4, market.getLastUpdated());

				preparedStatement.setString(5, market.getBannedUsers().stream().map(UUID::toString).collect(Collectors.joining(",")));
				preparedStatement.setBoolean(6, market.isOpen());
				preparedStatement.setBoolean(7, market.isCloseWhenOutOfStock());

				preparedStatement.setString(8, market.getHomeLayout().getJSONString());
				preparedStatement.setString(9, market.getCategoryLayout().getJSONString());
				preparedStatement.setString(10, market.getId().toString());

				int result = preparedStatement.executeUpdate();

				if (callback != null)
					callback.accept(null, result > 0);

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void deleteMarket(@NonNull final AbstractMarket market, Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "markets WHERE id = ?")) {
				statement.setString(1, market.getId().toString());

				int result = statement.executeUpdate();
				callback.accept(null, result > 0);

			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void getMarkets(@NonNull final Callback<List<AbstractMarket>> callback) {
		final List<AbstractMarket> markets = new ArrayList<>();

		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "markets")) {
				final ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					final AbstractMarket market = extractMarket(resultSet);
					markets.add(market);
				}

				callback.accept(null, markets);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void createCategory(@NonNull final Category category, final Callback<Category> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {

			final String query = "INSERT INTO " + this.getTablePrefix() + "category (id, owning_market, name, icon, display_name, description, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			final String fetchQuery = "SELECT * FROM " + this.getTablePrefix() + "category WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				final PreparedStatement fetch = connection.prepareStatement(fetchQuery);

				fetch.setString(1, category.getId().toString());

				preparedStatement.setString(1, category.getId().toString());
				preparedStatement.setString(2, category.getOwningMarket().toString());
				preparedStatement.setString(3, category.getName().toLowerCase());
				preparedStatement.setString(4, category.getIcon().getType().name());
				preparedStatement.setString(5, category.getDisplayName());
				preparedStatement.setString(6, String.join(";;;", category.getDescription()));
				preparedStatement.setLong(7, category.getTimeCreated());
				preparedStatement.setLong(8, category.getLastUpdated());

				preparedStatement.executeUpdate();

				if (callback != null) {
					final ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractCategory(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void updateCategory(@NonNull final Category category, final Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			//id, owning_market, name, icon, display_name, description, created_at, updated_at
			final String query = "UPDATE " + this.getTablePrefix() + "category SET icon = ?, display_name = ?, description = ?, updated_at = ? WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

				preparedStatement.setString(1, category.getIcon().getType().toString());
				preparedStatement.setString(2, category.getDisplayName());
				preparedStatement.setString(3, String.join(";;;", category.getDescription()));
				preparedStatement.setLong(4, category.getLastUpdated());
				preparedStatement.setString(5, category.getId().toString());

				int result = preparedStatement.executeUpdate();

				if (callback != null)
					callback.accept(null, result > 0);

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void deleteCategory(@NonNull final Category category, Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "category WHERE id = ?")) {
				statement.setString(1, category.getId().toString());

				int result = statement.executeUpdate();
				callback.accept(null, result > 0);

			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void getCategories(@NonNull final Callback<List<Category>> callback) {
		final List<Category> categories = new ArrayList<>();

		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "category")) {
				final ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					final Category category = extractCategory(resultSet);
					categories.add(category);
				}

				callback.accept(null, categories);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void createMarketItem(@NonNull final MarketItem marketItem, final Callback<MarketItem> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {

			final String query = "INSERT INTO " + this.getTablePrefix() + "category_item (id, owning_category, item, currency, currency_item, price, stock, price_is_for_all, accepting_offers) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			final String fetchQuery = "SELECT * FROM " + this.getTablePrefix() + "category_item WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				final PreparedStatement fetch = connection.prepareStatement(fetchQuery);

				fetch.setString(1, marketItem.getId().toString());

				preparedStatement.setString(1, marketItem.getId().toString());
				preparedStatement.setString(2, marketItem.getOwningCategory().toString());
				preparedStatement.setString(3, SerializeUtil.encodeItem(marketItem.getItem()));
				preparedStatement.setString(4, marketItem.getCurrency());
				preparedStatement.setString(5, SerializeUtil.encodeItem(marketItem.getCurrencyItem()));
				preparedStatement.setDouble(6, marketItem.getPrice());
				preparedStatement.setInt(7, marketItem.getStock());
				preparedStatement.setBoolean(8, marketItem.isPriceForAll());
				preparedStatement.setBoolean(9, marketItem.isAcceptingOffers());

				preparedStatement.executeUpdate();

				if (callback != null) {
					final ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractMarketItem(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void updateMarketItem(@NonNull final MarketItem marketItem, final Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			final String query = "UPDATE " + this.getTablePrefix() + "category_item SET currency = ?, price = ?, stock = ?, price_is_for_all = ?, currency_item = ?, accepting_offers = ? WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

				preparedStatement.setString(1, marketItem.getCurrency());
				preparedStatement.setDouble(2, marketItem.getPrice());
				preparedStatement.setInt(3, marketItem.getStock());
				preparedStatement.setBoolean(4, marketItem.isPriceForAll());
				preparedStatement.setString(5, SerializeUtil.encodeItem(marketItem.getCurrencyItem()));
				preparedStatement.setBoolean(6, marketItem.isAcceptingOffers());
				preparedStatement.setString(7, marketItem.getId().toString());

				int result = preparedStatement.executeUpdate();

				if (callback != null)
					callback.accept(null, result > 0);

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
				resolveCallback(callback, e);
			}
		}));
	}

	public void deleteMarketItem(@NonNull final MarketItem marketItem, Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "category_item WHERE id = ?")) {
				statement.setString(1, marketItem.getId().toString());

				int result = statement.executeUpdate();
				callback.accept(null, result > 0);

			} catch (Exception e) {
				resolveCallback(callback, e);
				e.printStackTrace();
			}
		}));
	}

	public void deleteMarketItems(@NonNull final Category category, Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "category_item WHERE owning_category = ?")) {
				statement.setString(1, category.getId().toString());

				int result = statement.executeUpdate();
				callback.accept(null, result > 0);

			} catch (Exception e) {
				resolveCallback(callback, e);
				e.printStackTrace();
			}
		}));
	}

	public void getMarketItems(@NonNull final Callback<List<MarketItem>> callback) {
		final List<MarketItem> marketItems = new ArrayList<>();

		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "category_item")) {
				final ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					final MarketItem marketItem = extractMarketItem(resultSet);
					marketItems.add(marketItem);
				}

				callback.accept(null, marketItems);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void getMarketItemsByCategory(@NonNull final UUID categoryId, @NonNull final Callback<List<MarketItem>> callback) {
		final List<MarketItem> marketItems = new ArrayList<>();

		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "category_item WHERE owning_category = ?")) {
				statement.setString(1, categoryId.toString());

				final ResultSet resultSet = statement.executeQuery();

				while (resultSet.next()) {
					final MarketItem marketItem = extractMarketItem(resultSet);
					marketItems.add(marketItem);
				}

				callback.accept(null, marketItems);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void createMarketUser(@NonNull final MarketUser marketUser, final Callback<MarketUser> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {

			final String query = "INSERT INTO " + this.getTablePrefix() + "user (id, last_known_name, bio, preferred_language, currency_format_country, last_seen_at) VALUES (?, ?, ?, ?, ?, ?)";
			final String fetchQuery = "SELECT * FROM " + this.getTablePrefix() + "user WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				final PreparedStatement fetch = connection.prepareStatement(fetchQuery);

				fetch.setString(1, marketUser.getUUID().toString());

				preparedStatement.setString(1, marketUser.getUUID().toString());
				preparedStatement.setString(2, marketUser.getLastKnownName());
				preparedStatement.setString(3, String.join(";;;", marketUser.getBio()));
				preparedStatement.setString(4, marketUser.getPreferredLanguage());
				preparedStatement.setString(5, marketUser.getCurrencyFormatCountry());
				preparedStatement.setLong(6, marketUser.getLastSeenAt());

				preparedStatement.executeUpdate();

				if (callback != null) {
					final ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractMarketUser(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void updateMarketUser(@NonNull final MarketUser marketUser, final Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			final String query = "UPDATE " + this.getTablePrefix() + "user SET last_known_name = ?, bio = ?, preferred_language = ?, currency_format_country = ?, last_seen_at = ? WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

				preparedStatement.setString(1, marketUser.getLastKnownName());
				preparedStatement.setString(2, String.join(";;;", marketUser.getBio()));
				preparedStatement.setString(3, marketUser.getPreferredLanguage());
				preparedStatement.setString(4, marketUser.getCurrencyFormatCountry());
				preparedStatement.setLong(5, marketUser.getLastSeenAt());
				preparedStatement.setString(6, marketUser.getUUID().toString());

				int result = preparedStatement.executeUpdate();

				if (callback != null)
					callback.accept(null, result > 0);

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void getMarketUsers(@NonNull final Callback<List<MarketUser>> callback) {
		final List<MarketUser> marketUsers = new ArrayList<>();

		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "user")) {
				final ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					final MarketUser marketUser = extractMarketUser(resultSet);
					marketUsers.add(marketUser);
				}

				callback.accept(null, marketUsers);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void createOfflineItemPayment(@NonNull final Payment payment, final Callback<Payment> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {

			final String query = "INSERT INTO " + this.getTablePrefix() + "offline_payment (id, payment_for, currency, amount, reason, received_at) VALUES (?, ?, ?, ?, ?, ?)";
			final String fetchQuery = "SELECT * FROM " + this.getTablePrefix() + "offline_payment WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				final PreparedStatement fetch = connection.prepareStatement(fetchQuery);

				fetch.setString(1, payment.getId().toString());

				preparedStatement.setString(1, payment.getId().toString());
				preparedStatement.setString(2, payment.getFor().toString());
				preparedStatement.setString(3, SerializeUtil.encodeItem(payment.getCurrency()));
				preparedStatement.setDouble(4, payment.getAmount());
				preparedStatement.setString(5, payment.getReason());
				preparedStatement.setLong(6, payment.getTimeCreated());

				preparedStatement.executeUpdate();

				if (callback != null) {
					final ResultSet res = fetch.executeQuery();
					res.next();
					callback.accept(null, extractOfflineItemPayment(res));
				}

			} catch (Exception e) {
				e.printStackTrace();
				resolveCallback(callback, e);
			}
		}));
	}

	public void getOfflineItemPayments(@NonNull final Callback<List<Payment>> callback) {
		final List<Payment> offlineItemPayments = new ArrayList<>();

		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "offline_payment")) {
				final ResultSet resultSet = statement.executeQuery();
				while (resultSet.next()) {
					final Payment offlinePayment = extractOfflineItemPayment(resultSet);
					offlineItemPayments.add(offlinePayment);
				}

				callback.accept(null, offlineItemPayments);
			} catch (Exception e) {
				resolveCallback(callback, e);
			}
		}));
	}

	public void deleteOfflineItemPayment(@NonNull final Payment payment, Callback<Boolean> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("DELETE FROM " + this.getTablePrefix() + "offline_payment WHERE id = ?")) {
				statement.setString(1, payment.getId().toString());

				int result = statement.executeUpdate();
				callback.accept(null, result > 0);

			} catch (Exception e) {
				resolveCallback(callback, e);
				e.printStackTrace();
			}
		}));
	}

	private AbstractMarket extractMarket(@NonNull final ResultSet resultSet) throws SQLException {
		final ArrayList<UUID> bannedUsers = new ArrayList<>();
		Layout homeLayout, categoryLayout;

		if (resultSet.getString("banned_users") != null) {
			final List<String> possibleUUIDS = Arrays.stream(resultSet.getString("banned_users").split(",")).toList();

			for (String id : possibleUUIDS) {
				try {
					bannedUsers.add(UUID.fromString(id));
				} catch (IllegalArgumentException ignored) {
					continue;
				}
			}
		}

		homeLayout = resultSet.getString("home_layout") != null ? MarketLayout.decodeJSON(resultSet.getString("home_layout")) : new HomeLayout();
		categoryLayout = resultSet.getString("category_layout") != null ? MarketLayout.decodeJSON(resultSet.getString("category_layout")) : new HomeLayout();

		return new PlayerMarket(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("owner")),
				resultSet.getString("owner_name"),
				resultSet.getString("display_name"),
				new ArrayList<>(List.of(resultSet.getString("description").split(";;;"))),
				new ArrayList<>(),
				new ArrayList<>(),
				bannedUsers,
				resultSet.getBoolean("open"),
				resultSet.getBoolean("close_when_out_of_stock"),
				homeLayout,
				categoryLayout,
				resultSet.getLong("created_at"),
				resultSet.getLong("updated_at")
		);
	}

	private Category extractCategory(@NonNull final ResultSet resultSet) throws SQLException {
		return new MarketCategory(
				UUID.fromString(resultSet.getString("owning_market")),
				UUID.fromString(resultSet.getString("id")),
				CompMaterial.matchCompMaterial(resultSet.getString("icon")).orElse(CompMaterial.CHEST).parseItem(),
				resultSet.getString("name"),
				resultSet.getString("display_name"),
				new ArrayList<>(List.of(resultSet.getString("description").split(";;;"))),
				new ArrayList<>(),
				resultSet.getLong("created_at"),
				resultSet.getLong("updated_at")
		);
	}

	private MarketItem extractMarketItem(@NonNull final ResultSet resultSet) throws SQLException {
		return new CategoryItem(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("owning_category")),
				SerializeUtil.decodeItem(resultSet.getString("item")),
				resultSet.getString("currency"),
				SerializeUtil.decodeItem(resultSet.getString("currency_item")),
				resultSet.getDouble("price"),
				resultSet.getInt("stock"),
				resultSet.getBoolean("price_is_for_all"),
				resultSet.getBoolean("accepting_offers")
		);
	}

	private MarketUser extractMarketUser(@NonNull final ResultSet resultSet) throws SQLException {
		return new MarketPlayer(
				UUID.fromString(resultSet.getString("id")),
				null,
				resultSet.getString("last_known_name"),
				new ArrayList<>(List.of(resultSet.getString("bio").split(";;;"))),
				resultSet.getString("preferred_language"),
				resultSet.getString("currency_format_country"),
				resultSet.getLong("last_seen_at")
		);
	}

	private Payment extractOfflineItemPayment(@NonNull final ResultSet resultSet) throws SQLException {
		return new OfflinePayment(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("payment_for")),
				SerializeUtil.decodeItem(resultSet.getString("currency")),
				resultSet.getDouble("amount"),
				resultSet.getString("reason"),
				resultSet.getLong("received_at")
		);
	}

//	final String query = "INSERT INTO " + this.getTablePrefix() + "user (id, last_known_name, bio, preferred_language, currency_format_country, last_seen_at) VALUES (?, ?, ?, ?, ?, ?)";

	private void resolveUpdateCallback(@Nullable UpdateCallback callback, @Nullable Exception ex) {
		if (callback != null) {
			callback.accept(ex);
		} else if (ex != null) {
			ex.printStackTrace();
		}
	}

	private void resolveCallback(@Nullable Callback<?> callback, @NotNull Exception ex) {
		if (callback != null) {
			callback.accept(ex, null);
		} else {
			ex.printStackTrace();
		}
	}
}
