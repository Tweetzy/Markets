package ca.tweetzy.markets.gui.shared;

import ca.tweetzy.flight.comp.SkullUtils;
import ca.tweetzy.flight.gui.template.BaseGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.settings.Translations;
import ca.tweetzy.markets.gui.user.market.MarketOverviewGUI;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class MarketsMainGUI extends BaseGUI {

	private final Player player;

	public MarketsMainGUI(@NonNull final Player player) {
		super(null, TranslationManager.string(player, Translations.GUI_MAIN_VIEW_TITLE), 6);
		this.player = player;
		draw();
	}

	@Override
	protected void draw() {
		final Market playerMarket = Markets.getMarketManager().getByOwner(this.player.getUniqueId());

		// your market
		setButton(1, 1, QuickItem
				.of(SkullUtils.getSkull(this.player.getUniqueId()))
				.name(TranslationManager.string(player, Translations.GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_NAME))
				.lore(playerMarket == null ? TranslationManager.list(player, Translations.GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_CREATE) : TranslationManager.list(player, Translations.GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_VIEW))
				.make(), click -> {

			if (playerMarket == null) {
				Markets.getMarketManager().create(this.player, created -> click.manager.showGUI(click.player, new MarketsMainGUI(click.player)));
				return;
			}

			// open market
			click.manager.showGUI(click.player, new MarketOverviewGUI(click.player, playerMarket));
		});

	}
}
