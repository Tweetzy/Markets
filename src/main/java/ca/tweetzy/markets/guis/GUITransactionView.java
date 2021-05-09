package ca.tweetzy.markets.guis;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.transaction.Transaction;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 06 2021
 * Time Created: 2:31 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUITransactionView extends Gui {

    final Player player;
    List<Transaction> transactionList;

    public GUITransactionView(Player player) {
        this.player = player;
        setTitle(TextUtils.formatText(Settings.GUI_TRANSACTIONS_TITLE.getString()));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_TRANSACTIONS_FILL_ITEM.getMaterial()));
        setRows(6);

        draw();
    }

    private void draw() {
        reset();

        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_TRANSACTIONS_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_TRANSACTIONS_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), e -> e.manager.showGUI(this.player, new GUIMain(this.player)));
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        // this is an experiment, will receive feedback from users on this feature
        Markets.newChain().async(() -> this.transactionList = Markets.getInstance().getTransactionManger().getTransactions().stream().filter(transaction -> transaction.getPurchaser().equals(this.player.getUniqueId()) || transaction.getMarketId().equals(Markets.getInstance().getMarketManager().getMarketByPlayer(this.player).getId())
        ).collect(Collectors.toList())).sync(() -> {
            pages = (int) Math.max(1, Math.ceil(this.transactionList.size() / (double) 28L));

            int slot = 10;
            List<Transaction> data = this.transactionList.stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());
            for (Transaction transaction : data) {
                setItem(slot, ConfigItemUtil.build(Settings.GUI_TRANSACTIONS_TRANSACTION_ITEM.getString(), Settings.GUI_TRANSACTIONS_TRANSACTION_NAME.getString(), Settings.GUI_TRANSACTIONS_TRANSACTION_LORE.getStringList(), 1, new HashMap<String, Object>(){{
                    put("%transaction_id%", transaction.getId().toString());
                    put("%transaction_buyer%", Bukkit.getOfflinePlayer(transaction.getPurchaser()).getName());
                    put("%transaction_market%", Markets.getInstance().getMarketManager().getMarketById(transaction.getMarketId()) == null ? "Not Available" : Markets.getInstance().getMarketManager().getMarketById(transaction.getMarketId()).getName());
                    put("%transaction_price%", String.format("%,.2f", transaction.getFinalPrice()));
                    put("%transaction_date%", Common.convertMillisToDate(transaction.getTime()));
                }}));

                slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
            }

        }).execute();
    }
}
