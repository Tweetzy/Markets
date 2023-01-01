package ca.tweetzy.markets.model;

import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


public final class InfoCollector<T> {

	private final Player player;
	private T input;
	private Consumer<Player> onExit;
	private final CompletableFuture<T> future = new CompletableFuture<>();

	private InfoCollector(@NonNull final Player player) {
		this.player = player;
	}

	public static <T> InfoCollector<T> of(@NonNull final Player player) {
		return new InfoCollector<>(player);
	}

	public InfoCollector<T> onExit(Consumer<Player> onExit) {
		this.onExit = onExit;
		return this;
	}

	public void collect(final String title, final String subtitle, final String actionbar, final Function<String, T> parser, final Predicate<T> validationExpression, Consumer<T> consumer) {
		new TitleInput(Markets.getInstance(), this.player, title, subtitle, actionbar) {
			@Override
			public void onExit(Player player) {
				if (InfoCollector.this.onExit != null)
					InfoCollector.this.onExit.accept(player);
				future.complete(null);
			}

			@Override
			public boolean onResult(String string) {
				try {
					T parsedInput = parser.apply(string);
					if (validationExpression.test(parsedInput)) {
						InfoCollector.this.input = parsedInput;
						future.complete(input);
						return true;
					} else {
						return false;
					}
				} catch (Exception ignored) {
					return false;
				}
			}
		};

		future.whenComplete((result, error) -> {
			if (result != null) {
				consumer.accept(result);
			}
		});
	}
}