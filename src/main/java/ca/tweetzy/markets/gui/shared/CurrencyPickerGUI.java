package ca.tweetzy.markets.gui.shared;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.gui.template.MaterialPickerGUI;
import ca.tweetzy.flight.gui.template.PagedGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.api.currency.IconableCurrency;
import ca.tweetzy.markets.impl.currency.FundsCurrency;
import ca.tweetzy.markets.impl.currency.ItemCurrency;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public final class CurrencyPickerGUI extends PagedGUI<AbstractCurrency> {

	private final Player player;
	private final BiConsumer<AbstractCurrency, ItemStack> selectedCurrency;

	public CurrencyPickerGUI(final Gui parent, @NonNull final Player player, @NonNull final BiConsumer<AbstractCurrency, ItemStack> selectedCurrency) {
		super(parent, TranslationManager.string(player, Translations.GUI_CURRENCY_PICKER_TITLE), 6, Markets.getCurrencyManager().getManagerContent());
		this.player = player;
		this.selectedCurrency = selectedCurrency;
		setAcceptsItems(true);
		draw();
	}

	@Override
	protected void drawAdditional() {
		// custom item
		setButton(getRows() - 1, 4, QuickItem
				.of(CompMaterial.HOPPER)
				.name(TranslationManager.string(player, Translations.GUI_CURRENCY_PICKER_ITEMS_CUSTOM_CURRENCY_NAME))
				.lore(Replacer.replaceVariables(
						TranslationManager.list(player, Translations.GUI_CURRENCY_PICKER_ITEMS_CUSTOM_CURRENCY_LORE),
						"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK),
						"right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK)
				)).make(), click -> {

			if (click.clickType == ClickType.RIGHT) {
				click.manager.showGUI(click.player, new MaterialPickerGUI(this, null, null, (event, selected) -> {
					if (selected != null) {
						this.selectedCurrency.accept(new ItemCurrency(), selected.parseItem());
					}
				}));
			}

			if (click.clickType == ClickType.LEFT) {
				final ItemStack cursor = click.cursor;
				if (cursor != null && cursor.getType() != CompMaterial.AIR.parseMaterial()) {

					final ItemStack currency = cursor.clone();
					currency.setAmount(1);

					this.selectedCurrency.accept(new ItemCurrency(), currency);
				}
			}
		});
	}

	@Override
	protected ItemStack makeDisplayItem(AbstractCurrency currency) {
		QuickItem quickItem = QuickItem.of(CompMaterial.PAPER);

		if (currency instanceof final IconableCurrency iconableCurrency)
			quickItem = QuickItem.of(iconableCurrency.getIcon());

		if (currency instanceof final FundsCurrency fundsCurrency) {
			quickItem.name(fundsCurrency.getDisplayName());
		} else {
			quickItem.name(currency.getCurrencyName().equalsIgnoreCase("vault") ? "&e&LDefault" : "&e" + currency.getCurrencyName());
		}

		quickItem.lore(Replacer.replaceVariables(List.of(
				"&7Owning Plugin&f: &e%currency_owning_plugin%",
				"",
				"&a&l%left_click% &7to select this currency"
		), "currency_owning_plugin", currency.getOwningPlugin(), "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)));

		return quickItem.make();
	}

	@Override
	protected void onClick(AbstractCurrency currency, GuiClickEvent event) {
		this.selectedCurrency.accept(currency, null);
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(6);
	}
}
