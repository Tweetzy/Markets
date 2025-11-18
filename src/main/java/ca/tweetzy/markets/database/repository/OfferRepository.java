package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.repository.BaseRepository;
import ca.tweetzy.markets.impl.MarketOffer;

import java.util.UUID;

public final class OfferRepository extends BaseRepository<MarketOffer, UUID> {
    
    public OfferRepository(DatabaseConnector connector, String tablePrefix) {
        super(connector, tablePrefix, "offer", new MarketsEntityMapper<>(MarketOffer.class));
    }
}

