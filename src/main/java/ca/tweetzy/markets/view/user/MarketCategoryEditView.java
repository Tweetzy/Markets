package ca.tweetzy.markets.view.user;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.MaterialPickerGUI;
import ca.tweetzy.flight.gui.template.PagedGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.*;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketCategoryEditView extends PagedGUI<MarketItem> {

	private final Player player;
	private final Market market;
	private final Category category;

	public MarketCategoryEditView(@NonNull final Player player, @NonNull final Market market, @NonNull final Category category) {
		super(new MarketOverviewView(player, market), TranslationManager.string(player, Translations.GUI_MARKET_CATEGORY_EDIT_TITLE, "category_name", category.getName()), 6, category.getItems());

		this.player = player;
		this.market = market;
		this.category = category;
		setAcceptsItems(true);

		draw();
	}

	@Override
	protected void drawAdditional() {

		drawIconButton();

		setButton(2, 1, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_LORE, "category_display_name", this.category.getDisplayName()))
				.make(), click -> new TitleInput(Markets.getInstance(), click.player, "<GRADIENT:65B1B4>&lCategory Name</GRADIENT:2B6F8A>", "&fEnter new name into chat") {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, MarketCategoryEditView.this);
			}

			@Override
			public boolean onResult(String string) {
				if (string.length() > 72) return false; // TODO tell them it's too long
				MarketCategoryEditView.this.category.setDisplayName(string);
				MarketCategoryEditView.this.category.sync(result -> {
					if (result == SynchronizeResult.SUCCESS)
						click.manager.showGUI(click.player, new MarketCategoryEditView(click.player, MarketCategoryEditView.this.market, MarketCategoryEditView.this.category));
				});
				return true;
			}
		});

		// description
		setButton(3, 1, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_LORE, "category_description", this.category.getDescription().get(0)))
				.make(), click -> new TitleInput(Markets.getInstance(), click.player, "<GRADIENT:65B1B4>&lCategory Description</GRADIENT:2B6F8A>", "&fEnter new description into chat") {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, MarketCategoryEditView.this);
			}

			@Override
			public boolean onResult(String string) {
				MarketCategoryEditView.this.category.setDescription(List.of(string));
				MarketCategoryEditView.this.category.sync(result -> {
					if (result == SynchronizeResult.SUCCESS)
						click.manager.showGUI(click.player, new MarketCategoryEditView(click.player, MarketCategoryEditView.this.market, MarketCategoryEditView.this.category));
				});
				return true;
			}
		});

		// settings button
		setButton(getRows() - 1, 2, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_LORE))
				.make(), click -> {


		});

		// new item button
		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_LORE))
				.make(), click -> click.manager.showGUI(click.player, new CategoryNewItemView(this.player, this.market, this.category)));

		// delete button
		setButton(getRows() - 1, 8, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_LORE))
				.make(), click -> this.category.unStore(result -> {

			if (result == SynchronizeResult.SUCCESS)
				click.manager.showGUI(click.player, new MarketOverviewView(click.player, this.market));
		}));
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
						this.category.setIcon(selected.parseItem());
						this.category.sync(result -> {
							if (result == SynchronizeResult.SUCCESS)
								click.manager.showGUI(click.player, new MarketCategoryEditView(click.player, MarketCategoryEditView.this.market, MarketCategoryEditView.this.category));
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
				.lore(Replacer.replaceVariables(
						List.of("&7----------------------------",
								"&7Price&f: &a$%market_item_price%",
								"&7Currency&f: &e%market_item_currency%",
								"",
								"&a&l%left_click% &7to edit price",
								"&b&l%right_click% &7to toggle price per stack",
								"&c&l%drop_button% &7to remove item",
								"&7----------------------------")
						, "market_item_price", String.format("%,.2f", marketItem.getPrice())
						, "market_item_currency", marketItem.getCurrency().split("/")[2]
						, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
						, "right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
						, "drop_button", TranslationManager.string(this.player, Translations.DROP_KEY)

				)).make();
	}

	@Override
	protected void onClick(MarketItem marketItem, GuiClickEvent click) {
		final Player player = click.player;

		switch (click.clickType) {
			case LEFT -> new TitleInput(Markets.getInstance(), click.player, "&eItem Price", "&fEnter item price in chat") {
				@Override
				public void onExit(Player player) {
					click.manager.showGUI(click.player, MarketCategoryEditView.this);
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

			case DROP -> marketItem.unStore(result -> {
				if (result != SynchronizeResult.SUCCESS)
					return;

				// give user the item or drop
				final ItemStack item = marketItem.getItem().clone();

				if (marketItem.getStock() <= item.getMaxStackSize())
					PlayerUtil.giveItem(player, item);
				else {
					item.setAmount(1);
					for (int i = 0; i < marketItem.getStock(); i++)
						PlayerUtil.giveItem(player, item);
				}

				reopen(click);
			});
		}
	}

	private void reopen(@NonNull GuiClickEvent click) {
		click.manager.showGUI(click.player, new MarketCategoryEditView(click.player, MarketCategoryEditView.this.market, MarketCategoryEditView.this.category));

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
