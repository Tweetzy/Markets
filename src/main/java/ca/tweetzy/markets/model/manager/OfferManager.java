package ca.tweetzy.markets.model.manager;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.api.market.Offer;
import ca.tweetzy.markets.impl.MarketOffer;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Consumer;

public final class OfferManager extends ListManager<Offer> {

	public OfferManager() {
		super("Offer");
	}

	public void create(@NonNull final Player sender, @NonNull final Market owningMarket, @NonNull final MarketItem marketItem, @NonNull final String currency, @NonNull final ItemStack currencyItem, final double offeredAmount, @NonNull final Consumer<Boolean> created) {
		final Offer offer = new MarketOffer(
				UUID.randomUUID(),
				sender.getUniqueId(),
				sender.getName(),
				owningMarket.getOwnerUUID(),
				marketItem.getId(),
				currency,
				currencyItem,
				offeredAmount,
				System.currentTimeMillis()
		);

		offer.store(storedOffer -> {
			if (storedOffer != null) {
				add(storedOffer);
				created.accept(true);
			} else {
				created.accept(false);
			}
		});
	}

	@Override
	public void load() {
		clear();

		Markets.getDataManager().getOffers((error, found) -> {
			if (error != null) return;
			found.forEach(this::add);
		});
	}
}
