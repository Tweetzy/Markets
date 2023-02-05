package ca.tweetzy.markets.gui.shared.view;

import ca.tweetzy.flight.comp.SkullUtils;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
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
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketCategoryViewGUI extends MarketsPagedGUI<MarketItem> {

	private final Player player;
	private final Market market;
	private final Category category;

	public MarketCategoryViewGUI(@NonNull final Player player, @NonNull final Market market, @NonNull final Category category) {
		super(new MarketViewGUI(player, market), player, TranslationManager.string(player, Translations.GUI_MARKET_CATEGORY_VIEW_TITLE,
				"market_display_name", market.getDisplayName(),
				"category_display_name", category.getDisplayName()
		), 6, category.getItems());

		this.player = player;
		this.market = market;
		this.category = category;

		setDefaultItem(this.market.getCategoryLayout().getBackgroundItem());

		setOnOpen(open -> this.category.getViewingPlayers().add(player));
		setOnClose(close -> this.category.getViewingPlayers().remove(player));

		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(MarketItem marketItem) {
		final QuickItem item = QuickItem.of(marketItem.getItem()).amount(marketItem.getStock()).lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_HEADER));

		item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_INFO,
				"market_item_price", String.format("%,.2f", marketItem.getPrice()),
				"market_item_currency", marketItem.getCurrencyDisplayName(),
				"market_item_stock", marketItem.getStock(),
				"true", TranslationManager.string(this.player, marketItem.isPriceForAll() ? Translations.TRUE : Translations.FALSE)
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
	protected void drawAdditional() {
		// decorations
		this.market.getCategoryLayout().getDecoration().forEach(this::setItem);

		// set custom shit
		setItem(this.market.getCategoryLayout().getOwnerProfileSlot(), QuickItem
				.of(SkullUtils.getSkull(this.market.getOwnerUUID()))
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_PROFILE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_PROFILE_LORE,
						"market_owner", this.market.getOwnerName(),
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make());

		setItem(this.market.getCategoryLayout().getReviewButtonSlot(), QuickItem
				.of(Settings.GUI_MARKET_VIEW_ITEMS_REVIEW.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_REVIEW_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_REVIEW_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
						"right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
				))
				.make());

		setItem(this.market.getCategoryLayout().getSearchButtonSlot(), QuickItem
				.of(Settings.GUI_MARKET_VIEW_ITEMS_SEARCH.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_SEARCH_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_SEARCH_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make());

		setAction(getRows() - 1, 0, click -> {
			this.category.getViewingPlayers().remove(click.player);
			click.manager.showGUI(click.player, new MarketViewGUI(this.player, this.market));
		});
	}

	@Override
	protected void onClick(MarketItem marketItem, GuiClickEvent click) {
		if (Markets.getCategoryItemManager().getByUUID(marketItem.getId()) == null) {
			click.manager.showGUI(click.player, new MarketCategoryViewGUI(this.player, this.market, this.category));
			Common.tell(click.player, TranslationManager.string(click.player, Translations.ITEM_NO_LONGER_AVAILABLE));
			return;
		}

		if (click.clickType == ClickType.LEFT) {
			click.manager.showGUI(click.player, new MarketItemPurchaseGUI(this.player, this.market, marketItem));
			this.category.getViewingPlayers().remove(player);
			marketItem.getViewingPlayers().add(player);
		}

		// todo implement offers
		if (click.clickType == ClickType.RIGHT && marketItem.isAcceptingOffers()) {
			click.manager.showGUI(click.player, new OfferCreateGUI(this, this.player, this.market, marketItem, new MarketOffer(this.player, this.market, marketItem)));
			this.category.getViewingPlayers().remove(player);
			marketItem.getViewingPlayers().add(player);
		}
	}

	@Override
	protected List<Integer> fillSlots() {
		return this.market.getCategoryLayout().getFillSlots();
	}

	@Override
	protected int getPreviousButtonSlot() {
		return this.market.getCategoryLayout().getPrevPageButtonSlot();
	}

	@Override
	protected int getNextButtonSlot() {
		return this.market.getCategoryLayout().getNextPageButtonSlot();
	}

	@Override
	protected int getBackExitButtonSlot() {
		return this.market.getCategoryLayout().getExitButtonSlot();
	}
}
