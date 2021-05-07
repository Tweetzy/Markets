package ca.tweetzy.markets.guis.market;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.events.MarketDeleteEvent;
import ca.tweetzy.markets.guis.items.GUIAllItems;
import ca.tweetzy.markets.guis.category.GUICategorySettings;
import ca.tweetzy.markets.guis.GUIMain;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 02 2021
 * Time Created: 4:19 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIMarketEdit extends Gui {

    final Market market;

    public GUIMarketEdit(Market market) {
        this.market = market;
        setTitle(TextUtils.formatText(Settings.GUI_MARKET_EDIT_TITLE.getString()));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setDefaultItem(Settings.GUI_MARKET_EDIT_FILL_ITEM.getMaterial().parseItem());
        setRows(6);

        draw();
    }

    private void draw() {
        reset();
        pages = (int) Math.max(1, Math.ceil(this.market.getCategories().size() / (double) 24));

        // make border
        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 10, 17, 19, 26, 28, 35, 37, 44, 46, 47, 48, 49, 50, 51, 52, 53).forEach(i -> {
            setItem(i, Settings.GUI_MARKET_EDIT_BORDER_ITEM.getMaterial().parseItem());
            if (Settings.GUI_MARKET_EDIT_GLOW_BORDER.getBoolean()) highlightItem(i);
        });

        setPrevPage(5, 4, new TItemBuilder(Objects.requireNonNull(Settings.GUI_BACK_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
        setNextPage(5, 5, new TItemBuilder(Objects.requireNonNull(Settings.GUI_NEXT_BTN_ITEM.getMaterial().parseMaterial())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
        setOnPage(e -> draw());

        setButton(0, 0, ConfigItemUtil.build(Settings.GUI_MARKET_EDIT_ITEMS_NAME_ITEM.getString(), Settings.GUI_MARKET_EDIT_ITEMS_NAME_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_NAME_LORE.getStringList(), 1, new HashMap<String, Object>() {{
            put("%market_name%", market.getName());
        }}), e -> {
            e.gui.exit();
            ChatPrompt.showPrompt(Markets.getInstance(), e.player, TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_market_name").getMessage()), chat -> {
                if (chat.getMessage().trim().length() >= 1) {
                    this.market.setName(chat.getMessage().trim());
                    this.market.setUpdatedAt(System.currentTimeMillis());
                }
            }).setOnCancel(() -> e.manager.showGUI(e.player, new GUIMarketEdit(this.market))).setOnClose(() -> e.manager.showGUI(e.player, new GUIMarketEdit(this.market)));
        });

        setButton(1, 0, ConfigItemUtil.build(this.market.isOpen() ? Settings.GUI_MARKET_EDIT_ITEMS_OPEN_ENABLE_ITEM.getString() : Settings.GUI_MARKET_EDIT_ITEMS_OPEN_DISABLE_ITEM.getString(), this.market.isOpen() ? Settings.GUI_MARKET_EDIT_ITEMS_OPEN_ENABLE_NAME.getString() : Settings.GUI_MARKET_EDIT_ITEMS_OPEN_DISABLE_NAME.getString(), market.isOpen() ? Settings.GUI_MARKET_EDIT_ITEMS_OPEN_ENABLE_LORE.getStringList() : Settings.GUI_MARKET_EDIT_ITEMS_OPEN_DISABLE_LORE.getStringList(), 1, null), e -> {
            this.market.setOpen(!this.market.isOpen());
            this.market.setUpdatedAt(System.currentTimeMillis());
            draw();
        });

        setButton(2, 0, ConfigItemUtil.build(Settings.GUI_MARKET_EDIT_ITEMS_ADD_CATEGORY_ITEM.getString(), Settings.GUI_MARKET_EDIT_ITEMS_ADD_CATEGORY_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_ADD_CATEGORY_LORE.getStringList(), 1, null), e -> {
            e.gui.exit();
            ChatPrompt.showPrompt(Markets.getInstance(), e.player, TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_category_name").getMessage()), chat -> {
                String possibleName = ChatColor.stripColor(chat.getMessage().trim());

                if (this.market.getCategories().isEmpty()) {
                    Markets.getInstance().getMarketManager().addCategoryToMarket(this.market, new MarketCategory(possibleName));
                    Markets.getInstance().getLocale().getMessage("created_category").processPlaceholder("market_category_name", possibleName).sendPrefixedMessage(e.player);
                    return;
                }

                if (this.market.getCategories().stream().anyMatch(marketCategory -> marketCategory.getName().equalsIgnoreCase(possibleName))) {
                    Markets.getInstance().getLocale().getMessage("category_already_created").processPlaceholder("market_category_name", possibleName).sendPrefixedMessage(e.player);
                    return;
                }

                Markets.getInstance().getMarketManager().addCategoryToMarket(this.market, new MarketCategory(possibleName));
                this.market.setUpdatedAt(System.currentTimeMillis());
                Markets.getInstance().getLocale().getMessage("created_category").processPlaceholder("market_category_name", possibleName).sendPrefixedMessage(e.player);

            }).setOnCancel(() -> e.manager.showGUI(e.player, new GUIMarketEdit(this.market))).setOnClose(() -> e.manager.showGUI(e.player, new GUIMarketEdit(this.market)));
        });

        setButton(3, 0, ConfigItemUtil.build(Settings.GUI_MARKET_EDIT_ITEMS_ALL_ITEMS_ITEM.getString(), Settings.GUI_MARKET_EDIT_ITEMS_ALL_ITEMS_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_ALL_ITEMS_LORE.getStringList(), 1, null), e -> e.manager.showGUI(e.player, new GUIAllItems(this.market, true)));

        setButton(4, 0, ConfigItemUtil.build(Settings.GUI_MARKET_EDIT_ITEMS_DELETE_MARKET_ITEM.getString(), Settings.GUI_MARKET_EDIT_ITEMS_DELETE_MARKET_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_DELETE_MARKET_LORE.getStringList(), 1, null), e -> {
            MarketDeleteEvent marketDeleteEvent = new MarketDeleteEvent(e.player, this.market);
            Bukkit.getPluginManager().callEvent(marketDeleteEvent);
            if (marketDeleteEvent.isCancelled()) return;

            if (Settings.GIVE_ITEMS_ON_MARKET_DELETE.getBoolean() && !this.market.getCategories().isEmpty()) {
                List<ItemStack> items = new ArrayList<>();
                Markets.newChain().async(() -> this.market.getCategories().forEach(category -> category.getItems().forEach(item -> items.add(item.getItemStack())))).sync(() -> PlayerUtils.giveItem(e.player, items)).execute();
            }
            Markets.getInstance().getMarketManager().deleteMarket(this.market);
            Markets.getInstance().getLocale().getMessage("removed_market").sendPrefixedMessage(e.player);
            e.gui.exit();
        });

        setButton(5, 0, ConfigItemUtil.build(Settings.GUI_CLOSE_BTN_ITEM.getString(), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), e -> e.manager.showGUI(e.player, new GUIMain(e.player)));

        List<MarketCategory> data = this.market.getCategories().stream().skip((page - 1) * 24L).limit(24L).collect(Collectors.toList());
        int slot = 11;
        for (MarketCategory category : data) {
            setButton(slot, ConfigItemUtil.build(category.getIcon(), Settings.GUI_MARKET_EDIT_ITEMS_CATEGORY_NAME.getString(), Settings.GUI_MARKET_EDIT_ITEMS_CATEGORY_LORE.getStringList(), 1, new HashMap<String, Object>() {{
                put("%category_name%", category.getName());
                put("%category_display_name%", category.getDisplayName());
            }}), e -> e.manager.showGUI(e.player, new GUICategorySettings(this.market, category)));

            slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 4 : slot + 1;
        }
    }
}
