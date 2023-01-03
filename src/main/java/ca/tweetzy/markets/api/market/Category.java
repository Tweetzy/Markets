package ca.tweetzy.markets.api.market;

import ca.tweetzy.markets.api.*;
import lombok.NonNull;

import java.util.UUID;

public interface Category extends Identifiable, UserIdentifiable, Displayable, Trackable, Synchronize, Storeable<Category> {

	@NonNull UUID getOwningMarket();
}
