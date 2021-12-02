package ca.tweetzy.markets.guis.ratings;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.market.GUIMarketView;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.MarketRating;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 16 2021
 * Time Created: 4:46 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@SuppressWarnings("all")
public class GUIRatingStarSelect extends Gui {

	private final List<String> skullTextures = Arrays.asList(
			Settings.GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_ON_1.getString(),
			Settings.GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_ON_2.getString(),
			Settings.GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_ON_3.getString(),
			Settings.GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_ON_4.getString(),
			Settings.GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_ON_5.getString()
	);

	private final List<String> skullTexturesSelected = Arrays.asList(
			Settings.GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_OFF_1.getString(),
			Settings.GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_OFF_2.getString(),
			Settings.GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_OFF_3.getString(),
			Settings.GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_OFF_4.getString(),
			Settings.GUI_RATINGS_STAR_SELECT_RATING_TEXTURE_OFF_5.getString()
	);

	private final Market market;
	private int selectedStar;
	private String message;

	public GUIRatingStarSelect(Market market, int stars, String message) {
		this.market = market;
		this.selectedStar = stars;
		this.message = message;
		setTitle(TextUtils.formatText(Settings.GUI_RATINGS_STAR_SELECT_TITLE.getString()));
		setDefaultItem(Settings.GUI_RATINGS_STAR_SELECT_FILL_ITEM.getMaterial().parseItem());
		setAcceptsItems(false);
		setAllowDrops(false);
		setUseLockedCells(true);
		setRows(4);
		draw();

		setOnClose(e -> e.manager.showGUI(e.player, new GUIMarketView(this.market)));
	}

	private void draw() {
		int slot = 11;
		int star = 1;
		for (int i = 1; i < 6; i++) {
			int finalStar = star;
			setButton(slot, ConfigItemUtil.build(Settings.GUI_RATINGS_STAR_SELECT_RATING_USE_CUSTOM_SKULL.getBoolean() ? Common.getCustomTextureHead(skullTexturesSelected.get(star - 1), false) : Settings.GUI_RATINGS_STAR_SELECT_RATING_ITEM.getMaterial().parseItem(), Settings.GUI_RATINGS_STAR_SELECT_RATING_NAME.getString(), Settings.GUI_RATINGS_STAR_SELECT_RATING_LORE.getStringList(), 1, new HashMap<String, Object>() {{
				put("%star_number%", finalStar);
			}}), e -> {
				selectedStar = finalStar;
				draw();
			});

			if (i == selectedStar) {
				setItem(slot, ConfigItemUtil.build(Settings.GUI_RATINGS_STAR_SELECT_RATING_USE_CUSTOM_SKULL.getBoolean() ? Common.getCustomTextureHead(skullTextures.get(star - 1), false) : Settings.GUI_RATINGS_STAR_SELECT_RATING_ITEM.getMaterial().parseItem(), Settings.GUI_RATINGS_STAR_SELECT_RATING_NAME.getString(), Settings.GUI_RATINGS_STAR_SELECT_RATING_LORE.getStringList(), 1, new HashMap<String, Object>() {{
					put("%star_number%", finalStar);
				}}));
			}

			slot++;
			star++;
		}

		setButton(22, ConfigItemUtil.build(Settings.GUI_RATINGS_STAR_SELECT_MESSAGE_ITEM.getString(), Settings.GUI_RATINGS_STAR_SELECT_MESSAGE_NAME.getString(), Settings.GUI_RATINGS_STAR_SELECT_MESSAGE_LORE.getStringList(), 1, new HashMap<String, Object>() {{
			put("%rating_message%", message);
		}}), e -> {
			e.gui.exit();
			ChatPrompt.showPrompt(Markets.getInstance(), e.player, TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_rating_message").getMessage()), chat -> {
				String msg = chat.getMessage();
				if (msg != null && msg.length() != 0) {
					if (msg.length() > Settings.RATING_MAX_MESSAGE_LENGTH.getInt()) {
						Markets.getInstance().getLocale().getMessage("rating_message_too_long").sendPrefixedMessage(e.player);
						reOpen(e);
						return;
					}

					this.message = ChatColor.stripColor(msg);
					reOpen(e);
				}

			}).setOnClose(() -> reOpen(e)).setOnCancel(() -> reOpen(e));
		});

		for (int i = 20; i < 22; i++) {
			setButton(i, ConfigItemUtil.build(Settings.GUI_RATINGS_STAR_SELECT_CANCEL_ITEM.getString(), Settings.GUI_RATINGS_STAR_SELECT_CANCEL_NAME.getString(), Settings.GUI_RATINGS_STAR_SELECT_CANCEL_LORE.getStringList(), 1, null), e -> e.manager.showGUI(e.player, new GUIMarketView(this.market)));
		}

		for (int i = 23; i < 25; i++) {
			setButton(i, ConfigItemUtil.build(Settings.GUI_RATINGS_STAR_SELECT_CONFIRM_ITEM.getString(), Settings.GUI_RATINGS_STAR_SELECT_CONFIRM_NAME.getString(), Settings.GUI_RATINGS_STAR_SELECT_CONFIRM_LORE.getStringList(), 1, null), e -> {
				if (this.message.length() == 0) {
					this.message = Markets.getInstance().getLocale().getMessage("misc.no_rating_message").getMessage();
				}

				this.market.getRatings().removeIf(marketRating -> marketRating.getRater().equals(e.player.getUniqueId()));
				MarketRating marketRating = new MarketRating(e.player.getUniqueId(), this.selectedStar, this.message);
				marketRating.setMarketId(this.market.getId());
				this.market.getRatings().add(marketRating);
				e.gui.close();
			});
		}
	}

	private void reOpen(GuiClickEvent e) {
		e.manager.showGUI(e.player, new GUIRatingStarSelect(this.market, this.selectedStar, this.message));
	}
}
