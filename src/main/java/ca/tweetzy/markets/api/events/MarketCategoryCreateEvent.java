package ca.tweetzy.markets.api.events;

import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import lombok.Getter;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:55 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MarketCategoryCreateEvent extends AbstractMarketEvent {

	@Getter
	final MarketCategory marketCategory;

	public MarketCategoryCreateEvent(Market market, MarketCategory marketCategory) {
		super(market, false);
		this.marketCategory = marketCategory;
	}
}
