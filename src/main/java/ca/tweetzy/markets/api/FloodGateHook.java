package ca.tweetzy.markets.api;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.geysermc.floodgate.util.DeviceOs;

@UtilityClass
public final class FloodGateHook {

	private boolean isFloodGateActive() {
		return Bukkit.getServer().getPluginManager().isPluginEnabled("floodgate");
	}

	public boolean isFloodGateUser(@NonNull final Player player) {
		if (!isFloodGateActive()) return false;
		return FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId());
	}

	public boolean isMobileUser(@NonNull final Player player) {
		if (!isFloodGateUser(player)) return false;

		final FloodgatePlayer floodgatePlayer = FloodgateApi.getInstance().getPlayer(player.getUniqueId());
		if (floodgatePlayer == null) return false;

		return floodgatePlayer.getDeviceOs() == DeviceOs.IOS || floodgatePlayer.getDeviceOs() == DeviceOs.GOOGLE;
	}
}