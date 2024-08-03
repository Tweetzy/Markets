package ca.tweetzy.markets.gui.user.category;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.MaterialPickerGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.*;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketCategoryViewGUI;
import ca.tweetzy.markets.gui.shared.view.content.MarketViewGUI;
import ca.tweetzy.markets.gui.user.market.MarketOverviewGUI;
import ca.tweetzy.markets.model.FloodGateCheck;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketCategoryEditGUI extends MarketsPagedGUI<MarketItem> {

	private final Player player;
	private final Market market;
	private final Category category;

	public MarketCategoryEditGUI(@NonNull final Player player, @NonNull final Market market, @NonNull final Category category) {
		super(new MarketOverviewGUI(player, market), player, TranslationManager.string(player, Translations.GUI_MARKET_CATEGORY_EDIT_TITLE, "category_name", category.getName()), 6, category.getItems());

		this.player = player;
		this.market = market;
		this.category = category;
		setAcceptsItems(true);
		setDefaultItem(QuickItem.bg(Settings.GUI_MARKET_CATEGORY_EDIT_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected void drawFixed() {

		drawIconButton();

		setButton(2, 1, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_LORE, "category_display_name", this.category.getDisplayName()))
				.make(), click -> new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(click.player, Translations.PROMPT_CATEGORY_NAME_TITLE), TranslationManager.string(click.player, Translations.PROMPT_CATEGORY_NAME_SUBTITLE)) {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, MarketCategoryEditGUI.this);
			}

			@Override
			public boolean onResult(String string) {
				if (string.length() > 72) {
					Common.tell(click.player, TranslationManager.string(Translations.CATEGORY_NAME_TOO_LONG));
					return false;
				}
				MarketCategoryEditGUI.this.category.setDisplayName(string);
				MarketCategoryEditGUI.this.category.sync(result -> {
					if (result == SynchronizeResult.SUCCESS)
						click.manager.showGUI(click.player, new MarketCategoryEditGUI(click.player, MarketCategoryEditGUI.this.market, MarketCategoryEditGUI.this.category));
				});
				return true;
			}
		});

		// description
		setButton(3, 1, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_LORE, "category_description", this.category.getDescription().get(0)))
				.make(), click -> new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(click.player, Translations.PROMPT_CATEGORY_DESC_TITLE), TranslationManager.string(click.player, Translations.PROMPT_CATEGORY_DESC_SUBTITLE)) {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, MarketCategoryEditGUI.this);
			}

			@Override
			public boolean onResult(String string) {
				MarketCategoryEditGUI.this.category.setDescription(List.of(string));
				MarketCategoryEditGUI.this.category.sync(result -> {
					if (result == SynchronizeResult.SUCCESS)
						click.manager.showGUI(click.player, new MarketCategoryEditGUI(click.player, MarketCategoryEditGUI.this.market, MarketCategoryEditGUI.this.category));
				});
				return true;
			}
		});

		// settings button
