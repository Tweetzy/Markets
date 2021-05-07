package ca.tweetzy.markets.api.events;

import ca.tweetzy.markets.market.Market;
import lombok.Getter;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:55 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MarketCreateEvent extends AbstractMarketEvent {

    @Getter
    final Player creator;

    public MarketCreateEvent(Player creator, Market market) {
        super(market, false);
        this.creator = creator;
    }
}
