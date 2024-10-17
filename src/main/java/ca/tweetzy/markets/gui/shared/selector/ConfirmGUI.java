package ca.tweetzy.markets.gui.shared.selector;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.gui.MarketsBaseGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public final class ConfirmGUI extends MarketsBaseGUI {

	private final Consumer<Boolean> confirmed;

	public ConfirmGUI(Gui parent, @NonNull Player player, @NonNull final Consumer<Boolean> confirmed) {
		super(parent, player, TranslationManager.string(Translations.GUI_CONFIRM_ACTION_TITLE), 3);
		this.confirmed = confirmed;
		setDefaultItem(QuickItem.bg(Settings.GUI_CONFIRM_ACTION_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected void draw() {

		for (int i = 1; i < 4; i++) {
			setButton(1, i, QuickItem
					.of(Settings.GUI_CONFIRM_ACTION_ITEMS_YES.getString())
					.name(TranslationManager.string(Translations.GUI_CONFIRM_ACTION_ITEMS_YES_NAME))
					.lore(TranslationManager.list(Translations.GUI_CONFIRM_ACTION_ITEMS_YES_LORE))
					.make(), click -> this.confirmed.accept(true));
		}

		for (int i = 5; i < 8; i++) {
			setButton(1, i, QuickItem
					.of(Settings.GUI_CONFIRM_ACTION_ITEMS_NO.getString())
					.name(TranslationManager.string(Translations.GUI_CONFIRM_ACTION_ITEMS_NO_NAME))
					.lore(TranslationManager.list(Translations.GUI_CONFIRM_ACTION_ITEMS_NO_LORE))
					.make(), click -> this.confirmed.accept(false));
		}
	}
}
