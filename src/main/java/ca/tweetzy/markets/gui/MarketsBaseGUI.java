package ca.tweetzy.markets.gui;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.template.BaseGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class MarketsBaseGUI extends BaseGUI {

	@Getter
	protected final Player player;

	public MarketsBaseGUI(Gui parent, @NonNull final Player player, @NonNull String title, int rows) {
		super(parent, title, rows);
		this.player = player;
	}

	public MarketsBaseGUI(Gui parent, @NonNull final Player player, @NonNull String title) {
		super(parent, title);
		this.player = player;
	}

	public MarketsBaseGUI(@NonNull final Player player, @NonNull String title) {
		super(title);
		this.player = player;
	}

	@Override
	protected ItemStack getBackButton() {
		return QuickItem
				.of(Settings.GUI_SHARED_ITEMS_BACK_BUTTON.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_BACK_BUTTON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_BACK_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make();
	}

	@Override
	protected ItemStack getExitButton() {
		return QuickItem
				.of(Settings.GUI_SHARED_ITEMS_EXIT_BUTTON.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_EXIT_BUTTON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_EXIT_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make();
	}

	@Override
	protected ItemStack getPreviousPageButton() {
		return QuickItem
				.of(Settings.GUI_SHARED_ITEMS_PREVIOUS_BUTTON.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_PREVIOUS_BUTTON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_PREVIOUS_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make();
	}

	@Override
	protected ItemStack getNextPageButton() {
		return QuickItem
				.of(Settings.GUI_SHARED_ITEMS_NEXT_BUTTON.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_NEXT_BUTTON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_NEXT_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make();
	}
}
