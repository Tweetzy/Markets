package ca.tweetzy.markets.model;

import ca.tweetzy.markets.api.MarketUser;

import java.util.UUID;

public final class PlayerManager extends KeyValueManager<UUID, MarketUser> {

	public PlayerManager() {
		super("Player");
	}

	@Override
	public void load() {

	}
}
