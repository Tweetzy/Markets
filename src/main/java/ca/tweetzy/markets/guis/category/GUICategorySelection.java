package ca.tweetzy.markets.guis.category;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.guis.items.GUIAddItem;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 04 2021
 * Time Created: 5:58 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUICategorySelection extends Gui {

    private final Market market;
    private final MarketCategory selectedCategory;
    private final double itemPrice;
    private final boolean useCustomCurrency;
    private final boolean priceIsForStack;
    private final ItemStack item;
    private final ItemStack currency;

    public GUICategorySelection(Player player, Market market, MarketCategory selectedCategory, double itemPrice, boolean useCustomCurrency, boolean priceIsForStack, ItemStack item, ItemStack currency) {
        this.market = market;
        this.selectedCategory = selectedCategory;
        this.itemPrice = itemPrice;
        this.useCustomCurrency = useCustomCurrency;
        this.priceIsForStack = priceIsForStack;
        this.item = item;
        this.currency = currency;
        setTitle(TextUtils.formatText(Settings.GUI_CATEGORY_SELECT_TITLE.getString()));
        setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_CATEGORY_SELECT_FILL_ITEM.getMaterial()));
        setAllowDrops(false);
        setAcceptsItems(false);
        setUseLockedCells(true);
        setAllowShiftClick(false);

        int totalCategories = this.market.getCategories().size();
        if (totalCategories >= 1 && totalCategories <= 9) setRows(1);
        if (totalCategories >= 10 && totalCategories <= 18) setRows(2);
        if (totalCategories >= 19 && totalCategories <= 27) setRows(3);
        if (totalCategories >= 28 && totalCategories <= 36) setRows(4);
        if (totalCategories >= 37 && totalCategories <= 45) setRows(5);
        if (totalCategories >= 46) setRows(6);

        int slot = 0;
        for (MarketCategory category : this.market.getCategories()) {
            setButton(slot++, ConfigItemUtil.build(XMaterial.matchXMaterial(category.getIcon()).parseItem(), Settings.GUI_CATEGORY_SELECT_CATEGORY_NAME.getString(), Settings.GUI_CATEGORY_SELECT_CATEGORY_LORE.getStringList(), 1, new HashMap<String, Object>(){{
                put("%category_name%", category.getDisplayName());
                put("%category_description%", category.getDescription());
            }}), ClickType.LEFT, e -> {
                e.gui.exit();
                e.manager.showGUI(player, new GUIAddItem(player, this.market, category, this.itemPrice, this.useCustomCurrency, this.priceIsForStack, this.item, this.currency));
            });
        }

        setOnClose(close -> close.manager.showGUI(player, new GUIAddItem(player, this.market, this.selectedCategory, this.itemPrice, this.useCustomCurrency, this.priceIsForStack, this.item, this.currency)));
    }
}
