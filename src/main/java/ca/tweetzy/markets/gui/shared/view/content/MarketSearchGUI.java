package ca.tweetzy.markets.gui.shared.view.content;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.gui.shared.checkout.MarketItemPurchaseGUI;
import ca.tweetzy.markets.gui.shared.checkout.OfferCreateGUI;
import ca.tweetzy.markets.impl.MarketOffer;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketSearchGUI extends MarketsPagedGUI<MarketItem> {

	private final String keywords;

	public MarketSearchGUI(Gui parent, @NonNull Player player, @NonNull String keywords) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_SEARCH_TITLE, "search_keywords", keywords), 6, Markets.getMarketManager().getSearchResults(player, keywords));
		this.keywords = keywords;
		draw();
	}

	public MarketSearchGUI(Gui parent, @NonNull Player player, @NonNull final Market market, @NonNull String keywords) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_SEARCH_TITLE, "search_keywords", keywords), 6, Markets.getMarketManager().getSearchResults(player, market, keywords));
		this.keywords = keywords;
		draw();
	}

	public MarketSearchGUI(Gui parent, @NonNull Player player, @NonNull final Category category, @NonNull String keywords) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_SEARCH_TITLE, "search_keywords", keywords), 6, Markets.getMarketManager().getSearchResults(category, keywords));
		this.keywords = keywords;
		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(MarketItem marketItem) {
		final QuickItem item = QuickItem.of(marketItem.getItem()).amount(marketItem.getStock()).lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_HEADER));

		item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_INFO,
				"market_item_price", String.format("%,.2f", marketItem.getPrice()),
				"market_item_currency", marketItem.getCurrencyDisplayName(),
				"market_item_stock", marketItem.getStock(),
				"market_item_wholesale", TranslationManager.string(this.player, marketItem.isPriceForAll() ? Translations.TRUE : Translations.FALSE)
		));

		item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_BUY, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)));

		if (marketItem.isAcceptingOffers())
			item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_MAKE_OFFER, "right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)));

		if (marketItem.isCurrencyOfItem())
			item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_VIEW_CURRENCY, "shift_right_click", TranslationManager.string(this.player, Translations.MOUSE_SHIFT_RIGHT_CLICK)));

		item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_FOOTER));
		return item.make();
	}

	@Override
	protected void onClick(MarketItem marketItem, GuiClickEvent click) {
		if (Markets.getCategoryItemManager().getByUUID(marketItem.getId()) == null) {
			click.manager.showGUI(click.player, new MarketSearchGUI(this.parent, this.player, this.keywords));
			Common.tell(click.player, TranslationManager.string(click.player, Translations.ITEM_NO_LONGER_AVAILABLE));
			return;
		}

		final Category category = Markets.getCategoryManager().getByUUID(marketItem.getOwningCategory());
		final Market market = Markets.getMarketManager().getByUUID(category.getOwningMarket());

		if (click.clickType == ClickType.LEFT) {
			click.manager.showGUI(click.player, new MarketItemPurchaseGUI(this.player, market, marketItem));
			category.getViewingPlayers().remove(player);
			marketItem.getViewingPlayers().add(player);
		}

		if (click.clickType == ClickType.RIGHT && marketItem.isAcceptingOffers()) {
			click.manager.showGUI(click.player, new OfferCreateGUI(this, this.player, market, marketItem, new MarketOffer(this.player, market, marketItem)));
			category.getViewingPlayers().remove(player);
			marketItem.getViewingPlayers().add(player);
		}
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(6);
	}
}
