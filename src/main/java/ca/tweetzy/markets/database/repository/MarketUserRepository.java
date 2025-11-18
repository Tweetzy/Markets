package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.repository.BaseRepository;
import ca.tweetzy.markets.impl.MarketPlayer;

import java.util.UUID;

public final class MarketUserRepository extends BaseRepository<MarketPlayer, UUID> {
    
    public MarketUserRepository(DatabaseConnector connector, String tablePrefix) {
        super(connector, tablePrefix, "user", new MarketsEntityMapper<>(MarketPlayer.class));
    }
}

