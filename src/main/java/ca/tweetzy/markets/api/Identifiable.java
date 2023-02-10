package ca.tweetzy.markets.api;

import lombok.NonNull;

import java.util.UUID;

public interface Identifiable {

	/**
	 * The identifier for the group.
	 *
	 * @return The id of the group.
	 */
	@NonNull
	UUID getId();
}
