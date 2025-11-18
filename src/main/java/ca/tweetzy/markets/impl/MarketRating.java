package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.database.annotations.Column;
import ca.tweetzy.flight.database.annotations.Id;
import ca.tweetzy.flight.database.annotations.Table;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.Rating;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

@Table("review")
public final class MarketRating implements Rating {

	@Id
	@Column("id")
	private UUID id;
	
	@Column("market")
	private UUID market;
	
	@Column("rater")
	private UUID raterUUID;
	
	@Column("rater_name")
	private String raterName;
	
	@Column("feedback")
	private String feedback;
	
	@Column("stars")
	private int stars;
	
	@Column("posted_on")
	private long createdAt;

	public MarketRating() {
	}

	public MarketRating(@NonNull UUID id, @NonNull UUID market, @NonNull UUID raterUUID, @NonNull String raterName, @NonNull String feedback, int stars, long createdAt) {
		this.id = id;
		this.market = market;
		this.raterUUID = raterUUID;
		this.raterName = raterName;
		this.feedback = feedback;
		this.stars = stars;
		this.createdAt = createdAt;
	}

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
		Markets.getRatingRepository().save(this, (error, created) -> {
			if (error == null)
				stored.accept(created);
		});
	}
}
