package ca.tweetzy.markets.guis.market;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.guis.GUIMain;
import ca.tweetzy.markets.guis.category.GUICategoryItems;
import ca.tweetzy.markets.guis.items.GUIAllItems;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:38 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIMarketView extends Gui {

    private final Market market;

    public GUIMarketView(Market market) {
        this.market = market;
        setTitle(TextUtils.formatText(Settings.GUI_MARKET_VIEW_TITLE.getString().replace("%market_name%", market.getName())));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setAllowShiftClick(false);
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_MARKET_VIEW_FILL_ITEM.getMaterial()));
        setRows(6);

        draw();
    }

    private void draw() {
        reset();
        pages = (int) Math.max(1, Math.ceil(this.market.getCategories().size() / (double) 9));

        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_MARKET_VIEW_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_MARKET_VIEW_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIMain(e.player)));
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        setButton(2, 2, ConfigItemUtil.build(Settings.GUI_MARKET_VIEW_ITEMS_ALL_ITEMS_ITEM.getString(), Settings.GUI_MARKET_VIEW_ITEMS_ALL_ITEMS_NAME.getString(), Settings.GUI_MARKET_VIEW_ITEMS_ALL_ITEMS_LORE.getStringList(), 1, null), ClickType.LEFT, e -> {
            e.manager.showGUI(e.player, new GUIAllItems(this.market, false));
        });

        int slot = 21;
        List<MarketCategory> data = this.market.getCategories().stream().skip((page - 1) * 9L).limit(9L).collect(Collectors.toList());
        for (MarketCategory category : data) {
            setButton(slot, ConfigItemUtil.build(category.getIcon(), Settings.GUI_MARKET_VIEW_ITEMS_ALL_CATEGORY_NAME.getString(), Settings.GUI_MARKET_VIEW_ITEMS_ALL_CATEGORY_LORE.getStringList(), 1, new HashMap<String, Object>() {{
                put("%category_description%", category.getDescription());
                put("%category_display_name%", category.getDisplayName());
                put("%category_name%", category.getName());
            }}), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUICategoryItems(this.market, category)));

            slot = slot == 24 ? slot + 4 : slot + 1;
        }
    }
}
