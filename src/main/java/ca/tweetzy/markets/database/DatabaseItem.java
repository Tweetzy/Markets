package ca.tweetzy.markets.database;

import ca.tweetzy.markets.api.MarketsAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 13 2021
 * Time Created: 5:29 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class DatabaseItem implements Serializable {

	private byte[] item;

	public DatabaseItem(ItemStack item) {
		this.item = MarketsAPI.getInstance().serializeItem(item);
	}

	public ItemStack getItem() {
		return MarketsAPI.getInstance().deserializeItem(this.item);
	}
}
