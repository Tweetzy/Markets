package ca.tweetzy.markets.guis.items;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.market.GUIMarketEdit;
import ca.tweetzy.markets.guis.market.GUIMarketView;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 04 2021
 * Time Created: 2:28 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIAllItems extends Gui {

    final Market market;
    final boolean isEditing;
    final List<MarketItem> marketItemList;

    public GUIAllItems(Market market, boolean isEditing) {
        this.market = market;
        this.isEditing = isEditing;
        this.marketItemList = new ArrayList<>();
        this.market.getCategories().forEach(marketCategory -> this.marketItemList.addAll(marketCategory.getItems()));

        setTitle(TextUtils.formatText(this.isEditing ? Settings.GUI_ALL_ITEMS_TITLE_EDIT.getString().replace("%market_name%", this.market.getName()) : Settings.GUI_ALL_ITEMS_TITLE.getString().replace("%market_name%", this.market.getName())));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setAllowShiftClick(false);
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_ALL_ITEMS_FILL_ITEM.getMaterial()));
        setRows(6);

        draw();
    }

    private void draw() {
        reset();
        pages = (int) Math.max(1, Math.ceil(this.marketItemList.size() / (double) 28));

        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_ALL_ITEMS_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_ALL_ITEMS_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> {
            e.manager.showGUI(e.player, this.isEditing ? new GUIMarketEdit(this.market) : new GUIMarketView(this.market));
        });

        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        Markets.newChain().async(() -> {
            List<MarketItem> data = this.marketItemList.stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());
            int slot = 10;
            for (MarketItem marketItem : data) {
                ItemStack item = marketItem.getItemStack().clone();
                List<String> lore = Common.getItemLore(item);
                lore.addAll(this.isEditing ? Settings.GUI_ALL_ITEMS_ITEMS_ITEM_EDIT_LORE.getStringList() : Settings.GUI_ALL_ITEMS_ITEMS_ITEM_LORE.getStringList());

                setButton(slot, ConfigItemUtil.build(item, Settings.GUI_ALL_ITEMS_ITEMS_ITEM_NAME.getString(), lore, item.getAmount(), new HashMap<String, Object>() {{
                    put("%item_name%", Common.getItemName(item));
                    put("%market_item_price%", String.format("%,.2f", marketItem.getPrice()));
                    put("%market_item_price_for_stack%", marketItem.isPriceForStack());
                }}), e -> {
                    if (this.isEditing) {
                        Common.handleMarketItemEdit(e, this.market, marketItem, null);
                    } else {
                        if (this.market.getOwner().equals(e.player.getUniqueId())) {
                            Markets.getInstance().getLocale().getMessage("cannot_buy_from_own_market").sendPrefixedMessage(e.player);
                            return;
                        }

                        e.manager.showGUI(e.player, new GUIItemPurchase(this.market, marketItem));
                    }
                });

                slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
            }
        }).execute();
    }
}
