package ca.tweetzy.markets.guis.payment;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.guis.category.GUICategoryItems;
import ca.tweetzy.markets.guis.items.GUIAllItems;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import org.bukkit.event.inventory.ClickType;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 03 2021
 * Time Created: 12:23 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUICustomCurrencyView extends Gui {

    private final Market market;
    private final MarketCategory category;
    private final MarketItem marketItem;
    private final boolean isFromAllItems;

    public GUICustomCurrencyView(Market market, MarketCategory category, MarketItem marketItem, boolean isFromAllItems) {
        this.market = market;
        this.category = category;
        this.marketItem = marketItem;
        this.isFromAllItems = isFromAllItems;
        setTitle(TextUtils.formatText(Settings.GUI_CUSTOM_CURRENCY_VIEW_TITLE.getString()));
        setDefaultItem(Settings.GUI_CUSTOM_CURRENCY_VIEW_FILL_ITEM.getMaterial().parseItem());
        setAcceptsItems(false);
        setAllowDrops(false);
        setUseLockedCells(true);
        setRows(4);
        draw();

        setOnClose(close -> close.manager.showGUI(close.player, this.isFromAllItems ? new GUIAllItems(this.market, false) : new GUICategoryItems(this.market, this.category)));
    }

    private void draw() {
        setItem(1, 4, this.marketItem.getCurrencyItem());
        setButton(3, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, this.isFromAllItems ? new GUIAllItems(this.market, false) : new GUICategoryItems(this.market, this.category)));
    }
}
