package ca.tweetzy.markets.guis.items;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.api.events.MarketItemPurchaseEvent;
import ca.tweetzy.markets.guis.market.GUIMarketView;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.transaction.Payment;
import ca.tweetzy.markets.transaction.Transaction;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:46 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIItemPurchase extends Gui {

    private final Market market;
    private final MarketItem marketItem;
    private int purchaseQty;

    public GUIItemPurchase(Market market, MarketItem marketItem) {
        this.market = market;
        this.marketItem = marketItem;
        this.purchaseQty = marketItem.getItemStack().getAmount();
        setTitle(TextUtils.formatText(Settings.GUI_ITEM_PURCHASE_TITLE.getString()));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setAllowShiftClick(false);
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_ITEM_PURCHASE_FILL_ITEM.getMaterial()));
        setRows(6);

        draw();
    }

    private void draw() {
        reset();

        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_ITEM_PURCHASE_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_ITEM_PURCHASE_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        setButton(5, 4, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CLOSE_BTN_ITEM.getString()), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIMarketView(this.market)));

        setItem(2, 4, purchaseItemQtySpecific(this.purchaseQty));
        addPurchaseInfoItem();

        setButton(2, 2, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_ITEM_PURCHASE_ITEMS_DECR_ITEM.getString()), Settings.GUI_ITEM_PURCHASE_ITEMS_DECR_NAME.getString(), Settings.GUI_ITEM_PURCHASE_ITEMS_DECR_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%decrement_amount%", Settings.DECREMENT_NUMBER_ONE.getInt());
        }}), e -> {
            if (purchaseQty - Settings.DECREMENT_NUMBER_ONE.getInt() >= 1) {
                purchaseQty -= Settings.DECREMENT_NUMBER_ONE.getInt();
            } else {
                purchaseQty = 1;
            }
            updateDisplay();
        });

        setButton(3, 2, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_ITEM_PURCHASE_ITEMS_DECR_ITEM.getString()), Settings.GUI_ITEM_PURCHASE_ITEMS_DECR_NAME.getString(), Settings.GUI_ITEM_PURCHASE_ITEMS_DECR_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%decrement_amount%", Settings.DECREMENT_NUMBER_TWO.getInt());
        }}), e -> {
            if (purchaseQty - Settings.DECREMENT_NUMBER_TWO.getInt() >= 1) {
                purchaseQty -= Settings.DECREMENT_NUMBER_TWO.getInt();
            } else {
                purchaseQty = 1;
            }
            updateDisplay();
        });

        setButton(2, 6, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_ITEM_PURCHASE_ITEMS_INC_ITEM.getString()), Settings.GUI_ITEM_PURCHASE_ITEMS_INC_NAME.getString(), Settings.GUI_ITEM_PURCHASE_ITEMS_INC_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%increment_amount%", Settings.INCREMENT_NUMBER_ONE.getInt());
        }}), e -> {
            if (purchaseQty + Settings.INCREMENT_NUMBER_ONE.getInt() <= this.marketItem.getItemStack().getAmount()) {
                purchaseQty += Settings.INCREMENT_NUMBER_ONE.getInt();
            } else {
                purchaseQty = this.marketItem.getItemStack().getAmount();
            }
            updateDisplay();
        });

        setButton(3, 6, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_ITEM_PURCHASE_ITEMS_INC_ITEM.getString()), Settings.GUI_ITEM_PURCHASE_ITEMS_INC_NAME.getString(), Settings.GUI_ITEM_PURCHASE_ITEMS_INC_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%increment_amount%", Settings.INCREMENT_NUMBER_TWO.getInt());
        }}), e -> {
            if (purchaseQty + Settings.INCREMENT_NUMBER_TWO.getInt() <= this.marketItem.getItemStack().getAmount()) {
                purchaseQty += Settings.INCREMENT_NUMBER_TWO.getInt();
            } else {
                purchaseQty = this.marketItem.getItemStack().getAmount();
            }
            updateDisplay();
        });
    }

    private void updateDisplay() {
        setItem(2, 4, purchaseItemQtySpecific(this.purchaseQty));
        addPurchaseInfoItem();
    }

    private ItemStack purchaseItemQtySpecific(int qty) {
        ItemStack stack = this.marketItem.getItemStack().clone();
        stack.setAmount(qty);
        return stack;
    }

    private void addPurchaseInfoItem() {
        setButton(3, 4, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_ITEM_PURCHASE_ITEMS_PURCHASE_ITEM.getString()), Settings.GUI_ITEM_PURCHASE_ITEMS_PURCHASE_NAME.getString(), marketItem.isUseItemCurrency() ? Settings.GUI_ITEM_PURCHASE_ITEMS_PURCHASE_LORE_CUSTOM_CURRENCY.getStringList() : Settings.GUI_ITEM_PURCHASE_ITEMS_PURCHASE_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%purchase_quantity%", purchaseQty);
            put("%stack_price%", marketItem.isUseItemCurrency() ? Math.round(marketItem.getPrice()) : String.format("%,.2f", marketItem.getPrice()));
            put("%market_item_price_for_stack%", marketItem.getTranslatedPriceForStack());
            put("%purchase_price%", marketItem.isUseItemCurrency() ? marketItem.isPriceForStack() ? Math.round(marketItem.getPrice()) : Math.round(purchaseQty * marketItem.getPrice()) : String.format("%,.2f", marketItem.isPriceForStack() ? marketItem.getPrice() : purchaseQty * marketItem.getPrice()));
            put("%market_item_currency%", marketItem.isUseItemCurrency() ? Common.getItemName(marketItem.getCurrencyItem()) : "");
        }}), e -> {
            if (e.clickType == ClickType.LEFT) {
                handlePurchase(e.player, this.market, this.market.getCategories().stream().filter(c -> this.marketItem.getCategoryId().equals(c.getId())).findFirst().get(), this.marketItem);
            }
        });
    }

    private void handlePurchase(Player buyer, Market market, MarketCategory category, MarketItem marketItem) {
        Market foundMarket = Markets.getInstance().getMarketManager().getMarketById(market.getId());
        if (foundMarket == null) return;
        MarketCategory foundCategory = foundMarket.getCategories().stream().filter(c -> c.getId().equals(category.getId())).findFirst().orElse(null);
        if (foundCategory == null) return;
        MarketItem foundItem = foundCategory.getItems().stream().filter(i -> i.getId().equals(marketItem.getId())).findFirst().orElse(null);
        if (foundItem == null) return;

        ItemStack item = foundItem.getItemStack();
        int originalSize = item.getAmount();

        double price = foundItem.isPriceForStack() ? foundItem.getPrice() : this.purchaseQty * foundItem.getPrice();
        double totalTax = Settings.TAX_ENABLED.getBoolean() ? price * Settings.TAX_AMOUNT.getDouble() / 100 : 0;

        if (marketItem.isUseItemCurrency()) {
            if (MarketsAPI.getInstance().getItemCountInPlayerInventory(buyer, marketItem.getCurrencyItem()) < (Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? Math.round(price + totalTax) : Math.round(price))) {
                Markets.getInstance().getLocale().getMessage("not_enough_money_custom_currency").processPlaceholder("currency_item", Common.getItemName(marketItem.getCurrencyItem())).sendPrefixedMessage(buyer);
                return;
            }
        } else {
            if (!EconomyManager.hasBalance(buyer, Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? price + totalTax : price)) {
                Markets.getInstance().getLocale().getMessage("not_enough_money").sendPrefixedMessage(buyer);
                return;
            }
        }

        MarketItemPurchaseEvent marketItemPurchaseEvent = new MarketItemPurchaseEvent(foundMarket, foundItem, Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? foundItem.isUseItemCurrency() ? Math.round(price + totalTax) : price + totalTax : foundItem.isUseItemCurrency() ? Math.round(price) : price, this.purchaseQty);
        Bukkit.getPluginManager().callEvent(marketItemPurchaseEvent);
        if (marketItemPurchaseEvent.isCancelled()) return;

        if (foundItem.isUseItemCurrency()) {
            transferCustomCurrency(market.getOwner(), buyer, foundItem, price);
        } else {
            transferMoney(market.getOwner(), buyer, price);
        }

        if (foundItem.isPriceForStack() || originalSize - this.purchaseQty <= 0) {
            PlayerUtils.giveItem(buyer, item);
            foundCategory.getItems().remove(foundItem);
        } else {
            item.setAmount(this.purchaseQty);
            PlayerUtils.giveItem(buyer, item);
            item.setAmount(originalSize - this.purchaseQty);
            foundItem.setItemStack(item);
        }

        Markets.getInstance().getGuiManager().showGUI(buyer, new GUIMarketView(foundMarket));

        if (Settings.LOG_TRANSACTIONS.getBoolean()) {
            Markets.getInstance().getTransactionManger().addTransaction(new Transaction(
                    foundMarket.getId(),
                    buyer.getUniqueId(),
                    item,
                    this.purchaseQty,
                    Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? foundItem.isUseItemCurrency() ? Math.round(price + totalTax) : price + totalTax : foundItem.isUseItemCurrency() ? Math.round(price) : Math.round(price)
            ));
        }
    }

    private void transferCustomCurrency(UUID seller, Player buyer, MarketItem item, double price) {
        double totalTax = Settings.TAX_ENABLED.getBoolean() ? price * Settings.TAX_AMOUNT.getDouble() / 100 : 0;
        OfflinePlayer theSeller = Bukkit.getOfflinePlayer(seller);

        ItemStack currency = item.getCurrencyItem().clone();

        int maxStackSize = currency.getMaxStackSize();
        int fullStacks = (int) (Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? Math.round(price - totalTax) : Math.round(price)) / maxStackSize;
        int remainder = (int) (Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? Math.round(price - totalTax) : Math.round(price)) % maxStackSize;

        ItemStack maxSizeCurrency = currency.clone();
        ItemStack remainderCurrency = currency.clone();
        maxSizeCurrency.setAmount(maxStackSize);
        remainderCurrency.setAmount(remainder);

        for (int i = 0; i < fullStacks; i++) {
            Markets.getInstance().getTransactionManger().addPayment(new Payment(seller, maxSizeCurrency));
        }

        if (remainder != 0) {
            currency.setAmount(remainder);
            Markets.getInstance().getTransactionManger().addPayment(new Payment(seller, remainderCurrency));
        }

        MarketsAPI.getInstance().removeSpecificItemQuantityFromPlayer(buyer, item.getCurrencyItem(), (int) (Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? Math.round(price + totalTax) : Math.round(price)));

        Markets.getInstance().getLocale().getMessage("money_remove_custom_currency").processPlaceholder("price", Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? Math.round(price + totalTax) : Math.round(price)).processPlaceholder("currency_item", Common.getItemName(item.getCurrencyItem())).sendPrefixedMessage(buyer);
        if (theSeller.isOnline()) {
            Markets.getInstance().getLocale().getMessage("money_add_custom_currency").processPlaceholder("price", Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? Math.round(price) : Math.round(price - totalTax)).processPlaceholder("currency_item", Common.getItemName(item.getCurrencyItem())).sendPrefixedMessage(theSeller.getPlayer());
        }
    }

    private void transferMoney(UUID seller, Player buyer, double price) {
        double totalTax = Settings.TAX_ENABLED.getBoolean() ? price * Settings.TAX_AMOUNT.getDouble() / 100 : 0;

        OfflinePlayer theSeller = Bukkit.getOfflinePlayer(seller);
        EconomyManager.deposit(theSeller, Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? price : price - totalTax);
        EconomyManager.withdrawBalance(buyer, Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? price + totalTax : price);

        Markets.getInstance().getLocale().getMessage("money_remove").processPlaceholder("price", String.format("%,.2f", Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? price + totalTax : price)).sendPrefixedMessage(buyer);
        if (theSeller.isOnline()) {
            Markets.getInstance().getLocale().getMessage("money_add").processPlaceholder("price", String.format("%,.2f", Settings.TAX_BUYER_INSTEAD_OF_SELLER.getBoolean() ? price : price - totalTax)).sendPrefixedMessage(theSeller.getPlayer());
        }
    }
}
