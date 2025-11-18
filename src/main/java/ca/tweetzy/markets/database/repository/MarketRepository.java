package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.repository.BaseRepository;
import ca.tweetzy.markets.impl.PlayerMarket;

import java.util.UUID;

public final class MarketRepository extends BaseRepository<PlayerMarket, UUID> {
    
    public MarketRepository(DatabaseConnector connector, String tablePrefix) {
        super(connector, tablePrefix, "markets", new MarketsEntityMapper<>(PlayerMarket.class));
    }
}

