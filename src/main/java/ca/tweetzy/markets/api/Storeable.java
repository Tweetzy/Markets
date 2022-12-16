package ca.tweetzy.markets.api;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface Storeable<T> {

	 void store(@NonNull final Consumer<T> stored);
}
