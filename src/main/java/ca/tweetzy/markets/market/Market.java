package ca.tweetzy.markets.market;

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

    private long createdAt;
    private long updatedAt;

    private List<MarketCategory> categories;

    public Market(UUID id, UUID owner, String ownerName, String name, MarketType marketType) {
        this.id = id;
        this.owner = owner;
        this.ownerName = ownerName;
        this.name = name;
        this.description = "&7Welcome to my market";
        this.marketType = marketType;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.categories = new ArrayList<>();
        this.open = true;
    }

    public Market(UUID owner, String ownerName, String name) {
        this(UUID.randomUUID(), owner, ownerName, name, MarketType.PLAYER);
    }
}
