package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.BankEntry;
import ca.tweetzy.markets.impl.MarketBankEntry;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public final class BankManager extends ListManager<BankEntry> {

	public BankManager() {
		super("Bank");
	}

	public void create(@NonNull final Player sender, @NonNull final ItemStack itemStack, final int amount, @NonNull final Consumer<Boolean> created) {
		final BankEntry entry = new MarketBankEntry(
				UUID.randomUUID(),
				sender.getUniqueId(),
				itemStack,
				amount
		);

		entry.store(storedBankEntry -> {
			if (storedBankEntry != null) {
				add(storedBankEntry);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
	}

	@Override
	public void load() {
		clear();


	}
}
