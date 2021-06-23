package ca.tweetzy.markets.guis.items;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.gui.SimplePagedGui;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.category.GUICategorySettings;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

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
        setTitle(TextUtils.formatText("&eSelect an Icon"));
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
                marketCategory.setIcon(XMaterial.matchXMaterial(material).parseMaterial());
                market.setUpdatedAt(System.currentTimeMillis());
                Markets.getInstance().getLocale().getMessage("updated_category_icon").sendPrefixedMessage(e.player);
                e.gui.exit();
                e.manager.showGUI(e.player, new GUICategorySettings(market, marketCategory));
            });
        }
    }
}
