package ca.tweetzy.markets.economy;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 06 2021
 * Time Created: 3:19 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class Currency implements Serializable {

    private ItemStack item;
    private int amount;

    public Currency(ItemStack item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    public Currency() {
        this(null, 0);
    }
}
