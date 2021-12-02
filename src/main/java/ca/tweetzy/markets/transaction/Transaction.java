package ca.tweetzy.markets.transaction;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 06 2021
 * Time Created: 1:24 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class Transaction {

	private UUID id;
	private UUID marketId;
	private UUID purchaser;
	private ItemStack itemStack;
	private int purchaseQty;
	private double finalPrice;
	private long time;

	public Transaction(UUID id, UUID marketId, UUID purchaser, ItemStack itemStack, int purchaseQty, double finalPrice) {
		this.id = id;
		this.marketId = marketId;
		this.purchaser = purchaser;
		this.itemStack = itemStack;
		this.purchaseQty = purchaseQty;
		this.finalPrice = finalPrice;
		this.time = System.currentTimeMillis();
	}

	public Transaction(UUID marketId, UUID purchaser, ItemStack itemStack, int purchaseQty, double finalPrice) {
		this(UUID.randomUUID(), marketId, purchaser, itemStack, purchaseQty, finalPrice);
	}
}
