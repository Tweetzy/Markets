package ca.tweetzy.markets.market;

import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.structures.Triple;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 02 2021
 * Time Created: 12:51 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MarketPlayerManager {

	private final Map<UUID, MarketPlayer> players = Collections.synchronizedMap(new HashMap<>());
	private final Map<UUID, Triple<Market, MarketCategory, MarketItem>> addingCustomCurrencyItem = Collections.synchronizedMap(new HashMap<>());
	private final Map<UUID, Triple<ItemStack, Integer, Double>> addingRequestCustomCurrencyItem = Collections.synchronizedMap(new HashMap<>());

	public void addPlayer(MarketPlayer marketPlayer) {
		this.players.putIfAbsent(marketPlayer.getPlayer().getUniqueId(), marketPlayer);
	}

	public MarketPlayer getPlayer(UUID id) {
		return this.players.getOrDefault(id, null);
	}

	public void removePlayer(UUID id) {
		this.players.remove(id);
	}

	public void removePlayer(Player player) {
		this.removePlayer(player.getUniqueId());
	}

    /*
    ================= ADDING ITEM WITH CUSTOM CURRENCY STUFF =================
     */

	public void addPlayerToCustomCurrencyItem(UUID id, Market market, MarketCategory category, MarketItem item) {
		this.addingCustomCurrencyItem.put(id, new Triple<>(market, category, item));
	}

	public void removePlayerFromCustomCurrencyItem(UUID id) {
		this.addingCustomCurrencyItem.remove(id);
	}

	public Map<UUID, Triple<Market, MarketCategory, MarketItem>> getAddingCustomCurrencyItem() {
		return Collections.unmodifiableMap(this.addingCustomCurrencyItem);
	}

	public Triple<Market, MarketCategory, MarketItem> getPlayerAddingCustomCurrencyItem(UUID id) {
		return this.addingCustomCurrencyItem.get(id);
	}

    /*
    ================= CREATING REQUEST USING A CUSTOM CURRENCY =================
     */

	public void addPlayerToRequestCustomCurrencyItem(UUID id, ItemStack item, int amount, double price) {
		this.addingRequestCustomCurrencyItem.put(id, new Triple<>(item, amount, price));
	}

	public void removePlayerFromRequestCustomCurrencyItem(UUID id) {
		this.addingRequestCustomCurrencyItem.remove(id);
	}

	public Map<UUID, Triple<ItemStack, Integer, Double>> getAddingRequestCustomCurrencyItem() {
		return Collections.unmodifiableMap(this.addingRequestCustomCurrencyItem);
	}

	public Triple<ItemStack, Integer, Double> getPlayerAddingRequestCustomCurrencyItem(UUID id) {
		return this.addingRequestCustomCurrencyItem.get(id);
	}
}
