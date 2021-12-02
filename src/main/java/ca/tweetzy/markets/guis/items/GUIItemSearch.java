package ca.tweetzy.markets.guis.items;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.guis.payment.GUICustomCurrencyView;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.structures.Pair;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 27 2021
 * Time Created: 12:48 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIItemSearch extends Gui {

	private final List<Pair<Market, MarketItem>> items;

	public GUIItemSearch(List<Pair<Market, MarketItem>> items) {
		this.items = items;
		setTitle(Settings.GUI_SEARCH_TITLE.getString());
		setAllowDrops(false);
		setAcceptsItems(false);
		setUseLockedCells(true);
		setAllowShiftClick(false);
		setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_SEARCH_FILL_ITEM.getMaterial()));
		setRows(6);
		draw();
	}

	private void draw() {
		reset();
		pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 28));

		// make border
		for (int i : Numbers.GUI_BORDER_6_ROWS) {
			setItem(i, GuiUtils.getBorderItem(Settings.GUI_SEARCH_BORDER_ITEM.getMaterial()));
			if (Settings.GUI_SEARCH_GLOW_BORDER.getBoolean()) highlightItem(i);
		}

		setPrevPage(5, 3, new TItemBuilder(Common.getItemStack(Settings.GUI_BACK_BTN_ITEM.getString())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
		setButton(5, 4, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CLOSE_BTN_ITEM.getString()), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.gui.close());
		setNextPage(5, 5, new TItemBuilder(Common.getItemStack(Settings.GUI_NEXT_BTN_ITEM.getString())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
		setOnPage(e -> draw());

		int slot = 10;
		for (Pair<Market, MarketItem> marketItem : this.items) {
			ItemStack item = marketItem.getSecond().getItemStack().clone();
			List<String> lore = Common.getItemLore(item);
			lore.addAll(marketItem.getSecond().isUseItemCurrency() ? Settings.GUI_SEARCH_ITEM_LORE_CUSTOM_CURRENCY.getStringList() : Settings.GUI_SEARCH_ITEM_LORE.getStringList());

			setButton(slot, ConfigItemUtil.build(item, Common.getItemName(item), lore, item.getAmount(), new HashMap<String, Object>() {{
				put("%item_name%", Common.getItemName(item));
				put("%market_name%", marketItem.getFirst().getName());
				put("%market_item_price%", marketItem.getSecond().isUseItemCurrency() ? Math.round(marketItem.getSecond().getPrice()) : String.format("%,.2f", marketItem.getSecond().getPrice()));
				put("%market_item_price_for_stack%", marketItem.getSecond().getTranslatedPriceForStack());
				put("%market_item_currency%", marketItem.getSecond().isUseItemCurrency() ? Common.getItemName(marketItem.getSecond().getCurrencyItem()) : "");
			}}), e -> {
				switch (e.clickType) {
					case LEFT:
						e.manager.showGUI(e.player, new GUIItemPurchase(marketItem.getFirst(), marketItem.getSecond()));
						break;
					case RIGHT:
						if (!marketItem.getSecond().isUseItemCurrency()) {
							return;
						}
						e.manager.showGUI(e.player, new GUICustomCurrencyView(marketItem.getFirst(), null, marketItem.getSecond(), true));
						break;
				}
			});

			slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
		}
	}
}
