package ca.tweetzy.markets.model.manager;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.ItemUtil;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.manager.ListManager;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.api.market.offer.Offer;
import ca.tweetzy.markets.impl.MarketOffer;
import ca.tweetzy.markets.model.sync.CrossServerNotificationManager;
import ca.tweetzy.markets.model.sync.NotificationEvent;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public Offer getByUUID(@NonNull final UUID uuid) {
		return getManagerContent().stream().filter(offer -> offer.getId().equals(uuid)).findFirst().orElse(null);
	}

	public void create(@NonNull final Player sender, @NonNull final Market owningMarket, @NonNull final MarketItem marketItem, @NonNull final String currency, @NonNull final ItemStack currencyItem, final int amountWant, final double offeredAmount, @NonNull final Consumer<Boolean> created) {
		final Offer offer = new MarketOffer(
				UUID.randomUUID(),
				sender.getUniqueId(),
				sender.getName(),
				owningMarket.getOwnerUUID(),
				marketItem.getId(),
				amountWant,
				currency,
				currencyItem,
				offeredAmount,
				System.currentTimeMillis()
		);

		offer.store(storedOffer -> {
			// Schedule all operations on main thread to avoid connection issues
			if (!Markets.getInstance().isEnabled()) {
				// Plugin disabled, don't schedule task
				created.accept(false);
				return;
			}
			
			Bukkit.getScheduler().runTask(Markets.getInstance(), () -> {
				try {
					if (storedOffer != null) {
						add(storedOffer);
						
						// Notify sender
						if (sender.isOnline()) {
							Common.tell(sender, TranslationManager.string(sender, Translations.OFFER_SENT, "owner_name", owningMarket.getOwnerName()));
						}
						
						// Send cross-server notification to market owner
						CrossServerNotificationManager notificationManager = Markets.getNotificationManager();
						if (notificationManager != null) {
							Map<String, Object> notificationData = new HashMap<>();
							notificationData.put("sender_name", sender.getName());
							notificationData.put("item_name", ItemUtil.getItemName(marketItem.getItem()));
							
							notificationManager.sendNotification(
								owningMarket.getOwnerUUID(),
								NotificationEvent.NotificationType.OFFER_RECEIVED,
								notificationData
							);
						} else {
							// Fallback to local notification if notification manager not available
							final OfflinePlayer owner = Bukkit.getOfflinePlayer(owningMarket.getOwnerUUID());
							if (owner.isOnline()) {
								Common.tell(owner.getPlayer(), TranslationManager.string(owner.getPlayer(), Translations.OFFER_RECEIVED, "sender_name", sender.getName()));
							}
						}
						
						// Call the created callback last, after all operations complete
						created.accept(true);
					} else {
						created.accept(false);
					}
				} catch (Exception e) {
					Markets.getInstance().getLogger().severe("Error in offer creation callback: " + e.getMessage());
					e.printStackTrace();
					created.accept(false);
				}
			});
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
