package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.Rating;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

@AllArgsConstructor
public final class MarketRating implements Rating {

	private final UUID id;
	private final UUID market;
	private final UUID raterUUID;
	private final String raterName;
	private String feedback;
	private int stars;
	private final long createdAt;

	public MarketRating(@NonNull final Market market, @NonNull final Player rater, final int stars, @NonNull final String feedback) {
		this(UUID.randomUUID(), market.getId(), rater.getUniqueId(), rater.getName(), feedback, stars, System.currentTimeMillis());
	}

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@Override
	public @NonNull UUID getMarketID() {
		return this.market;
	}

	@Override
	public @NonNull UUID getRaterUUID() {
		return this.raterUUID;
	}

	@Override
	public @NonNull String getRaterName() {
		return this.raterName;
	}

	@Override
	public @NonNull String getFeedback() {
		return this.feedback;
	}

	@Override
	public int getStars() {
		return this.stars;
	}

	@Override
	public void setStars(int stars) {
		this.stars = stars;
	}

	@Override
	public void setFeedback(@NonNull String feedback) {
		this.feedback = feedback;
	}

	@Override
	public long getTimeCreated() {
		return this.createdAt;
	}

	@Override
	public long getLastUpdated() {
		return this.createdAt;
	}

	@Override
	public void store(@NonNull Consumer<Rating> stored) {
		Markets.getDataManager().createMarketRating(this, (error, created) -> {
			if (error == null)
				stored.accept(created);
		});
	}
}
