package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.currency.TransactionResult;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.api.market.Offer;
import ca.tweetzy.markets.api.market.OfferRejectReason;
import lombok.AllArgsConstructor;
import lombok.NonNull;
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
