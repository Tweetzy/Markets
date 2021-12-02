package ca.tweetzy.markets.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 05 2021
 * Time Created: 2:19 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@Getter
@Setter
public class Request {

	private UUID id;
	private UUID requester;
	private long date;
	private List<RequestItem> requestedItems;

	public Request(UUID id, UUID requester, long date, List<RequestItem> requestedItems) {
		this.id = id;
		this.requester = requester;
		this.date = date;
		this.requestedItems = requestedItems;
	}

	public Request(UUID requester, List<RequestItem> requestedItems) {
		this(UUID.randomUUID(), requester, System.currentTimeMillis(), requestedItems);
	}
}
