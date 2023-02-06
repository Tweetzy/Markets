package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.Rating;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class RatingManager extends ListManager<Rating> {

	public RatingManager() {
		super("Rating");
	}

	public List<Rating> getRatingsByOrFor(@NonNull final OfflinePlayer profileUser) {
		return getRatingsByOrFor(profileUser.getUniqueId());
	}

	public List<Rating> getRatingsByOrFor(@NonNull final UUID uuid) {
		final Market marketOwned = Markets.getMarketManager().getByOwner(uuid);

		final List<Rating> ratingsReceived = marketOwned == null ? new ArrayList<>() : marketOwned.getRatings();
		final List<Rating> ratingsGiven = getManagerContent().stream().filter(rating -> rating.getRaterUUID().equals(uuid)).collect(Collectors.toList());

		ratingsGiven.addAll(ratingsReceived);

		return ratingsGiven;
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
