package ca.tweetzy.markets.structures;

import lombok.Getter;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 27 2021
 * Time Created: 6:29 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
public class Pair<L, R> {

	private final L first;
	private final R second;

	public Pair(L first, R second) {
		this.first = first;
		this.second = second;
	}
}
