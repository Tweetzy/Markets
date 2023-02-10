package ca.tweetzy.markets.api;

public interface Navigable<E extends Enum<E>> {

	E next();

	E previous();
}