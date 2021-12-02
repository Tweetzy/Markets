package ca.tweetzy.markets.transaction;

import ca.tweetzy.markets.api.MarketsAPI;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 03 2021
 * Time Created: 2:07 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
public class Payment implements Serializable {

	private final UUID to;
	private final byte[] item;

	public Payment(UUID to, ItemStack item) {
		this.to = to;
		this.item = MarketsAPI.getInstance().serializeItem(item);
	}

	public ItemStack getItem() {
		return MarketsAPI.getInstance().deserializeItem(this.item);
	}
}
