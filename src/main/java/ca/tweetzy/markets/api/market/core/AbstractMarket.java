package ca.tweetzy.markets.api.market.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class AbstractMarket implements Market {

	@Getter
	protected MarketType marketType;
}
