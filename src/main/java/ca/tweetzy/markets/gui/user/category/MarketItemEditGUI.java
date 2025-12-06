package ca.tweetzy.markets.gui.user.category;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.MathUtil;
import ca.tweetzy.flight.utils.PlayerUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.gui.MarketsBaseGUI;
import ca.tweetzy.markets.gui.shared.selector.CurrencyPickerGUI;
import ca.tweetzy.markets.model.sync.StockReservationManager;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public final class MarketItemEditGUI extends MarketsBaseGUI {

	private final Market market;
	private final Category category;
	private final MarketItem marketItem;

	public MarketItemEditGUI(@NonNull final Player player, @NonNull final Market market, @NonNull final Category category, @NonNull final MarketItem marketItem) {
		super(new MarketCategoryEditGUI(player, market, category), player, TranslationManager.string(player, Translations.GUI_EDIT_ITEM_TITLE), 6);
		this.market = market;
		this.category = category;
		this.marketItem = marketItem;

		setAcceptsItems(true);
		setDefaultItem(QuickItem.bg(Settings.GUI_EDIT_ITEM_BACKGROUND.getItemStack()));
		setOnOpen(open -> this.marketItem.setBeingEdited(true));
		setOnClose(close -> this.marketItem.setBeingEdited(false));
		draw();
	}

	@Override
	protected void draw() {
		setItem(1, 4, this.marketItem.getItem());

		drawWholesaleButton();
		drawOffersButton();
		drawStockButton();
		drawCurrencyButton();

		applyBackExit();
	}

	private void drawStockButton() {
		setButton(3, 7, QuickItem.of(Settings.GUI_EDIT_ITEM_ITEMS_STOCK_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_EDIT_ITEM_ITEMS_STOCK_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_EDIT_ITEM_ITEMS_STOCK_LORE,
						"market_item_stock", this.marketItem.getStock(),
						"right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK),
						"shift_left_click", TranslationManager.string(this.player, Translations.MOUSE_SHIFT_LEFT_CLICK)
				))
				.make(), click -> {

			if (click.clickType == ClickType.LEFT) {
				final ItemStack cursor = click.cursor;
				if (cursor != null && cursor.getType() != CompMaterial.AIR.get()) {
					if (!this.marketItem.getItem().isSimilar(cursor)) return;

					this.marketItem.addStock(cursor, result -> {
						if (result == SynchronizeResult.FAILURE) return;

						click.player.setItemOnCursor(CompMaterial.AIR.parseItem());
						drawStockButton();
					});
				}
			}

			if (click.clickType == ClickType.SHIFT_LEFT) {
				// Check for active stock reservations (cross-server purchase protection)
				// Note: We don't check isBeingEdited() here because the GUI sets it when open,
				// and addStock() will handle synchronization to prevent purchases during stock addition
				final StockReservationManager reservationManager = Markets.getStockReservationManager();
				if (reservationManager != null && reservationManager.isReserved(this.marketItem.getId())) {
					Common.tell(click.player, TranslationManager.string(click.player, Translations.PLAYERS_LOOKING_AT_ITEM));
					return;
				}

				int itemCount = PlayerUtil.getItemCountInPlayerInventory(click.player, this.marketItem.getItem());
				if (itemCount == 0) return;

				// Create a temporary item stack to use with addStock() for proper synchronization
				final ItemStack tempItem = this.marketItem.getItem().clone();
				tempItem.setAmount(itemCount);

				// Use addStock() which is now properly synchronized
				this.marketItem.addStock(tempItem, result -> {
					if (result == SynchronizeResult.FAILURE) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.PLAYERS_LOOKING_AT_ITEM));
						return;
					}

					// Only remove items from inventory if stock addition succeeded
					PlayerUtil.removeSpecificItemQuantityFromPlayer(click.player, this.marketItem.getItem(), itemCount);
					drawStockButton();
				});
			}

			if (click.clickType == ClickType.DROP) {
				// Handle Q key item drop
				final ItemStack cursor = click.cursor;
				if (cursor != null && cursor.getType() != CompMaterial.AIR.get()) {
					if (!this.marketItem.getItem().isSimilar(cursor)) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.PLAYERS_LOOKING_AT_ITEM));
						return;
					}

					// Check for active stock reservations (cross-server purchase protection)
					// Note: We don't check isBeingEdited() here because the GUI sets it when open,
					// and addStock() will handle synchronization to prevent purchases during stock addition
					final StockReservationManager reservationManager = Markets.getStockReservationManager();
					if (reservationManager != null && reservationManager.isReserved(this.marketItem.getId())) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.PLAYERS_LOOKING_AT_ITEM));
						return;
					}

					this.marketItem.addStock(cursor, result -> {
						if (result == SynchronizeResult.FAILURE) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.PLAYERS_LOOKING_AT_ITEM));
							return;
						}

						click.player.setItemOnCursor(CompMaterial.AIR.parseItem());
						drawStockButton();
					});
				}
			}

			if (click.clickType == ClickType.RIGHT) {

				if (!this.marketItem.getViewingPlayers().isEmpty()) {
					Common.tell(click.player, TranslationManager.string(click.player, Translations.PLAYERS_LOOKING_AT_ITEM));
					return;
				}

				// Check if item is being purchased - prevent stock withdrawal during purchase
				if (this.marketItem.isBeingEdited()) {
					Common.tell(click.player, TranslationManager.string(click.player, Translations.PLAYERS_LOOKING_AT_ITEM));
					return;
				}

				// Check for active stock reservations (cross-server purchase protection)
				final StockReservationManager reservationManager = Markets.getStockReservationManager();
				if (reservationManager != null && reservationManager.isReserved(this.marketItem.getId())) {
					Common.tell(click.player, TranslationManager.string(click.player, Translations.PLAYERS_LOOKING_AT_ITEM));
					return;
				}

				click.gui.exit();
				new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(click.player, Translations.PROMPT_STOCK_WITHDRAW_TITLE), TranslationManager.string(click.player, Translations.PROMPT_STOCK_WITHDRAW_SUBTITLE)) {
					@Override
					public void onExit(Player player) {
						click.manager.showGUI(click.player, MarketItemEditGUI.this);
					}

					@Override
					public boolean onResult(String string) {
						string = ChatColor.stripColor(string);

						if (!MathUtil.isInt(string)) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_A_NUMBER, "value", string));
							return false;
						}

						int qty = Integer.parseInt(string);
						
						if (qty <= 0) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_A_NUMBER, "value", string));
							return false;
						}
						
						// Re-check stock and beingEdited after user input (race condition protection)
						if (marketItem.isBeingEdited()) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.PLAYERS_LOOKING_AT_ITEM));
							return false;
						}
						
						if (reservationManager != null && reservationManager.isReserved(marketItem.getId())) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.PLAYERS_LOOKING_AT_ITEM));
							return false;
						}
						
						// Reload item to get latest stock value
						MarketItem reloadedItem = Markets.getCategoryItemManager().getByUUID(marketItem.getId());
						int currentStock = (reloadedItem != null) ? reloadedItem.getStock() : marketItem.getStock();
						
						if (currentStock < qty) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_ENOUGH_STOCK));
							return false;
						}

						// Validate stock won't go negative (safety check)
						int calculatedNewStock = currentStock - qty;
						final int finalNewStock;
						final int finalQty;
						
						if (calculatedNewStock < 0) {
							Markets.getInstance().getLogger().warning("Stock withdrawal would result in negative stock for item " + marketItem.getId() + ". Current: " + currentStock + ", Withdrawing: " + qty);
							finalNewStock = 0;
							finalQty = currentStock; // Adjust quantity to available stock
						} else {
							finalNewStock = calculatedNewStock;
							finalQty = qty;
						}

						// Update stock atomically
						marketItem.setStock(finalNewStock);

						final ItemStack item = marketItem.getItem().clone();
						item.setAmount(1);

						Bukkit.getServer().getScheduler().runTask(Markets.getInstance(), () -> {
							for (int i = 0; i < finalQty; i++)
								PlayerUtil.giveItem(click.player, item);
						});

						final int syncNewStock = finalNewStock; // Final variable for use in lambda
						marketItem.sync(result -> {
							if (result == SynchronizeResult.FAILURE) {
								Markets.getInstance().getLogger().severe("Failed to sync stock withdrawal for item " + marketItem.getId() + ". Stock may be inconsistent! Expected stock: " + syncNewStock);
								// Attempt to rollback by giving items back to market (if possible)
								// For now, just log the error
							}
							click.manager.showGUI(click.player, new MarketItemEditGUI(click.player, MarketItemEditGUI.this.market, MarketItemEditGUI.this.category, MarketItemEditGUI.this.marketItem));
						});

						return true;
					}
				};
			}
		});
	}

	private void drawOffersButton() {
		if (!Settings.DISABLE_OFFERS.getBoolean()) {
			setButton(3, 1, QuickItem
					.of(this.marketItem.isAcceptingOffers() ? Settings.GUI_EDIT_ITEM_ITEMS_ACCEPTING_OFFERS_ITEM.getItemStack() : Settings.GUI_EDIT_ITEM_ITEMS_REJECTING_OFFERS_ITEM.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_EDIT_ITEM_ITEMS_OFFERS_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_EDIT_ITEM_ITEMS_OFFERS_LORE,
							"enabled", TranslationManager.string(this.player, this.marketItem.isAcceptingOffers() ? Translations.ENABLED : Translations.DISABLED),
							"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
					))
					.make(), click -> {

				this.marketItem.setIsAcceptingOffers(!this.marketItem.isAcceptingOffers());
				this.marketItem.sync(result -> {
					if (result == SynchronizeResult.SUCCESS)
						drawOffersButton();
				});
			});
		}
	}

	private void drawWholesaleButton() {
		if (!Settings.DISABLE_WHOLESALE.getBoolean()) {
			setButton(3, 3, QuickItem
					.of(this.marketItem.isPriceForAll() ? Settings.GUI_EDIT_ITEM_ITEMS_IS_WHOLESALE_ITEM.getItemStack() : Settings.GUI_EDIT_ITEM_ITEMS_NOT_WHOLESALE_ITEM.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_EDIT_ITEM_ITEMS_WHOLESALE_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_EDIT_ITEM_ITEMS_WHOLESALE_LORE,
							"enabled", TranslationManager.string(this.player, this.marketItem.isPriceForAll() ? Translations.ENABLED : Translations.DISABLED),
							"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
					))
					.make(), click -> {

				this.marketItem.setPriceIsForAll(!this.marketItem.isPriceForAll());
				this.marketItem.sync(result -> {
					if (result == SynchronizeResult.SUCCESS)
						drawWholesaleButton();
				});
			});
		}
	}

	private void drawCurrencyButton() {
		if (Settings.CURRENCY_ALLOW_PICK.getBoolean() || Settings.CURRENCY_USE_ITEM_ONLY.getBoolean())
			setButton(3, 5, QuickItem
					.of(Settings.GUI_EDIT_ITEM_ITEMS_CURRENCY_ITEM.getItemStack())
					.name(Translations.string(this.player, Translations.GUI_EDIT_ITEM_ITEMS_CURRENCY_NAME))
					.lore(Translations.list(this.player, Translations.GUI_EDIT_ITEM_ITEMS_CURRENCY_LORE,
							"left_click", Translations.string(this.player, Translations.MOUSE_LEFT_CLICK),
							"market_item_currency", this.marketItem.getCurrencyDisplayName()))
					.make(), click -> click.manager.showGUI(click.player, new CurrencyPickerGUI(this, click.player, (currency, item) -> {

				this.marketItem.setCurrency(currency.getStoreableName());

				if (item != null)
					this.marketItem.setCurrencyItem(item);

				this.marketItem.sync(result -> click.manager.showGUI(click.player, new MarketItemEditGUI(MarketItemEditGUI.this.player, MarketItemEditGUI.this.market, MarketItemEditGUI.this.category, MarketItemEditGUI.this.marketItem)));
			})));
	}
}
