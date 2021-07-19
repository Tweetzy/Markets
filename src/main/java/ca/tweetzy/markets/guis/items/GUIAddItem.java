package ca.tweetzy.markets.guis.items;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.events.MarketItemAddEvent;
import ca.tweetzy.markets.guis.category.GUICategorySelection;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 03 2021
 * Time Created: 3:26 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIAddItem extends Gui {

    private final Player player;
    private final Market market;

    private double itemPrice;
    private boolean useCustomCurrency;
    private boolean priceIsForStack;
    private MarketCategory selectedCategory;

    private ItemStack item;
    private ItemStack currency;

    public GUIAddItem(Player player, Market market, MarketCategory selectedCategory, double itemPrice, boolean useCustomCurrency, boolean priceIsForStack, ItemStack item, ItemStack currency) {
        this.player = player;
        this.market = market;
        this.selectedCategory = selectedCategory;
        this.itemPrice = itemPrice;
        this.useCustomCurrency = useCustomCurrency;
        this.priceIsForStack = priceIsForStack;
        this.item = item;
        this.currency = currency;
        setTitle(TextUtils.formatText(Settings.GUI_ADD_ITEM_TITLE.getString()));
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_ADD_ITEM_FILL_ITEM.getMaterial()));
        setAllowDrops(true);
        setAcceptsItems(true);
        setUseLockedCells(true);
        setAllowShiftClick(false);
        setRows(6);
        draw();
        setOnClose(close -> {
            if (getItem(2, 2) != null && getItem(2, 2).getType() != XMaterial.AIR.parseMaterial() && this.useCustomCurrency) {
                PlayerUtils.giveItem(close.player, getItem(2, 2));
            }

            if (getItem(2, 4) != null && getItem(2, 4).getType() != XMaterial.AIR.parseMaterial()) {
                PlayerUtils.giveItem(close.player, getItem(2, 4));
            }
        });
    }

    public GUIAddItem(Player player, Market market) {
        this(player, market, null, 1D, false, false, null, null);
    }

    private void draw() {
        reset();

        // draw the border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_ADD_ITEM_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_ADD_ITEM_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        placePriceButton();
        placePriceForStackButton();
        placeUseCustomCurrencyButton();
        placeSelectedCategoryButton();

        setUnlocked(2, 4, true);
        setItem(2, 4, XMaterial.AIR.parseItem());

        if (this.currency != null && this.currency != XMaterial.AIR.parseItem() && this.useCustomCurrency) {
            setItem(2, 2, this.currency);
        }

        if (this.item != null && this.item.getType() != XMaterial.AIR.parseMaterial()) {
            setItem(2, 4, this.item);
        }

        // cancel / confirm buttons
        setItems(29, 30, GuiUtils.createButtonItem(Settings.GUI_ADD_ITEM_ITEMS_CANCEL_ADD_ITEM.getMaterial(), TextUtils.formatText(Settings.GUI_ADD_ITEM_ITEMS_CANCEL_ADD_NAME.getString()), TextUtils.formatText(Settings.GUI_ADD_ITEM_ITEMS_CANCEL_ADD_LORE.getStringList())));
        setItems(32, 33, GuiUtils.createButtonItem(Settings.GUI_ADD_ITEM_ITEMS_CONFIRM_ADD_ITEM.getMaterial(), TextUtils.formatText(Settings.GUI_ADD_ITEM_ITEMS_CONFIRM_ADD_NAME.getString()), TextUtils.formatText(Settings.GUI_ADD_ITEM_ITEMS_CONFIRM_ADD_LORE.getStringList())));
        setActionForRange(29, 30, ClickType.LEFT, e -> e.gui.close());
        setActionForRange(32, 33, ClickType.LEFT, e -> {
            assignItemStacks();

            if (this.item == null || this.item.getType() == XMaterial.AIR.parseMaterial()) {
                Markets.getInstance().getLocale().getMessage("place_sell_item").sendPrefixedMessage(e.player);
                return;
            }

            if (this.market.getCategories().isEmpty()) {
                Markets.getInstance().getLocale().getMessage("market_category_required").sendPrefixedMessage(player);
                return;
            }

            if (this.selectedCategory == null) {
                Markets.getInstance().getLocale().getMessage("select_market_category").sendPrefixedMessage(player);
                return;
            }

            if (this.currency == null || this.currency.getType() == XMaterial.AIR.parseMaterial() && this.useCustomCurrency) {
                Markets.getInstance().getLocale().getMessage("place_currency_item").sendPrefixedMessage(e.player);
                return;
            }

            if (this.itemPrice <= 0) { // Technically this should never be the case...
                Markets.getInstance().getLocale().getMessage("price_is_zero_or_less").sendPrefixedMessage(e.player);
                return;
            }

            this.priceIsForStack = this.item.getAmount() == 1;

            MarketItem marketItem = new MarketItem(this.selectedCategory, this.item, this.itemPrice, this.priceIsForStack);
            if (this.useCustomCurrency) {
                marketItem.setUseItemCurrency(true);
                marketItem.setCurrencyItem(this.currency);
            }

            MarketItemAddEvent marketItemAddEvent = new MarketItemAddEvent(this.market, marketItem);
            Bukkit.getPluginManager().callEvent(marketItemAddEvent);
            if (marketItemAddEvent.isCancelled()) return;

            this.market.setUpdatedAt(System.currentTimeMillis());
            Markets.getInstance().getMarketManager().addItemToCategory(this.selectedCategory, marketItem);
            Markets.getInstance().getLocale().getMessage("added_item_to_category").processPlaceholder("item_name", Common.getItemName(this.item)).processPlaceholder("market_category_name", this.selectedCategory.getName()).sendPrefixedMessage(e.player);
            setItem(2, 4, XMaterial.AIR.parseItem());
            e.gui.close();
        });
    }

    private void placePriceButton() {
        setButton(3, 4, ConfigItemUtil.build(Settings.GUI_ADD_ITEM_ITEMS_PRICE_ITEM.getString(), Settings.GUI_ADD_ITEM_ITEMS_PRICE_NAME.getString(), this.useCustomCurrency ? Settings.GUI_ADD_ITEM_ITEMS_PRICE_LORE_CUSTOM_CURRENCY.getStringList() : Settings.GUI_ADD_ITEM_ITEMS_PRICE_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%market_item_price%", useCustomCurrency ? Math.round(itemPrice) : String.format("%,.2f", itemPrice));
            put("%market_item_currency%", useCustomCurrency && currency != null ? Common.getItemName(currency) : "");
        }}), ClickType.LEFT, e -> {
            assignItemStacks();
            e.gui.exit();
            ChatPrompt.showPrompt(Markets.getInstance(), e.player, TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_market_item_price").getMessage()), chat -> {
                String msg = chat.getMessage();
                if (msg != null && msg.length() != 0 && NumberUtils.isDouble(msg) && Double.parseDouble(msg) > 0) {
                    this.itemPrice = Double.parseDouble(msg);
                    reopen(e.player);
                }
            }).setOnClose(() -> reopen(e.player)).setOnCancel(() -> reopen(e.player));
        });
    }

    private void placePriceForStackButton() {
        setButton(2, 5, ConfigItemUtil.build(
                this.priceIsForStack ? Settings.GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_ENABLED_ITEM.getString() : Settings.GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_DISABLED_ITEM.getString(),
                this.priceIsForStack ? Settings.GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_ENABLED_NAME.getString() : Settings.GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_DISABLED_NAME.getString(),
                this.priceIsForStack ? Settings.GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_ENABLED_LORE.getStringList() : Settings.GUI_ADD_ITEM_ITEMS_PRICE_FOR_STACK_DISABLED_LORE.getStringList(),
                1, null), ClickType.LEFT, e -> {
            this.priceIsForStack = !this.priceIsForStack;
            placePriceForStackButton();
        });
    }

    private void placeUseCustomCurrencyButton() {
        setButton(2, 3, ConfigItemUtil.build(
                this.useCustomCurrency ? Settings.GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_ENABLED_ITEM.getString() : Settings.GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_DISABLED_ITEM.getString(),
                this.useCustomCurrency ? Settings.GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_ENABLED_NAME.getString() : Settings.GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_DISABLED_NAME.getString(),
                this.useCustomCurrency ? Settings.GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_ENABLED_LORE.getStringList() : Settings.GUI_ADD_ITEM_ITEMS_USE_CUSTOM_CURRENCY_DISABLED_LORE.getStringList(),
                1, null), ClickType.LEFT, e -> {
            this.useCustomCurrency = !this.useCustomCurrency;
            placeUseCustomCurrencyButton();
            placePriceButton();
        });

        if (this.useCustomCurrency) {
            setItem(2, 2, XMaterial.AIR.parseItem());
            setUnlocked(2, 2, true);
        }
        if (!this.useCustomCurrency) {
            if (getItem(2, 2) != null && getItem(2, 2).getType() != XMaterial.AIR.parseMaterial()) {
                PlayerUtils.giveItem(this.player, getItem(2, 2));
            }
            setItem(2, 2, GuiUtils.createButtonItem(Settings.GUI_ADD_ITEM_ITEMS_CURRENCY_HOLDER_ITEM.getMaterial(), TextUtils.formatText(Settings.GUI_ADD_ITEM_ITEMS_CURRENCY_HOLDER_NAME.getString()), TextUtils.formatText(Settings.GUI_ADD_ITEM_ITEMS_CURRENCY_HOLDER_LORE.getStringList())));
            setUnlocked(2, 2, false);
        }
    }

    private void placeSelectedCategoryButton() {
        setButton(2, 6, ConfigItemUtil.build(Settings.GUI_ADD_ITEM_ITEMS_SELECTED_CATEGORY_ITEM.getString(), Settings.GUI_ADD_ITEM_ITEMS_SELECTED_CATEGORY_NAME.getString(), Settings.GUI_ADD_ITEM_ITEMS_SELECTED_CATEGORY_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%selected_market_category%", selectedCategory != null ? selectedCategory.getDisplayName() : Markets.getInstance().getLocale().getMessage("misc.category not selected").getMessage());
        }}), ClickType.LEFT, e -> {
            assignItemStacks();
            e.gui.exit();
            e.manager.showGUI(e.player, new GUICategorySelection(e.player, this.market, this.selectedCategory, this.itemPrice, this.useCustomCurrency, this.priceIsForStack, this.item, this.currency));
        });
    }

    private void reopen(Player player) {
        Markets.getInstance().getGuiManager().showGUI(player, new GUIAddItem(player, this.market, this.selectedCategory, this.itemPrice, this.useCustomCurrency, this.priceIsForStack, this.item, this.currency));
    }

    private void assignItemStacks() {
        this.item = getItem(2, 4);
        this.currency = getItem(2, 2);
    }
}
