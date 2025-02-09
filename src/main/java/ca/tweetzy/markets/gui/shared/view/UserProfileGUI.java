package ca.tweetzy.markets.gui.shared.view;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.TimeUtil;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.MarketUser;
import ca.tweetzy.markets.api.market.core.Rating;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public final class UserProfileGUI extends MarketsPagedGUI<Rating> {

	private final OfflinePlayer profileUser;
	private boolean serverProfile = false;

	public UserProfileGUI(Gui parent, @NonNull Player player, @NonNull final OfflinePlayer profileUser) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_USER_PROFILE_TITLE,
				"player_name", profileUser.getUniqueId().equals(UUID.fromString(Settings.SERVER_MARKET_UUID.getString())) ? TranslationManager.string(Translations.SERVER_MARKET_NAME) : profileUser.getName()
		), 6, Markets.getRatingManager().getRatingsByOrFor(profileUser));
		this.profileUser = profileUser;
		this.serverProfile = profileUser.getUniqueId().equals(UUID.fromString(Settings.SERVER_MARKET_UUID.getString()));
		setDefaultItem(QuickItem.bg(Settings.GUI_USER_PROFILE_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected void drawFixed() {
		final MarketUser user = Markets.getPlayerManager().get(this.profileUser.getUniqueId());

		ItemStack texture = user.isServerMarket() ? QuickItem.of(Settings.SERVER_MARKET_TEXTURE.getString()).make() : QuickItem.of(this.profileUser).make();

		setItem(1, 4, QuickItem
				.of(texture)
				.name(TranslationManager.string(this.player, Translations.GUI_USER_PROFILE_ITEMS_USER_NAME, "player_name", serverProfile ? TranslationManager.string(Translations.SERVER_MARKET_NAME) : this.profileUser.getName()))
				.lore(TranslationManager.list(this.player, Translations.GUI_USER_PROFILE_ITEMS_USER_LORE,
						"user_last_seen", TimeUtil.convertToReadableDate(serverProfile ? System.currentTimeMillis() : user.getLastSeenAt(), Settings.DATETIME_FORMAT.getString()),
						"true", TranslationManager.string(this.player, (this.profileUser.isOnline() || this.serverProfile) ? Translations.TRUE : Translations.FALSE)
				))
				.make()
		);

		applyBackExit();
	}

	@Override
	protected ItemStack makeDisplayItem(Rating rating) {
		return QuickItem
				.of(Bukkit.getOfflinePlayer(rating.getRaterUUID()))
				.name(TranslationManager.string(player, Translations.GUI_USER_PROFILE_ITEMS_RATING_NAME, "rater_name", rating.getRaterName()))
				.lore(TranslationManager.list(player, Translations.GUI_USER_PROFILE_ITEMS_RATING_LORE,
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
		return List.of(28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43);
	}
}
