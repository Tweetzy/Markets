package ca.tweetzy.markets.listeners;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.guis.category.GUICategorySettings;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.MarketPlayer;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.request.Request;
import ca.tweetzy.markets.request.RequestItem;
import ca.tweetzy.markets.structures.Triple;
import ca.tweetzy.markets.utils.Common;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 06 2021
 * Time Created: 6:11 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class PlayerListeners implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Markets.newChain().async(() -> {
			Player player = e.getPlayer();
			Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);
			Markets.getInstance().getMarketPlayerManager().addPlayer(new MarketPlayer(player, market));
			if (market == null) return;
			if (market.getOwnerName().equals(player.getName())) return;
			market.setOwnerName(player.getName());
		}).execute();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		Markets.getInstance().getMarketPlayerManager().removePlayer(player);
	}

	@EventHandler
	public void addingItemWithCustomCurrency(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (!Markets.getInstance().getMarketPlayerManager().getAddingCustomCurrencyItem().containsKey(player.getUniqueId()))
			return;
		if (e.getItem() == null || e.getItem().getType() == XMaterial.AIR.parseMaterial()) {
			Markets.getInstance().getLocale().getMessage("air.currency").sendPrefixedMessage(player);
			return;
		}

		ItemStack itemToBeUsedAsCurrency = e.getItem().clone();
		itemToBeUsedAsCurrency.setAmount(1);

		Triple<Market, MarketCategory, MarketItem> toAdd = Markets.getInstance().getMarketPlayerManager().getPlayerAddingCustomCurrencyItem(player.getUniqueId());
		toAdd.getThird().setCurrencyItem(itemToBeUsedAsCurrency);
		toAdd.getThird().setUseItemCurrency(true);
		toAdd.getFirst().setUpdatedAt(System.currentTimeMillis());
		MarketsAPI.getInstance().removeSpecificItemQuantityFromPlayer(player, toAdd.getThird().getItemStack(), toAdd.getThird().getItemStack().getAmount());

		if (!toAdd.getSecond().getItems().contains(toAdd.getThird())) {
			Markets.getInstance().getMarketManager().addItemToCategory(toAdd.getSecond(), toAdd.getThird());
			Markets.getInstance().getLocale().getMessage("added_item_to_category").processPlaceholder("item_name", Common.getItemName(toAdd.getThird().getItemStack())).processPlaceholder("market_category_name", toAdd.getSecond().getName()).sendPrefixedMessage(player);
		} else {
			Markets.getInstance().getLocale().getMessage("updated_market_item_currency").sendPrefixedMessage(player);
			Markets.getInstance().getGuiManager().showGUI(player, new GUICategorySettings(toAdd.getFirst(), toAdd.getSecond()));
		}

		Markets.getInstance().getMarketPlayerManager().removePlayerFromCustomCurrencyItem(player.getUniqueId());
	}

	@EventHandler
	public void addingRequestWithCustomCurrency(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		if (!Markets.getInstance().getMarketPlayerManager().getAddingRequestCustomCurrencyItem().containsKey(player.getUniqueId()))
			return;
		if (e.getItem() == null || e.getItem().getType() == XMaterial.AIR.parseMaterial()) {
			Markets.getInstance().getLocale().getMessage("air.currency").sendPrefixedMessage(player);
			return;
		}

		ItemStack itemToBeUsedAsCurrency = e.getItem().clone();
		itemToBeUsedAsCurrency.setAmount(1);

		Triple<ItemStack, Integer, Double> toAdd = Markets.getInstance().getMarketPlayerManager().getPlayerAddingRequestCustomCurrencyItem(player.getUniqueId());

		double priceForAll = toAdd.getThird();
		double pricePerItem = priceForAll / toAdd.getSecond();
		int maxStackSize = toAdd.getFirst().getMaxStackSize();
		int fullStacks = toAdd.getSecond() / maxStackSize;
		int remainder = toAdd.getSecond() % maxStackSize;

		Request request = new Request(player.getUniqueId(), null);
		List<RequestItem> requestItems = new ArrayList<>();

		for (int i = 0; i < fullStacks; i++) {
			requestItems.add(new RequestItem(request.getId(), toAdd.getFirst(), itemToBeUsedAsCurrency, maxStackSize, pricePerItem * maxStackSize, false, true));
		}

		if (remainder != 0) {
			requestItems.add(new RequestItem(request.getId(), toAdd.getFirst(), itemToBeUsedAsCurrency, remainder, pricePerItem * remainder, false, true));
		}

		request.setRequestedItems(requestItems);

		Markets.getInstance().getRequestManager().addRequest(request);
		Markets.getInstance().getLocale().getMessage("created_request").processPlaceholder("request_amount", toAdd.getSecond()).processPlaceholder("request_item_name", Common.getItemName(toAdd.getFirst())).processPlaceholder("request_price", String.format("%,.2f", priceForAll)).sendPrefixedMessage(player);
		Markets.getInstance().getMarketPlayerManager().removePlayerFromRequestCustomCurrencyItem(player.getUniqueId());
	}
}
