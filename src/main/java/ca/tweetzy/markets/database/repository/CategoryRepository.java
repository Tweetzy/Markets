package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.repository.BaseRepository;
import ca.tweetzy.markets.impl.MarketCategory;

import java.util.UUID;

public final class CategoryRepository extends BaseRepository<MarketCategory, UUID> {
    
    public CategoryRepository(DatabaseConnector connector, String tablePrefix) {
        super(connector, tablePrefix, "category", new MarketsEntityMapper<>(MarketCategory.class));
    }
}

