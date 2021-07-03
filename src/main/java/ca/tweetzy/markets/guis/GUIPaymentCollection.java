package ca.tweetzy.markets.guis;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.SimplePagedGui;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.market.GUIMarketEdit;
import ca.tweetzy.markets.guis.market.GUIMarketView;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.transaction.Payment;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 03 2021
 * Time Created: 2:48 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIPaymentCollection extends Gui {

    private final Player player;
    private final List<Payment> payments;

    public GUIPaymentCollection(Player player) {
        this.player = player;
        this.payments = Markets.getInstance().getTransactionManger().getPayments(this.player.getUniqueId());
        setTitle(TextUtils.formatText(Settings.GUI_PAYMENT_COLLECTION_TITLE.getString()));
        setDefaultItem(Settings.GUI_PAYMENT_COLLECTION_FILL_ITEM.getMaterial().parseItem());
        setAcceptsItems(false);
        setAllowDrops(false);
        setUseLockedCells(true);
        setRows(6);
        draw();
    }

    private void draw() {
        reset();
        pages = (int) Math.max(1, Math.ceil(this.payments.size() / (double) 45));

        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.gui.close());
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        Markets.newChain().async(() -> {
            List<Payment> data = this.payments.stream().skip((page - 1) * 45L).limit(45L).collect(Collectors.toList());

            int slot = 0;
            for (Payment payment : data) {
                setButton(slot++, payment.getItem(), e -> {
                    PlayerUtils.giveItem(this.player, payment.getItem().clone());
                    Markets.getInstance().getTransactionManger().removePayment(payment);
                    e.manager.showGUI(e.player, new GUIPaymentCollection(e.player));
                });
            }
        }).execute();
    }
}
