package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.database.annotations.Column;
import ca.tweetzy.flight.database.annotations.Id;
import ca.tweetzy.flight.database.annotations.Nested;
import ca.tweetzy.flight.database.annotations.Table;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.BankEntry;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

@Table("bank_entry")
public final class MarketBankEntry implements BankEntry {

	@Id
	@Column("id")
	private UUID id;
	
	@Column("owner")
	private UUID owner;
	
	@Nested
	@Column("item")
	private ItemStack item;
	
	@Column("quantity")
	private int quantity;

	@Column("currency")
	private String currency;
	
	@Nested
	@Column("currency_item")
	private ItemStack currencyItem;
	
	@Column("price")
	private double price;

	public MarketBankEntry() {
	}

	public MarketBankEntry(@NonNull UUID id, @NonNull UUID owner, @NonNull ItemStack item, int quantity, @NonNull String currency, ItemStack currencyItem, double price) {
		this.id = id;
		this.owner = owner;
		this.item = item;
		this.quantity = quantity;
		this.currency = currency;
		this.currencyItem = currencyItem;
		this.price = price;
	}

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
	public String getCurrency() {
		return this.currency;
	}

	@Override
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	@Override
	public ItemStack getCurrencyItem() {
		return this.currencyItem;
	}

	@Override
	public void setCurrencyItem(ItemStack currencyItem) {
		this.currencyItem = currencyItem;
	}

	@Override
	public double getPrice() {
		return this.price;
	}

	@Override
	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public void store(@NonNull Consumer<BankEntry> stored) {
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().createBankEntry(this, (error, created) -> {
			if (error == null && created != null) {
				// Log successful bank entry creation
				if (Markets.getTransactionLogger() != null) {
					org.bukkit.OfflinePlayer owner = org.bukkit.Bukkit.getOfflinePlayer(this.owner);
					Markets.getTransactionLogger().logBankEntryCreate(owner.getName(), 
						ca.tweetzy.flight.utils.ItemUtil.getItemName(this.item), 
						this.quantity, this.currency, this.price);
				}
				stored.accept(created);
			} else if (error != null) {
				stored.accept(null);
			}
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getBankEntryRepository().deleteById(this.id, (error, deleted) -> {
			if (deleted != null && deleted) {
				Markets.getBankManager().remove(this);
			}

			if (syncResult != null)
				syncResult.accept(error == null && deleted != null && deleted ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}

	@Override
	public void sync(@Nullable Consumer<SynchronizeResult> syncResult) {
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().updateBankEntry(this, (error, success) -> {
			if (error == null && success) {
				// Log successful bank entry update
				if (Markets.getTransactionLogger() != null) {
					org.bukkit.OfflinePlayer owner = org.bukkit.Bukkit.getOfflinePlayer(this.owner);
					Markets.getTransactionLogger().logBankEntryUpdate(owner.getName(), 
						this.id.toString(), 
						ca.tweetzy.flight.utils.ItemUtil.getItemName(this.item), 
						this.quantity);
				}
			}
			
			if (syncResult != null)
				syncResult.accept(error == null && success ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}
}
