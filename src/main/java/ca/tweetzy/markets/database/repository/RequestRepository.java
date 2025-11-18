package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.repository.BaseRepository;
import ca.tweetzy.markets.impl.MarketRequest;

import java.util.UUID;

public final class RequestRepository extends BaseRepository<MarketRequest, UUID> {
    
    public RequestRepository(DatabaseConnector connector, String tablePrefix) {
        super(connector, tablePrefix, "request", new MarketsEntityMapper<>(MarketRequest.class));
    }
}

