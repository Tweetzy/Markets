package ca.tweetzy.markets.gui.user.market;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.MaterialPickerGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.layout.MarketLayoutType;
import ca.tweetzy.markets.gui.MarketsBaseGUI;
import ca.tweetzy.markets.gui.user.layout.MarketLayoutEditorGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public final class MarketSettingsGUI extends MarketsBaseGUI {

	private final Player player;
	private final Market market;

	public MarketSettingsGUI(@NonNull final Player player, @NonNull final Market market) {
		super(new MarketOverviewGUI(player, market), player, TranslationManager.string(player, Translations.GUI_MARKET_SETTINGS_TITLE), 6);
		this.player = player;
		this.market = market;
		setAcceptsItems(true);
		setDefaultItem(QuickItem.bg(Settings.GUI_MARKET_SETTINGS_BACKGROUND.getItemStack()));

		draw();
	}

	@Override
	protected void draw() {

		// opened?
		drawOpenButton();

		// close when out of stock
		drawCloseWhenOutOfStockButton();

		// banned users
		drawBannedUsersButton();

		// layout
		if (!Settings.DISABLE_LAYOUT_EDITING.getBoolean()) {
			drawHomeLayoutButton();
			drawCategoryLayoutButton();
		}

		applyBackExit();
	}

	private void drawOpenButton() {
		setButton(1, 1, QuickItem
				.of(this.market.isOpen() ? Settings.GUI_MARKET_SETTINGS_ITEMS_OPEN_ITEM.getItemStack() : Settings.GUI_MARKET_SETTINGS_ITEMS_CLOSE_ITEM.getItemStack())
				.name(TranslationManager.string(player, Translations.GUI_MARKET_SETTINGS_ITEMS_TOGGLE_OPEN_NAME))
				.lore(TranslationManager.list(player, Translations.GUI_MARKET_SETTINGS_ITEMS_TOGGLE_OPEN_LORE,
						"open", TranslationManager.string(this.player, this.market.isOpen() ? Translations.OPEN : Translations.CLOSED),
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> {

			this.market.setOpen(!this.market.isOpen());
			this.market.sync(result -> {
				if (result == SynchronizeResult.FAILURE) return;
				drawOpenButton();
			});
		});
	}

	private void drawCloseWhenOutOfStockButton() {
		setButton(1, 4, QuickItem
				.of(this.market.isCloseWhenOutOfStock() ? Settings.GUI_MARKET_SETTINGS_ITEMS_CLOSE_WHEN_OUT_OF_STOCK_ENABLED_ITEM.getItemStack() : Settings.GUI_MARKET_SETTINGS_ITEMS_CLOSE_WHEN_OUT_OF_STOCK_DISABLED_ITEM.getItemStack())
				.name(TranslationManager.string(player, Translations.GUI_MARKET_SETTINGS_ITEMS_TOGGLE_CLOSE_WHEN_OUT_OF_STOCK_NAME))
				.lore(TranslationManager.list(player, Translations.GUI_MARKET_SETTINGS_ITEMS_TOGGLE_CLOSE_WHEN_OUT_OF_STOCK_LORE,
						"enabled", TranslationManager.string(this.player, this.market.isCloseWhenOutOfStock() ? Translations.ENABLED : Translations.DISABLED),
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> {

			this.market.setCloseWhenOutOfStock(!this.market.isCloseWhenOutOfStock());
			this.market.sync(result -> {
				if (result == SynchronizeResult.FAILURE) return;
				drawCloseWhenOutOfStockButton();
			});
		});
	}

	private void drawBannedUsersButton() {
		setButton(1, 7, QuickItem
				.of(Settings.GUI_MARKET_SETTINGS_ITEMS_BANNED_USERS_ITEM.getItemStack())
				.name(TranslationManager.string(player, Translations.GUI_MARKET_SETTINGS_ITEMS_BANNED_USERS_NAME))
				.lore(TranslationManager.list(player, Translations.GUI_MARKET_SETTINGS_ITEMS_BANNED_USERS_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> click.manager.showGUI(click.player, new MarketBannedUsersGUI(this.player, this.market)));
	}

	private void drawHomeLayoutButton() {
		setButton(3, 2, QuickItem
				.of(Settings.GUI_MARKET_SETTINGS_ITEMS_HOME_LAYOUT_ITEM.getItemStack())
				.name(TranslationManager.string(player, Translations.GUI_MARKET_SETTINGS_ITEMS_HOME_LAYOUT_NAME))
				.lore(TranslationManager.list(player, Translations.GUI_MARKET_SETTINGS_ITEMS_HOME_LAYOUT_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
						"right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
				))
				.make(), click -> executeLayoutHandle(click, MarketLayoutType.HOME));
	}

	private void drawCategoryLayoutButton() {
		setButton(3, 6, QuickItem
				.of(Settings.GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_ITEM.getItemStack())
				.name(TranslationManager.string(player, Translations.GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_NAME))
				.lore(TranslationManager.list(player, Translations.GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
						"right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
				))
				.make(), click -> executeLayoutHandle(click, MarketLayoutType.CATEGORY));
	}

	private void executeLayoutHandle(@NonNull final GuiClickEvent click, @NonNull final MarketLayoutType layoutType) {
		final ItemStack cursor = click.cursor;

		if (click.clickType == ClickType.valueOf(Settings.CLICK_LAYOUT_BG_APPLY.getString().toUpperCase()) && cursor != null && cursor.getType() != CompMaterial.AIR.parseMaterial()) {
			final ItemStack newIcon = cursor.clone();
			newIcon.setAmount(1);

			if (layoutType == MarketLayoutType.HOME)
				this.market.getHomeLayout().setBackgroundItem(newIcon);
			else
				this.market.getCategoryLayout().setBackgroundItem(newIcon);

			this.market.sync(result -> {
				if (result == SynchronizeResult.SUCCESS)
					draw();
			});
			return;
		}

		if (click.clickType == ClickType.LEFT) {
			click.manager.showGUI(click.player, new MarketLayoutEditorGUI(click.player, this.market, layoutType));
			draw();
		}

		if (click.clickType == ClickType.RIGHT) {

//			final ItemStack cursor = click.cursor;
//			if (cursor != null && cursor.getType() != CompMaterial.AIR.parseMaterial()) {
//				final ItemStack newIcon = cursor.clone();
//				newIcon.setAmount(1);
//
//				if (layoutType == MarketLayoutType.HOME)
//					this.market.getHomeLayout().setBackgroundItem(newIcon);
//				else
//					this.market.getCategoryLayout().setBackgroundItem(newIcon);
//
//				this.market.sync(result -> {
//					if (result == SynchronizeResult.SUCCESS)
//						draw();
//				});
//
//				return;
//			}

			click.manager.showGUI(click.player, new MaterialPickerGUI(this, null, null, (event, selected) -> {
				if (selected != null)
					if (layoutType == MarketLayoutType.HOME)
						this.market.getHomeLayout().setBackgroundItem(selected);
					else
						this.market.getCategoryLayout().setBackgroundItem(selected);

				this.market.sync(result -> {
					if (result == SynchronizeResult.SUCCESS)
						click.manager.showGUI(click.player, new MarketSettingsGUI(this.player, this.market));
				});
			}));
		}
		return;
	}

}
