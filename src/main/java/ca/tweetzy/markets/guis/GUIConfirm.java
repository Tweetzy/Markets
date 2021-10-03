package ca.tweetzy.markets.guis;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.guis.category.GUICategorySettings;
import ca.tweetzy.markets.guis.market.GUIMarketEdit;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.event.inventory.ClickType;

import java.util.Objects;

/**
 * The current file has been created by Kiran Hart
 * Date Created: September 28 2021
 * Time Created: 11:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public final class GUIConfirm extends Gui {

	public enum ConfirmAction {
		DELETE_MARKET,
		DELETE_CATEGORY,
		FEATURE_MARKET;
	}

	private final Market market;
	private final MarketCategory marketCategory;

	public GUIConfirm(final Market market, final MarketCategory marketCategory, ConfirmAction confirmAction) {
		this.market = market;
		this.marketCategory = marketCategory;
		setTitle(Settings.GUI_CONFIRM_TITLE.getString());
		setDefaultItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
		setUseLockedCells(true);
		setAcceptsItems(false);
		setAllowDrops(false);
		setRows(1);

		// 0 1 2 3 4 5 6 7 8
		for (int i = 0; i < 4; i++) {
			setButton(i, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_ITEMS_CANCEL_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_ITEMS_CANCEL_NAME.getString()).setLore(Settings.GUI_CONFIRM_ITEMS_CANCEL_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
				switch (confirmAction) {
					case DELETE_MARKET:
					case FEATURE_MARKET:
						e.manager.showGUI(e.player, new GUIMarketEdit(this.market));
						break;
					case DELETE_CATEGORY:
						e.manager.showGUI(e.player, new GUICategorySettings(this.market, this.marketCategory));
						break;
				}
			});
		}

		for (int i = 5; i < 9; i++) {
			setButton(i, new TItemBuilder(Objects.requireNonNull(Settings.GUI_CONFIRM_ITEMS_CONFIRM_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_CONFIRM_ITEMS_CONFIRM_NAME.getString()).setLore(Settings.GUI_CONFIRM_ITEMS_CONFIRM_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> {
				switch (confirmAction) {
					case DELETE_MARKET:
						MarketsAPI.getInstance().deleteMarket(e.player, this.market);
						break;
					case DELETE_CATEGORY:
						MarketsAPI.getInstance().deleteMarketCategory(e.player, this.market, this.marketCategory);
						break;
					case FEATURE_MARKET:
						MarketsAPI.getInstance().featureMarket(e.player, this.market);
						break;
				}
			});
		}
	}
}
