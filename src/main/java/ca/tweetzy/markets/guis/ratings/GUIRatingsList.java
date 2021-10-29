package ca.tweetzy.markets.guis.ratings;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.market.GUIMarketView;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.MarketRating;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.Objects;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 16 2021
 * Time Created: 2:36 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIRatingsList extends Gui {

    private final Market market;

    public GUIRatingsList(Market market) {
        this.market = market;
        setTitle(TextUtils.formatText(Settings.GUI_RATINGS_LIST_TITLE.getString()));
        setDefaultItem(Settings.GUI_RATINGS_LIST_FILL_ITEM.getMaterial().parseItem());
        setAcceptsItems(false);
        setAllowDrops(false);
        setUseLockedCells(true);
        setRows(6);
        draw();
    }

    private void draw() {
        reset();
        pages = (int) Math.max(1, Math.ceil(this.market.getRatings().size() / (double) 9));

        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_RATINGS_LIST_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_RATINGS_LIST_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        setPrevPage(5, 3, new TItemBuilder(Common.getItemStack(Settings.GUI_BACK_BTN_ITEM.getString())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CLOSE_BTN_ITEM.getString()), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIMarketView(this.market)));
        setNextPage(5, 5, new TItemBuilder(Common.getItemStack(Settings.GUI_NEXT_BTN_ITEM.getString())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        Markets.newChain().async(() -> {
            int slot = 10;
            for (MarketRating rating : this.market.getRatings()) {
                setItem(slot++, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_RATINGS_LIST_RATING_ITEM.getString()), Settings.GUI_RATINGS_LIST_RATING_NAME.getString(), Settings.GUI_RATINGS_LIST_RATING_LORE.getStringList(), 1, new HashMap<String, Object>() {{
                    put("%rating_rater%", Bukkit.getOfflinePlayer(rating.getRater()).getName());
                    put("%rating_stars%", rating.getStars());
                    put("%rating_message%", rating.getMessage());
                }}));
            }
        }).execute();
    }
}
