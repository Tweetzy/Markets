package ca.tweetzy.markets.structures;

import lombok.Getter;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 02 2021
 * Time Created: 6:32 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
public class Triple<L, M, R> {

	private final L first;
	private final M second;
	private final R third;

	public Triple(L first, M second, R third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}
}
