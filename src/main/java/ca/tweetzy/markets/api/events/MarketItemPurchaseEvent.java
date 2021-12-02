package ca.tweetzy.markets.api.events;

import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketItem;
import lombok.Getter;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 06 2021
 * Time Created: 1:37 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MarketItemPurchaseEvent extends AbstractMarketEvent {

	@Getter
	final MarketItem marketItem;

	@Getter
	final double finalPrice;

	@Getter
	final int purchaseQty;

	public MarketItemPurchaseEvent(Market market, MarketItem marketItem, double finalPrice, int purchaseQty) {
		super(market, false);
		this.marketItem = marketItem;
		this.finalPrice = finalPrice;
		this.purchaseQty = purchaseQty;
	}
}
