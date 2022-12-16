package ca.tweetzy.markets.api;

public interface Trackable {

	/**
	 * Time when the element was created
	 *
	 * @return created time
	 */
	long getTimeCreated();

	/**
	 * Time when element was last updated
	 *
	 * @return last updated time
	 */
	long getLastUpdated();
}