package ca.tweetzy.markets.guis.requests;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.request.Request;
import ca.tweetzy.markets.request.RequestItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.transaction.Payment;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 05 2021
 * Time Created: 3:00 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIRequestFulfillment extends Gui {

    private final Request request;

    public GUIRequestFulfillment(Request request) {
        this.request = request;
        setTitle(TextUtils.formatText(Settings.GUI_REQUEST_FULFILLMENT_TITLE.getString()));
        setAllowDrops(false);
        setAcceptsItems(false);
        setAllowShiftClick(false);
        setUseLockedCells(true);
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_REQUEST_FULFILLMENT_FILL_ITEM.getMaterial()));
        setRows(6);
        draw();
        setOnClose(close -> close.manager.showGUI(close.player, new GUIOpenRequests(close.player, true)));
    }

    private void draw() {
        reset();

        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_REQUEST_FULFILLMENT_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_REQUEST_FULFILLMENT_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        Markets.newChain().asyncFirst(() -> request.getRequestedItems().stream().skip((page - 1) * 28L).limit(28L).filter(requestItem -> !requestItem.isFulfilled()).collect(Collectors.toList())).asyncLast((data) -> {
            pages = (int) Math.max(1, Math.ceil(this.request.getRequestedItems().size() / (double) 28L));
            setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
            setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.gui.close());
            setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
            setOnPage(e -> draw());

            int slot = 10;
            for (RequestItem requestItem : data) {
                ItemStack item = requestItem.getItem().clone();

                List<String> lore = Common.getItemLore(item);
                lore.addAll(requestItem.isUseCustomCurrency() ? Settings.GUI_REQUEST_FULFILLMENT_REQUEST_ITEM_LORE_CUSTOM_CURRENCY.getStringList() : Settings.GUI_REQUEST_FULFILLMENT_REQUEST_ITEM_LORE.getStringList());

                setButton(slot, ConfigItemUtil.build(item, Common.getItemName(item), lore, requestItem.getAmount(), new HashMap<String, Object>() {{
                    put("%request_requesting_player%", Bukkit.getOfflinePlayer(request.getRequester()).getName());
                    put("%request_amount%", requestItem.getAmount());
                    put("%request_price%", requestItem.isUseCustomCurrency() ? Math.round(requestItem.getPrice()) : String.format("%,.2f", requestItem.getPrice()));
                    put("%market_item_currency%", requestItem.isUseCustomCurrency() ? Common.getItemName(requestItem.getCurrency()) : "");
                }}), e -> {
                    Markets.newChain().async(() -> {
                        if (MarketsAPI.getInstance().getItemCountInPlayerInventory(e.player, requestItem.getItem()) < requestItem.getAmount()) {
                            Markets.getInstance().getLocale().getMessage("not_enough_items").sendPrefixedMessage(e.player);
                            return;
                        }

                        OfflinePlayer requester = Bukkit.getOfflinePlayer(this.request.getRequester());

                        if (requestItem.isUseCustomCurrency()) {
                            if (Markets.getInstance().getCurrencyBank().getTotalCurrency(requester.getUniqueId(), requestItem.getCurrency()) < Math.round(requestItem.getPrice())) {
                                Markets.getInstance().getLocale().getMessage("player_does_not_have_funds").processPlaceholder("player", requester.getName()).sendPrefixedMessage(e.player);
                                return;
                            }

                            MarketsAPI.getInstance().removeSpecificItemQuantityFromPlayer(e.player, requestItem.getItem(), requestItem.getAmount());
                            Markets.getInstance().getCurrencyBank().removeCurrency(requester.getUniqueId(), requestItem.getCurrency(), (int) Math.round(requestItem.getPrice()));

                            ItemStack currency = requestItem.getCurrency().clone();

                            int maxStackSize = currency.getMaxStackSize();
                            int fullStacks = (int) Math.round(requestItem.getPrice()) / maxStackSize;
                            int remainder = (int) Math.round(requestItem.getPrice()) % maxStackSize;

                            ItemStack maxSizeCurrency = currency.clone();
                            ItemStack remainderCurrency = currency.clone();
                            maxSizeCurrency.setAmount(maxStackSize);
                            remainderCurrency.setAmount(remainder);

                            for (int i = 0; i < fullStacks; i++) {
                                Markets.getInstance().getTransactionManger().addPayment(new Payment(e.player.getUniqueId(), maxSizeCurrency));
                            }

                            if (remainder != 0) {
                                currency.setAmount(remainder);
                                Markets.getInstance().getTransactionManger().addPayment(new Payment(e.player.getUniqueId(), remainderCurrency));
                            }

                            Markets.getInstance().getLocale().getMessage("money_add_custom_currency").processPlaceholder("currency_item", Common.getItemName(currency)).processPlaceholder("price", String.format("%,.2f", requestItem.getPrice())).sendPrefixedMessage(e.player);
                            if (requester.isOnline()) {
                                Markets.getInstance().getLocale().getMessage("money_remove_custom_currency").processPlaceholder("currency_item", Common.getItemName(currency)).processPlaceholder("price", String.format("%,.2f", requestItem.getPrice())).sendPrefixedMessage(requester.getPlayer());
                            }

                            handleRequest(requester, requestItem);
                            draw();
                            return;
                        }

                        if (!Markets.getInstance().getEconomyManager().has(requester, requestItem.getPrice())) {
                            Markets.getInstance().getLocale().getMessage("player_does_not_have_funds").processPlaceholder("player", requester.getName()).sendPrefixedMessage(e.player);
                            return;
                        }

                        MarketsAPI.getInstance().removeSpecificItemQuantityFromPlayer(e.player, requestItem.getItem(), requestItem.getAmount());
                        Markets.getInstance().getEconomyManager().withdrawPlayer(requester, requestItem.getPrice());
                        Markets.getInstance().getEconomyManager().depositPlayer(e.player, requestItem.getPrice());

                        Markets.getInstance().getLocale().getMessage("money_add").processPlaceholder("price", String.format("%,.2f", requestItem.getPrice())).sendPrefixedMessage(e.player);
                        if (requester.isOnline()) {
                            Markets.getInstance().getLocale().getMessage("money_remove").processPlaceholder("price", String.format("%,.2f", requestItem.getPrice())).sendPrefixedMessage(requester.getPlayer());
                        }

                        handleRequest(requester, requestItem);
                        draw();
                    }).execute();
                });

                slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
            }
        }).execute();
    }

    private void handleRequest(OfflinePlayer requester, RequestItem requestItem) {
        ItemStack requestedItem = requestItem.getItem();
        requestedItem.setAmount(requestItem.getAmount());

        Markets.getInstance().getTransactionManger().addPayment(new Payment(
                requester.getUniqueId(),
                requestedItem
        ));

        this.request.getRequestedItems().remove(requestItem);
        if (this.request.getRequestedItems().size() == 0) {
            Markets.getInstance().getRequestManager().deleteRequest(this.request);
        }
    }
}
