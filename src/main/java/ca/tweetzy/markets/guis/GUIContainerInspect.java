package ca.tweetzy.markets.guis;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.guis.category.GUICategoryItems;
import ca.tweetzy.markets.guis.items.GUIAllItems;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 29 2021
 * Time Created: 5:20 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIContainerInspect extends Gui {

	private final int[] fillSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 46, 47, 48, 50, 51, 52, 53};

	private final Market market;
	private final MarketCategory category;
	private final ItemStack container;
	private List<ItemStack> items;


	/**
	 * Used to inspect a shulker box from it's item stack.
	 *
	 * @param container is the shulker box
	 */
	public GUIContainerInspect(Market market, MarketCategory category, ItemStack container) {
		this.market = market;
		this.category = category;
		this.container = container;
		setTitle(TextUtils.formatText(Settings.GUI_INSPECT_TITLE.getString()));
		setDefaultItem(Settings.GUI_INSPECT_BG_ITEM.getMaterial().parseItem());
		setUseLockedCells(false);
		setAcceptsItems(false);
		setAllowDrops(false);
		setRows(6);

		if (this.container.getType().name().contains("SHULKER_BOX")) {
			BlockStateMeta meta = (BlockStateMeta) this.container.getItemMeta();
			ShulkerBox skulkerBox = (ShulkerBox) meta.getBlockState();
			this.items = Arrays.asList(skulkerBox.getInventory().getContents());
		}

		draw();
		setOnClose(close -> {
			if (this.category == null) {
				close.manager.showGUI(close.player, new GUIAllItems(this.market, false));
			} else {
				close.manager.showGUI(close.player, new GUICategoryItems(this.market, this.category));
			}
		});
	}

	private void draw() {
		reset();
		pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) 27L));

		for (int i : fillSlots) setItem(i, getDefaultItem());

		setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
		setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), null), e -> {
			if (this.category == null) {
				e.manager.showGUI(e.player, new GUIAllItems(this.market, false));
			} else {
				e.manager.showGUI(e.player, new GUICategoryItems(this.market, this.category));
			}
		});
		setOnPage(e -> draw());

		int slot = 9;
		List<ItemStack> data = this.items.stream().skip((page - 1) * 27L).limit(27L).collect(Collectors.toList());
		for (ItemStack item : data) {
			setItem(slot++, item);
		}
	}
}
