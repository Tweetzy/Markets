package ca.tweetzy.markets.guis.items;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.gui.SimplePagedGui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.guis.category.GUICategorySettings;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.structures.Triple;
import ca.tweetzy.markets.utils.Common;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 03 2021
 * Time Created: 12:16 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUIIconSelect extends SimplePagedGui {

    public GUIIconSelect(Market market, MarketCategory marketCategory) {
        this(market, marketCategory, false);
    }

    public GUIIconSelect(Market market, MarketCategory marketCategory, boolean isForCurrency) {
        setTitle(Settings.GUI_SELECT_MATERIAL_TITLE.getString());
        setUseLockedCells(true);
        setAllowShiftClick(false);
        setHeaderBackItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
        setFooterBackItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
        setDefaultItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());

        List<Material> supportedVersionItems = new ArrayList<>();
        for (XMaterial allValidItemMaterial : XMaterial.getAllValidItemMaterials()) {
            if (allValidItemMaterial.isSupported() && allValidItemMaterial.parseMaterial() != null) {
                supportedVersionItems.add(allValidItemMaterial.parseMaterial());
            }
        }

        supportedVersionItems = supportedVersionItems.stream().distinct().collect(Collectors.toList());

        int slot = 9;
        for (Material material : supportedVersionItems) {
            setButton(slot++, GuiUtils.createButtonItem(material, material.name().toLowerCase(Locale.ROOT).replace("_", " ")), ClickType.LEFT, e -> {
                if (isForCurrency) {

                    if (!Markets.getInstance().getMarketPlayerManager().getAddingCustomCurrencyItem().containsKey(e.player.getUniqueId())) {
                        e.gui.exit();
                        return;
                    }

                    ItemStack itemToBeUsedAsCurrency = XMaterial.matchXMaterial(material).parseItem();
                    assert itemToBeUsedAsCurrency != null;
                    itemToBeUsedAsCurrency.setAmount(1);

                    Triple<Market, MarketCategory, MarketItem> toAdd = Markets.getInstance().getMarketPlayerManager().getPlayerAddingCustomCurrencyItem(e.player.getUniqueId());
                    toAdd.getThird().setCurrencyItem(itemToBeUsedAsCurrency);
                    toAdd.getThird().setUseItemCurrency(true);
                    toAdd.getFirst().setUpdatedAt(System.currentTimeMillis());
                    MarketsAPI.getInstance().removeSpecificItemQuantityFromPlayer(e.player, toAdd.getThird().getItemStack(), toAdd.getThird().getItemStack().getAmount());

                    if (!toAdd.getSecond().getItems().contains(toAdd.getThird())) {
                        Markets.getInstance().getMarketManager().addItemToCategory(toAdd.getSecond(), toAdd.getThird());
                        Markets.getInstance().getLocale().getMessage("added_item_to_category").processPlaceholder("item_name", Common.getItemName(toAdd.getThird().getItemStack())).processPlaceholder("market_category_name", toAdd.getSecond().getName()).sendPrefixedMessage(e.player);
                    } else {
                        Markets.getInstance().getLocale().getMessage("updated_market_item_currency").sendPrefixedMessage(e.player);
                        Markets.getInstance().getGuiManager().showGUI(e.player, new GUICategorySettings(toAdd.getFirst(), toAdd.getSecond()));
                    }

                    Markets.getInstance().getMarketPlayerManager().removePlayerFromCustomCurrencyItem(e.player.getUniqueId());
                    e.gui.exit();

                } else {
                    marketCategory.setIcon(XMaterial.matchXMaterial(material).parseMaterial());
                    market.setUpdatedAt(System.currentTimeMillis());
                    Markets.getInstance().getLocale().getMessage("updated_category_icon").sendPrefixedMessage(e.player);
                    e.gui.exit();
                    e.manager.showGUI(e.player, new GUICategorySettings(market, marketCategory));
                }
            });
        }
    }
}
