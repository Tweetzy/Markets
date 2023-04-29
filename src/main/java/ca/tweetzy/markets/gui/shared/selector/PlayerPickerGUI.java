package ca.tweetzy.markets.gui.shared.selector;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class PlayerPickerGUI extends MarketsPagedGUI<Player> {

	private final Player player;
	private final Consumer<Player> selectedPlayer;

	public PlayerPickerGUI(final Gui parent, @NonNull final Player player, List<UUID> ignoredUsers, @NonNull final Consumer<Player> selectedPlayer) {
		super(
				parent,
				player,
				TranslationManager.string(player, Translations.GUI_USER_PICKER_TITLE),
				6,
				ignoredUsers != null ? Bukkit.getOnlinePlayers().stream().filter(user -> !user.getUniqueId().equals(player.getUniqueId()) && !ignoredUsers.contains(user.getUniqueId())).collect(Collectors.toList()) : Bukkit.getOnlinePlayers().stream().filter(user -> !user.getUniqueId().equals(player.getUniqueId())).collect(Collectors.toList())
		);

		this.player = player;
		this.selectedPlayer = selectedPlayer;
		setAcceptsItems(true);
		setDefaultItem(QuickItem.bg(Settings.GUI_PLAYER_PICKER_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(Player player) {
		QuickItem quickItem = QuickItem.of(player);
		return quickItem.make();
	}

	@Override
	protected void onClick(Player player, GuiClickEvent event) {
		this.selectedPlayer.accept(player);
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(6);
	}
}
