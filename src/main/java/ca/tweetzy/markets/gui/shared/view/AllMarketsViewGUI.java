package ca.tweetzy.markets.gui.shared.view;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.MarketSortType;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketUser;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketSearchGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketViewGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class AllMarketsViewGUI extends MarketsPagedGUI<Market> {

	MarketUser marketUser;

	public AllMarketsViewGUI(Gui parent, @NonNull Player player) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_ALL_MARKETS_TITLE), 6, new ArrayList<>(Markets.getMarketManager().getOpenMarketsExclusive(player)));
		setDefaultItem(QuickItem.bg(Settings.GUI_ALL_MARKETS_BACKGROUND.getItemStack()));
		this.marketUser = Markets.getPlayerManager().get(player.getUniqueId());
		draw();
	}

	@Override
	protected void prePopulate() {
		if (this.marketUser.getMarketSortType() == MarketSortType.NAME) {
			items.sort(Comparator.comparing(Market::getDisplayName).reversed());
		}

		if (this.marketUser.getMarketSortType() == MarketSortType.ITEMS) {
			items.sort(Comparator.comparing(Market::getItemCount).reversed());
		}

		if (this.marketUser.getMarketSortType() == MarketSortType.REVIEWS) {
			items.sort(Comparator.comparing(Market::getReviewAvg).reversed());
		}

		if (this.marketUser.getMarketSortType() == MarketSortType.LAST_UPDATED) {
			items.sort(Comparator.comparing(Market::getLastUpdated).reversed());
		}
	}

	@Override
	protected ItemStack makeDisplayItem(Market market) {
		return QuickItem
				.of(Bukkit.getOfflinePlayer(market.getOwnerUUID()))
				.name(market.getDisplayName())
				.lore(market.getDescription())
				.lore(TranslationManager.list(this.player, Translations.GUI_ALL_MARKETS_ITEMS_MARKET_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
						"market_ratings_total", market.getRatings().size(),
						"market_ratings_stars", StringUtils.repeat("★", (int) market.getReviewAvg())
				))
				.make();
	}

	@Override
	protected void drawFixed() {
		setButton(getRows() - 1, 8, QuickItem
				.of(Settings.GUI_ALL_MARKETS_ITEMS_FILTER_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_ALL_MARKETS_ITEMS_FILTER_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_ALL_MARKETS_ITEMS_FILTER_LORE, "market_sort_type", marketUser.getMarketSortType().getTranslatedName()))
				.make(), click -> {
			this.marketUser.setMarketSortType(this.marketUser.getMarketSortType().next());
			draw();
		});


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
