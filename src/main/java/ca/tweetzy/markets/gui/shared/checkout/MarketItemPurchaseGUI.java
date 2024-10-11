package ca.tweetzy.markets.gui.shared.checkout;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.gui.MarketsBaseGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketCategoryViewGUI;
import ca.tweetzy.markets.model.Taxer;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class MarketItemPurchaseGUI extends MarketsBaseGUI {

	private final Player player;
	private final Market market;
	private final MarketItem marketItem;
	private int purchaseQty;

	public MarketItemPurchaseGUI(@NonNull final Player player, @NonNull final Market market, @NonNull final MarketItem marketItem) {
		super(new MarketCategoryViewGUI(player, market, Markets.getCategoryManager().getByUUID(marketItem.getOwningCategory()), false), player, TranslationManager.string(player, Translations.GUI_PURCHASE_ITEM_TITLE, "market_display_name", market.getDisplayName()), 6);

		this.player = player;
		this.market = market;
		this.marketItem = marketItem;

		if (this.marketItem.isPriceForAll() || this.marketItem.getStock() == 1)
			this.purchaseQty = this.marketItem.getStock();
		else
			this.purchaseQty = 1;

		setOnClose(open -> this.marketItem.getViewingPlayers().add(player));
		setOnClose(close -> this.marketItem.getViewingPlayers().remove(player));
		setDefaultItem(QuickItem.bg(Settings.GUI_PURCHASE_ITEM_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected void draw() {
		drawPurchasingItem();

		if (this.marketItem.isCurrencyOfItem())
			setItem(2, 4, QuickItem
					.of(this.marketItem.getCurrencyItem())
					.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_CUSTOM_CURRENCY_LORE))
					.make()
			);

		drawPriceBreakdown();

		if (this.marketItem.isInfinite() || this.marketItem.getStock() != 1 && !this.marketItem.isPriceForAll()) {
			drawDecrementButtons();
			drawIncrementButtons();
		}

		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_PURCHASE_ITEM_ITEMS_BUY.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_BUY_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_BUY_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make(), click -> {

			this.marketItem.performPurchase(this.market, click.player, this.purchaseQty, result -> {
				this.marketItem.getViewingPlayers().remove(click.player);
				click.manager.showGUI(click.player, new MarketCategoryViewGUI(this.player, this.market, Markets.getCategoryManager().getByUUID(marketItem.getOwningCategory()), false));
			});
		});

		applyBackExit();
		setAction(getRows() - 1, 0, click -> {
			this.marketItem.getViewingPlayers().remove(click.player);
			click.manager.showGUI(click.player, new MarketCategoryViewGUI(this.player, this.market, Markets.getCategoryManager().getByUUID(marketItem.getOwningCategory()), false));
		});
	}

	private void drawIncrementButtons() {
		setButton(1, 6, QuickItem
				.of(Settings.GUI_PURCHASE_ITEM_ITEMS_INCREMENT.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_INC1_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_INC1_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make(), click -> adjustPurchaseQty(AdjustmentType.INCREASE, click.clickType.isShiftClick() ? 1 * Settings.PURCHASE_ITEM_SHIFT_MULTI_AMT.getInt() : 1));

		setButton(2, 7, QuickItem
				.of(Settings.GUI_PURCHASE_ITEM_ITEMS_INCREMENT.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_INC5_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_INC5_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make(), click -> adjustPurchaseQty(AdjustmentType.INCREASE, click.clickType.isShiftClick() ? 5 * Settings.PURCHASE_ITEM_SHIFT_MULTI_AMT.getInt() : 5));

		setButton(3, 6, QuickItem
				.of(Settings.GUI_PURCHASE_ITEM_ITEMS_INCREMENT.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_INC10_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_INC10_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make(), click -> adjustPurchaseQty(AdjustmentType.INCREASE, click.clickType.isShiftClick() ? 10 * Settings.PURCHASE_ITEM_SHIFT_MULTI_AMT.getInt() : 10));
	}

	private void drawDecrementButtons() {
		setButton(1, 2, QuickItem
				.of(Settings.GUI_PURCHASE_ITEM_ITEMS_DECREMENT.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_DEC1_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_DEC1_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make(), click -> adjustPurchaseQty(AdjustmentType.DECREASE, click.clickType.isShiftClick() ? 1 * Settings.PURCHASE_ITEM_SHIFT_MULTI_AMT.getInt() : 1));

		setButton(2, 1, QuickItem
				.of(Settings.GUI_PURCHASE_ITEM_ITEMS_DECREMENT.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_DEC5_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_DEC5_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make(), click -> adjustPurchaseQty(AdjustmentType.DECREASE, click.clickType.isShiftClick() ? 5 * Settings.PURCHASE_ITEM_SHIFT_MULTI_AMT.getInt() : 5));

		setButton(3, 2, QuickItem
				.of(Settings.GUI_PURCHASE_ITEM_ITEMS_DECREMENT.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_DEC10_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_DEC10_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make(), click -> adjustPurchaseQty(AdjustmentType.DECREASE, click.clickType.isShiftClick() ? 10 * Settings.PURCHASE_ITEM_SHIFT_MULTI_AMT.getInt() : 10));
	}

	private void drawPurchasingItem() {
		setItem(1, 4, QuickItem
				.of(this.marketItem.getItem().clone())
				.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PURCHASE_ITEM_LORE, "market_item_stock", this.marketItem.isInfinite() ? "∞" : this.marketItem.getStock()))
				.amount(this.purchaseQty)
				.make());
	}

	private void drawPriceBreakdown() {
		final QuickItem quickItem = QuickItem.of(Settings.GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN.getItemStack());
		quickItem.name(TranslationManager.string(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_NAME));

		quickItem.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_INFO,
				"purchase_quantity", this.purchaseQty,
				"market_item_price", String.format("%,.2f", this.marketItem.getPrice()),
				"market_item_currency", this.marketItem.getCurrencyDisplayName()
		));

		quickItem.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_SUBTOTAL,
				"purchase_sub_total", String.format("%,.2f", this.marketItem.isPriceForAll() ? this.marketItem.getPrice() : this.marketItem.getPrice() * this.purchaseQty)
		));

		if (Settings.TAX_ENABLED.getBoolean())
			quickItem.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_TAX,
					"sales_tax", String.format("%,.2f", Taxer.calculateTaxAmount(this.marketItem.isPriceForAll() ? this.marketItem.getPrice() : this.marketItem.getPrice() * this.purchaseQty))
			));

		quickItem.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_TOTAL,
				"purchase_total", String.format("%,.2f", Taxer.getTaxedTotal(this.marketItem.isPriceForAll() ? this.marketItem.getPrice() : this.marketItem.getPrice() * this.purchaseQty))
		));

		setItem(4, 4, quickItem.make());
	}

	private void adjustPurchaseQty(@NonNull final AdjustmentType adjustmentType, final int amount) {
		if (adjustmentType == AdjustmentType.INCREASE) {
			int newAmt = this.purchaseQty + amount;

			if (!this.marketItem.isInfinite() && newAmt > this.marketItem.getStock())
				newAmt = this.marketItem.getStock();

			this.purchaseQty = newAmt;
		}

		if (adjustmentType == AdjustmentType.DECREASE) {
			int newAmt = this.purchaseQty - amount;
			if (newAmt <= 0)
				newAmt = 1;

			this.purchaseQty = newAmt;
		}

		drawPurchasingItem();
		drawPriceBreakdown();
	}

	private enum AdjustmentType {
		INCREASE,
		DECREASE
	}
}
