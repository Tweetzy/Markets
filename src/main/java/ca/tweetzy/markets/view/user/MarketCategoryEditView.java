package ca.tweetzy.markets.view.user;

import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.MaterialPickerGUI;
import ca.tweetzy.flight.gui.template.PagedGUI;
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
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketCategoryEditView extends PagedGUI<MarketItem> {

	private final Player player;
	private final Market market;
	private final Category category;

	public MarketCategoryEditView(@NonNull final Player player, @NonNull final Market market, @NonNull final Category category) {
		super(new MarketOverviewView(player, market), Replacer.replaceVariables(Settings.GUI_MARKET_CATEGORY_EDIT_TITLE.getString(), "category_name", category.getName()), 6, category.getItems());
		this.player = player;
		this.market = market;
		this.category = category;

		draw();
	}

	@Override
	protected void drawAdditional() {

		setButton(1, 1, QuickItem
				.of(this.category.getIcon())
				.name(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_ICON_NAME.getString())
				.lore(Replacer.replaceVariables(
						Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_ICON_LORE.getStringList(),
						"category_icon", ChatUtil.capitalizeFully(this.category.getIcon().getType())
				)).make(), click -> click.manager.showGUI(click.player, new MaterialPickerGUI(this, null, "", (event, selected) -> {

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
				.name(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_NAME.getString())
				.lore(Replacer.replaceVariables(
						Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DPN_LORE.getStringList(),
						"category_display_name", this.category.getDisplayName()
				)).make(), click -> new TitleInput(Markets.getInstance(), click.player, "<GRADIENT:65B1B4>&lCategory Name</GRADIENT:2B6F8A>", "&fEnter new name into chat") {

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
				.name(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_NAME.getString())
				.lore(Replacer.replaceVariables(
						Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DESC_LORE.getStringList(),
						"category_description", this.category.getDescription().get(0)
				)).make(), click -> new TitleInput(Markets.getInstance(), click.player, "<GRADIENT:65B1B4>&lCategory Description</GRADIENT:2B6F8A>", "&fEnter new description into chat") {

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
				.name(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_NAME.getString())
				.lore(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_SETTINGS_LORE.getStringList())
				.make(), click -> {


		});

		// new item button
		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_ITEM.getItemStack())
				.name(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_NAME.getString())
				.lore(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_NEW_ITEM_LORE.getStringList())
				.make(), click -> click.manager.showGUI(click.player, new CategoryNewItemView(this.player, this.market, this.category)));

		// unStore button
		setButton(getRows() - 1, 8, QuickItem
				.of(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_ITEM.getItemStack())
				.name(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_NAME.getString())
				.lore(Settings.GUI_MARKET_CATEGORY_EDIT_ITEMS_DELETE_LORE.getStringList())
				.make(), click -> this.category.unStore(result -> {

			if (result == SynchronizeResult.SUCCESS)
				click.manager.showGUI(click.player, new MarketOverviewView(click.player, this.market));
		}));
	}

	@Override
	protected ItemStack makeDisplayItem(MarketItem marketItem) {
		return null;
	}

	@Override
	protected void onClick(MarketItem marketItem, GuiClickEvent click) {

	}
}
