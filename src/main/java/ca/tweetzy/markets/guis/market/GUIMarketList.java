package ca.tweetzy.markets.guis.market;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.GUIMain;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:29 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIMarketList extends Gui {

    private List<Market> markets;

    public GUIMarketList() {
        setTitle(TextUtils.formatText(Settings.GUI_MARKET_LIST_TITLE.getString()));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setAllowShiftClick(false);
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_MARKET_LIST_FILL_ITEM.getMaterial()));
        setRows(6);

        draw();
    }

    private void draw() {
        reset();
        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_MARKET_LIST_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_MARKET_LIST_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        Markets.newChain().asyncFirst(() -> {
            this.markets = Markets.getInstance().getMarketManager().getMarkets();
            return this.markets.stream().filter(market -> market.isOpen() && !market.isUnpaid()).skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());
        }).asyncLast((data) -> {
            pages = (int) Math.max(1, Math.ceil(this.markets.size() / (double) 28L));
            setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
            setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIMain(e.player)));
            setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
            setOnPage(e -> draw());

            int slot = 10;
            for (Market market : data) {
                setButton(slot, ConfigItemUtil.build(Common.getPlayerHead(market.getOwnerName()), Settings.GUI_MARKET_LIST_MARKET_NAME.getString(), Settings.GUI_MARKET_LIST_MARKET_LORE.getStringList(), 1, new HashMap<String, Object>() {{
                    put("%market_name%", market.getName());
                    put("%market_description%", market.getDescription());
                    put("%market_owner%", market.getOwnerName());
                }}), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIMarketView(market)));

                slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
            }
        }).execute();
    }
}
