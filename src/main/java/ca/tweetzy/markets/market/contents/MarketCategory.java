package ca.tweetzy.markets.market.contents;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.markets.Markets;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 30 2021
 * Time Created: 3:48 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Setter
@Getter
public class MarketCategory implements Serializable {

    private UUID marketId;
    private UUID id;
    private String name;
    private String displayName;
    private String description;
    private Material icon;

    // TODO implement a sale system for a category.
    private boolean saleActive;
    private double saleDiscount;

    private List<MarketItem> items;

    public MarketCategory(UUID id, String name, String displayName, String description, ItemStack icon, List<MarketItem> items, boolean saleActive, double saleDiscount) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon.getType();
        this.items = items;
        this.saleActive = saleActive;
        this.saleDiscount = saleDiscount;
    }

    public MarketCategory(String name) {
        this(UUID.randomUUID(), name, name, Markets.getInstance().getLocale().getMessage("misc.default category description").getMessage(), XMaterial.CHEST.parseItem(), new ArrayList<>(), false, 0D);
    }

    public MarketCategory(String name, String description) {
        this(UUID.randomUUID(), name, name, description, XMaterial.CHEST.parseItem(), new ArrayList<>(), false, 0D);
    }
}
