package ca.tweetzy.markets.api.event;

import ca.tweetzy.markets.api.market.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public final class MarketTransactionEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final OfflinePlayer buyer;
	private final OfflinePlayer seller;
	private final TransactionType type;
	private final ItemStack item;
	private final String currency;
	private final int quantity;
	private final double price;

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
