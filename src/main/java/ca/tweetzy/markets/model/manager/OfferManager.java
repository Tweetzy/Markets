package ca.tweetzy.markets.model.manager;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.api.market.offer.Offer;
import ca.tweetzy.markets.impl.MarketOffer;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class OfferManager extends ListManager<Offer> {

	public OfferManager() {
		super("Offer");
	}

	public List<Offer> getOffersSentTo(@NonNull final UUID playerUUID) {
		return getManagerContent().stream().filter(offer -> offer.getOfferFor().equals(playerUUID)).collect(Collectors.toList());
	}

	public void create(@NonNull final Player sender, @NonNull final Market owningMarket, @NonNull final MarketItem marketItem, @NonNull final String currency, @NonNull final ItemStack currencyItem, final double offeredAmount, @NonNull final Consumer<Boolean> created) {
		final Offer offer = new MarketOffer(
				UUID.randomUUID(),
				sender.getUniqueId(),
				sender.getName(),
				owningMarket.getOwnerUUID(),
				marketItem.getId(),
				marketItem.getStock(),
				currency,
				currencyItem,
				offeredAmount,
				System.currentTimeMillis()
		);

		offer.store(storedOffer -> {
			if (storedOffer != null) {
				add(storedOffer);
				created.accept(true);

				Common.tell(sender, TranslationManager.string(sender, Translations.OFFER_SENT, "owner_name", owningMarket.getOwnerName()));
				final OfflinePlayer owner = Bukkit.getOfflinePlayer(owningMarket.getOwnerUUID());

				if (owner.isOnline())
					Common.tell(owner.getPlayer(), TranslationManager.string(owner.getPlayer(), Translations.OFFER_RECEIVED, "sender_name", sender.getName()));

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
