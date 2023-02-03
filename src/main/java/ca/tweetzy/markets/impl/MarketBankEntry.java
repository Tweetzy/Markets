package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.api.market.BankEntry;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class MarketBankEntry implements BankEntry {

	private final UUID id;
	private final UUID owner;
	private final ItemStack item;
	private int quantity;

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@Override
	public @NonNull UUID getOwner() {
		return this.owner;
	}

	@Override
	public @NonNull ItemStack getItem() {
		return this.item;
	}

	@Override
	public int getQuantity() {
		return this.quantity;
	}

	@Override
	public void setQuantity(int amount) {
		this.quantity = amount;
	}

	@Override
	public void store(@NonNull Consumer<BankEntry> stored) {

	}
}
