package ca.tweetzy.markets.economy;

import ca.tweetzy.markets.Markets;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 06 2021
 * Time Created: 1:55 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CurrencyBank {

    private final HashMap<UUID, ArrayList<Currency>> bank = new HashMap<>();

    public HashMap<UUID, ArrayList<Currency>> getBank() {
        return this.bank;
    }

    public void addCurrency(UUID id, ItemStack item, int amount) {
        if (!this.bank.containsKey(id)) this.bank.put(id, new ArrayList<>());
        ArrayList<Currency> currencyStorage = this.bank.get(id);

        if (currencyStorage.stream().anyMatch(currency -> currency.getItem().isSimilar(item))) {
            Currency currencyObj = currencyStorage.stream().filter(currency -> currency.getItem().isSimilar(item)).findFirst().orElse(new Currency());
            currencyObj.setAmount(currencyObj.getAmount() + amount);
        } else {
            currencyStorage.add(new Currency(item, amount));
        }
    }

    public int getTotalCurrency(UUID id, ItemStack item) {
        if (!this.bank.containsKey(id)) return 0;
        ArrayList<Currency> currencyStorage = this.bank.get(id);
        if (currencyStorage == null || currencyStorage.size() == 0) return 0;
        return currencyStorage.stream().filter(currency -> currency.getItem().isSimilar(item)).mapToInt(Currency::getAmount).sum();
    }

    public void removeCurrency(UUID id, ItemStack item, int amount) {
        if (!this.bank.containsKey(id)) return;
        ArrayList<Currency> currencyStorage = this.bank.get(id);
        if (currencyStorage == null || currencyStorage.size() == 0) return;
        if (currencyStorage.stream().noneMatch(currency -> currency.getItem().isSimilar(item))) return;

        Currency currencyObj = currencyStorage.stream().filter(currency -> currency.getItem().isSimilar(item)).findFirst().orElse(null);
        if (currencyObj == null) return;

        int newValue = currencyObj.getAmount() - amount;

        if (newValue >= 1) {
            currencyObj.setAmount(newValue);
        } else {
            currencyStorage.remove(currencyObj);
        }
        this.bank.put(id, currencyStorage);
    }

    public ArrayList<Currency> getPlayerCurrencies(UUID id) {
        if (!this.bank.containsKey(id)) this.bank.put(id, new ArrayList<>());
        return this.bank.get(id);
    }

    public void saveBank() {
        Markets.newChain().sync(() -> {
            Markets.getInstance().getData().set("player banks", null);
            this.bank.keySet().forEach(key -> {
                String node = "player banks." + key.toString() + ".";
                this.bank.get(key).forEach(currency -> {
                    String id = UUID.randomUUID().toString();
                    Markets.getInstance().getData().set(node + "currencies." + id + ".item", currency.getItem());
                    Markets.getInstance().getData().set(node + "currencies." + id + ".total", currency.getAmount());
                });
            });

            Markets.getInstance().getData().save();
        }).execute();
    }

    public void loadBank() {
        Markets.newChain().async(() -> {
            ConfigurationSection section = Markets.getInstance().getData().getConfigurationSection("player banks");
            if (section == null || section.getKeys(false).size() == 0) return;

            section.getKeys(false).forEach(key -> {
                ConfigurationSection currencySection = Markets.getInstance().getData().getConfigurationSection("player banks." + key + ".currencies");
                if (currencySection == null || currencySection.getKeys(false).size() == 0) return;

                currencySection.getKeys(false).forEach(currencyKey -> {
                    addCurrency(
                            UUID.fromString(key),
                            Markets.getInstance().getData().getItemStack("player banks." + key + ".currencies." + currencyKey + ".item"),
                            Markets.getInstance().getData().getInt("player banks." + key + ".currencies." + currencyKey + ".total"
                            )
                    );
                });
            });
        }).execute();
    }
}
