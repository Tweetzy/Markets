package ca.tweetzy.markets.guis.category;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.items.GUIItemPurchase;
import ca.tweetzy.markets.guis.market.GUIMarketView;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:23 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUICategoryItems extends Gui {

    final Market market;
    final MarketCategory marketCategory;

    public GUICategoryItems(Market market, MarketCategory marketCategory) {
        this.market = market;
        this.marketCategory = marketCategory;
        setTitle(TextUtils.formatText(Settings.GUI_MARKET_CATEGORY_TITLE.getString().replace("%market_name%", market.getName()).replace("%category_display_name%", marketCategory.getDisplayName())));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_MARKET_CATEGORY_FILL_ITEM.getMaterial()));
        setRows(6);

        draw();
    }

    private void draw() {
        reset();
        pages = (int) Math.max(1, Math.ceil(this.marketCategory.getItems().size() / (double) 28));

        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_MARKET_CATEGORY_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_MARKET_CATEGORY_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), e -> e.manager.showGUI(e.player, new GUIMarketView(this.market)));
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        int slot = 10;
        List<MarketItem> data = this.marketCategory.getItems().stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());
        for (MarketItem marketItem : data) {
            ItemStack item = marketItem.getItemStack().clone();
            List<String> lore = Common.getItemLore(item);
            lore.addAll(Settings.GUI_MARKET_CATEGORY_ITEM_LORE.getStringList());

            setButton(slot, ConfigItemUtil.build(item, Settings.GUI_MARKET_CATEGORY_ITEM_NAME.getString(), lore, item.getAmount(), new HashMap<String, Object>() {{
                put("%item_name%", Common.getItemName(item));
                put("%market_item_price%", String.format("%,.2f", marketItem.getPrice()));
                put("%market_item_price_for_stack%", marketItem.isPriceForStack());
            }}), e -> {
                if (this.market.getOwner().equals(e.player.getUniqueId())) {
                    Markets.getInstance().getLocale().getMessage("cannot_buy_from_own_market").sendPrefixedMessage(e.player);
                    return;
                }

                e.manager.showGUI(e.player, new GUIItemPurchase(this.market, marketItem));
            });

            slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
        }
    }

}
