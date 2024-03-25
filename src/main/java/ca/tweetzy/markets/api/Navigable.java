package ca.tweetzy.markets.api;

public interface Navigable<E extends Enum<E>> {

	default E next() {
		E[] values = enumClass().getEnumConstants();
		E current = (E) this;
		int ordinal = current.ordinal();
		int nextOrdinal = (ordinal + 1) % values.length;
		return values[nextOrdinal];
	}

	default E previous() {
		E[] values = enumClass().getEnumConstants();
		E current = (E) this;
		int ordinal = current.ordinal();
		int previousOrdinal = (ordinal - 1 + values.length) % values.length;
		return values[previousOrdinal];
	}

	Class<E> enumClass();
}