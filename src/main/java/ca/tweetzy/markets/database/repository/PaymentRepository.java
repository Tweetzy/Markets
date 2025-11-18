package ca.tweetzy.markets.database.repository;

import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.repository.BaseRepository;
import ca.tweetzy.markets.impl.OfflinePayment;

import java.util.UUID;

public final class PaymentRepository extends BaseRepository<OfflinePayment, UUID> {
    
    public PaymentRepository(DatabaseConnector connector, String tablePrefix) {
        super(connector, tablePrefix, "offline_payment", new MarketsEntityMapper<>(OfflinePayment.class));
    }
}

