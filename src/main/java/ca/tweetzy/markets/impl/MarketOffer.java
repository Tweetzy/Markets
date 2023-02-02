package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.currency.TransactionResult;
import ca.tweetzy.markets.api.market.Offer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class MarketOffer implements Offer {

	private final UUID uuid;
	private final UUID sender;
	private final String senderName;
	private final UUID offerTo;
	private final UUID marketItem;
	private final String currency;
	private final ItemStack currencyItem;
	private final double offeredAmount;
	private final long offeredAt;

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
	public void reject(@NonNull Consumer<TransactionResult> result) {

	}

	@Override
	public void store(@NonNull Consumer<Offer> stored) {
		Markets.getDataManager().createOffer(this, (error, created) -> {
			if (error == null)
				stored.accept(created);
		});
	}
}
