package ca.tweetzy.markets.api;

import lombok.NonNull;

public interface UserIdentifiable {

	@NonNull
	String getId();
}
