package ca.tweetzy.markets.utils;

import ca.tweetzy.core.compatibility.XMaterial;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: December 23 2021
 * Time Created: 12:02 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@UtilityClass
public final class InventorySafeMaterials {

	public List<XMaterial> get() {
		final List<XMaterial> list = new ArrayList<>();

		final Inventory drawer = Bukkit.createInventory(null, 9, "Valid Materials");

		for (int i = 0; i < XMaterial.values().length; i++) {
			final XMaterial material = XMaterial.values()[i];
			drawer.setItem(0, material.parseItem());
			if (drawer.getItem(0) != null) {
				drawer.setItem(0, null);
				list.add(material);
			}
		}

		return list.stream().sorted(Comparator.comparing(XMaterial::name)).collect(Collectors.toList());
	}
}
