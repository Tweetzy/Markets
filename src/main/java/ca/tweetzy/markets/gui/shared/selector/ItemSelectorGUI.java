package ca.tweetzy.markets.gui.shared.selector;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.gui.helper.InventorySafeMaterials;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.ChatUtil;
import ca.tweetzy.flight.utils.Filterer;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class ItemSelectorGUI extends MarketsPagedGUI<ItemStack> {

	private final String searchQuery;
	private final boolean enableAir;
	private final BiConsumer<GuiClickEvent, ItemStack> selected;

	public ItemSelectorGUI(Gui parent, @NonNull Player player, boolean enableAir, @NonNull BiConsumer<GuiClickEvent, ItemStack> selected) {
		this(parent, player, null, enableAir, selected);
	}

	private ItemSelectorGUI(Gui parent, @NonNull Player player, String searchQuery, boolean enableAir, @NonNull BiConsumer<GuiClickEvent, ItemStack> selected) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_MATERIAL_PICKER_TITLE), 6, buildMaterialsList(searchQuery));
		this.searchQuery = searchQuery;
		this.enableAir = enableAir;
		this.selected = selected;
		setDefaultItem(QuickItem.bg(Settings.GUI_MATERIAL_PICKER_BACKGROUND.getItemStack()));
		setAsync(true);
		draw();
	}

	private static List<ItemStack> buildMaterialsList(String searchQuery) {
		List<ItemStack> validMaterials = new ArrayList<>(InventorySafeMaterials.get().stream().map(CompMaterial::parseItem).toList());

		if (searchQuery != null && !searchQuery.isBlank()) {
			validMaterials = validMaterials.stream()
					.filter(mat -> Filterer.searchByItemInfo(searchQuery, mat))
					.collect(Collectors.toList());
		}
		return validMaterials;
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(5);
	}

	@Override
	protected void drawFixed() {
		// Search button (row 5, slot 4)
		setButton(49, QuickItem
				.of(Settings.GUI_MATERIAL_PICKER_ITEMS_SEARCH.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_MATERIAL_PICKER_ITEMS_SEARCH_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_MATERIAL_PICKER_ITEMS_SEARCH_LORE))
				.make(), click -> {
			click.gui.exit();
			new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(this.player, Translations.PROMPT_MATERIAL_PICKER_TITLE), TranslationManager.string(this.player, Translations.PROMPT_MATERIAL_PICKER_SUBTITLE)) {
				@Override
				public boolean onResult(String string) {
					if (string == null || string.isBlank()) return false;
					click.manager.showGUI(click.player, new ItemSelectorGUI(ItemSelectorGUI.this.parent, ItemSelectorGUI.this.player, string, ItemSelectorGUI.this.enableAir, ItemSelectorGUI.this.selected));
					return true;
				}

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(click.player, ItemSelectorGUI.this);
				}
			};
		});

		if (this.searchQuery != null) {
			setButton(52, QuickItem
					.of(Settings.GUI_MATERIAL_PICKER_ITEMS_RESET.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_MATERIAL_PICKER_ITEMS_CLEAR_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_MATERIAL_PICKER_ITEMS_CLEAR_LORE))
					.make(), click -> click.manager.showGUI(click.player, new ItemSelectorGUI(this.parent, this.player, null, this.enableAir, this.selected)));
		}

		if (this.enableAir) {
			setButton(47, QuickItem
					.of(Settings.GUI_MATERIAL_PICKER_ITEMS_AIR.getItemStack())
					.name(TranslationManager.string(this.player, Translations.GUI_MATERIAL_PICKER_ITEMS_AIR_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_MATERIAL_PICKER_ITEMS_AIR_LORE))
					.make(), click -> this.selected.accept(click, CompMaterial.AIR.parseItem()));
		}
	}

	@Override
	protected ItemStack makeDisplayItem(@NotNull ItemStack itemStack) {
		return QuickItem
				.of(itemStack)
				.name(TranslationManager.string(this.player, Translations.GUI_MATERIAL_PICKER_ITEMS_ITEM_NAME, "item_name", ChatUtil.capitalizeFully(itemStack.getType())))
				.lore(TranslationManager.list(this.player, Translations.GUI_MATERIAL_PICKER_ITEMS_ITEM_LORE))
				.make();
	}

	@Override
	protected void onClick(ItemStack object, GuiClickEvent click) {

		this.selected.accept(click, object);
	}
}
