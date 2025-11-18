package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.repository.BaseRepository;
import ca.tweetzy.markets.impl.MarketBankEntry;

import java.util.UUID;

public final class BankEntryRepository extends BaseRepository<MarketBankEntry, UUID> {
    
    public BankEntryRepository(DatabaseConnector connector, String tablePrefix) {
        super(connector, tablePrefix, "bank_entry", new MarketsEntityMapper<>(MarketBankEntry.class));
    }
}

