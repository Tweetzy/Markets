package ca.tweetzy.markets.guis.category;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.events.MarketCategoryRemoveEvent;
import ca.tweetzy.markets.guis.items.GUIIconSelect;
import ca.tweetzy.markets.guis.market.GUIMarketEdit;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 01 2021
 * Time Created: 6:15 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUICategorySettings extends Gui {

    final Market market;
    final MarketCategory marketCategory;

    public GUICategorySettings(Market market, MarketCategory marketCategory) {
        this.market = market;
        this.marketCategory = marketCategory;
        setTitle(TextUtils.formatText(Settings.GUI_CATEGORY_EDIT_TITLE.getString().replace("%category_name%", this.marketCategory.getName())));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setAllowShiftClick(false);
        setDefaultItem(Settings.GUI_CATEGORY_EDIT_FILL_ITEM.getMaterial().parseItem());
        setRows(6);

        draw();
    }

    private void draw() {
        reset();
        pages = (int) Math.max(1, Math.ceil(this.marketCategory.getItems().size() / (double) 24));

        // make border
        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 10, 17, 19, 26, 28, 35, 37, 44, 46, 47, 48, 49, 50, 51, 52, 53).forEach(i -> {
            setItem(i, Settings.GUI_CATEGORY_EDIT_BORDER_ITEM.getMaterial().parseItem());
            if (Settings.GUI_CATEGORY_EDIT_GLOW_BORDER.getBoolean()) highlightItem(i);
        });

        setPrevPage(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        setButton(0, 0, ConfigItemUtil.build(Settings.GUI_CATEGORY_EDIT_ITEMS_NAME_ITEM.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_NAME_NAME.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_NAME_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%category_display_name%", marketCategory.getDisplayName());
            put("%category_name%", marketCategory.getName());
        }}), ClickType.LEFT, e -> {
            ChatPrompt.showPrompt(Markets.getInstance(), e.player, TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_category_display_name").getMessage()), chat -> {
                if (chat.getMessage().length() >= 1) {
                    this.marketCategory.setDisplayName(chat.getMessage());
                    this.market.setUpdatedAt(System.currentTimeMillis());
                    Markets.getInstance().getLocale().getMessage("updated_category_name").sendPrefixedMessage(e.player);
                    e.manager.showGUI(e.player, new GUICategorySettings(this.market, this.marketCategory));
                }
            });
        });

        setButton(1, 0, ConfigItemUtil.build(Settings.GUI_CATEGORY_EDIT_ITEMS_DESCRIPTION_ITEM.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_DESCRIPTION_NAME.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_DESCRIPTION_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%category_description%", marketCategory.getDescription());
        }}), ClickType.LEFT, e -> {
            ChatPrompt.showPrompt(Markets.getInstance(), e.player, TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_category_description").getMessage()), chat -> {
                if (chat.getMessage().length() >= 1) {
                    this.marketCategory.setDescription(chat.getMessage());
                    this.market.setUpdatedAt(System.currentTimeMillis());
                    Markets.getInstance().getLocale().getMessage("updated_category_description").sendPrefixedMessage(e.player);
                    e.manager.showGUI(e.player, new GUICategorySettings(this.market, this.marketCategory));
                }
            });
        });

        setButton(2, 0, new TItemBuilder(this.marketCategory.getIcon()).setName(Settings.GUI_CATEGORY_EDIT_ITEMS_ICON_NAME.getString()).setLore(Settings.GUI_CATEGORY_EDIT_ITEMS_ICON_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIIconSelect(this.market, this.marketCategory)));

        setButton(3, 0, ConfigItemUtil.build(Settings.GUI_CATEGORY_EDIT_ITEMS_EMPTY_ITEM.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_EMPTY_NAME.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_EMPTY_LORE.getStringList(), 1, null), ClickType.LEFT, e -> {
            PlayerUtils.giveItem(e.player, this.marketCategory.getItems().stream().map(MarketItem::getItemStack).collect(Collectors.toList()));
            this.marketCategory.getItems().clear();
            this.market.setUpdatedAt(System.currentTimeMillis());
            draw();
        });

        setButton(4, 0, ConfigItemUtil.build(Settings.GUI_CATEGORY_EDIT_ITEMS_DELETE_ITEM.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_DELETE_NAME.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_DELETE_LORE.getStringList(), 1, null), ClickType.RIGHT, e -> {
            MarketCategoryRemoveEvent marketCategoryRemoveEvent = new MarketCategoryRemoveEvent(this.market, this.marketCategory);
            Bukkit.getPluginManager().callEvent(marketCategoryRemoveEvent);
            if (marketCategoryRemoveEvent.isCancelled()) return;

            if (Settings.GIVE_ITEMS_ON_CATEGORY_DELETE.getBoolean()) {
                PlayerUtils.giveItem(e.player, this.marketCategory.getItems().stream().map(MarketItem::getItemStack).collect(Collectors.toList()));
            }

            this.market.getCategories().remove(this.marketCategory);
            this.market.setUpdatedAt(System.currentTimeMillis());
            e.manager.showGUI(e.player, new GUIMarketEdit(this.market));
            Markets.getInstance().getLocale().getMessage("removed_category").processPlaceholder("market_category_name", this.marketCategory.getName()).sendPrefixedMessage(e.player);
        });

        setButton(5, 0, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIMarketEdit(this.market)));

        Markets.newChain().async(() -> {
            List<MarketItem> data = this.marketCategory.getItems().stream().skip((page - 1) * 24L).limit(24L).collect(Collectors.toList());
            int slot = 11;
            for (MarketItem marketItem : data) {
                ItemStack item = marketItem.getItemStack().clone();

                List<String> lore = Common.getItemLore(item);
                lore.addAll(Settings.GUI_CATEGORY_EDIT_ITEMS_ITEM_LORE.getStringList());

                setButton(slot, ConfigItemUtil.build(item, Settings.GUI_CATEGORY_EDIT_ITEMS_ITEM_NAME.getString(), lore, item.getAmount(), new HashMap<String, Object>() {{
                    put("%item_name%", Common.getItemName(item));
                    put("%market_item_price%", String.format("%,.2f", marketItem.getPrice()));
                    put("%market_item_price_for_stack%", marketItem.isPriceForStack());
                }}), e -> Common.handleMarketItemEdit(e, this.market, marketItem, this.marketCategory));

                slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 4 : slot + 1;
            }
        }).execute();
    }
}
