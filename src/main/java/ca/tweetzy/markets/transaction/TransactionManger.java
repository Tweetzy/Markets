package ca.tweetzy.markets.transaction;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 06 2021
 * Time Created: 1:57 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class TransactionManger {

	private final List<Transaction> transactions = Collections.synchronizedList(new ArrayList<>());
	private final List<Payment> payments = Collections.synchronizedList(new ArrayList<>());

	public void addPayment(Payment payment) {
		Objects.requireNonNull(payment, "Cannot add a null Payment to the payment list");
		this.payments.add(payment);
	}

	public List<Payment> getPayments() {
		return Collections.unmodifiableList(this.payments);
	}

	public List<Payment> getPayments(UUID to) {
		return this.payments.stream().filter(payment -> payment.getTo().equals(to)).collect(Collectors.toList());
	}

	public void removePayment(Payment payment) {
		this.payments.remove(payment);
	}

	public void addTransaction(Transaction transaction) {
		Objects.requireNonNull(transaction, "Cannot add a null Transaction to transaction list");
		this.transactions.add(transaction);
	}

	public Transaction getTransactionById(UUID id) {
		return this.transactions.stream().filter(transaction -> transaction.getId().equals(id)).findFirst().orElse(null);
	}

	public List<Transaction> getTransactions() {
		return Collections.unmodifiableList(this.transactions);
	}

	public void savePayment(Payment payment) {
		Objects.requireNonNull(payment, "Cannot save a null payment");
		String node = "payment collection." + UUID.randomUUID().toString();
		Markets.getInstance().getData().set(node, MarketsAPI.getInstance().convertToBase64(payment));
	}

	public void savePayments(Payment... payments) {
		Markets.newChain().sync(() -> {
			Markets.getInstance().getData().set("payment collection", null);
			for (Payment payment : payments) {
				savePayment(payment);
			}
			Markets.getInstance().getData().save();
		}).execute();
	}

	public void loadPayments() {
		if (Settings.DATABASE_USE.getBoolean()) {
			Markets.getInstance().getDataManager().getPayments(callback -> callback.forEach(this::addPayment));
		} else {
			Markets.newChain().async(() -> {
				ConfigurationSection section = Markets.getInstance().getData().getConfigurationSection("payment collection");
				if (section == null || section.getKeys(false).size() == 0) return;
				Markets.getInstance().getData().getConfigurationSection("payment collection").getKeys(false).forEach(payment -> {
					addPayment((Payment) MarketsAPI.getInstance().convertBase64ToObject(Markets.getInstance().getData().getString("payment collection." + payment)));
				});
			}).execute();
		}
	}

	public void saveTransactions(Transaction... transactions) {
		Markets.newChain().sync(() -> {
			for (Transaction transaction : transactions) {
				if (Markets.getInstance().getData().contains("transactions." + transaction.getId().toString()))
					continue;
				saveTransaction(transaction);
			}
			Markets.getInstance().getData().save();
		}).execute();
	}

	public void saveTransaction(Transaction transaction) {
		Objects.requireNonNull(transaction, "Cannot save a null transaction");
		String node = "transactions." + transaction.getId().toString();
		Markets.getInstance().getData().set(node + ".market id", transaction.getMarketId().toString());
		Markets.getInstance().getData().set(node + ".purchaser", transaction.getPurchaser().toString());
		Markets.getInstance().getData().set(node + ".purchase quantity", transaction.getPurchaseQty());
		Markets.getInstance().getData().set(node + ".price", transaction.getFinalPrice());
		Markets.getInstance().getData().set(node + ".time", transaction.getTime());
		Markets.getInstance().getData().set(node + ".item", transaction.getItemStack());
	}

	public void loadTransactions() {
		if (Settings.DATABASE_USE.getBoolean()) {
			Markets.getInstance().getDataManager().getTransactions(callback -> callback.forEach(this::addTransaction));
		} else {
			Markets.newChain().async(() -> {
				ConfigurationSection section = Markets.getInstance().getData().getConfigurationSection("transactions");
				if (section == null || section.getKeys(false).size() == 0) return;
				Markets.getInstance().getData().getConfigurationSection("transactions").getKeys(false).forEach(transactionId -> {
					Transaction transaction = new Transaction(
							UUID.fromString(transactionId),
							UUID.fromString(Markets.getInstance().getData().getString("transactions." + transactionId + ".market id")),
							UUID.fromString(Markets.getInstance().getData().getString("transactions." + transactionId + ".purchaser")),
							Markets.getInstance().getData().getItemStack("markets." + transactionId + ".item"),
							Markets.getInstance().getData().getInt("transactions." + transactionId + ".purchase quantity"),
							Markets.getInstance().getData().getDouble("transactions." + transactionId + ".price")
					);

					transaction.setTime(Markets.getInstance().getData().getLong("transactions." + transactionId + ".time"));
					addTransaction(transaction);
				});
			}).execute();
		}
	}
}
