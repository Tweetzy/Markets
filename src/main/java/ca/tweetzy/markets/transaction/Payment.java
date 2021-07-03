package ca.tweetzy.markets.transaction;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 03 2021
 * Time Created: 2:07 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
public class Payment {

    private final UUID to;
    private final ItemStack item;

    public Payment(UUID to, ItemStack item) {
        this.to = to;
        this.item = item;
    }
}
