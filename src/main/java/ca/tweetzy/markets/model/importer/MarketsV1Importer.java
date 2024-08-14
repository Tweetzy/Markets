package ca.tweetzy.markets.model.importer;

import lombok.NonNull;

import java.util.function.Consumer;

public final class MarketsV1Importer extends Importer {

	public MarketsV1Importer() {
		super("Markets v1");
	}

	@Override
	public void run(@NonNull Consumer<Boolean> success) {


	}
}
