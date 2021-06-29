package ca.tweetzy.markets.market;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: June 29 2021
 * Time Created: 1:00 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class MarketRating {

    private UUID id;
    private UUID rater;
    private int stars;
    private String message;

    public MarketRating(UUID id, UUID rater, int stars, String message) {
        this.id = id;
        this.rater = rater;
        this.stars = stars;
        this.message = message;
    }

    public MarketRating(UUID rater, int stars, String message) {
        this(UUID.randomUUID(), rater, stars, message);
    }
}
