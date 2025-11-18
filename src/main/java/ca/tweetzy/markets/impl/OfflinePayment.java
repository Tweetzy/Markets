package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.database.annotations.Column;
import ca.tweetzy.flight.database.annotations.Id;
import ca.tweetzy.flight.database.annotations.Nested;
import ca.tweetzy.flight.database.annotations.Table;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.currency.Payment;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Consumer;

@Table("offline_payment")
public final class OfflinePayment implements Payment {

	@Id
	@Column("id")
	private UUID uuid;
	
	@Column("payment_for")
	private UUID paymentFor;
	
	@Nested
	@Column("currency")
	private ItemStack currency;
	
	@Column("amount")
	private double amount;
	
	@Column("reason")
	private String reason;
	
	@Column("received_at")
	private long receivedAt;

	public OfflinePayment() {
	}

	public OfflinePayment(@NonNull UUID uuid, @NonNull UUID paymentFor, @NonNull ItemStack currency, double amount, @NonNull String reason, long receivedAt) {
		this.uuid = uuid;
		this.paymentFor = paymentFor;
		this.currency = currency;
		this.amount = amount;
		this.reason = reason;
		this.receivedAt = receivedAt;
	}

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
		Markets.getPaymentRepository().save(this, (error, created) -> {
			if (error == null)
				stored.accept(created);
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getPaymentRepository().deleteById(this.uuid, (error, deleted) -> {
			if (deleted != null && deleted) {
				Markets.getOfflineItemPaymentManager().remove(this.uuid);
			}

			if (syncResult != null)
				syncResult.accept(error == null && deleted != null && deleted ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}

}
