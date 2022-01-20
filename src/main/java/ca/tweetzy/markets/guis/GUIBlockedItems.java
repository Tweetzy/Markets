package ca.tweetzy.markets.guis;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.market.contents.BlockedItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 27 2021
 * Time Created: 4:24 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIBlockedItems extends Gui {

	private List<BlockedItem> blockedItems;

	public GUIBlockedItems() {
		setTitle(TextUtils.formatText(Settings.GUI_BLOCKED_ITEMS_TITLE.getString()));
		setAllowDrops(false);
		setAcceptsItems(false);
		setUseLockedCells(true);
		setAllowShiftClick(false);
		setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_BLOCKED_ITEMS_FILL_ITEM.getMaterial()));
		setRows(6);
		draw();
	}

	private void draw() {
		reset();

		// make border
		for (int i : Numbers.GUI_BORDER_6_ROWS) {
			setItem(i, GuiUtils.getBorderItem(Settings.GUI_BLOCKED_ITEMS_BORDER_ITEM.getMaterial()));
			if (Settings.GUI_BLOCKED_ITEMS_GLOW_BORDER.getBoolean()) highlightItem(i);
		}

		Markets.newChain().asyncFirst(() -> {
			this.blockedItems = Markets.getInstance().getMarketManager().getBlockedItems();
			return this.blockedItems.stream().skip((page - 1) * 28L).limit(28L).collect(Collectors.toList());
		}).asyncLast((data) -> {
			pages = (int) Math.max(1, Math.ceil(this.blockedItems.size() / (double) 28L));
			setPrevPage(5, 3, new TItemBuilder(Common.getItemStack(Settings.GUI_BACK_BTN_ITEM.getString())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
			setButton(5, 4, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CLOSE_BTN_ITEM.getString()), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.gui.exit());
			setNextPage(5, 5, new TItemBuilder(Common.getItemStack(Settings.GUI_NEXT_BTN_ITEM.getString())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
			setOnPage(e -> draw());

			int slot = 10;
			for (BlockedItem blockedItem : data) {
				ItemStack item = blockedItem.getItem().clone();
				setButton(slot, GuiUtils.createButtonItem(item, TextUtils.formatText(Settings.GUI_BLOCKED_ITEMS_ITEM_NAME.getString().replace("%item_name%", Common.getItemName(item))), TextUtils.formatText(Settings.GUI_BLOCKED_ITEMS_ITEM_LORE.getStringList())), ClickType.LEFT, e -> {
					Markets.getInstance().getMarketManager().removeBlockedItem(blockedItem);
					draw();
				});
				slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 3 : slot + 1;
			}

		}).execute();
	}
}
