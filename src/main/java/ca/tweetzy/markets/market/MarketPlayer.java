package ca.tweetzy.markets.market;

import ca.tweetzy.markets.api.MarketsAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 02 2021
 * Time Created: 12:38 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class MarketPlayer {

	private Player player;
	private Market market;

	public MarketPlayer(Player player, Market market) {
		this.player = player;
		this.market = market;
	}

	public MarketPlayer(Player player) {
		this(player, null);
	}

	public int getItemLimit() {
		return MarketsAPI.getInstance().maxAllowedMarketItems(this.player);
	}
}
