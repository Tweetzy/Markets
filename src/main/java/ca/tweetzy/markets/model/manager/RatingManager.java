package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.Rating;
import lombok.NonNull;

import java.util.function.Consumer;

public final class RatingManager extends ListManager<Rating> {

	public RatingManager() {
		super("Rating");
	}

	public void create(@NonNull final Market market, @NonNull final Rating rating, @NonNull final Consumer<Boolean> created) {
		rating.store(storedRating -> {
			if (storedRating != null) {
				add(storedRating);
				market.getRatings().add(rating);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
	}

	@Override
	public void load() {

	}
}
