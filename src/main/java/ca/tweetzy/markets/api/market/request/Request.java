package ca.tweetzy.markets.api.market.request;

import ca.tweetzy.markets.api.Identifiable;
import ca.tweetzy.markets.api.Storeable;
import ca.tweetzy.markets.api.Synchronize;
import ca.tweetzy.markets.api.Trackable;

public interface Request extends Identifiable, Synchronize, Trackable, Storeable<Request> {


}
