package ca.tweetzy.markets.gui.shared.selector;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.MaterialPickerGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.ChatUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public final class ItemSelectorGUI extends MaterialPickerGUI {

	private final boolean enableAir;

	public ItemSelectorGUI(Gui parent, boolean enableAir, @NonNull BiConsumer<GuiClickEvent, ItemStack> selected) {
		super(
				parent,
				TranslationManager.string(Translations.GUI_MATERIAL_PICKER_TITLE),
				null,
				TranslationManager.string(Translations.PROMPT_MATERIAL_PICKER_TITLE),
				TranslationManager.string(Translations.PROMPT_MATERIAL_PICKER_SUBTITLE),
				selected);
		this.enableAir = enableAir;
		draw();
	}

	@Override
	protected void drawExtra() {
		if (this.enableAir) 
			setButton(getRows() -1, 2, QuickItem
					.of(Settings.GUI_MATERIAL_PICKER_ITEMS_AIR.getItemStack())
					.name(TranslationManager.string(Translations.GUI_MATERIAL_PICKER_ITEMS_AIR_NAME))
					.lore(TranslationManager.list(Translations.GUI_MATERIAL_PICKER_ITEMS_AIR_LORE))
					.make(), click -> {
				this.selected.accept(click, CompMaterial.AIR.parseItem());
			});
	}

	@Override
	protected ItemStack buildSearchButton() {
		return QuickItem
				.of(Settings.GUI_MATERIAL_PICKER_ITEMS_SEARCH.getItemStack())
				.name(TranslationManager.string(Translations.GUI_MATERIAL_PICKER_ITEMS_SEARCH_NAME))
				.lore(TranslationManager.list(Translations.GUI_MATERIAL_PICKER_ITEMS_SEARCH_LORE))
				.make();
	}

	@Override
	protected ItemStack buildResetButton() {
		return QuickItem
				.of(Settings.GUI_MATERIAL_PICKER_ITEMS_RESET.getItemStack())
				.name(TranslationManager.string(Translations.GUI_MATERIAL_PICKER_ITEMS_CLEAR_NAME))
				.lore(TranslationManager.list(Translations.GUI_MATERIAL_PICKER_ITEMS_CLEAR_LORE))
				.make();
	}

	@Override
	protected ItemStack buildIcon(@NotNull ItemStack itemStack) {
		return QuickItem
				.of(itemStack)
				.name(TranslationManager.string(Translations.GUI_MATERIAL_PICKER_ITEMS_ITEM_NAME, "item_name", ChatUtil.capitalizeFully(itemStack.getType())))
				.lore(TranslationManager.list(Translations.GUI_MATERIAL_PICKER_ITEMS_ITEM_LORE))
				.make();
	}
}
