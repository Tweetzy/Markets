package ca.tweetzy.markets.market.contents;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.markets.Markets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 30 2021
 * Time Created: 3:49 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class MarketItem {

    private UUID id;
    private UUID categoryId;

    private ItemStack itemStack;
    private ItemStack currencyItem;
    private boolean useItemCurrency;
    private double price;

    private boolean priceForStack;

    public MarketItem(UUID id, ItemStack itemStack, ItemStack currencyItem, double price, boolean useItemCurrency, boolean priceForStack, UUID categoryId) {
        this.id = id;
        this.itemStack = itemStack;
        this.price = price;
        this.priceForStack = priceForStack;
        this.categoryId = categoryId;
        this.currencyItem = currencyItem;
        this.useItemCurrency = useItemCurrency;
    }

    public MarketItem(MarketCategory category, ItemStack itemStack, double price, boolean priceForStack) {
        this(UUID.randomUUID(), itemStack, XMaterial.AIR.parseItem(), price, false, priceForStack, category.getId());
    }

    public String getTranslatedPriceForStack() {
        return priceForStack ? Markets.getInstance().getLocale().getMessage("misc.price is for stack.true").getMessage() : Markets.getInstance().getLocale().getMessage("misc.price is for stack.false").getMessage();
    }
}
