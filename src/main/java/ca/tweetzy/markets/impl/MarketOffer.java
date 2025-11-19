package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.database.annotations.Column;
import ca.tweetzy.flight.database.annotations.Id;
import ca.tweetzy.flight.database.annotations.Nested;
import ca.tweetzy.flight.database.annotations.Table;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.currency.TransactionResult;
import ca.tweetzy.markets.api.market.BankEntry;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.api.market.offer.Offer;
import ca.tweetzy.markets.api.market.offer.OfferRejectReason;
import ca.tweetzy.markets.model.sync.StockReservationManager;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Table("offer")
public final class MarketOffer implements Offer {

	@Id
	@Column("id")
	private UUID uuid;
	
	@Column("sender")
	private UUID sender;
	
	@Column("sender_name")
	private String senderName;
	
	@Column("offer_to")
	private UUID offerTo;
	
	@Column("market_item")
	private UUID marketItem;
	
	@Column("request_amount")
	private int requestAmount;
	
	@Column("currency")
	private String currency;
	
	@Nested
	@Column("currency_item")
	private ItemStack currencyItem;
	
	@Column("offered_amount")
	private double offeredAmount;
	
	@Column("offered_at")
	private long offeredAt;

	public MarketOffer() {
	}

	public MarketOffer(@NonNull UUID uuid, @NonNull UUID sender, @NonNull String senderName, @NonNull UUID offerTo, @NonNull UUID marketItem, int requestAmount, @NonNull String currency, ItemStack currencyItem, double offeredAmount, long offeredAt) {
		this.uuid = uuid;
		this.sender = sender;
		this.senderName = senderName;
		this.offerTo = offerTo;
		this.marketItem = marketItem;
		this.requestAmount = requestAmount;
		this.currency = currency;
		this.currencyItem = currencyItem;
		this.offeredAmount = offeredAmount;
		this.offeredAt = offeredAt;
	}

	public MarketOffer(@NonNull final Player sender, @NonNull final Market market, @NonNull final MarketItem marketItem) {
		this(
				UUID.randomUUID(),
				sender.getUniqueId(),
				sender.getName(),
				market.getOwnerUUID(),
				marketItem.getId(),
				marketItem.getStock(),
				marketItem.getCurrency(),
				marketItem.getCurrencyItem(),
				marketItem.getPrice() * marketItem.getStock(),
				System.currentTimeMillis()
		);
	}

	@Override

	public @NonNull UUID getId() {
		return this.uuid;
	}

	@Override
	public @NonNull UUID getOfferSender() {
		return this.sender;
	}

	@Override
	public @NonNull String getOfferSenderName() {
		return this.senderName;
	}

	@Override
	public @NonNull UUID getOfferFor() {
		return this.offerTo;
	}

	@Override
	public @NonNull UUID getMarketItem() {
		return this.marketItem;
	}

	@Override
	public int getRequestAmount() {
		return this.requestAmount;
	}

	@Override
	public @NonNull String getCurrency() {
		return this.currency;
	}

	@Override
	public ItemStack getCurrencyItem() {
		return this.currencyItem;
	}

	@Override
	public double getOfferedAmount() {
		return this.offeredAmount;
	}

	@Override
	public void setCurrency(@NonNull final String currency) {
		this.currency = currency;
	}

	@Override
	public void setCurrencyItem(@NonNull ItemStack currencyItem) {
		this.currencyItem = currencyItem;
	}

	@Override
	public void setOfferedAmount(double amount) {
		this.offeredAmount = amount;
	}

	@Override
	public void setRequestAmount(int amount) {
		this.requestAmount = amount;
	}

	@Override
	public long getTimeCreated() {
		return this.offeredAt;
	}

	@Override
	public long getLastUpdated() {
		return this.offeredAt;
	}

