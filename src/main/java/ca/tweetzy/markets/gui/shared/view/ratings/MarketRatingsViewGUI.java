package ca.tweetzy.markets.gui.shared.view.ratings;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.TimeUtil;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.Rating;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketRatingsViewGUI extends MarketsPagedGUI<Rating> {

	public MarketRatingsViewGUI(Gui parent, @NonNull final Player player, @NonNull final Market market) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_RATINGS_TITLE, "market_display_name", market.getDisplayName()), 6, market.getRatings());
		setDefaultItem(QuickItem.bg(Settings.GUI_RATINGS_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(Rating rating) {
		return QuickItem
				.of(Bukkit.getOfflinePlayer(rating.getRaterUUID()))
				.name(TranslationManager.string(player, Translations.GUI_RATINGS_ITEMS_RATING_NAME, "rater_name", rating.getRaterName()))
				.lore(TranslationManager.list(player, Translations.GUI_RATINGS_ITEMS_RATING_LORE,
						"rating_stars", StringUtils.repeat("★", rating.getStars()),
						"rating_date", TimeUtil.convertToReadableDate(rating.getTimeCreated(), Settings.DATETIME_FORMAT.getString()),
						"rating_feedback", rating.getFeedback()
				))
				.make();
	}

	@Override
	protected void onClick(Rating rating, GuiClickEvent click) {
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(6);
	}
}
