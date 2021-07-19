package ca.tweetzy.markets.guis.market;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.category.GUICategoryItems;
import ca.tweetzy.markets.guis.items.GUIAllItems;
import ca.tweetzy.markets.guis.ratings.GUIRatingStarSelect;
import ca.tweetzy.markets.guis.ratings.GUIRatingsList;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.MarketRating;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.Numbers;
import org.apache.commons.lang.StringUtils;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:38 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIMarketView extends Gui {

    private final Market market;

    public GUIMarketView(Market market) {
        this.market = market;
        setTitle(TextUtils.formatText(Settings.GUI_MARKET_VIEW_TITLE.getString().replace("%market_name%", market.getName())));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setAllowShiftClick(false);
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_MARKET_VIEW_FILL_ITEM.getMaterial()));
        setRows(6);

        draw();
    }

    private void draw() {
        reset();
        pages = (int) Math.max(1, Math.ceil(this.market.getCategories().size() / (double) 9));

        // make border
        for (int i : Numbers.GUI_BORDER_6_ROWS) {
            setItem(i, GuiUtils.getBorderItem(Settings.GUI_MARKET_VIEW_BORDER_ITEM.getMaterial()));
            if (Settings.GUI_MARKET_VIEW_GLOW_BORDER.getBoolean()) highlightItem(i);
        }

        setPrevPage(5, 3, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setButton(5, 4, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIMarketList()));
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        setButton(2, 2, ConfigItemUtil.build(Settings.GUI_MARKET_VIEW_ITEMS_ALL_ITEMS_ITEM.getString(), Settings.GUI_MARKET_VIEW_ITEMS_ALL_ITEMS_NAME.getString(), Settings.GUI_MARKET_VIEW_ITEMS_ALL_ITEMS_LORE.getStringList(), 1, null), ClickType.LEFT, e -> {
            e.manager.showGUI(e.player, new GUIAllItems(this.market, false));
        });

        setButton(5, 7, ConfigItemUtil.build(Settings.GUI_MARKET_VIEW_ITEMS_RATINGS_USE_CUSTOM_SKULL.getBoolean() ? Common.getCustomTextureHead(String.format("%s%s", "http://textures.minecraft.net/texture/", Settings.GUI_MARKET_VIEW_ITEMS_RATINGS_CUSTOM_SKULL_LINK.getString()), false) : Settings.GUI_MARKET_VIEW_ITEMS_RATINGS_ITEM.getMaterial().parseItem(), Settings.GUI_MARKET_VIEW_ITEMS_RATINGS_NAME.getString(), Settings.GUI_MARKET_VIEW_ITEMS_RATINGS_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%average_rating_number%", market.getRatings().size() == 0 ? Markets.getInstance().getLocale().getMessage("misc.no_ratings").getMessage() : String.format("%,.2f", market.getRatings().stream().mapToInt(MarketRating::getStars).average().orElse(0D)));
            put("%average_rating_stars%", market.getRatings().size() == 0 ? Markets.getInstance().getLocale().getMessage("misc.no_ratings").getMessage() : StringUtils.repeat("★", (int) Math.round(market.getRatings().stream().mapToInt(MarketRating::getStars).average().orElse(0D))));
        }}), e -> {
            switch (e.clickType) {
                case LEFT:
                    e.manager.showGUI(e.player, new GUIRatingsList(this.market));
                    break;
                case RIGHT:
                    if (this.market.getOwner().equals(e.player.getUniqueId())) {
                        Markets.getInstance().getLocale().getMessage("cannot_rate_own_market").sendPrefixedMessage(e.player);
                        return;
                    }

                    MarketRating rating = market.getRatings().stream().filter(ratings -> ratings.getRater().equals(e.player.getUniqueId())).findFirst().orElse(null);
                    if (rating != null) {
                        if (!Settings.ALLOW_RATING_CHANGE.getBoolean()) {
                            Markets.getInstance().getLocale().getMessage("already_left_rating").sendPrefixedMessage(e.player);
                            return;
                        }

                        if (Settings.ALLOW_RATING_CHANGE.getBoolean() && System.currentTimeMillis() < rating.getTime() + Settings.RATING_CHANGE_DELAY.getInt() * 1000L) {
                            long[] remainingTimeValues = Common.getRemainingTimeValues((rating.getTime() + Settings.RATING_CHANGE_DELAY.getInt() * 1000L - System.currentTimeMillis()) / 1000L);
                            Markets.getInstance().getLocale().getMessage("must_wait_to_change_rating")
                                    .processPlaceholder("remaining_days", remainingTimeValues[0])
                                    .processPlaceholder("remaining_hours", remainingTimeValues[1])
                                    .processPlaceholder("remaining_minutes", remainingTimeValues[2])
                                    .processPlaceholder("remaining_seconds", remainingTimeValues[3])
                                    .sendPrefixedMessage(e.player);
                            return;
                        }
                    }

                    e.manager.showGUI(e.player, new GUIRatingStarSelect(this.market, 1, ""));
                    break;
                default:
                    return;
            }
        });

        Markets.newChain().asyncFirst(() -> this.market.getCategories().stream().skip((page - 1) * 9L).limit(9L).collect(Collectors.toList())).asyncLast((data) -> {
            int slot = 21;
            for (MarketCategory category : data) {
                setButton(slot, ConfigItemUtil.build(category.getIcon(), Settings.GUI_MARKET_VIEW_ITEMS_ALL_CATEGORY_NAME.getString(), Settings.GUI_MARKET_VIEW_ITEMS_ALL_CATEGORY_LORE.getStringList(), 1, new HashMap<String, Object>() {{
                    put("%category_description%", category.getDescription());
                    put("%category_display_name%", category.getDisplayName());
                    put("%category_name%", category.getName());
                }}), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUICategoryItems(this.market, category)));

                slot = slot == 24 ? slot + 5 : slot + 1;
            }
        }).execute();
    }
}
