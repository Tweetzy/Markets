package ca.tweetzy.markets.exception;

import lombok.NonNull;

public final class MarketNotFoundException extends NullPointerException {

	public MarketNotFoundException(@NonNull final String message) {
		super(message);
	}
}
