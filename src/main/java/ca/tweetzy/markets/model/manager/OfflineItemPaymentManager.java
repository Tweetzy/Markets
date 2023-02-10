package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.currency.Payment;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.impl.OfflinePayment;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class OfflineItemPaymentManager extends ListManager<Payment> {

	public OfflineItemPaymentManager() {
		super("Offline Item Payments");
	}

	public void remove(@NonNull final UUID paymentId) {
		synchronized (this.managerContent) {
			this.managerContent.removeIf(payment -> payment.getId().equals(paymentId));
		}
	}

	public List<Payment> getPaymentsFor(@NonNull final UUID user) {
		return getManagerContent().stream().filter(payment -> payment.getFor().equals(user)).collect(Collectors.toList());
	}

	public void create(@NonNull UUID paymentFor, @NonNull final ItemStack currency, final int amount, @NonNull final String reason, @NonNull final Consumer<Boolean> created) {
		final Payment payment = new OfflinePayment(
				UUID.randomUUID(),
				paymentFor,
				currency,
				amount,
				reason,
				System.currentTimeMillis()
		);

		payment.store(storedPayment -> {
			if (storedPayment != null) {
				add(storedPayment);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
	}

	@Override
	public void load() {
		clear();

		Markets.getDataManager().getOfflineItemPayments((error, payments) -> {
			if (error != null) return;
			payments.forEach(this::add);
		});
	}
}
