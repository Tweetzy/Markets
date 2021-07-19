package ca.tweetzy.markets.market;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.market.contents.MarketCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 30 2021
 * Time Created: 3:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class Market {

    private UUID id;
    private UUID owner;
    private String ownerName;

    private String name;
    private String description;
    private MarketType marketType;
    private boolean open;
    private boolean unpaid;

    private long createdAt;
    private long updatedAt;

    private List<MarketRating> ratings;
    private List<MarketCategory> categories;

    public Market(UUID id, UUID owner, String ownerName, String name, MarketType marketType) {
        this.id = id;
        this.owner = owner;
        this.ownerName = ownerName;
        this.name = name;
        this.description = Markets.getInstance().getLocale().getMessage("misc.default market description").getMessage();
        this.marketType = marketType;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.categories = new ArrayList<>();
        this.ratings = new ArrayList<>();
        this.open = true;
        this.unpaid = false;
    }

    public Market(UUID owner, String ownerName, String name) {
        this(UUID.randomUUID(), owner, ownerName, name, MarketType.PLAYER);
    }
}
