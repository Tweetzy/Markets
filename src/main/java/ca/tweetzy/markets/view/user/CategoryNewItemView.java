package ca.tweetzy.markets.view.user;

import ca.tweetzy.flight.gui.template.BaseGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.PlayerUtil;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class CategoryNewItemView extends BaseGUI {

	private final Player player;
	private final Market market;
	private final Category category;

	public CategoryNewItemView(@NonNull final Player player, @NonNull final Market market, @NonNull final Category category) {
		super(new MarketCategoryEditView(player, market, category), TranslationManager.string(Translations.GUI_CATEGORY_ADD_ITEM_TITLE, "category_name", category.getName()), 6);
		this.player = player;
		this.market = market;
		this.category = category;

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

	@Override
	protected void draw() {

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
