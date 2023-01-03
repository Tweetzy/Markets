package ca.tweetzy.markets.api.market;

import lombok.NonNull;

import java.util.UUID;

public interface MarketItem {

	@NonNull UUID getOwningCategory();


}