	@Override
	public void accept(@NonNull Consumer<TransactionResult> result) {
		// Acquire distributed lock for offer acceptance to prevent duplicate acceptances
		StockReservationManager reservationManager = Markets.getStockReservationManager();
		String offerLockKey = "offer:" + this.uuid + ":accept";
		boolean offerLockAcquired = reservationManager == null || 
			(Markets.getDataManager().getRedisLockManager() != null && 
			 Markets.getDataManager().getRedisLockManager().acquireLock(offerLockKey, 30));
		
		if (!offerLockAcquired) {
			result.accept(TransactionResult.ERROR);
			return;
		}
		
		try {
			final MarketItem locatedItem = Markets.getCategoryItemManager().getByUUID(this.marketItem);

			if (locatedItem == null) {
				unStore(deleteResult -> {
					if (deleteResult == SynchronizeResult.SUCCESS) {
						result.accept(TransactionResult.NO_LONGER_AVAILABLE);
					}
				});
				return;
			}

			// Check if item is being purchased - prevent offer acceptance during purchase
			if (locatedItem.isBeingEdited()) {
				unStore(deleteResult -> {
					if (deleteResult == SynchronizeResult.SUCCESS) {
						result.accept(TransactionResult.FAILED_OUT_OF_STOCK);
					}
				});
				return;
			}

			// Also acquire lock for the market item to prevent stock issues
			boolean itemLockAcquired = reservationManager == null || 
				reservationManager.reserveStock(this.marketItem, this.requestAmount);
			
			if (!itemLockAcquired) {
				result.accept(TransactionResult.FAILED_OUT_OF_STOCK);
				return;
			}
			
			try {
				// Re-check beingEdited after acquiring lock (race condition protection)
				if (locatedItem.isBeingEdited()) {
					unStore(deleteResult -> {
						if (deleteResult == SynchronizeResult.SUCCESS) {
							result.accept(TransactionResult.FAILED_OUT_OF_STOCK);
						}
					});
					return;
				}

				// Reload item to get latest stock value
				MarketItem reloadedItem = Markets.getCategoryItemManager().getByUUID(this.marketItem);
				int currentStock = (reloadedItem != null) ? reloadedItem.getStock() : locatedItem.getStock();

				if (currentStock < this.requestAmount) {
					unStore(deleteResult -> {
						if (deleteResult == SynchronizeResult.SUCCESS) {
							result.accept(TransactionResult.FAILED_OUT_OF_STOCK);
						}
					});
					return;
				}

				final OfflinePlayer offerSender = Bukkit.getOfflinePlayer(this.sender);
				final OfflinePlayer itemOwner = Bukkit.getOfflinePlayer(this.offerTo);

				// Validate currency format before splitting
				if (this.currency == null || this.currency.isEmpty() || !this.currency.contains("/")) {
					unStore(deleteResult -> {
						if (deleteResult == SynchronizeResult.SUCCESS) {
							result.accept(TransactionResult.ERROR);
						}
					});
					return;
				}

				final String[] currencyParts = this.currency.split("/");
				if (currencyParts.length < 2 || currencyParts[0].isEmpty() || currencyParts[1].isEmpty()) {
					unStore(deleteResult -> {
						if (deleteResult == SynchronizeResult.SUCCESS) {
							result.accept(TransactionResult.ERROR);
						}
					});
					return;
				}

				final String currencyPlugin = currencyParts[0];
				final String currencyName = currencyParts[1];

				boolean hasEnoughMoney = isCurrencyOfItem() ?
						Markets.getBankManager().getEntryCountByPlayer(this.sender, this.currencyItem) >= (int) this.offeredAmount :
						Markets.getCurrencyManager().has(offerSender, currencyPlugin, currencyName, this.offeredAmount);

				if (!hasEnoughMoney) {
					unStore(deleteResult -> {
						if (deleteResult == SynchronizeResult.SUCCESS) {
							result.accept(TransactionResult.FAILED_NO_MONEY);
						}
					});
					return;
				}

				if (isCurrencyOfItem()) {
					final BankEntry entry = Markets.getBankManager().getEntryByPlayer(this.sender, this.currencyItem);
					final int newTotal = entry.getQuantity() - (int) this.offeredAmount;

					if (newTotal <= 0) {
						entry.unStore(entryResult -> {
							if (entryResult == SynchronizeResult.FAILURE) return;

							// give the buyer their items
							giveItemAndCleanup(locatedItem, result);

							// give seller their items
							giveSellerItemsOrMakePayment(itemOwner);

						});
					} else {
						entry.setQuantity(newTotal);
						entry.sync(entryResult -> {
							if (entryResult == SynchronizeResult.FAILURE) return;

							// give the buyer their items
							giveItemAndCleanup(locatedItem, result);

							// give seller their items
							giveSellerItemsOrMakePayment(itemOwner);
						});
					}
				} else {
					Markets.getCurrencyManager().deposit(Bukkit.getOfflinePlayer(this.offerTo), currencyPlugin, currencyName, this.offeredAmount);
					Markets.getCurrencyManager().withdraw(offerSender, currencyPlugin, currencyName, this.offeredAmount);

					giveItemAndCleanup(locatedItem, result);
				}
			} finally {
				// Release item lock
				if (reservationManager != null) {
					reservationManager.releaseReservation(this.marketItem);
				}
			}
		} finally {
			// Release offer lock
			if (Markets.getDataManager().getRedisLockManager() != null) {
				Markets.getDataManager().getRedisLockManager().releaseLock(offerLockKey);
			}
		}
	}

