package ca.tweetzy.markets.gui.shared;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.template.BaseGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.ChatUtil;
import ca.tweetzy.flight.utils.ItemUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class MarketItemPurchaseGUI extends BaseGUI {

	private final Player player;
	private final Market market;
	private final MarketItem marketItem;
	private int purchaseQty;

	public MarketItemPurchaseGUI(@NonNull final Player player, @NonNull final Market market, @NonNull final MarketItem marketItem) {
		super(new MarketCategoryViewGUI(player, market, Markets.getCategoryManager().getByUUID(marketItem.getOwningCategory())), TranslationManager.string(player, Translations.GUI_PURCHASE_ITEM_TITLE, "market_display_name", market.getDisplayName()), 6);

		this.player = player;
		this.market = market;
		this.marketItem = marketItem;

		if (this.marketItem.isPriceForAll() || this.marketItem.getStock() == 1)
			this.purchaseQty = this.marketItem.getStock();
		else
			this.purchaseQty = 1;


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

		if (this.marketItem.getStock() != 1 && !this.marketItem.isPriceForAll()) {
			drawDecrementButtons();
			drawIncrementButtons();
		}

		setButton(getRows() - 1, 4, QuickItem
				.of(CompMaterial.LIME_DYE)
				.name("<GRADIENT:65B1B4>&lPurchase</GRADIENT:2B6F8A>")
				.make(), click -> {

			this.marketItem.performPurchase(this.market, click.player, this.purchaseQty, result -> {
				click.manager.showGUI(click.player, new MarketCategoryViewGUI(this.player, this.market, Markets.getCategoryManager().getByUUID(marketItem.getOwningCategory())));
			});
		});

		applyBackExit();
	}

	private void drawIncrementButtons() {
		setButton(1, 6, QuickItem
				.of(CompMaterial.LIME_STAINED_GLASS_PANE)
				.name("<GRADIENT:65B1B4>&l+1</GRADIENT:2B6F8A>")
				.make(), click -> adjustPurchaseQty(AdjustmentType.INCREASE, 1));

		setButton(2, 7, QuickItem
				.of(CompMaterial.LIME_STAINED_GLASS_PANE)
				.name("<GRADIENT:65B1B4>&l+5</GRADIENT:2B6F8A>")
				.make(), click -> adjustPurchaseQty(AdjustmentType.INCREASE, 5));

		setButton(3, 6, QuickItem
				.of(CompMaterial.LIME_STAINED_GLASS_PANE)
				.name("<GRADIENT:65B1B4>&l+10</GRADIENT:2B6F8A>")
				.make(), click -> adjustPurchaseQty(AdjustmentType.INCREASE, 10));
	}

	private void drawDecrementButtons() {
		setButton(1, 2, QuickItem
				.of(CompMaterial.RED_STAINED_GLASS_PANE)
				.name("<GRADIENT:65B1B4>&l-1</GRADIENT:2B6F8A>")
				.make(), click -> adjustPurchaseQty(AdjustmentType.DECREASE, 1));

		setButton(2, 1, QuickItem
				.of(CompMaterial.RED_STAINED_GLASS_PANE)
				.name("<GRADIENT:65B1B4>&l-5</GRADIENT:2B6F8A>")
				.make(), click -> adjustPurchaseQty(AdjustmentType.DECREASE, 5));

		setButton(3, 2, QuickItem
				.of(CompMaterial.RED_STAINED_GLASS_PANE)
				.name("<GRADIENT:65B1B4>&l-10</GRADIENT:2B6F8A>")
				.make(), click -> adjustPurchaseQty(AdjustmentType.DECREASE, 10));
	}

	private void drawPurchasingItem() {
		setItem(1, 4, QuickItem
				.of(this.marketItem.getItem().clone())
				.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PURCHASE_ITEM_LORE, "market_item_stock", this.marketItem.getStock()))
				.amount(this.purchaseQty)
				.make());
	}

	private void drawPriceBreakdown() {
		final QuickItem quickItem = QuickItem.of(CompMaterial.PAPER);
		quickItem.name(TranslationManager.string(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_NAME));

		quickItem.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_INFO,
				"purchase_quantity", this.purchaseQty,
				"market_item_price", String.format("%,.2f", this.marketItem.getPrice()),
				"market_item_currency", ItemUtil.getStackName(this.marketItem.getCurrencyItem())
		));

		quickItem.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_SUBTOTAL,
				"purchase_sub_total", String.format("%,.2f", this.marketItem.getPrice() * this.purchaseQty)
		));

		quickItem.lore(TranslationManager.list(this.player, Translations.GUI_PURCHASE_ITEM_ITEMS_PRICE_BREAKDOWN_LORE_TOTAL,
				"purchase_total", String.format("%,.2f", this.marketItem.getPrice() * this.purchaseQty)
		));

		setItem(4, 4, quickItem.make());
	}

	private void adjustPurchaseQty(@NonNull final AdjustmentType adjustmentType, final int amount) {
		if (adjustmentType == AdjustmentType.INCREASE) {
			int newAmt = this.purchaseQty + amount;
			if (newAmt > this.marketItem.getStock())
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
