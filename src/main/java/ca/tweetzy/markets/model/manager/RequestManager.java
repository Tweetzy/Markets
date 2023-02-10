package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.Request;
import ca.tweetzy.markets.impl.MarketRequest;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class RequestManager extends ListManager<Request> {

	public RequestManager() {
		super("Request");
	}

	public List<Request> getRequestsBy(@NonNull final UUID user) {
		return getManagerContent().stream().filter(request -> request.getOwner().equals(user)).collect(Collectors.toList());
	}


	public List<Request> getRequestsExclude(@NonNull final UUID user) {
		return getManagerContent().stream().filter(request -> !request.getOwner().equals(user)).collect(Collectors.toList());
	}

	public List<Request> getRequestsExclude(@NonNull final OfflinePlayer player) {
		return getRequestsExclude(player.getUniqueId());
	}

	public List<Request> getRequestsBy(@NonNull final OfflinePlayer player) {
		return getRequestsBy(player.getUniqueId());
	}

	public void create(@NonNull final Player owner, @NonNull final ItemStack requestedItem, final String currency, @NonNull final ItemStack currencyItem, final double price, final int requestedAmount, @NonNull final Consumer<Boolean> created) {
		final Request request = new MarketRequest(
				UUID.randomUUID(),
				owner.getUniqueId(),
				owner.getName(),
				requestedItem,
				currency,
				currencyItem,
				price,
				requestedAmount,
				System.currentTimeMillis()
		);

		request.store(storedRequest -> {
			if (storedRequest != null) {
				add(storedRequest);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
	}

	@Override
	public void load() {
		clear();

		Markets.getDataManager().getRequests((error, found) -> {
			if (error != null) return;
			found.forEach(this::add);
		});
	}
}
