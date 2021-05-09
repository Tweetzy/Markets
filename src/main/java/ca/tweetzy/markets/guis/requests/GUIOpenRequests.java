package ca.tweetzy.markets.guis.requests;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.guis.GUIMain;
import ca.tweetzy.markets.request.Request;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 07 2021
 * Time Created: 3:33 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIOpenRequests extends Gui {

    final Player player;
    final boolean all;
    List<Request> playerRequests;

    public GUIOpenRequests(Player player, boolean all) {
        this.player = player;
        this.all = all;
        setTitle(TextUtils.formatText(all ? Settings.GUI_OPEN_REQUEST_TITLE_ALL.getString() : Settings.GUI_OPEN_REQUEST_TITLE.getString()));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_OPEN_REQUEST_FILL_ITEM.getMaterial()));
        setRows(6);

        draw();
    }

    private void draw() {
        reset();

        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_OPEN_REQUEST_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_OPEN_REQUEST_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), e -> e.manager.showGUI(this.player, new GUIMain(this.player)));
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        if (!this.all) {
            setButton(5, 0, new TItemBuilder(Settings.GUI_OPEN_REQUEST_ITEMS_EMPTY_ITEM.getMaterial().parseMaterial()).setName(Settings.GUI_OPEN_REQUEST_ITEMS_EMPTY_NAME.getString()).setLore(Settings.GUI_OPEN_REQUEST_ITEMS_EMPTY_LORE.getStringList()).toItemStack(), e -> {
                Markets.getInstance().getRequestManager().deletePlayerRequests(this.player);
                draw();
            });

            setButton(5, 8, new TItemBuilder(Settings.GUI_OPEN_REQUEST_ITEMS_COLLECTION_ITEM.getMaterial().parseMaterial()).setName(Settings.GUI_OPEN_REQUEST_ITEMS_COLLECTION_NAME.getString()).setLore(Settings.GUI_OPEN_REQUEST_ITEMS_COLLECTION_LORE.getStringList()).toItemStack(), e -> {
                e.manager.showGUI(this.player, new GUIRequestCollection(this.player));
            });
        }

        Markets.newChain().async(() -> this.playerRequests = this.all ? Markets.getInstance().getRequestManager().getNonFulfilledRequests() : Markets.getInstance().getRequestManager().getRequestsByPlayer(this.player, false)).sync(() -> {
            pages = (int) Math.max(1, Math.ceil(this.playerRequests.size() / (double) 28L));

            int slot = 10;
            List<Request> data = this.playerRequests.stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());

            for (Request request : data) {
                ItemStack item = request.getItem().clone();

                List<String> lore = Common.getItemLore(item);
                lore.addAll(this.all ? Settings.GUI_OPEN_REQUEST_ITEMS_REQUEST_LORE_ALL.getStringList() : Settings.GUI_OPEN_REQUEST_ITEMS_REQUEST_LORE.getStringList());

                setButton(slot, ConfigItemUtil.build(item, Settings.GUI_OPEN_REQUEST_ITEMS_REQUEST_NAME.getString(), lore, request.getAmount(), new HashMap<String, Object>() {{
                    put("%request_item_name%", Common.getItemName(item));
                    put("%request_amount%", request.getAmount());
                    put("%request_price%", String.format("%,.2f", request.getPrice()));
                    put("%request_requesting_player%", Bukkit.getOfflinePlayer(request.getRequester()).getName());
                }}), e -> {
                    if (!this.all && e.clickType == ClickType.MIDDLE) {
                        Markets.getInstance().getRequestManager().deleteRequest(request);
                        draw();
                        return;
                    }

                    if (request.getRequester().equals(this.player.getUniqueId())) {
                        Markets.getInstance().getLocale().getMessage("cannot_fulfill_own").sendPrefixedMessage(this.player);
                        return;
                    }

                    if (MarketsAPI.getInstance().getItemCountInPlayerInventory(this.player, request.getItem()) < request.getAmount()) {
                        Markets.getInstance().getLocale().getMessage("not_enough_items").sendPrefixedMessage(this.player);
                        return;
                    }

                    OfflinePlayer requester = Bukkit.getOfflinePlayer(request.getRequester());

                    if (!Markets.getInstance().getEconomy().has(requester, request.getPrice())) {
                        Markets.getInstance().getLocale().getMessage("player_does_not_have_funds").processPlaceholder("player", requester.getName()).sendPrefixedMessage(this.player);
                        return;
                    }

                    Markets.getInstance().getEconomy().withdrawPlayer(requester, request.getPrice());
                    Markets.getInstance().getEconomy().depositPlayer(this.player, request.getPrice());
                    MarketsAPI.getInstance().removeSpecificItemQuantityFromPlayer(this.player, request.getItem(), request.getAmount());
                    request.setFulfilled(true);

                    Markets.getInstance().getLocale().getMessage("money_add").processPlaceholder("price", String.format("%,.2f", request.getPrice())).sendPrefixedMessage(this.player);
                    if (requester.isOnline()) {
                        Markets.getInstance().getLocale().getMessage("money_remove").processPlaceholder("price", String.format("%,.2f", request.getPrice())).sendPrefixedMessage(requester.getPlayer());
                    }

                    e.manager.showGUI(this.player, new GUIOpenRequests(this.player, true));
                });

                slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
            }
        }).execute();
    }
}
