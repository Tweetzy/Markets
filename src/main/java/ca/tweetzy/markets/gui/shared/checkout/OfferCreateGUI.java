package ca.tweetzy.markets.gui.shared.checkout;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.ItemUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.api.market.offer.Offer;
import ca.tweetzy.markets.gui.MarketsBaseGUI;
import ca.tweetzy.markets.gui.shared.selector.CurrencyPickerGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketCategoryViewGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class OfferCreateGUI extends MarketsBaseGUI {

	private final Gui parent;
	private final Player player;
	private final Market market;
	private final MarketItem marketItem;
	private final Offer offer;

	public OfferCreateGUI(final Gui parent, @NonNull final Player player, @NonNull final Market market, @NonNull final MarketItem marketItem, @NonNull final Offer offer) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_OFFER_CREATE_TITLE), 6);
		this.parent = parent;
		this.player = player;
		this.market = market;
		this.marketItem = marketItem;
		this.offer = offer;

		setOnClose(open -> this.marketItem.getViewingPlayers().add(player));
		setOnClose(close -> this.marketItem.getViewingPlayers().remove(player));
		setDefaultItem(QuickItem.bg(Settings.GUI_OFFER_CREATE_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected void draw() {

		setItem(1, 1, QuickItem
				.of(this.marketItem.getItem())
				.amount(Math.min(this.marketItem.getStock(), this.marketItem.getItem().getMaxStackSize()))
				.make()
		);

		setButton(1, 4, QuickItem
				.of(Settings.GUI_OFFER_CREATE_ITEMS_AMOUNT.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_OFFER_CREATE_ITEMS_AMOUNT_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_OFFER_CREATE_ITEMS_AMOUNT_LORE,
						"offer_amount", this.offer.getOfferedAmount(),
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(this.player, Translations.PROMPT_OFFER_PRICE_TITLE), TranslationManager.string(this.player, Translations.PROMPT_OFFER_PRICE_SUBTITLE)) {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, OfferCreateGUI.this);
			}

			@Override
			public boolean onResult(String string) {
				string = ChatColor.stripColor(string);

				if (!NumberUtils.isNumber(string)) {
					Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_A_NUMBER, "value", string));
					return false;
				}

				final double offerAmount = Double.parseDouble(string);

				if (offerAmount <= 0) {
					Common.tell(click.player, TranslationManager.string(click.player, Translations.MUST_BE_HIGHER_THAN_ZERO, "value", string));
					return false;
				}

				OfferCreateGUI.this.offer.setOfferedAmount(offerAmount);
				click.manager.showGUI(click.player, new OfferCreateGUI(OfferCreateGUI.this.parent, OfferCreateGUI.this.player, OfferCreateGUI.this.market, OfferCreateGUI.this.marketItem, OfferCreateGUI.this.offer));
				return true;
			}
		});

		setItem(3, 4, QuickItem
				.of(Settings.GUI_OFFER_CREATE_ITEMS_BREAKDOWN.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_OFFER_CREATE_ITEMS_BREAKDOWN_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_OFFER_CREATE_ITEMS_BREAKDOWN_LORE,
						"sender_name", this.player.getName(),
						"seller_name", this.market.getOwnerName(),
						"offer_amount", this.offer.getOfferedAmount(),
						"offer_currency", this.offer.getCurrencyDisplayName(),
						"requested_amount", this.offer.getRequestAmount(),
						"market_item_name", ItemUtil.getItemName(this.marketItem.getItem())))
				.make()
		);

		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_OFFER_CREATE_ITEMS_CREATE_OFFER.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_OFFER_CREATE_ITEMS_CREATE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_OFFER_CREATE_ITEMS_CREATE_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> {

			// check item availability one last time
			if (Markets.getCategoryItemManager().getByUUID(this.offer.getMarketItem()) == null) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.ITEM_NO_LONGER_AVAILABLE));
				return;
			}

			if (Settings.DISABLE_OFFERS.getBoolean()) {
				Common.tell(player, TranslationManager.string(player, Translations.OFFERS_DISABLED));
				return;
			}

			Markets.getOfferManager().create(
					click.player,
					this.market,
					this.marketItem,
					this.offer.getCurrency(),
					this.offer.getCurrencyItem(),
					this.offer.getOfferedAmount(),
					created -> {
						if (!created) return;
						this.marketItem.getViewingPlayers().remove(this.player);
						click.manager.showGUI(click.player, new MarketCategoryViewGUI(click.player, this.market, Markets.getCategoryManager().getByUUID(this.marketItem.getOwningCategory())));
					});
		});

		if (Settings.CURRENCY_ALLOW_PICK.getBoolean() || Settings.CURRENCY_USE_ITEM_ONLY.getBoolean())
			setButton(1, 7, QuickItem
					.of(Settings.GUI_OFFER_CREATE_ITEMS_CURRENCY.getItemStack())
					.name(Translations.string(this.player, Translations.GUI_OFFER_CREATE_ITEMS_CURRENCY_NAME))
					.lore(Translations.list(this.player, Translations.GUI_OFFER_CREATE_ITEMS_CURRENCY_LORE,
							"left_click", Translations.string(this.player, Translations.MOUSE_LEFT_CLICK),
							"offer_currency", this.offer.getCurrencyDisplayName()))
					.make(), click -> {

				click.manager.showGUI(click.player, new CurrencyPickerGUI(this, click.player, (currency, item) -> {
					click.gui.exit();

					this.offer.setCurrency(currency.getStoreableName());

					if (item != null)
						this.offer.setCurrencyItem(item);

					click.manager.showGUI(click.player, new OfferCreateGUI(OfferCreateGUI.this.parent, OfferCreateGUI.this.player, OfferCreateGUI.this.market, OfferCreateGUI.this.marketItem, OfferCreateGUI.this.offer));
				}));
			});

		applyBackExit();
		setAction(getRows() - 1, 0, click -> {
			this.marketItem.getViewingPlayers().remove(click.player);
			click.manager.showGUI(click.player, new MarketCategoryViewGUI(this.player, this.market, Markets.getCategoryManager().getByUUID(marketItem.getOwningCategory())));
		});
	}
}
