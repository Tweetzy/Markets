package ca.tweetzy.markets.gui.user;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.ItemUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.currency.TransactionResult;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.api.market.Offer;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public final class OffersGUI extends MarketsPagedGUI<Offer> {

	private final Player player;

	public OffersGUI(Gui parent, @NonNull final Player player) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_OFFERS_TITLE), 6, Markets.getOfferManager().getOffersSentTo(player.getUniqueId()));
		this.player = player;
		setAcceptsItems(true);
		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(Offer offer) {
		final MarketItem marketItem = Markets.getCategoryItemManager().getByUUID(offer.getMarketItem());

		final QuickItem item = QuickItem
				.of(marketItem == null ? Objects.requireNonNull(CompMaterial.RED_STAINED_GLASS_PANE.parseItem()) : marketItem.getItem())
				.lore(TranslationManager.list(this.player, Translations.GUI_OFFERS_ITEMS_OFFER_LORE_HEADER));


		if (marketItem == null || marketItem.getStock() < offer.getRequestAmount()) {
			item.lore(TranslationManager.list(this.player, Translations.GUI_OFFERS_ITEMS_OFFER_LORE_REJECT_STOCK));
			item.lore(TranslationManager.list(this.player, Translations.GUI_OFFERS_ITEMS_OFFER_LORE_REJECT, "right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)));
		} else {
			item.lore(TranslationManager.list(this.player, Translations.GUI_OFFERS_ITEMS_OFFER_LORE_INFO,
					"market_item_price", marketItem.getPrice(),
					"market_item_currency", marketItem.getCurrencyDisplayName(),
					"offer_requested_amount", offer.getRequestAmount(),
					"offer_requester_name", offer.getOfferSenderName(),
					"offer_amount", offer.getOfferedAmount(),
					"offer_currency", offer.getCurrencyDisplayName()
			));

			item.lore(TranslationManager.list(this.player, Translations.GUI_OFFERS_ITEMS_OFFER_LORE_ACCEPT, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)));
			item.lore(TranslationManager.list(this.player, Translations.GUI_OFFERS_ITEMS_OFFER_LORE_REJECT, "right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)));
		}

		item.lore(TranslationManager.list(this.player, Translations.GUI_OFFERS_ITEMS_OFFER_LORE_FOOTER));

		return item.make();
	}

	@Override
	protected void onClick(Offer offer, GuiClickEvent click) {
		final MarketItem marketItem = Markets.getCategoryItemManager().getByUUID(offer.getMarketItem());

		if (click.clickType == ClickType.LEFT) {
			offer.accept(result -> {
				final OfflinePlayer offerSender = Bukkit.getOfflinePlayer(offer.getOfferSender());

				if (offerSender.isOnline())
					switch (result) {
						case SUCCESS ->
								Common.tell(offerSender.getPlayer(), TranslationManager.string(offerSender.getPlayer(), Translations.OFFER_ACCEPTED, "owner_name", click.player.getName(), "market_item_name", ItemUtil.getStackName(marketItem.getItem())));
						case FAILED_NO_MONEY ->
								Common.tell(offerSender.getPlayer(), TranslationManager.string(offerSender.getPlayer(), Translations.OFFER_REJECT_NO_MONEY, "owner_name", click.player.getName(), "market_item_name", ItemUtil.getStackName(marketItem.getItem())));
						case FAILED_OUT_OF_STOCK ->
								Common.tell(offerSender.getPlayer(), TranslationManager.string(offerSender.getPlayer(), Translations.OFFER_REJECT_INSUFFICIENT_STOCK, "owner_name", click.player.getName(), "market_item_name", ItemUtil.getStackName(marketItem.getItem())));
						case NO_LONGER_AVAILABLE ->
								Common.tell(offerSender.getPlayer(), TranslationManager.string(offerSender.getPlayer(), Translations.OFFER_REJECT_NOT_AVAILABLE, "owner_name", click.player.getName()));
					}

				click.manager.showGUI(click.player, new OffersGUI(this.parent, click.player));
			});
		}

		if (click.clickType == ClickType.RIGHT) {
			offer.reject((result, reason) -> {
				if (result != TransactionResult.SUCCESS) return;

				final OfflinePlayer offerSender = Bukkit.getOfflinePlayer(offer.getOfferSender());

				if (offerSender.isOnline())
					switch (reason) {
						case NOT_ACCEPTED ->
								Common.tell(offerSender.getPlayer(), TranslationManager.string(offerSender.getPlayer(), Translations.OFFER_REJECT_NOT_ACCEPTED, "owner_name", click.player.getName(), "market_item_name", ItemUtil.getStackName(marketItem.getItem())));
						case ITEM_NO_LONGER_AVAILABLE ->
								Common.tell(offerSender.getPlayer(), TranslationManager.string(offerSender.getPlayer(), Translations.OFFER_REJECT_NOT_AVAILABLE, "owner_name", click.player.getName()));
						case INSUFFICIENT_STOCK ->
								Common.tell(offerSender.getPlayer(), TranslationManager.string(offerSender.getPlayer(), Translations.OFFER_REJECT_INSUFFICIENT_STOCK, "owner_name", click.player.getName(), "market_item_name", ItemUtil.getStackName(marketItem.getItem())));
					}

				click.manager.showGUI(click.player, new OffersGUI(this.parent, click.player));
			});
		}
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(5);
	}
}
