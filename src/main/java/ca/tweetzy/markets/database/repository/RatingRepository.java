package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.repository.BaseRepository;
import ca.tweetzy.markets.impl.MarketRating;

import java.util.UUID;

public final class RatingRepository extends BaseRepository<MarketRating, UUID> {
    
    public RatingRepository(DatabaseConnector connector, String tablePrefix) {
        super(connector, tablePrefix, "review", new MarketsEntityMapper<>(MarketRating.class));
    }
}

