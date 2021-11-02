package ca.tweetzy.markets.api.heads;

import ca.tweetzy.core.compatibility.XMaterial;
import lombok.experimental.UtilityClass;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: October 29 2021
 * Time Created: 3:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@UtilityClass
public final class HeadDatabaseHook {

	private boolean enabled() {
		return Bukkit.getPluginManager().isPluginEnabled("HeadDatabase");
	}

	public ItemStack getHead(final int id) {
		if (!enabled()) {
			return XMaterial.PLAYER_HEAD.parseItem();
		}

		HeadDatabaseAPI api = new HeadDatabaseAPI();
		try {
			return api.getItemHead(String.valueOf(id));
		} catch (NullPointerException e) {
			return XMaterial.PLAYER_HEAD.parseItem();
		}
	}
}
