package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.BankEntry;
import ca.tweetzy.markets.impl.MarketBankEntry;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class BankManager extends ListManager<BankEntry> {

	public BankManager() {
		super("Bank");
	}

	public List<BankEntry> getEntriesByPlayer(@NonNull final UUID owner) {
		return getManagerContent().stream().filter(entry -> entry.getOwner().equals(owner)).collect(Collectors.toList());
	}

	public BankEntry getEntryByPlayer(@NonNull final UUID owner, @NonNull final ItemStack itemStack) {
		return getManagerContent().stream().filter(entry -> entry.getOwner().equals(owner) && entry.getItem().isSimilar(itemStack)).findFirst().orElse(null);
	}

	public int getEntryCountByPlayer(@NonNull final UUID owner, @NonNull final ItemStack itemStack) {
		final BankEntry entry = getEntryByPlayer(owner, itemStack);

		if (entry == null) return 0;
		return entry.getQuantity();
	}

	public void create(@NonNull final Player sender, @NonNull final ItemStack itemStack, final int amount, @NonNull final Consumer<Boolean> created) {
		ItemStack toAdd = itemStack.clone();
		toAdd.setAmount(1);

		final BankEntry entry = new MarketBankEntry(
				UUID.randomUUID(),
				sender.getUniqueId(),
				toAdd,
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

		Markets.getDataManager().getBankEntries((error, found) -> {
			if (error != null) return;
			found.forEach(this::add);
		});
	}
}
