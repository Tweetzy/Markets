package ca.tweetzy.markets.guis;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.input.PlayerChatInput;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.api.events.MarketCreateEvent;
import ca.tweetzy.markets.guis.items.GUIItemSearch;
import ca.tweetzy.markets.guis.market.GUIMarketEdit;
import ca.tweetzy.markets.guis.market.GUIMarketList;
import ca.tweetzy.markets.guis.payment.GUITransactionView;
import ca.tweetzy.markets.guis.requests.GUIOpenRequests;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.structures.Pair;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: April 30 2021
 * Time Created: 4:20 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIMain extends Gui {

	private final Player player;

	public GUIMain(Player player) {
		this.player = player;
		setTitle(TextUtils.formatText(Settings.GUI_MAIN_TITLE.getString()));
		setAllowDrops(false);
		setAcceptsItems(false);
		setAllowShiftClick(false);
		setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_MAIN_FILL_ITEM.getMaterial()));
		setUseLockedCells(Settings.GUI_MAIN_FILL_SLOTS.getBoolean());
		setRows(6);

		draw();
	}

	private void draw() {
		if (Settings.GUI_MAIN_USE_BORDER.getBoolean()) {
			for (int i : Numbers.GUI_BORDER_6_ROWS) {
				setItem(i, GuiUtils.getBorderItem(Settings.GUI_MAIN_BORDER_ITEM.getMaterial()));
				if (Settings.GUI_MAIN_GLOW_BORDER.getBoolean()) highlightItem(i);

			}
		}

		final boolean viewAllDisabled = Settings.DISABLE_VIEW_ALL_MARKETS.getBoolean();

		if (!viewAllDisabled) {
			setButton(2, 2, new TItemBuilder(Settings.GUI_MAIN_ITEMS_ALL_MARKETS_ITEM_USE_CUSTOM_HEAD.getBoolean() ? Common.getCustomTextureHead(String.format("%s%s", "http://textures.minecraft.net/texture/", Settings.GUI_MAIN_ITEMS_ALL_MARKETS_ITEM_CUSTOM_HEAD_LINK.getString()), false) : Settings.GUI_MAIN_ITEMS_ALL_MARKETS_ITEM.getMaterial().parseItem()).setName(Settings.GUI_MAIN_ITEMS_ALL_MARKETS_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_ALL_MARKETS_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
				e.manager.showGUI(this.player, new GUIMarketList());
			});
		}

		setButton(2, viewAllDisabled ? 2 : 4, new TItemBuilder(Common.getPlayerHead(this.player)).setName(Settings.GUI_MAIN_ITEMS_YOUR_MARKET_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_YOUR_MARKET_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
			Market market = Markets.getInstance().getMarketManager().getMarketByPlayer(player);

			if (market == null) {
				if (!Settings.AUTO_CREATE_MARKET.getBoolean()) {
					Markets.getInstance().getLocale().getMessage("market_required").sendPrefixedMessage(this.player);
					return;
				}

				if (!Common.chargeCreationFee(player)) {
					return;
				}


				market = new Market(player.getUniqueId(), player.getName(), Markets.getInstance().getLocale().getMessage("misc.default market name").processPlaceholder("player", player.getName()).getMessage());

				MarketCreateEvent marketCreateEvent = new MarketCreateEvent(player, market);
				Bukkit.getPluginManager().callEvent(marketCreateEvent);
				if (marketCreateEvent.isCancelled()) return;

				// Create a new market for the player
				Markets.getInstance().getMarketManager().addMarket(market);
				Markets.getInstance().getLocale().getMessage("created_market").sendPrefixedMessage(player);
			}

			market.setUnpaid(false);
			if (market.isUnpaid()) {
				Markets.getInstance().getLocale().getMessage("upkeep_fee_not_paid").sendPrefixedMessage(player);
				return;
			}

			e.manager.showGUI(this.player, new GUIMarketEdit(market));
		});

		setButton(2, 6, new TItemBuilder(Common.getItemStack(Settings.GUI_MAIN_ITEMS_REQUESTS_ITEM.getString())).setName(Settings.GUI_MAIN_ITEMS_REQUESTS_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_REQUESTS_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
			e.manager.showGUI(this.player, new GUIOpenRequests(this.player, false));
		});

		setButton(viewAllDisabled ? 2 : 3, viewAllDisabled ? 4 : 2, new TItemBuilder(Common.getItemStack(Settings.GUI_MAIN_ITEMS_OPEN_REQUESTS_ITEM.getString())).setName(Settings.GUI_MAIN_ITEMS_OPEN_REQUESTS_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_OPEN_REQUESTS_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
			e.manager.showGUI(this.player, new GUIOpenRequests(this.player, true));
		});

		setButton(3, viewAllDisabled ? 4 : 6, new TItemBuilder(Common.getItemStack(Settings.GUI_MAIN_ITEMS_TRANSACTIONS_ITEM.getString())).setName(Settings.GUI_MAIN_ITEMS_TRANSACTIONS_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_TRANSACTIONS_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
			e.manager.showGUI(this.player, new GUITransactionView(this.player));
		});

		setButton(3, 4, new TItemBuilder(Common.getItemStack(Settings.GUI_MAIN_ITEMS_BANK_ITEM.getString())).setName(Settings.GUI_MAIN_ITEMS_BANK_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_BANK_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
			e.manager.showGUI(this.player, new GUIBank(this.player));
		});

		if (!Settings.REQUEST_ONLY_MODE.getBoolean())
			setButton(4, 4, new TItemBuilder(Common.getItemStack(Settings.GUI_MAIN_ITEMS_SEARCH_ITEM.getString())).setName(Settings.GUI_MAIN_ITEMS_SEARCH_NAME.getString()).setLore(Settings.GUI_MAIN_ITEMS_SEARCH_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
				e.gui.exit();

				PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Markets.getInstance(), e.player);
				builder.isValidInput((p, str) -> ChatColor.stripColor(str.trim()).length() >= 1);
				builder.sendValueMessage(TextUtils.formatText(Markets.getInstance().getLocale().getMessage("misc.search").getMessage()));
				builder.toCancel("cancel");
				builder.onCancel(p -> e.manager.showGUI(e.player, new GUIMain(e.player)));
				builder.setValue((p, value) -> value);
				builder.onFinish((p, value) -> {
					String phrase = ChatColor.stripColor(value.trim());

					if (phrase.length() == 0) {
						Markets.getInstance().getLocale().getMessage("search_phrase_empty").sendPrefixedMessage(player);
						return;
					}

					Markets.newChain().asyncFirst(() -> {
						List<Pair<Market, MarketItem>> items = new ArrayList<>();
						Markets.getInstance().getMarketManager().getMarkets().stream().filter(market -> market.isOpen() && !market.isUnpaid() && !market.getOwner().equals(player.getUniqueId())).collect(Collectors.toList()).forEach(market -> {
							market.getCategories().forEach(category -> category.getItems().forEach(item -> items.add(new Pair<>(market, item))));
						});

						return items.stream().filter(marketItem -> MarketsAPI.getInstance().checkSearchCriteria(phrase, marketItem.getSecond().getItemStack())).collect(Collectors.toList());
					}).syncLast(data -> Markets.getInstance().getGuiManager().showGUI(player, new GUIItemSearch(data))).execute();
				});


				PlayerChatInput<String> input = builder.build();
				input.start();
			});
	}
}
