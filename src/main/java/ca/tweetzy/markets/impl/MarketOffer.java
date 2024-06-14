package ca.tweetzy.markets.impl;

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
import ca.tweetzy.markets.settings.Translations;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@AllArgsConstructor
public final class MarketOffer implements Offer {

	private final UUID uuid;
	private final UUID sender;
	private final String senderName;
	private final UUID offerTo;
	private final UUID marketItem;
	private final int requestAmount;
	private String currency;
	private ItemStack currencyItem;
	private double offeredAmount;
	private final long offeredAt;

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
	public long getTimeCreated() {
		return this.offeredAt;
	}

	@Override
	public long getLastUpdated() {
		return this.offeredAt;
	}

	@Override
	public void accept(@NonNull Consumer<TransactionResult> result) {
		final MarketItem locatedItem = Markets.getCategoryItemManager().getByUUID(this.marketItem);

		if (locatedItem == null) {
			unStore(deleteResult -> {
				if (deleteResult == SynchronizeResult.SUCCESS) {
					result.accept(TransactionResult.NO_LONGER_AVAILABLE);
				}
			});
			return;
		}

		if (locatedItem.getStock() < this.requestAmount) {
			unStore(deleteResult -> {
				if (deleteResult == SynchronizeResult.SUCCESS) {
					result.accept(TransactionResult.FAILED_OUT_OF_STOCK);
				}
			});
			return;
		}

		final OfflinePlayer offerSender = Bukkit.getOfflinePlayer(this.sender);
		final OfflinePlayer itemOwner = Bukkit.getOfflinePlayer(this.offerTo);

		final String currencyPlugin = this.currency.split("/")[0];
		final String currencyName = this.currency.split("/")[1];

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
	}

	private void giveItemAndCleanup(@NonNull final MarketItem marketItem, @NonNull Consumer<TransactionResult> transactionResultConsumer) {
		unStore(result -> {
			if (result == SynchronizeResult.SUCCESS)
				transactionResultConsumer.accept(TransactionResult.SUCCESS);
		});

		// delete the item
		marketItem.unStore(result -> {
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
		Markets.getDataManager().createOffer(this, (error, created) -> {
			if (error == null) {
				stored.accept(created);
			}
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getDataManager().deleteOffer(this, (error, updateStatus) -> {
			if (updateStatus) {
				Markets.getOfferManager().remove(this);
			}

			if (syncResult != null)
				syncResult.accept(error == null ? updateStatus ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE : SynchronizeResult.FAILURE);
		});
	}
}
