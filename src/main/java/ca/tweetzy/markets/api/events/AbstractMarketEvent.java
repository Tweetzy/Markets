package ca.tweetzy.markets.api.events;

import ca.tweetzy.markets.market.Market;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 4:56 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class AbstractMarketEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    protected Market market;

    public AbstractMarketEvent(Market market, boolean async) {
        super(async);
        this.market = market;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
