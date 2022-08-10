package ca.tweetzy.markets.api.heads;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.skulls.Skulls;
import ca.tweetzy.skulls.api.interfaces.Skull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * The current file has been created by Kiran Hart
 * Date Created: October 29 2021
 * Time Created: 3:30 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@UtilityClass
public final class SkullsHook {

	private boolean enabled() {
		return Bukkit.getPluginManager().isPluginEnabled("Skulls") && !Skulls.getSkullManager().isLoading();
	}

	public ItemStack getHead(final int id) {
		final Skull skull = Skulls.getAPI().getSkull(id);
		return enabled() && skull != null ? skull.getItemStack() : XMaterial.PLAYER_HEAD.parseItem();
	}
}
