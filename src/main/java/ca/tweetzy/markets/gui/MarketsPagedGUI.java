package ca.tweetzy.markets.gui;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.BaseGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public abstract class MarketsPagedGUI<T> extends BaseGUI {

	@Getter
	protected final Player player;
	protected final Gui parent;
	protected List<T> items;

	public MarketsPagedGUI(Gui parent, @NonNull final Player player, @NonNull String title, int rows, @NonNull List<T> items) {
		super(parent, title, rows);
		this.parent = parent;
		this.player = player;
		this.items = items;
	}

	public MarketsPagedGUI(@NonNull final Player player, @NonNull String title, int rows, @NonNull List<T> items) {
		this(null, player, title, rows, items);
	}

	@Override
	protected void draw() {
		reset();
		populateItems();
		drawFixed();

		applyBackExit();
	}

	protected void prePopulate() {
	}

	protected void drawFixed() {
	}

	private void populateItems() {
		if (this.items != null) {
			this.fillSlots().forEach(slot -> setItem(slot, getDefaultItem()));
			prePopulate();

			final List<T> itemsToFill = this.items.stream().skip((page - 1) * (long) this.fillSlots().size()).limit(this.fillSlots().size()).collect(Collectors.toList());
			pages = (int) Math.max(1, Math.ceil(this.items.size() / (double) this.fillSlots().size()));

			setPrevPage(getPreviousButtonSlot(), getPreviousButton());
			setNextPage(getNextButtonSlot(), getNextButton());
			setOnPage(e -> draw());

			for (int i = 0; i < this.rows * 9; i++) {
				if (this.fillSlots().contains(i) && this.fillSlots().indexOf(i) < itemsToFill.size()) {
					final T object = itemsToFill.get(this.fillSlots().indexOf(i));
					setButton(i, this.makeDisplayItem(object), click -> this.onClick(object, click));
				}
			}
		}
	}

	protected abstract ItemStack makeDisplayItem(final T object);

	protected abstract void onClick(final T object, final GuiClickEvent clickEvent);

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
	protected ItemStack getPreviousButton() {
		return QuickItem
				.of(Settings.GUI_SHARED_ITEMS_PREVIOUS_BUTTON.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_PREVIOUS_BUTTON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_PREVIOUS_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make();
	}

	@Override
	protected ItemStack getNextButton() {
		return QuickItem
				.of(Settings.GUI_SHARED_ITEMS_NEXT_BUTTON.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_SHARED_ITEMS_NEXT_BUTTON_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_SHARED_ITEMS_NEXT_BUTTON_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
				.make();
	}

	@Override
	protected int getPreviousButtonSlot() {
		return 48;
	}

	@Override
	protected int getNextButtonSlot() {
		return 50;
	}
}
