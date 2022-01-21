package ca.tweetzy.markets.guis.requests;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.GUIMain;
import ca.tweetzy.markets.guis.payment.GUIPaymentCollection;
import ca.tweetzy.markets.request.Request;
import ca.tweetzy.markets.request.RequestItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 07 2021
 * Time Created: 3:33 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIOpenRequests extends Gui {

	private final Player player;
	private final boolean all;
	private List<Request> playerRequests = null;

	public GUIOpenRequests(Player player, boolean all) {
		this.player = player;
		this.all = all;
		setTitle(TextUtils.formatText(all ? Settings.GUI_OPEN_REQUEST_TITLE_ALL.getString() : Settings.GUI_OPEN_REQUEST_TITLE.getString()));
		setAllowDrops(false);
		setAcceptsItems(false);
		setAllowShiftClick(false);
		setUseLockedCells(true);
		setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_OPEN_REQUEST_FILL_ITEM.getMaterial()));
		setRows(6);

		draw();
	}

	public GUIOpenRequests(Player player, List<Request> requests) {
		this(player, true);
		this.playerRequests = requests;
	}

	private void draw() {
		reset();

		// make border
		for (int i : Numbers.GUI_BORDER_6_ROWS) {
			setItem(i, GuiUtils.getBorderItem(Settings.GUI_OPEN_REQUEST_BORDER_ITEM.getMaterial()));
			if (Settings.GUI_OPEN_REQUEST_GLOW_BORDER.getBoolean()) highlightItem(i);
		}

		if (!this.all) {
			setButton(5, 0, new TItemBuilder(Common.getItemStack(Settings.GUI_OPEN_REQUEST_ITEMS_EMPTY_ITEM.getString())).setName(Settings.GUI_OPEN_REQUEST_ITEMS_EMPTY_NAME.getString()).setLore(Settings.GUI_OPEN_REQUEST_ITEMS_EMPTY_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
				Markets.getInstance().getRequestManager().getPlayerRequests(this.player).forEach(Markets.getInstance().getRequestManager()::deleteRequest);
				e.manager.showGUI(e.player, new GUIOpenRequests(this.player, false));
			});

			setButton(5, 8, new TItemBuilder(Common.getItemStack(Settings.GUI_OPEN_REQUEST_ITEMS_COLLECTION_ITEM.getString())).setName(Settings.GUI_OPEN_REQUEST_ITEMS_COLLECTION_NAME.getString()).setLore(Settings.GUI_OPEN_REQUEST_ITEMS_COLLECTION_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
				e.manager.showGUI(this.player, new GUIPaymentCollection(this.player, false));
			});
		}

		Markets.newChain().asyncFirst(() -> {
			if (this.playerRequests == null) {
				this.playerRequests = this.all ? Markets.getInstance().getRequestManager().getNonFulfilledRequests() : Markets.getInstance().getRequestManager().getPlayerRequests(this.player);
			}

			return this.playerRequests.stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.playerRequests.size() / (double) 28L));
			setPrevPage(5, 3, new TItemBuilder(Common.getItemStack(Settings.GUI_BACK_BTN_ITEM.getString())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
			setButton(5, 4, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CLOSE_BTN_ITEM.getString()), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(this.player, new GUIMain(this.player)));
			setNextPage(5, 5, new TItemBuilder(Common.getItemStack(Settings.GUI_NEXT_BTN_ITEM.getString())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
			setOnPage(e -> draw());

			int slot = 10;
			for (Request request : data) {
				ItemStack item = Common.getPlayerHead(Bukkit.getOfflinePlayer(request.getRequester()).getName());

				List<String> lore = new ArrayList<>(this.all ? Settings.GUI_OPEN_REQUEST_ITEMS_REQUEST_LORE_ALL.getStringList() : Settings.GUI_OPEN_REQUEST_ITEMS_REQUEST_LORE.getStringList());

				setButton(slot, ConfigItemUtil.build(item, Settings.GUI_OPEN_REQUEST_ITEMS_REQUEST_NAME.getString(), lore, 1, new HashMap<String, Object>() {{
					put("%request_item%", Common.getItemName(request.getRequestedItems().get(0).getItem()));
					put("%request_item_name%", Common.getItemName(request.getRequestedItems().get(0).getItem()));
					put("%request_amount%", request.getRequestedItems().stream().mapToInt(RequestItem::getAmount).sum());
					put("%request_requesting_player%", Bukkit.getOfflinePlayer(request.getRequester()).getName());
				}}), e -> {
					if (!this.all && e.clickType == ClickType.DROP) {
						Markets.getInstance().getRequestManager().deleteRequest(request);
						e.manager.showGUI(e.player, new GUIOpenRequests(this.player, false));
						return;
					}

					if (e.clickType == ClickType.LEFT) {
						if (request.getRequester().equals(this.player.getUniqueId()) && !Settings.ALLOW_OWNER_FULFILL_REQUEST.getBoolean()) {
							Markets.getInstance().getLocale().getMessage("cannot_fulfill_own").sendPrefixedMessage(this.player);
							return;
						}

						e.manager.showGUI(e.player, new GUIRequestFulfillment(request));
					}
				});

				slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
			}
		}).execute();
	}
}
