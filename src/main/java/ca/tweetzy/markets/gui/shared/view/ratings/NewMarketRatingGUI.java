package ca.tweetzy.markets.gui.shared.view.ratings;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.Rating;
import ca.tweetzy.markets.gui.MarketsBaseGUI;
import ca.tweetzy.markets.impl.MarketRating;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class NewMarketRatingGUI extends MarketsBaseGUI {

	private final Gui parent;
	private final Market market;
	private final Rating rating;

	public NewMarketRatingGUI(Gui parent, @NonNull final Player player, @NonNull final Market market, @NonNull final Rating rating) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_NEW_RATING_TITLE, "market_display_name", market.getDisplayName()), 6);
		this.parent = parent;
		this.market = market;
		this.rating = rating;
		setDefaultItem(QuickItem.bg(Settings.GUI_NEW_RATING_BACKGROUND.getItemStack()));
		draw();
	}

	public NewMarketRatingGUI(Gui parent, @NonNull final Player player, @NonNull final Market market) {
		this(parent, player, market, new MarketRating(market, player, 5, TranslationManager.string(player, Translations.DEFAULTS_REVIEW_MSG)));
	}

	@Override
	protected void draw() {
		setButton(1, 4, QuickItem
				.of(Settings.GUI_NEW_RATING_ITEMS_MSG_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_NEW_RATING_ITEMS_MSG_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_NEW_RATING_ITEMS_MSG_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
						"review_message", this.rating.getFeedback()
				))
				.make(), click -> new TitleInput(Markets.getInstance(), this.player, TranslationManager.string(player, Translations.PROMPT_NEW_REVIEW_TITLE), TranslationManager.string(player, Translations.PROMPT_NEW_REVIEW_SUBTITLE)) {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, NewMarketRatingGUI.this);
			}

			@Override
			public boolean onResult(String string) {
				if (string.length() > 128) {
					Common.tell(click.player, TranslationManager.string(click.player, Translations.REVIEW_TOO_LONG));
					return false;
				}

				NewMarketRatingGUI.this.rating.setFeedback(string);
				click.manager.showGUI(click.player, new NewMarketRatingGUI(NewMarketRatingGUI.this.parent, NewMarketRatingGUI.this.player, NewMarketRatingGUI.this.market, NewMarketRatingGUI.this.rating));
				return true;
			}
		});

		drawStars();
		drawCreateButton();

		applyBackExit();
	}

	private void drawCreateButton() {
		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_NEW_RATING_ITEMS_CREATE_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_NEW_RATING_ITEMS_CREATE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_NEW_RATING_ITEMS_CREATE_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> {

			// checks if reviews are enabled
			if (Settings.DISABLE_REVIEWS.getBoolean()) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.REVIEWS_DISABLED));
				return;
			}

			// check if they even purchased anything from that market
			if (!Markets.getRatingManager().userMeetsReviewRequirements(this.market, click.player)) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.MUST_BUY_ITEM_TO_REVIEW));
				return;
			}

			if (!Markets.getRatingManager().canUserRateMarket(this.market, click.player)) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_ALLOWED_TO_REVIEW));
				return;
			}

			Markets.getRatingManager().create(this.market, this.rating, created -> {
				if (created)
					click.manager.showGUI(click.player, this.parent);
			});
		});
	}

	private void drawStars() {
		int start = 2;
		for (int i = 0; i < 5; i++) {
			int star = Integer.valueOf(i + 1);

			setButton(3, start++, QuickItem
					.of(Settings.GUI_NEW_RATING_ITEMS_STAR_ITEM.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_NEW_RATING_ITEMS_STAR_NAME, "star_level", star))
					.lore(TranslationManager.list(this.player, Translations.GUI_NEW_RATING_ITEMS_STAR_LORE,
							"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
							"star_level", star
					))
					.glow(this.rating.getStars() == star)
					.make(), click -> {

				if (this.rating.getStars() != star) {
					this.rating.setStars(star);
					drawStars();
				}
			});
		}
	}
}
