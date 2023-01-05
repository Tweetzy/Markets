package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.api.market.MarketUser;
import ca.tweetzy.markets.api.manager.KeyValueManager;

import java.util.UUID;

public final class PlayerManager extends KeyValueManager<UUID, MarketUser> {

	public PlayerManager() {
		super("Player");
	}

	@Override
	public void load() {

	}
}