	private void giveItemAndCleanup(@NonNull final MarketItem marketItem, @NonNull Consumer<TransactionResult> transactionResultConsumer) {
		unStore(result -> {
			if (result == SynchronizeResult.SUCCESS)
				transactionResultConsumer.accept(TransactionResult.SUCCESS);
		});

		// Update stock atomically - reload to get latest value
		MarketItem reloadedItem = Markets.getCategoryItemManager().getByUUID(marketItem.getId());
		int currentStock = (reloadedItem != null) ? reloadedItem.getStock() : marketItem.getStock();

		int calculatedNewTotal = currentStock - this.requestAmount;
		final int finalNewTotal;
		if (calculatedNewTotal < 0) {
			Markets.getInstance().getLogger().warning("Stock update in offer acceptance would result in negative stock for item " + marketItem.getId() + ". Current: " + currentStock + ", Requested: " + this.requestAmount);
			finalNewTotal = 0;
		} else {
			finalNewTotal = calculatedNewTotal;
		}

		marketItem.setStock(finalNewTotal);
		marketItem.sync(syncResult -> {
			if (syncResult == SynchronizeResult.FAILURE) {
				Markets.getInstance().getLogger().severe("Failed to sync stock update in offer acceptance for item " + marketItem.getId() + ". Stock may be inconsistent! Expected stock: " + finalNewTotal);
			}
		});

		Markets.getOfflineItemPaymentManager().create(
				this.sender,
				QuickItem.of(marketItem.getItem().clone()).amount(1).make(),
				(int) this.requestAmount,
				TranslationManager.string(Translations.OFFER_ACCEPTED_PAYMENT), success -> {
				});
	}

	private void giveSellerItemsOrMakePayment(@NonNull final OfflinePlayer itemOwner) {
		Markets.getOfflineItemPaymentManager().create(
				itemOwner.getUniqueId(),
				QuickItem.of(this.currencyItem).amount(1).make(),
				(int) this.offeredAmount,
				TranslationManager.string(Translations.OFFER_ACCEPTED_PAYMENT), success -> {
				});
	}

	@Override
	public void reject(@NonNull BiConsumer<TransactionResult, OfferRejectReason> result) {
		final MarketItem locatedItem = Markets.getCategoryItemManager().getByUUID(this.marketItem);

		unStore(deleteResult -> {
			if (deleteResult == SynchronizeResult.SUCCESS) {
				result.accept(
						TransactionResult.SUCCESS,
						locatedItem == null ? OfferRejectReason.ITEM_NO_LONGER_AVAILABLE : locatedItem.getStock() < requestAmount ? OfferRejectReason.INSUFFICIENT_STOCK : OfferRejectReason.NOT_ACCEPTED
				);
			}
		});
	}

	@Override
	public void store(@NonNull Consumer<Offer> stored) {
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().createOffer(this, (error, created) -> {
			if (error == null && created != null) {
				stored.accept(created);
			} else if (error != null) {
				stored.accept(null);
			} else {
				// created is null but no error - this shouldn't happen but handle it
				stored.accept(null);
			}
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getOfferRepository().deleteById(this.uuid, (error, deleted) -> {
			if (deleted != null && deleted) {
				Markets.getOfferManager().remove(this);
			}

			if (syncResult != null)
				syncResult.accept(error == null && deleted != null && deleted ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}
}
