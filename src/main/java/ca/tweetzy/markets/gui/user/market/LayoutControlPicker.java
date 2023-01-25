package ca.tweetzy.markets.gui.user.market;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.PagedGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.api.market.LayoutControl;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.function.Consumer;

public final class LayoutControlPicker extends PagedGUI<LayoutControl> {

	private final Player player;
	private final Consumer<LayoutControl> selectedControl;

	public LayoutControlPicker(@NonNull final Gui parent, @NonNull final Player player, @NonNull final Consumer<LayoutControl> selectedControl) {
		super(parent, TranslationManager.string(player, Translations.GUI_LAYOUT_CONTROL_PICKER_TITLE), 4, Arrays.asList(LayoutControl.values()));
		this.player = player;
		this.selectedControl = selectedControl;
		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(LayoutControl layoutControl) {
		return switch (layoutControl) {
			case EXIT_BACK_BUTTON -> QuickItem
					.of(Settings.GUI_LAYOUT_CONTROL_PICKER_ITEMS_EXIT.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_EXIT_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_EXIT_LORE))
					.make();

			case PROFILE_BUTTON -> QuickItem
					.of(this.player)
					.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_PROFILE_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_PROFILE_LORE))
					.make();

			case PREVIOUS_PAGE_BUTTON -> QuickItem
					.of(Settings.GUI_LAYOUT_CONTROL_PICKER_ITEMS_PREV_PAGE.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_BACK_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_BACK_LORE))
					.make();
			case NEXT_PAGE_BUTTON -> QuickItem
					.of(Settings.GUI_LAYOUT_CONTROL_PICKER_ITEMS_NEXT_PAGE.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_NEXT_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_NEXT_LORE))
					.make();

			case SEARCH_BUTTON -> QuickItem
					.of(Settings.GUI_LAYOUT_CONTROL_PICKER_ITEMS_SEARCH.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_SEARCH_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_SEARCH_LORE))
					.make();

			case REVIEW_BUTTON -> QuickItem
					.of(Settings.GUI_LAYOUT_CONTROL_PICKER_ITEMS_REVIEW.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_REVIEW_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_CONTROL_PICKER_ITEMS_REVIEW_LORE))
					.make();
		};
	}

	@Override
	protected void onClick(LayoutControl layoutControl, GuiClickEvent click) {
		this.selectedControl.accept(layoutControl);
	}
}
