package ca.tweetzy.markets.market.contents;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 30 2021
 * Time Created: 3:49 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class MarketItem implements Serializable {

    private UUID id;
    private UUID categoryId;

    private ItemStack itemStack;
    private double price;

    private boolean priceForStack;

    public MarketItem(UUID id, ItemStack itemStack, double price, boolean priceForStack, UUID categoryId) {
        this.id = id;
        this.itemStack = itemStack;
        this.price = price;
        this.priceForStack = priceForStack;
        this.categoryId = categoryId;
    }

    public MarketItem(MarketCategory category, ItemStack itemStack, double price, boolean priceForStack) {
        this(UUID.randomUUID(), itemStack, price, priceForStack, category.getId());
    }
}
