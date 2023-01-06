package ca.tweetzy.markets.view.user;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.template.BaseGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.PlayerUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.impl.CategoryItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CategoryNewItemView extends BaseGUI {

	private final Player player;
	private final Market market;
	private final Category category;
	private final MarketItem marketItem;

	public CategoryNewItemView(@NonNull final Player player, @NonNull final Market market, @NonNull final Category category, final MarketItem marketItem) {
		super(new MarketCategoryEditView(player, market, category), TranslationManager.string(Translations.GUI_CATEGORY_ADD_ITEM_TITLE, "category_name", category.getName()), 6);
		this.player = player;
		this.market = market;
		this.category = category;
		if (marketItem == null)
			this.marketItem = new CategoryItem(this.category.getId());
		else
			this.marketItem = marketItem;

		// pre setup
		setAcceptsItems(true);
		setUnlocked(1, 4);

		setOnClose(close -> {
			final ItemStack placedItem = getItem(1, 4);

			if (placedItem != null)
				PlayerUtil.giveItem(close.player, placedItem);
		});

		draw();
	}

	public CategoryNewItemView(@NonNull final Player player, @NonNull final Market market, @NonNull final Category category) {
		this(player, market, category, null);
	}

	@Override
	protected void draw() {

		if (this.marketItem.getItem().getType() != CompMaterial.AIR.parseMaterial())
			setItem(1, 4, this.marketItem.getItem());


		setButton(2, 4, QuickItem
				.of(CompMaterial.SUNFLOWER)
				.name("&bPrice")
				.lore("&7The current price is &f: &a$" + this.marketItem.getPrice())
				.make(), click -> {

			if (getItem(1, 4) != null)
				this.marketItem.setItem(getItem(1, 4));

			click.gui.exit();

			new TitleInput(Markets.getInstance(), click.player, "&eItem Price", "&fEnter item price in chat") {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(click.player, CategoryNewItemView.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!NumberUtils.isNumber(string)) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_A_NUMBER, "value", string));
						return false;
					}

					final double price = Double.parseDouble(string);
					CategoryNewItemView.this.marketItem.setPrice(price);

					click.manager.showGUI(click.player, new CategoryNewItemView(CategoryNewItemView.this.player, CategoryNewItemView.this.market, CategoryNewItemView.this.category, CategoryNewItemView.this.marketItem));
					return true;
				}
			};
		});

		// new item button
		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_CATEGORY_ADD_ITEM_ITEMS_NEW_ITEM_ITEM.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_CATEGORY_ADD_ITEM_ITEMS_NEW_ITEM_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_CATEGORY_ADD_ITEM_ITEMS_NEW_ITEM_LORE))
				.make(), click -> {

			final ItemStack placedItem = getItem(1, 4);
			if (placedItem == null) return;

			this.marketItem.setItem(placedItem.clone());
			this.marketItem.setStock(placedItem.clone().getAmount());
			if (this.marketItem.getPrice() <= 0) return;

			// create the item
			Markets.getCategoryItemManager().create(
					this.category,
					this.marketItem.getItem(),
					this.marketItem.getCurrency(),
					this.marketItem.getCurrencyItem(),
					this.marketItem.getPrice(),
					this.marketItem.isPriceForAll(),
					created -> {
						if (created) {
							setItem(1, 4, CompMaterial.AIR.parseItem());
							click.manager.showGUI(click.player, new MarketCategoryEditView(this.player, this.market, this.category));
						}
					});
		});


		applyBackExit();
		// override logic here
		setAction(getRows() - 1, 0, click -> {
			final ItemStack placedItem = getItem(1, 4);

			if (placedItem != null) {
				Common.tell(click.player, "&ctake yo shit out first");//todo add msg for dis
				return;
			}

			click.manager.showGUI(click.player, new MarketCategoryEditView(this.player, this.market, this.category));
		});
	}
}