//		setButton(getRows() - 1, 2, QuickItem
//				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_ITEM.getItemStack())
//				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_NAME))
//				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_LORE))
//				.make(), click -> {
//
//
//		});

		// new item button
		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, FloodGateCheck.isMobileUser(this.player) ? Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_NAME_MOBILE : Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_NAME))
				.lore(TranslationManager.list(this.player, FloodGateCheck.isMobileUser(this.player) ? Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_LORE_MOBILE : Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_LORE, "category_id", this.category.getName()))
				.make(), click -> {

			if (FloodGateCheck.isMobileUser(click.player))
				return;

			if (Markets.getPlayerManager().isAtMarketItemLimit(click.player)) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.AT_MAX_ITEM_LIMIT));
				return;
			}

			click.manager.showGUI(click.player, new CategoryNewItemGUI(this.player, this.market, this.category));
		});

		// delete button
		setButton(getRows() - 1, 8, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_LORE))
				.make(), click -> {

			// remove items first if any
			if (!this.category.getItems().isEmpty()) {
				this.category.getItems().forEach(item -> item.getViewingPlayers().clear());

				Markets.getDataManager().deleteMarketItems(this.category, (error, itemResult) -> {
					if (error == null && itemResult) {
						this.category.getItems().forEach(item -> {
							giveBackMarketItem(item);
							Markets.getCategoryItemManager().remove(item);
						});

						performCategoryDeletion(click);
					}
				});
			} else
				performCategoryDeletion(click);
		});
	}

	private void drawIconButton() {
		setButton(1, 1, QuickItem
				.of(this.category.getIcon())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_ICON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_ICON_LORE,
						"category_icon", ChatUtil.capitalizeFully(this.category.getIcon().getType())
						, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
						, "right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
				)).make(), click -> {

			if (click.clickType == ClickType.RIGHT) {
				final ItemStack cursor = click.cursor;
				if (cursor != null && cursor.getType() != CompMaterial.AIR.parseMaterial()) {
					final ItemStack newIcon = cursor.clone();
					newIcon.setAmount(1);

					this.category.setIcon(newIcon);
					this.category.sync(result -> {
						if (result == SynchronizeResult.SUCCESS)
							drawIconButton();
					});
				}
			}

			if (click.clickType == ClickType.LEFT) {
				click.manager.showGUI(click.player, new MaterialPickerGUI(this, null, "", (event, selected) -> {

					if (selected != null) {
						this.category.setIcon(selected);
						this.category.sync(result -> {
							if (result == SynchronizeResult.SUCCESS)
								click.manager.showGUI(click.player, new MarketCategoryEditGUI(click.player, MarketCategoryEditGUI.this.market, MarketCategoryEditGUI.this.category));
						});
					}
				}));
			}
		});
	}

	@Override
	protected ItemStack makeDisplayItem(MarketItem marketItem) {
		return QuickItem
				.of(marketItem.getItem())
				.amount(marketItem.getPlusOneStock())
				.lore(Replacer.replaceVariables(
						TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_MARKET_ITEM_LORE)
						, "market_item_price", String.format("%,.2f", marketItem.getPrice())
						, "market_item_currency", marketItem.getCurrencyDisplayName()
						, "market_item_stock", marketItem.isInfinite() ? "∞" : marketItem.getStock()
						, "market_item_wholesale", TranslationManager.string(this.player, marketItem.isPriceForAll() ? Translations.TRUE : Translations.FALSE)
						, "market_item_accepting_offers", TranslationManager.string(this.player, marketItem.isAcceptingOffers() ? Translations.TRUE : Translations.FALSE)
						, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
						, "right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
						, "drop_button", TranslationManager.string(this.player, Translations.DROP_KEY)

				)).make();
	}

	@Override
	protected void onClick(MarketItem marketItem, GuiClickEvent click) {
		final Player player = click.player;

		final MarketItem locate = Markets.getCategoryItemManager().getByUUID(marketItem.getId());
		if (locate == null || locate.getStock() != marketItem.getStock()) {
			reopen(click);
			return;
		}

		switch (click.clickType) {
			case LEFT ->
					new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(click.player, Translations.PROMPT_ITEM_PRICE_TITLE), TranslationManager.string(click.player, Translations.PROMPT_ITEM_PRICE_SUBTITLE)) {
						@Override
						public void onExit(Player player) {
							click.manager.showGUI(click.player, MarketCategoryEditGUI.this);
						}

						@Override
						public boolean onResult(String string) {
							string = ChatColor.stripColor(string);

							if (!NumberUtils.isNumber(string)) {
								Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_A_NUMBER, "value", string));
								return false;
							}

							final double price = Double.parseDouble(string);
							marketItem.setPrice(price);
							marketItem.sync(result -> reopen(click));
							return true;
						}
					};

			case RIGHT -> click.manager.showGUI(click.player, new MarketItemEditGUI(this.player, this.market, this.category, marketItem));

			case DROP -> {
//				final MarketItem relocatedItem = Markets.getCategoryItemManager().getByUUID(marketItem.getId());

				marketItem.unStore(result -> {
					if (result != SynchronizeResult.SUCCESS)
						return;

					// close guis of other users
					marketItem.getViewingPlayers().forEach(viewingUser -> {
						click.manager.showGUI(viewingUser, new MarketCategoryViewGUI(viewingUser, this.market, this.category));
					});

					// give user the item or drop
					giveBackMarketItem(marketItem);
					reopen(click);
				});
			}
		}
	}

	private void giveBackMarketItem(@NonNull final MarketItem marketItem) {
		final ItemStack item = marketItem.getItem().clone();
		item.setAmount(1);

		Bukkit.getServer().getScheduler().runTask(Markets.getInstance(), () -> {
			for (int i = 0; i < marketItem.getStock(); i++)
				PlayerUtil.giveItem(this.player, item);
		});
	}

	private void reopen(@NonNull GuiClickEvent click) {
		click.manager.showGUI(click.player, new MarketCategoryEditGUI(click.player, MarketCategoryEditGUI.this.market, MarketCategoryEditGUI.this.category));

	}

	private void performCategoryDeletion(@NonNull final GuiClickEvent click) {
		this.category.unStore(categoryRemoveResult -> {
			if (categoryRemoveResult == SynchronizeResult.SUCCESS) {
				this.category.getViewingPlayers().forEach(viewingUser -> {
					click.manager.showGUI(viewingUser, new MarketViewGUI(viewingUser, this.market));
				});

				click.manager.showGUI(click.player, new MarketOverviewGUI(click.player, this.market));
			}
		});
	}

	@Override
	protected List<Integer> fillSlots() {
		return List.of(
				12, 13, 14, 15, 16,
				21, 22, 23, 24, 25,
				30, 31, 32, 33, 34
		);
	}
}
