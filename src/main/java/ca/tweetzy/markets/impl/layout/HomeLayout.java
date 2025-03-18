package ca.tweetzy.markets.impl.layout;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.markets.impl.MarketLayout;
import ca.tweetzy.markets.settings.Settings;

import java.util.HashMap;

public final class HomeLayout extends MarketLayout {

	public HomeLayout() {
		super(45,
				48,
				50,
				49,
				52,
				53,
				InventoryBorder.getInsideBorders(6),
				new HashMap<>(),
				Settings.DEFAULT_LAYOUT_BACKGROUND_ITEM.getItemStack()
		);
	}
}
