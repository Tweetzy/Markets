package ca.tweetzy.markets.gui.user.market;

import ca.tweetzy.flight.gui.template.BaseGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketLayoutType;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class MarketSettingsGUI extends BaseGUI {

	private final Player player;
	private final Market market;

	public MarketSettingsGUI(@NonNull final Player player, @NonNull final Market market) {
		super(new MarketOverviewGUI(player, market), TranslationManager.string(player, Translations.GUI_MARKET_SETTINGS_TITLE), 6);
		this.player = player;
		this.market = market;
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
		drawHomeLayoutButton();

		drawCategoryLayoutButton();

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
			drawOpenButton();
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
			drawCloseWhenOutOfStockButton();
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
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> click.manager.showGUI(click.player, new MarketLayoutEditorGUI(click.player, this.market, MarketLayoutType.HOME)));
	}

	private void drawCategoryLayoutButton() {
		setButton(3, 6, QuickItem
				.of(Settings.GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_ITEM.getItemStack())
				.name(TranslationManager.string(player, Translations.GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_NAME))
				.lore(TranslationManager.list(player, Translations.GUI_MARKET_SETTINGS_ITEMS_CATEGORY_LAYOUT_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
				))
				.make(), click -> click.manager.showGUI(click.player, new MarketLayoutEditorGUI(click.player, this.market, MarketLayoutType.CATEGORY)));
	}
}
