package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.repository.BaseRepository;
import ca.tweetzy.markets.impl.CategoryItem;

import java.util.UUID;

public final class MarketItemRepository extends BaseRepository<CategoryItem, UUID> {
    
    public MarketItemRepository(DatabaseConnector connector, String tablePrefix) {
        super(connector, tablePrefix, "category_item", new MarketsEntityMapper<>(CategoryItem.class));
    }
}

