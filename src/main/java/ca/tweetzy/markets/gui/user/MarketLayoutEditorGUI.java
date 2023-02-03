package ca.tweetzy.markets.gui.user;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.PagedGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.api.market.Layout;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketLayoutType;
import ca.tweetzy.markets.gui.user.market.MarketSettingsGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class MarketLayoutEditorGUI extends PagedGUI<Integer> {

	private final Player player;
	private final Market market;
	private final Layout layout;
	private final MarketLayoutType layoutType;

	public MarketLayoutEditorGUI(@NonNull final Player player, @NonNull final Market market, @NonNull final MarketLayoutType layoutType) {
		super(new MarketSettingsGUI(player, market), TranslationManager.string(player, layoutType == MarketLayoutType.HOME ? Translations.GUI_LAYOUT_EDITOR_TITLE_HOME : Translations.GUI_LAYOUT_EDITOR_TITLE_CATEGORY), 6, IntStream.rangeClosed(0, 53).boxed().collect(Collectors.toList()));
		this.player = player;
		this.market = market;
		this.layout = layoutType == MarketLayoutType.HOME ? market.getHomeLayout() : market.getCategoryLayout();
		this.layoutType = layoutType;

		setAcceptsItems(true);
		setOnClose(close -> this.market.sync(null));
		draw();
	}

	@Override
	protected void drawAdditional() {

		// controls
		setItem(this.layout.getExitButtonSlot(), getBackButton());
		setItem(this.layout.getPrevPageButtonSlot(), getPreviousButton());
		setItem(this.layout.getNextPageButtonSlot(), getNextButton());

		setItem(this.layout.getOwnerProfileSlot(), QuickItem
				.of(this.player)
				.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_PROFILE_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_PROFILE_LORE, "market_owner", this.player.getName()))
				.make());

		setItem(this.layout.getReviewButtonSlot(), QuickItem
				.of(Settings.GUI_LAYOUT_EDITOR_ITEMS_REVIEW.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_REVIEW_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_REVIEW_LORE))
				.make());

		setItem(this.layout.getSearchButtonSlot(), QuickItem
				.of(Settings.GUI_LAYOUT_EDITOR_ITEMS_SEARCH.getItemStack())
				.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_SEARCH_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_SEARCH_LORE))
				.make());

	}

	@Override
	protected ItemStack makeDisplayItem(Integer slot) {
		if (this.layout.getFillSlots().contains(slot))
			return QuickItem
					.of(CompMaterial.LIME_STAINED_GLASS_PANE)
					.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_FILL_SLOT_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_FILL_SLOT_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)))
					.make();

		if (this.layout.getDecoration().containsKey(slot))
			return QuickItem
					.of(this.market.getHomeLayout().getDecoration().get(slot))
					.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_DECO_SLOT_NAME))
					.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_DECO_SLOT_LORE,
							"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
							"right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
					))
					.make();

		return QuickItem
				.of(CompMaterial.WHITE_STAINED_GLASS_PANE)
				.name(TranslationManager.string(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_EMPTY_SLOT_NAME))
				.lore(TranslationManager.list(this.player, Translations.GUI_LAYOUT_EDITOR_ITEMS_EMPTY_SLOT_LORE,
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
						"right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
				))
				.make();
	}

	@Override
	protected void onClick(Integer slot, GuiClickEvent click) {
		// fill slots
		if (this.layout.getFillSlots().contains(slot)) {
			// prevent all slots from being removed from fill
			if (this.layout.getFillSlots().size() == 1) {
				Common.tell(click.player, TranslationManager.string(click.player, Translations.ONE_FILL_SLOT_REQUIRED));
				return;
			}

			this.layout.getFillSlots().remove(slot);
			draw();
			return;
		}

		// empty slots
		if (isEmptySlot(slot)) {
			if (click.clickType == ClickType.LEFT) {
				this.layout.getFillSlots().add(slot);
				draw();
			}

			if (click.clickType == ClickType.RIGHT) {

				final ItemStack cursor = click.cursor;
				if (cursor != null && cursor.getType() != CompMaterial.AIR.parseMaterial()) {
					final ItemStack newIcon = cursor.clone();
					newIcon.setAmount(1);

					this.layout.getDecoration().put(slot, newIcon);
					draw();
					return;
				}

				click.manager.showGUI(click.player, new LayoutControlPickerGUI(this, click.player, selectedControl -> {
					switch (selectedControl) {
						case EXIT_BACK_BUTTON -> this.layout.setExitButtonSlot(slot);
						case PROFILE_BUTTON -> this.layout.setOwnerProfileSlot(slot);
						case PREVIOUS_PAGE_BUTTON -> this.layout.setPrevPageButtonSlot(slot);
						case NEXT_PAGE_BUTTON -> this.layout.setNextPageButtonSlot(slot);
						case SEARCH_BUTTON -> this.layout.setSearchButtonSlot(slot);
						case REVIEW_BUTTON -> this.layout.setReviewButtonSlot(slot);
					}

					this.layout.getFillSlots().remove(slot);
					click.manager.showGUI(click.player, new MarketLayoutEditorGUI(this.player, this.market, this.layoutType));
				}));
			}
			return;
		}

		if (this.layout.getDecoration().containsKey(slot)) {
			if (click.clickType == ClickType.LEFT) {
				this.layout.getDecoration().remove(slot);
				draw();
			}

			if (click.clickType == ClickType.RIGHT) {

				final ItemStack cursor = click.cursor;
				if (cursor != null && cursor.getType() != CompMaterial.AIR.parseMaterial()) {
					final ItemStack newIcon = cursor.clone();
					newIcon.setAmount(1);

					this.layout.getDecoration().put(slot, newIcon);
					draw();
					return;
				}
			}
		}
	}

	@Override
	protected List<Integer> fillSlots() {
		return IntStream.rangeClosed(0, 53).boxed().collect(Collectors.toList());
	}

	private boolean isEmptySlot(final int slot) {
		return !this.layout.getFillSlots().contains(slot)
				&& !this.layout.getDecoration().containsKey(slot)
				&& this.layout.getExitButtonSlot() != slot
				&& this.layout.getNextPageButtonSlot() != slot
				&& this.layout.getPrevPageButtonSlot() != slot
				&& this.layout.getOwnerProfileSlot() != slot;
	}
}
