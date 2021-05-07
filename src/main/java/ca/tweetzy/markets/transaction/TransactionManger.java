package ca.tweetzy.markets.transaction;

import ca.tweetzy.markets.Markets;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 06 2021
 * Time Created: 1:57 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class TransactionManger {

    private final List<Transaction> transactions = new ArrayList<>();

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

    public void saveTransactions(Transaction... transactions) {
        Markets.newChain().sync(() -> {
            for (Transaction transaction : transactions) {
                if (Markets.getInstance().getData().contains("transactions." + transaction.getId().toString())) continue;
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
