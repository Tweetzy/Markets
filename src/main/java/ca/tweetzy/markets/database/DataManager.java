package ca.tweetzy.markets.database;

import ca.tweetzy.flight.database.Callback;
import ca.tweetzy.flight.database.DataManagerAbstract;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.UpdateCallback;
import ca.tweetzy.markets.api.market.AbstractMarket;
import ca.tweetzy.markets.impl.market.PlayerMarket;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DataManager extends DataManagerAbstract {

	public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
		super(databaseConnector, plugin);
	}

	public void createMarket(@NonNull final AbstractMarket market, final Callback<AbstractMarket> callback) {
		this.runAsync(() -> this.databaseConnector.connect(connection -> {

			final String query = "INSERT INTO " + this.getTablePrefix() + "market (id, type, display_name, description, owner_uuid, owner_name, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			final String fetchQuery = "SELECT * FROM " + this.getTablePrefix() + "market WHERE id = ?";

			try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
				final PreparedStatement fetch = connection.prepareStatement(fetchQuery);

				fetch.setString(1, market.toString());

				preparedStatement.setString(1, market.toString());
				preparedStatement.setString(2, market.getMarketType().name());
				preparedStatement.setString(3, market.getDisplayName());
				preparedStatement.setString(4, String.join(";;;", market.getDescription()));
				preparedStatement.setString(5, market.getOwnerUUID().toString());
				preparedStatement.setString(6, market.getOwnerName());
				preparedStatement.setLong(7, market.getTimeCreated());
				preparedStatement.setLong(8, market.getLastUpdated());

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

	public void getMarkets(@NonNull final Callback<List<AbstractMarket>> callback) {
		final List<AbstractMarket> markets = new ArrayList<>();

		this.runAsync(() -> this.databaseConnector.connect(connection -> {
			try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + this.getTablePrefix() + "market")) {
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

	private AbstractMarket extractMarket(@NonNull final ResultSet resultSet) throws SQLException {
		return new PlayerMarket(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("owner_uuid")),
				resultSet.getString("owner_name"),
				resultSet.getString("display_name"),
				new ArrayList<>(List.of(resultSet.getString("description").split(";;;"))),
				new ArrayList<>(),
				new ArrayList<>(),
				resultSet.getLong("created_at"),
				resultSet.getLong("updated_at")
		);
	}

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
