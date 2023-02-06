package ca.tweetzy.markets.gui.shared.view;

import ca.tweetzy.flight.comp.SkullUtils;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.gui.shared.view.ratings.MarketRatingsViewGUI;
import ca.tweetzy.markets.gui.shared.view.ratings.NewMarketRatingGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketViewGUI extends MarketsPagedGUI<Category> {

	private final Player player;
	private final Market market;

	public MarketViewGUI(Gui parent, @NonNull final Player player, @NonNull final Market market) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_MARKET_VIEW_TITLE, "market_display_name", market.getDisplayName()), 6, market.getCategories());
		this.player = player;
		this.market = market;

		setDefaultItem(market.getHomeLayout().getBackgroundItem());

		draw();
	}

	public MarketViewGUI(@NonNull final Player player, @NonNull final Market market) {
		this(null, player, market);
	}


	@Override
	protected void drawAdditional() {
		// decorations
		this.market.getHomeLayout().getDecoration().forEach(this::setItem);

		// set custom shit
		setItem(this.market.getHomeLayout().getOwnerProfileSlot(), QuickItem
				.of(SkullUtils.getSkull(this.market.getOwnerUUID()))
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_VIEW_ITEMS_PROFILE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_VIEW_ITEMS_PROFILE_LORE,
						"market_owner", this.market.getOwnerName(),
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make());

		setButton(this.market.getHomeLayout().getReviewButtonSlot(), QuickItem
				.of(Settings.GUI_MARKET_VIEW_ITEMS_REVIEW.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_VIEW_ITEMS_REVIEW_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_VIEW_ITEMS_REVIEW_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
						"right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
				))
				.make(), click -> {

			if (click.clickType == ClickType.LEFT)
				click.manager.showGUI(click.player, new NewMarketRatingGUI(this, click.player, this.market));

			if (click.clickType == ClickType.RIGHT)
				click.manager.showGUI(click.player, new MarketRatingsViewGUI(this, click.player, this.market));

		});

		setItem(this.market.getHomeLayout().getSearchButtonSlot(), QuickItem
				.of(Settings.GUI_MARKET_VIEW_ITEMS_SEARCH.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_VIEW_ITEMS_SEARCH_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_VIEW_ITEMS_SEARCH_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make());

	}

	@Override
	protected ItemStack makeDisplayItem(Category category) {
		return QuickItem
				.of(category.getIcon())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_VIEW_ITEMS_CATEGORY_NAME, "category_display_name", category.getDisplayName()))
				.lore(category.getDescription())
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_VIEW_ITEMS_CATEGORY_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.hideTags(true)
				.make();
	}

	@Override
	protected void onClick(Category category, GuiClickEvent click) {
		click.manager.showGUI(click.player, new MarketCategoryViewGUI(click.player, this.market, category));
	}

	@Override
	protected List<Integer> fillSlots() {
		return this.market.getHomeLayout().getFillSlots();
	}

	@Override
	protected int getPreviousButtonSlot() {
		return this.market.getHomeLayout().getPrevPageButtonSlot();
	}

	@Override
	protected int getNextButtonSlot() {
		return this.market.getHomeLayout().getNextPageButtonSlot();
	}

	@Override
	protected int getBackExitButtonSlot() {
		return this.market.getHomeLayout().getExitButtonSlot();
	}
}
