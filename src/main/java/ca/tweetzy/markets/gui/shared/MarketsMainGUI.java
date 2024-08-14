package ca.tweetzy.markets.gui.shared;

import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.gui.MarketsBaseGUI;
import ca.tweetzy.markets.gui.shared.view.AllMarketsViewGUI;
import ca.tweetzy.markets.gui.shared.view.requests.RequestsGUI;
import ca.tweetzy.markets.gui.user.BankGUI;
import ca.tweetzy.markets.gui.user.OffersGUI;
import ca.tweetzy.markets.gui.user.OfflinePaymentsGUI;
import ca.tweetzy.markets.gui.user.market.MarketOverviewGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class MarketsMainGUI extends MarketsBaseGUI {

	private final Player player;

	public MarketsMainGUI(@NonNull final Player player) {
		super(null, player, TranslationManager.string(player, Translations.GUI_MAIN_VIEW_TITLE), Settings.GUI_MAIN_VIEW_ROWS.getInt());
		this.player = player;
		setDefaultItem(QuickItem.bg(Settings.GUI_MAIN_VIEW_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected void draw() {
		final Market playerMarket = Markets.getMarketManager().getByOwner(this.player.getUniqueId());

		// global markets
		setButton(Settings.GUI_MAIN_VIEW_ITEMS_ALL_MARKETS_SLOT.getInt(),
				QuickItem
						.of(Settings.GUI_MAIN_VIEW_ITEMS_ALL_MARKETS.getItemStack())
						.hideTags(true)
						.name(TranslationManager.string(this.player, Translations.GUI_MAIN_VIEW_ITEMS_GLOBAL_NAME))
						.lore(TranslationManager.list(this.player, Translations.GUI_MAIN_VIEW_ITEMS_GLOBAL_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
						.make(), click -> click.manager.showGUI(click.player, new AllMarketsViewGUI(new MarketsMainGUI(click.player), click.player)));

		// your market
		setButton(Settings.GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_SLOT.getInt(),
				QuickItem
						.of(this.player)
						.hideTags(true)
						.name(TranslationManager.string(player, Translations.GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_NAME))
						.lore(playerMarket == null ? TranslationManager.list(player, Translations.GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_CREATE, "market_cost", String.format("%,.2f", Settings.CREATION_COST_COST.getDouble())) : TranslationManager.list(player, Translations.GUI_MAIN_VIEW_ITEMS_YOUR_MARKET_LORE_VIEW))
						.make(), click -> {

					if (playerMarket == null) {
						if (!Settings.ALLOW_ANYONE_TO_CREATE_MARKET.getBoolean() && !click.player.hasPermission("markets.createmarket")) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_ALLOWED_TO_CREATE));
							return;
						}

						Markets.getMarketManager().create(this.player, created -> {
							if (created)
								click.manager.showGUI(click.player, new MarketsMainGUI(click.player));
						});
						return;
					}

					// open market
					click.manager.showGUI(click.player, new MarketOverviewGUI(click.player, playerMarket));
				});

		// requests
		setButton(Settings.ALLOW_REQUESTS.getBoolean() ? Settings.GUI_MAIN_VIEW_ITEMS_REQUESTS_SLOT.getInt() : -1,
				QuickItem
						.of(Settings.GUI_MAIN_VIEW_ITEMS_REQUESTS.getItemStack())
						.hideTags(true)
						.name(TranslationManager.string(this.player, Translations.GUI_MAIN_VIEW_ITEMS_REQUESTS_NAME))
						.lore(TranslationManager.list(this.player, Translations.GUI_MAIN_VIEW_ITEMS_REQUESTS_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
						.make(), click -> click.manager.showGUI(click.player, new RequestsGUI(new MarketsMainGUI(click.player), click.player, true)));

		// payments
		setButton(Settings.GUI_MAIN_VIEW_ITEMS_PAYMENTS_SLOT.getInt(),
				QuickItem
						.of(Settings.GUI_MAIN_VIEW_ITEMS_PAYMENTS.getItemStack())
						.hideTags(true).name(TranslationManager.string(this.player, Translations.GUI_MAIN_VIEW_ITEMS_PAYMENTS_NAME))
						.lore(TranslationManager.list(this.player, Translations.GUI_MAIN_VIEW_ITEMS_PAYMENTS_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
						.make(), click -> click.manager.showGUI(click.player, new OfflinePaymentsGUI(new MarketsMainGUI(click.player), click.player)));

		// bank
		setButton(Settings.ALLOW_BANK.getBoolean() ? Settings.GUI_MAIN_VIEW_ITEMS_BANK_SLOT.getInt() : -1,
				QuickItem
						.of(Settings.GUI_MAIN_VIEW_ITEMS_BANK.getItemStack())
						.hideTags(true)
						.name(TranslationManager.string(this.player, Translations.GUI_MAIN_VIEW_ITEMS_BANK_NAME))
						.lore(TranslationManager.list(this.player, Translations.GUI_MAIN_VIEW_ITEMS_BANK_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
						.make(), click -> click.manager.showGUI(click.player, new BankGUI(new MarketsMainGUI(click.player), click.player)));

		// offers
		setButton(Settings.GUI_MAIN_VIEW_ITEMS_OFFERS_SLOT.getInt(),
				QuickItem
						.of(Settings.GUI_MAIN_VIEW_ITEMS_OFFERS.getItemStack())
						.hideTags(true)
						.name(TranslationManager.string(this.player, Translations.GUI_MAIN_VIEW_ITEMS_OFFERS_NAME))
						.lore(TranslationManager.list(this.player, Translations.GUI_MAIN_VIEW_ITEMS_OFFERS_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
						.make(), click -> click.manager.showGUI(click.player, new OffersGUI(new MarketsMainGUI(click.player), click.player)));

	}
}
