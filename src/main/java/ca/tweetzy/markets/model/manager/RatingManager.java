package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.Trackable;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.Rating;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class RatingManager extends ListManager<Rating> {

	public RatingManager() {
		super("Rating");
	}

	public List<Rating> getRatingsByOrFor(@NonNull final OfflinePlayer profileUser) {
		return getRatingsByOrFor(profileUser.getUniqueId());
	}

	public boolean userMeetsReviewRequirements(@NonNull final Market market, @NonNull final Player player) {
		final int totalTransactionsMade = Markets.getTransactionManager().getTransactionsMadeToMarket(market.getOwnerUUID(), player.getUniqueId());
		return totalTransactionsMade >= Settings.MIN_PURCHASES_BEFORE_REVIEW.getInt();
	}

	public boolean canUserRateMarket(@NonNull final Market market, @NonNull final Player player) {
		final List<Rating> userRatingsByMarket = market.getRatings().stream().filter(rating -> rating.getRaterUUID().equals(player.getUniqueId())).toList();
		if (userRatingsByMarket.isEmpty()) return true;

		final Rating latestRating = Collections.max(userRatingsByMarket, Comparator.comparing(Trackable::getTimeCreated));

		if (latestRating == null)
			return true;

		final long lastRatingTime = latestRating.getTimeCreated();
		return timeDifferenceInSeconds(lastRatingTime, System.currentTimeMillis()) >= Settings.TIME_BETWEEN_RATINGS.getInt();
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

	private long timeDifferenceInSeconds(long time1, long time2) {
		return Math.abs(time2 - time1) / 1000;
	}
}
