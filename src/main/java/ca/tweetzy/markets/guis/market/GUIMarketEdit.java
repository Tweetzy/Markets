package ca.tweetzy.markets.guis.market;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.input.PlayerChatInput;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.api.events.MarketDeleteEvent;
import ca.tweetzy.markets.guis.GUIConfirm;
import ca.tweetzy.markets.guis.GUIMain;
import ca.tweetzy.markets.guis.category.GUICategorySettings;
import ca.tweetzy.markets.guis.items.GUIAddItem;
import ca.tweetzy.markets.guis.items.GUIAllItems;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 02 2021
 * Time Created: 4:19 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIMarketEdit extends Gui {

    private final Market market;

    public GUIMarketEdit(Market market) {
        this.market = market;
        setTitle(TextUtils.formatText(Settings.GUI_MARKET_EDIT_TITLE.getString()));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setAllowShiftClick(false);
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_MARKET_EDIT_FILL_ITEM.getMaterial()));
        setRows(6);

        draw();
    }

    private void draw() {
        reset();
        pages = (int) Math.max(1, Math.ceil(this.market.getCategories().size() / (double) 24));

        // make border
        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 10, 17, 19, 26, 28, 35, 37, 44, 46, 47, 48, 49, 50, 51, 52, 53).forEach(i -> {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_MARKET_EDIT_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_MARKET_EDIT_GLOW_BORDER.getBoolean()) highlightItem(i);
        });

        setPrevPage(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        setButton(0, 0, ConfigItemUtil.build(Settings.GUI_MARKET_EDIT_ITEMS_NAME_ITEM.getString(), Settings.GUI_MARKET_EDIT_ITEMS_NAME_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_NAME_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%market_name%", market.getName());
        }}), ClickType.LEFT, e -> {
            e.gui.exit();
            PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Markets.getInstance(), e.player);
            builder.isValidInput((p, str) -> str.trim().length() >= 1);
            builder.sendValueMessage(TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_market_name").getMessage()));
            builder.toCancel("cancel");
            builder.onCancel(p -> e.manager.showGUI(e.player, new GUIMarketEdit(this.market)));
            builder.setValue((p, value) -> value);
            builder.onFinish((p, value) -> {
                this.market.setName(value.trim());
                this.market.setUpdatedAt(System.currentTimeMillis());
                e.manager.showGUI(e.player, new GUIMarketEdit(this.market));
            });


            PlayerChatInput<String> input = builder.build();
            input.start();
        });

        setButton(1, 0, ConfigItemUtil.build(this.market.isOpen() ? Settings.GUI_MARKET_EDIT_ITEMS_OPEN_ENABLE_ITEM.getString() : Settings.GUI_MARKET_EDIT_ITEMS_OPEN_DISABLE_ITEM.getString(), this.market.isOpen() ? Settings.GUI_MARKET_EDIT_ITEMS_OPEN_ENABLE_NAME.getString() : Settings.GUI_MARKET_EDIT_ITEMS_OPEN_DISABLE_NAME.getString(), market.isOpen() ? Settings.GUI_MARKET_EDIT_ITEMS_OPEN_ENABLE_LORE.getStringList() : Settings.GUI_MARKET_EDIT_ITEMS_OPEN_DISABLE_LORE.getStringList(), 1, null), ClickType.LEFT, e -> {
            this.market.setOpen(!this.market.isOpen());
            this.market.setUpdatedAt(System.currentTimeMillis());
            draw();
        });

        setButton(2, 0, ConfigItemUtil.build(Settings.GUI_MARKET_EDIT_ITEMS_ADD_CATEGORY_ITEM.getString(), Settings.GUI_MARKET_EDIT_ITEMS_ADD_CATEGORY_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_ADD_CATEGORY_LORE.getStringList(), 1, null), ClickType.LEFT, e -> {
            e.gui.exit();

            PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Markets.getInstance(), e.player);
            builder.isValidInput((p, str) -> ChatColor.stripColor(str.trim()).length() >= 1);
            builder.sendValueMessage(TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_category_name").getMessage()));
            builder.toCancel("cancel");
            builder.onCancel(p -> e.manager.showGUI(e.player, new GUIMarketEdit(this.market)));
            builder.setValue((p, value) -> value);
            builder.onFinish((p, value) -> {
                String possibleName = ChatColor.stripColor(value.trim());

                MarketCategory newMarketCategory = new MarketCategory(possibleName);
                newMarketCategory.setMarketId(market.getId());

                if (this.market.getCategories().stream().anyMatch(marketCategory -> marketCategory.getName().equalsIgnoreCase(possibleName))) {
                    Markets.getInstance().getLocale().getMessage("category_already_created").processPlaceholder("market_category_name", possibleName).sendPrefixedMessage(e.player);
                    e.manager.showGUI(e.player, new GUIMarketEdit(this.market));
                    return;
                }

                Markets.getInstance().getMarketManager().addCategoryToMarket(this.market, newMarketCategory);
                this.market.setUpdatedAt(System.currentTimeMillis());
                Markets.getInstance().getLocale().getMessage("created_category").processPlaceholder("market_category_name", possibleName).sendPrefixedMessage(e.player);
                e.manager.showGUI(e.player, new GUIMarketEdit(this.market));
            });


            PlayerChatInput<String> input = builder.build();
            input.start();
        });

        setButton(3, 0, ConfigItemUtil.build(Settings.GUI_MARKET_EDIT_ITEMS_ALL_ITEMS_ITEM.getString(), Settings.GUI_MARKET_EDIT_ITEMS_ALL_ITEMS_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_ALL_ITEMS_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIAllItems(this.market, true)));

        setButton(4, 0, ConfigItemUtil.build(Settings.GUI_MARKET_EDIT_ITEMS_DELETE_MARKET_ITEM.getString(), Settings.GUI_MARKET_EDIT_ITEMS_DELETE_MARKET_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_DELETE_MARKET_LORE.getStringList(), 1, null), ClickType.RIGHT, e -> {
            e.manager.showGUI(e.player, new GUIConfirm(this.market, null, GUIConfirm.ConfirmAction.DELETE_MARKET));
        });

        setButton(5, 0, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIMain(e.player)));
        setButton(5, 1, ConfigItemUtil.build(Settings.GUI_MARKET_EDIT_ITEMS_ADD_ITEM_ITEM.getString(), Settings.GUI_MARKET_EDIT_ITEMS_ADD_ITEM_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_ADD_ITEM_LORE.getStringList(), 1, null), ClickType.LEFT, e -> {
            if (market.getCategories().isEmpty()) {
                Markets.getInstance().getLocale().getMessage("market_category_required").sendPrefixedMessage(e.player);
                return;
            }

            e.manager.showGUI(e.player, new GUIAddItem(e.player, this.market));
        });

        setButton(5, 2, ConfigItemUtil.build(Settings.GUI_MARKET_EDIT_ITEMS_FEATURE_ITEM.getString(), Settings.GUI_MARKET_EDIT_ITEMS_FEATURE_NAME.getString(), Markets.getInstance().getMarketManager().getFeaturedMarkets().containsKey(this.market.getId()) ? Settings.GUI_MARKET_EDIT_ITEMS_FEATURE_LORE_ALREADY.getStringList() : Settings.GUI_MARKET_EDIT_ITEMS_FEATURE_LORE.getStringList(), 1, new HashMap<String, Object>(){{
            put("%feature_cost%", String.format("%,.2f", Settings.FEATURE_COST.getDouble()));
        }}), ClickType.LEFT, e -> {
            e.manager.showGUI(e.player, new GUIConfirm(this.market, null, GUIConfirm.ConfirmAction.FEATURE_MARKET));
        });

        List<MarketCategory> data = this.market.getCategories().stream().skip((page - 1) * 24L).limit(24L).collect(Collectors.toList());
        int slot = 11;
        for (MarketCategory category : data) {
            setButton(slot, ConfigItemUtil.build(category.getIcon(), Settings.GUI_MARKET_EDIT_ITEMS_CATEGORY_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_CATEGORY_LORE.getStringList(), 1, new HashMap<String, Object>() {{
                put("%category_name%", category.getName());
                put("%category_display_name%", category.getDisplayName());
            }}), e -> e.manager.showGUI(e.player, new GUICategorySettings(this.market, category)));

            slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 4 : slot + 1;
        }
    }
}
