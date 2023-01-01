package ca.tweetzy.markets.manager;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public final class DelayManager {

	private final HashMap<UUID, Long> clickDelay = new HashMap<>();

	public void addCooldown(UUID uuid) {
		this.clickDelay.put(uuid, System.currentTimeMillis() + 250);
	}

	public HashMap<UUID, Long> getCooldowns() {
		return this.clickDelay;
	}

	public boolean canClick(Player player) {
		if (this.clickDelay.containsKey(player.getUniqueId())) {
			if (clickDelay.get(player.getUniqueId()) > System.currentTimeMillis()) {
				return false;
			}
		}

		addCooldown(player.getUniqueId());
		return true;
	}
}
