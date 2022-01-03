package ca.tweetzy.markets.guis.category;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.guis.GUIContainerInspect;
import ca.tweetzy.markets.guis.items.GUIItemPurchase;
import ca.tweetzy.markets.guis.market.GUIMarketView;
import ca.tweetzy.markets.guis.payment.GUICustomCurrencyView;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:23 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUICategoryItems extends Gui {

	private final Market market;
	private final MarketCategory marketCategory;

	public GUICategoryItems(Market market, MarketCategory marketCategory) {
		this.market = market;
		this.marketCategory = marketCategory;
		setTitle(TextUtils.formatText(Settings.GUI_MARKET_CATEGORY_TITLE.getString().replace("%market_name%", market.getName()).replace("%category_display_name%", marketCategory.getDisplayName())));
		setAllowDrops(false);
		setAcceptsItems(false);
		setUseLockedCells(true);
		setAllowShiftClick(false);
		setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_MARKET_CATEGORY_FILL_ITEM.getMaterial()));
		setRows(6);

		draw();
	}

	private void draw() {
		reset();

		// make border
		for (int i : Numbers.GUI_BORDER_6_ROWS) {
			setItem(i, GuiUtils.getBorderItem(Settings.GUI_MARKET_CATEGORY_BORDER_ITEM.getMaterial()));
			if (Settings.GUI_MARKET_CATEGORY_GLOW_BORDER.getBoolean()) highlightItem(i);
		}

		Markets.newChain().asyncFirst(() -> this.marketCategory.getItems().stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList())).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.marketCategory.getItems().size() / (double) 28));
			setPrevPage(5, 3, new TItemBuilder(Common.getItemStack(Settings.GUI_BACK_BTN_ITEM.getString())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
			setButton(5, 4, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CLOSE_BTN_ITEM.getString()), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIMarketView(this.market)));
			setNextPage(5, 5, new TItemBuilder(Common.getItemStack(Settings.GUI_NEXT_BTN_ITEM.getString())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
			setOnPage(e -> draw());

			int slot = 10;
			for (MarketItem marketItem : data) {
				ItemStack item = marketItem.getItemStack().clone();
				List<String> lore = Common.getItemLore(item);
				lore.addAll(marketItem.isUseItemCurrency() ? Settings.GUI_MARKET_CATEGORY_ITEM_LORE_CUSTOM_CURRENCY.getStringList() : Settings.GUI_MARKET_CATEGORY_ITEM_LORE.getStringList());

				if (item.getType().name().contains("SHULKER_BOX")) {
					lore.addAll(Settings.GUI_MARKET_CATEGORY_ITEM_INSPECT.getStringList());
				}

				setButton(slot, ConfigItemUtil.build(item, Settings.GUI_MARKET_CATEGORY_ITEM_NAME.getString(), lore, item.getAmount(), new HashMap<String, Object>() {{
					put("%item_name%", Common.getItemName(item));
					put("%market_item_price%", marketItem.isUseItemCurrency() ? MarketsAPI.formatNumber(marketItem.getPrice(), true) : MarketsAPI.formatNumber(marketItem.getPrice()));
					put("%market_item_price_for_stack%", marketItem.getTranslatedPriceForStack());
					put("%market_item_currency%", marketItem.isUseItemCurrency() ? Common.getItemName(marketItem.getCurrencyItem()) : "");
				}}), e -> {
					switch (e.clickType) {
						case LEFT:
							if (this.market.getOwner().equals(e.player.getUniqueId()) && !Settings.ALLOW_OWNER_TO_BUY_OWN_ITEMS.getBoolean()) {
								Markets.getInstance().getLocale().getMessage("cannot_buy_from_own_market").sendPrefixedMessage(e.player);
								return;
							}
							e.manager.showGUI(e.player, new GUIItemPurchase(this.market, marketItem));
							break;
						case RIGHT:
							if (!marketItem.isUseItemCurrency()) {
								return;
							}
							e.manager.showGUI(e.player, new GUICustomCurrencyView(this.market, this.marketCategory, marketItem, false));
							break;
						case SHIFT_RIGHT:
							if (item.getType().name().contains("SHULKER_BOX")) {
								e.manager.showGUI(e.player, new GUIContainerInspect(this.market, this.marketCategory, item));
							}
							break;
					}
				});

				slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
			}
		}).execute();
	}
}
