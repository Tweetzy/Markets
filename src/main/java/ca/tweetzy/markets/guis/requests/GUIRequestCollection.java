package ca.tweetzy.markets.guis.requests;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.GUIMain;
import ca.tweetzy.markets.request.Request;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 07 2021
 * Time Created: 4:39 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIRequestCollection extends Gui {

    final Player player;
    List<Request> requestedItems;

    public GUIRequestCollection(Player player) {
        this.player = player;
        setTitle(TextUtils.formatText(Settings.GUI_REQUEST_COLLECTION_TITLE.getString()));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setDefaultItem(Settings.GUI_REQUEST_COLLECTION_FILL_ITEM.getMaterial().parseItem());
        setRows(6);

        draw();
    }

    private void draw() {
        reset();

        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, Settings.GUI_REQUEST_COLLECTION_BORDER_ITEM.getMaterial().parseItem());
            if (Settings.GUI_REQUEST_COLLECTION_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), e -> e.manager.showGUI(this.player, new GUIOpenRequests(this.player, false)));
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        Markets.newChain().async(() -> this.requestedItems = Markets.getInstance().getRequestManager().getRequestsByPlayer(this.player, true).stream().filter(Request::isFulfilled).collect(Collectors.toList())).sync(() -> {
            pages = (int) Math.max(1, Math.ceil(this.requestedItems.size() / (double) 28L));

            int slot = 10;
            List<Request> data = this.requestedItems.stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());
            for (Request request : data) {
                ItemStack item = request.getItem().clone();
                item.setAmount(request.getAmount());
                setButton(slot, item, e -> {
                    PlayerUtils.giveItem(this.player, item);
                    Markets.getInstance().getRequestManager().deleteRequest(request);
                    draw();
                });

                slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
            }

        }).execute();
    }
}
