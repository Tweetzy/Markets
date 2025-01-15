package ca.tweetzy.markets.api.market.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public abstract class AbstractMarket implements Market {

	@Getter
	@Setter
	protected MarketType marketType;
}
