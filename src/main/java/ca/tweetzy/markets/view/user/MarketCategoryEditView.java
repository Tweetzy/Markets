package ca.tweetzy.markets.view.user;

import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.MaterialPickerGUI;
import ca.tweetzy.flight.gui.template.PagedGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.ChatUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.KeybindComponent;
import org.bukkit.entity.Player;
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

		draw();
	}

	@Override
	protected void drawAdditional() {

		setButton(1, 1, QuickItem
				.of(this.category.getIcon())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_ICON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_ICON_LORE, "category_icon", ChatUtil.capitalizeFully(this.category.getIcon().getType())))
				.make(), click -> click.manager.showGUI(click.player, new MaterialPickerGUI(this, null, "", (event, selected) -> {

			if (selected != null) {
				this.category.setIcon(selected.parseItem());
				this.category.sync(result -> {
					if (result == SynchronizeResult.SUCCESS)
						click.manager.showGUI(click.player, new MarketCategoryEditView(click.player, MarketCategoryEditView.this.market, MarketCategoryEditView.this.category));
				});
			}
		})));

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

		// unStore button
		setButton(getRows() - 1, 8, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_LORE))
				.make(), click -> this.category.unStore(result -> {

			if (result == SynchronizeResult.SUCCESS)
				click.manager.showGUI(click.player, new MarketOverviewView(click.player, this.market));
		}));
	}

	@Override
	protected ItemStack makeDisplayItem(MarketItem marketItem) {
		return QuickItem
				.of(marketItem.getItem())
				.lore(Replacer.replaceVariables(
						List.of("&7----------------------------",
								"&7Price&f: &a$%market_item_price%",
								"&7Currency&f: &eVault ($)",
								"",
								"&a&l%left_click% &7to edit price",
								"&b&l%right_click% &7to toggle price per stack",
								"&c&l%drop_button% &7to remove item",
								"&7----------------------------")
						, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
						, "right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
						, "drop_button", TranslationManager.string(this.player, Translations.DROP_KEY) //todo add a variable customization to replacer // TranslationManager.string(this.player, Translations.DROP_KEY)

				)).make();
	}

	@Override
	protected void onClick(MarketItem marketItem, GuiClickEvent click) {
		final Player player = click.player;

		KeybindComponent keybindComponent = new KeybindComponent("key.drop");
		player.spigot().sendMessage(keybindComponent);
		player.sendMessage(keybindComponent.getKeybind());
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
