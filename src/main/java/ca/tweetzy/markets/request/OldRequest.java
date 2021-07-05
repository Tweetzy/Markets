package ca.tweetzy.markets.request;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 05 2021
 * Time Created: 2:39 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
@Deprecated
public class OldRequest {

    private UUID id;
    private UUID requester;
    private ItemStack item;
    private int amount;
    private double price;
    private boolean fulfilled;

    public OldRequest(UUID id, UUID requester, ItemStack item, int amount, double price) {
        this.id = id;
        this.requester = requester;
        this.item = item;
        this.amount = amount;
        this.price = price;
        this.fulfilled = false;
    }

    public OldRequest(UUID requester, ItemStack item, int amount, double price) {
        this(UUID.randomUUID(), requester, item, amount, price);
    }
}
