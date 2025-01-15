package ca.tweetzy.markets.gui.admin;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.MarketSortType;
import ca.tweetzy.markets.api.market.core.AbstractMarket;
import ca.tweetzy.markets.api.market.core.MarketUser;
import ca.tweetzy.markets.gui.MarketsBaseGUI;
import ca.tweetzy.markets.gui.shared.selector.ConfirmGUI;
import ca.tweetzy.markets.gui.user.market.MarketOverviewGUI;
import ca.tweetzy.markets.impl.MarketPlayer;
import ca.tweetzy.markets.impl.ServerMarket;
import ca.tweetzy.markets.impl.layout.HomeLayout;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class MarketsAdminGUI extends MarketsBaseGUI {

	public MarketsAdminGUI(@NonNull Player player) {
		super(null, player, TranslationManager.string(player, Translations.GUI_MAIN_ADMIN_TITLE), 3);
		draw();
	}

	@Override
	protected void draw() {
		ServerMarket adminMarket = Markets.getMarketManager().getServerMarket();

		setButton(1, 4, QuickItem
				.of(adminMarket != null ? adminMarket.getDynamicIcon() : CompMaterial.CHEST.parseItem())
				.name(TranslationManager.string(Translations.GUI_MAIN_ADMIN_ITEMS_ADMIN_MARKET))
				.lore(TranslationManager.list(adminMarket == null ? Translations.GUI_MAIN_ADMIN_ITEMS_ADMIN_MARKET_LORE_CREATE : Translations.GUI_MAIN_ADMIN_ITEMS_ADMIN_MARKET_LORE_VIEW))
				.make(), click -> {

			if (adminMarket != null) {
				click.manager.showGUI(click.player, new MarketOverviewGUI(click.player, adminMarket));
				return;
			}

			final AbstractMarket market = new ServerMarket(
					UUID.randomUUID(),
					UUID.fromString(Settings.SERVER_MARKET_UUID.getString()),
					Settings.NAME.getString(),
					TranslationManager.string(player, Translations.DEFAULTS_MARKET_DISPLAY_NAME, "player_name", Settings.NAME.getString()),
					TranslationManager.list(player, Translations.DEFAULTS_MARKET_DESCRIPTION),
					new ArrayList<>(),
					new ArrayList<>(),
					new ArrayList<>(),
					false,
					true,
					new HomeLayout(),
					new HomeLayout(),
					System.currentTimeMillis(),
					System.currentTimeMillis()
			);

			if (Settings.USE_ADDITIONAL_CONFIRMS.getBoolean()) {
				click.manager.showGUI(click.player, new ConfirmGUI(this, click.player, confirmed -> {
					if (confirmed)
						market.store(storedMarket -> {
							if (storedMarket != null) {
								Markets.getMarketManager().add(storedMarket);
								createAdminUser();
								click.manager.showGUI(click.player, new MarketsAdminGUI(click.player));
							}
						});
					else
						click.manager.showGUI(click.player, new MarketsAdminGUI(click.player));
				}));

			} else {
				market.store(storedMarket -> {
					if (storedMarket != null) {
						Markets.getMarketManager().add(storedMarket);
						createAdminUser();
						click.manager.showGUI(click.player, new MarketsAdminGUI(click.player));
					}
				});
			}
		});
	}

	private void createAdminUser() {
		if (Markets.getPlayerManager().get(UUID.fromString(Settings.SERVER_MARKET_UUID.getString())) == null) {
			final MarketUser marketUser = new MarketPlayer(
					UUID.fromString(Settings.SERVER_MARKET_UUID.getString()),
					null,
					Settings.NAME.getString(),
					List.of("Server market"),
					"english",
					"US",
					MarketSortType.ITEMS,
					System.currentTimeMillis()
			);

			marketUser.store(storedUser -> {
				if (storedUser != null) {
					Markets.getPlayerManager().add(storedUser.getUUID(), storedUser);
				}
			});
		}
	}
}
