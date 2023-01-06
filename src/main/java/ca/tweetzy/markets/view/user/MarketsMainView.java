package ca.tweetzy.markets.view.user;

import ca.tweetzy.flight.comp.SkullUtils;
import ca.tweetzy.flight.gui.template.BaseGUI;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.entity.Player;

@SuppressWarnings("ConstantConditions")
public final class MarketsMainView extends BaseGUI {

	private final Player player;

	public MarketsMainView(@NonNull final Player player) {
		super(null, Settings.GUI_MAIN_VIEW_TITLE.getString(), 6);
		this.player = player;
		draw();
	}

	@Override
	protected void draw() {
		final Market playerMarket = Markets.getMarketManager().getByOwner(this.player.getUniqueId());

		// your market
		setButton(1, 1, QuickItem
				.of(SkullUtils.getSkull(this.player.getUniqueId()))
				.name(Settings.GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_NAME.getString())
				.lore(playerMarket == null ? Settings.GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_CREATE.getStringList() : Settings.GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_VIEW.getStringList())
				.make(), click -> {

			if (playerMarket == null) {
				Markets.getMarketManager().create(this.player, created -> click.manager.showGUI(click.player, new MarketsMainView(click.player)));
				return;
			}

			// open market
			click.manager.showGUI(click.player, new MarketOverviewView(click.player, playerMarket));
		});

	}
}
