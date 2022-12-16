package ca.tweetzy.markets.api.market;

import ca.tweetzy.markets.api.*;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

public interface Market extends Identifiable, Displayable, Trackable, Synchronize, Storeable<Market> {

	@NonNull UUID getOwnerUUID();

	@NonNull UUID getOwnerName();

	@NonNull List<Category> getCategories();

	@NonNull List<Rating> getRatings();

}
