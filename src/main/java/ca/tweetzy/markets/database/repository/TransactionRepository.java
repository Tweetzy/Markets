package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.repository.BaseRepository;
import ca.tweetzy.markets.impl.MarketTransaction;

import java.util.UUID;

public final class TransactionRepository extends BaseRepository<MarketTransaction, UUID> {
    
    public TransactionRepository(DatabaseConnector connector, String tablePrefix) {
        super(connector, tablePrefix, "transaction", new MarketsEntityMapper<>(MarketTransaction.class));
    }
}

