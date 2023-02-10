package ca.tweetzy.markets.gui.shared.view;

import ca.tweetzy.flight.comp.SkullUtils;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.Rating;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketSearchGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketViewGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class AllMarketsViewGUI extends MarketsPagedGUI<Market> {

	public AllMarketsViewGUI(Gui parent, @NonNull Player player) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_ALL_MARKETS_TITLE), 6, Markets.getMarketManager().getOpenMarketsExclusive(player));
		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(Market market) {
		return QuickItem
				.of(SkullUtils.getSkull(market.getOwnerUUID()))
				.name(market.getDisplayName())
				.lore(market.getDescription())
				.lore(TranslationManager.list(this.player, Translations.GUI_ALL_MARKETS_ITEMS_MARKET_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
						"market_ratings_total", market.getRatings().size(),
						"market_ratings_stars", StringUtils.repeat("★", (int) market.getRatings().stream().mapToInt(Rating::getStars).average().orElse(0))
				))
				.make();
	}

	@Override
	protected void drawAdditional() {
		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_ALL_MARKETS_ITEMS_SEARCH_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_ALL_MARKETS_ITEMS_SEARCH_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_ALL_MARKETS_ITEMS_SEARCH_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make(), click -> new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(click.player, Translations.PROMPT_SEARCH_TITLE), TranslationManager.string(click.player, Translations.PROMPT_SEARCH_SUBTITLE)) {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, AllMarketsViewGUI.this);
			}

			@Override
			public boolean onResult(String string) {
				click.manager.showGUI(click.player, new MarketSearchGUI(AllMarketsViewGUI.this, click.player, string));
				return true;
			}
		});
	}

	@Override
	protected void onClick(Market market, GuiClickEvent click) {
		// check if user is banned
		if (Markets.getMarketManager().isBannedFrom(market, click.player)) {
			Common.tell(click.player, TranslationManager.string(click.player, Translations.BANNED_FROM_MARKET, "market_owner", market.getOwnerName()));
			return;
		}

		// open the market
		// check again to make sure market is open
		if (!market.isOpen()) {
			Common.tell(click.player, TranslationManager.string(click.player, Translations.MARKET_IS_CLOSED, "market_owner", market.getOwnerName()));
			return;
		}

		click.manager.showGUI(click.player, new MarketViewGUI(this, click.player, market));
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(6);
	}
}
