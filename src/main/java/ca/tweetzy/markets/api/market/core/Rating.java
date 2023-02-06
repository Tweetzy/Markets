package ca.tweetzy.markets.api.market.core;

import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Trackable;
import lombok.NonNull;

import java.util.UUID;

public interface Rating extends Identifiable, Trackable, Storeable<Rating> {

	@NonNull UUID getMarketID();

	@NonNull UUID getRaterUUID();

	@NonNull String getRaterName();

	@NonNull String getFeedback();

	int getStars();

	void setStars(int stars);

	void setFeedback(@NonNull final String feedback);
}
