package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.currency.Payment;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class OfflinePayment implements Payment {

	private final UUID uuid;
	private final UUID paymentFor;
	private final ItemStack currency;
	private final double amount;
	private final String reason;
	private final long receivedAt;

	@Override
	public @NonNull UUID getId() {
		return this.uuid;
	}

	@Override
	public UUID getFor() {
		return this.paymentFor;
	}

	@Override
	public ItemStack getCurrency() {
		return this.currency;
	}

	@Override
	public double getAmount() {
		return this.amount;
	}

	@Override
	public String getReason() {
		return this.reason;
	}

	@Override
	public long getTimeCreated() {
		return this.receivedAt;
	}

	@Override
	public long getLastUpdated() {
		return this.receivedAt;
	}

	@Override
	public void store(@NonNull Consumer<Payment> stored) {
		Markets.getDataManager().createOfflineItemPayment(this, (error, created) -> {
			if (error == null)
				stored.accept(created);
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getDataManager().deleteOfflineItemPayment(this, (error, updateStatus) -> {
			if (updateStatus) {
				Markets.getOfflineItemPaymentManager().remove(this.uuid);
			}

			if (syncResult != null)
				syncResult.accept(error == null ? updateStatus ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE : SynchronizeResult.FAILURE);
		});
	}

}
