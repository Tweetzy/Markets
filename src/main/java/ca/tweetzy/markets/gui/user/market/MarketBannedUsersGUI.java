package ca.tweetzy.markets.gui.user.market;

import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.gui.template.PagedGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.gui.shared.selector.PlayerPickerGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public final class MarketBannedUsersGUI extends MarketsPagedGUI<UUID> {

	private final Player player;
	private final Market market;


	public MarketBannedUsersGUI(@NonNull final Player player, @NonNull final Market market) {
		super(new MarketSettingsGUI(player, market), player, TranslationManager.string(player, Translations.GUI_MARKET_BANNED_USERS_TITLE), 6, market.getBannedUsers());
		this.player = player;
		this.market = market;
		draw();
	}

	@Override
	protected void drawAdditional() {
		// new ban button
		setButton(getRows() - 1, 4, QuickItem
				.of(Settings.GUI_MARKET_BANNED_USERS_ITEMS_NEW_BAN.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_BANNED_USERS_ITEMS_NEW_BAN_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_BANNED_USERS_ITEMS_NEW_BAN_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make(), click -> click.manager.showGUI(click.player, new PlayerPickerGUI(this, this.player, this.market.getBannedUsers(), selected -> {

			if (selected != null) {
				if (!this.market.getBannedUsers().contains(selected.getUniqueId())) {
					this.market.getBannedUsers().add(selected.getUniqueId());

					this.market.sync(result -> {
						if (result == SynchronizeResult.FAILURE)
							this.market.getBannedUsers().remove(selected.getUniqueId());

						click.manager.showGUI(click.player, new MarketBannedUsersGUI(this.player, this.market));

					});
				}
			}
		})));
	}

	@Override
	protected ItemStack makeDisplayItem(UUID uuid) {
		final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

		return QuickItem
				.of(offlinePlayer)
				.name(TranslationManager.string(this.player, Translations.GUI_MARKET_BANNED_USERS_ITEMS_PLAYER_NAME, "player_name", offlinePlayer.getName()))
				.lore(TranslationManager.list(this.player, Translations.GUI_MARKET_BANNED_USERS_ITEMS_PLAYER_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make();
	}

	@Override
	protected void onClick(UUID uuid, GuiClickEvent click) {
		if (!this.market.getBannedUsers().contains(uuid)) return;

		this.market.getBannedUsers().remove(uuid);

		this.market.sync(result -> {
			if (result == SynchronizeResult.FAILURE)
				this.market.getBannedUsers().add(uuid);
			else
				click.manager.showGUI(click.player, new MarketBannedUsersGUI(this.player, this.market));
		});
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(6);
	}
}
