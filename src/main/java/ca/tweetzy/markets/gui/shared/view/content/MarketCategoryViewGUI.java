package ca.tweetzy.markets.gui.shared.view.content;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.gui.shared.checkout.MarketItemPurchaseGUI;
import ca.tweetzy.markets.gui.shared.checkout.OfferCreateGUI;
import ca.tweetzy.markets.gui.shared.view.UserProfileGUI;
import ca.tweetzy.markets.gui.shared.view.ratings.MarketRatingsViewGUI;
import ca.tweetzy.markets.gui.shared.view.ratings.NewMarketRatingGUI;
import ca.tweetzy.markets.impl.MarketOffer;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketCategoryViewGUI extends MarketsPagedGUI<MarketItem> {

	private final Player player;
	private final Market market;
	private final Category category;

	public MarketCategoryViewGUI(Gui parent, @NonNull final Player player, @NonNull final Market market, @NonNull final Category category) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_MARKET_CATEGORY_VIEW_TITLE,
				"market_display_name", market.getDisplayName(),
				"category_display_name", category.getDisplayName()
		), 6, category.getInStockItems());

		this.player = player;
		this.market = market;
		this.category = category;

		setDefaultItem(this.market.getCategoryLayout().getBackgroundItem());

		setOnOpen(open -> this.category.getViewingPlayers().add(player));
		setOnClose(close -> this.category.getViewingPlayers().remove(player));

		draw();
	}

	public MarketCategoryViewGUI(@NonNull final Player player, @NonNull final Market market, @NonNull final Category category) {
		this(new MarketViewGUI(player, market), player, market, category);
	}

	@Override
	protected ItemStack makeDisplayItem(MarketItem marketItem) {
		final QuickItem item = QuickItem.of(marketItem.getItem()).amount(marketItem.getPlusOneStock()).lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_HEADER));

		item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_INFO,
				"market_item_price", String.format("%,.2f", marketItem.getPrice()),
				"market_item_currency", marketItem.getCurrencyDisplayName(),
				"market_item_stock", marketItem.getStock(),
				"market_item_wholesale", TranslationManager.string(this.player, marketItem.isPriceForAll() ? Translations.TRUE : Translations.FALSE)
		));

		item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_BUY, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)));

		if (!Settings.DISABLE_OFFERS.getBoolean() && marketItem.isAcceptingOffers()) {
			item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_MAKE_OFFER, "right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)));
		}

		if (marketItem.isCurrencyOfItem())
			item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_VIEW_CURRENCY, "shift_right_click", TranslationManager.string(this.player, Translations.MOUSE_SHIFT_RIGHT_CLICK)));

		item.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_ITEM_LORE_FOOTER));
		return item.make();
	}

	@Override
	protected void drawFixed() {
		// decorations
		this.market.getCategoryLayout().getDecoration().forEach(this::setItem);

		// set custom shit
		setButton(this.market.getCategoryLayout().getOwnerProfileSlot(), QuickItem
				.of(Bukkit.getOfflinePlayer(this.market.getOwnerUUID()))
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_PROFILE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_PROFILE_LORE,
						"market_owner", this.market.getOwnerName(),
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> click.manager.showGUI(click.player, new UserProfileGUI(this, click.player, Bukkit.getOfflinePlayer(this.market.getOwnerUUID()))));

		if (!Settings.DISABLE_REVIEWS.getBoolean()) {
			setButton(this.market.getCategoryLayout().getReviewButtonSlot(), QuickItem
					.of(Settings.GUI_MARKET_VIEW_ITEMS_REVIEW.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_REVIEW_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_REVIEW_LORE,
							"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
							"right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
					))
					.make(), click -> {

				if (click.clickType == ClickType.LEFT)
					click.manager.showGUI(click.player, new NewMarketRatingGUI(this, click.player, this.market));

				if (click.clickType == ClickType.RIGHT)
					click.manager.showGUI(click.player, new MarketRatingsViewGUI(this, click.player, this.market));
			});
		}

		if (Settings.ENABLE_SEARCH_IN_MARKETS.getBoolean())
			setButton(this.market.getCategoryLayout().getSearchButtonSlot(), QuickItem
					.of(Settings.GUI_MARKET_VIEW_ITEMS_SEARCH.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_SEARCH_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_VIEW_ITEMS_SEARCH_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
					.make(), click -> new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(click.player, Translations.PROMPT_SEARCH_TITLE), TranslationManager.string(click.player, Translations.PROMPT_SEARCH_SUBTITLE)) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(click.player, MarketCategoryViewGUI.this);
				}

				@Override
				public boolean onResult(String string) {
					click.manager.showGUI(click.player, new MarketSearchGUI(MarketCategoryViewGUI.this, click.player, MarketCategoryViewGUI.this.category, string));
					return true;
				}
			});

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

		if (click.clickType == ClickType.RIGHT && !Settings.DISABLE_OFFERS.getBoolean() && marketItem.isAcceptingOffers()) {
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
