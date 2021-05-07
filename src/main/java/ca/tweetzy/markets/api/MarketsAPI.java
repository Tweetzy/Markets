package ca.tweetzy.markets.api;

import ca.tweetzy.markets.market.MarketManager;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:42 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MarketsAPI {

    private MarketsAPI() {}

    private static MarketsAPI instance;

    public static MarketsAPI getInstance() {
        if (instance == null) {
            instance = new MarketsAPI();
        }
        return instance;
    }

}
