package ca.tweetzy.markets.gui.user.market;

import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.PlayerUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.gui.shared.MarketsMainGUI;
import ca.tweetzy.markets.gui.shared.view.ratings.MarketRatingsViewGUI;
import ca.tweetzy.markets.gui.user.category.MarketCategoryEditGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketOverviewGUI extends MarketsPagedGUI<Category> {

	private final Player player;
	private final Market market;

	public MarketOverviewGUI(@NonNull final Player player, @NonNull final Market market) {
		super(new MarketsMainGUI(player), player, TranslationManager.string(player, Translations.GUI_MARKET_OVERVIEW_TITLE), 6, market.getCategories());
		this.player = player;
		this.market = market;
		setDefaultItem(QuickItem.bg(Settings.GUI_MARKET_OVERVIEW_BACKGROUND.getItemStack()));

		draw();
	}

	@Override
	protected void drawFixed() {

		// name
		setButton(1, 1, QuickItem
				.of(Settings.GUI_MARKET_OVERVIEW_ITEMS_DPN_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_OVERVIEW_ITEMS_DPN_NAME))
				.lore(TranslationManager.list(Translations.GUI_MARKET_OVERVIEW_ITEMS_DPN_LORE, "market_display_name", this.market.getDisplayName()))
				.make(), click -> new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(this.player, Translations.PROMPT_MARKET_NAME_TITLE), TranslationManager.string(this.player, Translations.PROMPT_MARKET_NAME_SUBTITLE)) {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, MarketOverviewGUI.this);
			}

			@Override
			public boolean onResult(String string) {
				if (string.length() > 72) {
					Common.tell(click.player, TranslationManager.string(Translations.MARKET_NAME_TOO_LONG));
					return false;
				}

				MarketOverviewGUI.this.market.setDisplayName(string);
				MarketOverviewGUI.this.market.sync(result -> {
					if (result == SynchronizeResult.SUCCESS) click.manager.showGUI(click.player, new MarketOverviewGUI(click.player, MarketOverviewGUI.this.market));
				});
				return true;
			}
		});

		// description
		setButton(2, 1, QuickItem
				.of(Settings.GUI_MARKET_OVERVIEW_ITEMS_DESC_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_OVERVIEW_ITEMS_DESC_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_OVERVIEW_ITEMS_DESC_LORE, "market_description", this.market.getDescription().get(0)))
				.make(), click -> new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(this.player, Translations.PROMPT_MARKET_DESC_TITLE), TranslationManager.string(this.player, Translations.PROMPT_MARKET_DESC_SUBTITLE)) {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, MarketOverviewGUI.this);
			}

			@Override
			public boolean onResult(String string) {
				MarketOverviewGUI.this.market.setDescription(List.of(string));
				MarketOverviewGUI.this.market.sync(result -> {
					if (result == SynchronizeResult.SUCCESS) click.manager.showGUI(click.player, new MarketOverviewGUI(click.player, MarketOverviewGUI.this.market));
				});
				return true;
			}
		});

		// settings button
		setButton(3, 1, QuickItem
				.of(Settings.GUI_MARKET_OVERVIEW_ITEMS_SETTINGS_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_OVERVIEW_ITEMS_SETTINGS_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_OVERVIEW_ITEMS_SETTINGS_LORE))
				.make(), click -> click.manager.showGUI(click.player, new MarketSettingsGUI(this.player, this.market)));

		// create category
		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_MARKET_OVERVIEW_ITEMS_NEW_CAT_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_OVERVIEW_ITEMS_NEW_CAT_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_OVERVIEW_ITEMS_NEW_CAT_LORE))
				.make(), click -> {

			if (Markets.getPlayerManager().isAtMarketCategoryLimit(click.player)) {
				Common.tell(click.player, TranslationManager.string(Translations.AT_MAX_CATEGORY_LIMIT));
				return;
			}

			new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(click.player, Translations.PROMPT_NEW_CATEGORY_TITLE), TranslationManager.string(click.player, Translations.PROMPT_NEW_CATEGORY_SUBTITLE)) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(click.player, MarketOverviewGUI.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string).replace(" ", "");
					if (string.length() > 32) {
						Common.tell(click.player, TranslationManager.string(Translations.CATEGORY_NAME_TOO_LONG));
						return false;
					}

					// check category name beforehand
					if (Markets.getCategoryManager().doesCategoryExistAlready(MarketOverviewGUI.this.market, string.toLowerCase())) {
						Common.tell(click.player, TranslationManager.string(Translations.CATEGORY_NAME_USED, "category_name", string));
						return false;
					}

					Markets.getCategoryManager().create(MarketOverviewGUI.this.market, string, created -> {
						click.manager.showGUI(click.player, new MarketOverviewGUI(click.player, MarketOverviewGUI.this.market));
					});

					return true;
				}
			};
		});

		setButton(getRows() - 1, 6, QuickItem
				.of(Settings.GUI_MARKET_OVERVIEW_ITEMS_REVIEWS_ITEM.getItemStack())
				.name(TranslationManager.string(Translations.GUI_MARKET_OVERVIEW_ITEMS_REVIEWS_NAME))
				.lore(TranslationManager.list(Translations.GUI_MARKET_OVERVIEW_ITEMS_REVIEWS_LORE,
						"market_ratings_total", market.getRatings().size(),
						"market_ratings_stars", market.getRatings().isEmpty() ? TranslationManager.string(Translations.NO_REVIEWS) : StringUtils.repeat("★", (int) market.getReviewAvg())
				)).make(), click -> click.manager.showGUI(click.player, new MarketRatingsViewGUI(this, click.player, this.market)));

		// unStore button
		setButton(getRows() - 1, 8, QuickItem
				.of(Settings.GUI_MARKET_OVERVIEW_ITEMS_DELETE_ITEM.getItemStack())
				.name(TranslationManager.string(Translations.GUI_MARKET_OVERVIEW_ITEMS_DELETE_NAME))
				.lore(TranslationManager.list(Translations.GUI_MARKET_OVERVIEW_ITEMS_DELETE_LORE))
				.make(), click -> {


			// skip category check if there is none
			if (this.market.getCategories().isEmpty()) {
				// just delete since no categories
				yeetMarket(click);
				return;
			}

			// loop through categories with items
			market.getCategories().forEach(category -> {
				category.getItems().forEach(item -> item.getViewingPlayers().clear());

				Markets.getDataManager().deleteMarketItems(category, (error, itemResult) -> {
					if (error == null && itemResult) {
						category.getItems().forEach(item -> {
							giveBackMarketItem(item);
							Markets.getCategoryItemManager().remove(item);
						});
					}
				});
			});

			// kill categories
			market.getCategories().forEach(category -> category.unStore(categoryRemoveResult -> {
			}));

			// remove market
			yeetMarket(click);
		});
	}

	private void yeetMarket(@NonNull final GuiClickEvent event) {
		market.unStore(result -> {
			if (result == SynchronizeResult.SUCCESS) {
				Common.tell(player, TranslationManager.string(Translations.DELETED_MARKET));
				event.manager.showGUI(event.player, new MarketsMainGUI(event.player));
			}
		});
	}

	private void giveBackMarketItem(@NonNull final MarketItem marketItem) {
		final ItemStack item = marketItem.getItem().clone();
		item.setAmount(1);

		Bukkit.getServer().getScheduler().runTask(Markets.getInstance(), () -> {
			for (int i = 0; i < marketItem.getStock(); i++)
				PlayerUtil.giveItem(this.player, item);
		});
	}

	@Override
	protected ItemStack makeDisplayItem(Category category) {
		return QuickItem.of(category.getIcon()).name(category.getDisplayName()).lore(category.getDescription()).hideTags(true)
				.make();
	}

	@Override
	protected void onClick(Category category, GuiClickEvent click) {
		click.manager.showGUI(click.player, new MarketCategoryEditGUI(click.player, this.market, category));
	}

	@Override
	protected List<Integer> fillSlots() {
		return List.of(12, 13, 14, 15, 16, 21, 22, 23, 24, 25, 30, 31, 32, 33, 34);
	}
}
