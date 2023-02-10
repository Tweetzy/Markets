package ca.tweetzy.markets.model.importer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.function.Consumer;

@AllArgsConstructor
public abstract class Importer {

	@Getter
	protected final String name;

	public abstract void run(@NonNull final Consumer<Boolean> success);
}
