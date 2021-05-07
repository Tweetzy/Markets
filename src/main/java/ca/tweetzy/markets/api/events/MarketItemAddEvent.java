package ca.tweetzy.markets.api.events;

import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketItem;
import lombok.Getter;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:56 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MarketItemAddEvent extends AbstractMarketEvent {

    @Getter
    final MarketItem marketItem;

    public MarketItemAddEvent(Market market, MarketItem marketItem) {
        super(market, false);
        this.marketItem = marketItem;
    }
}
