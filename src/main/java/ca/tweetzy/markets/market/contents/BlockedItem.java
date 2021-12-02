package ca.tweetzy.markets.market.contents;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 27 2021
 * Time Created: 4:28 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class BlockedItem {

	private UUID id;
	private ItemStack item;

	public BlockedItem(UUID id, ItemStack item) {
		this.id = id;
		this.item = item;
	}
}
