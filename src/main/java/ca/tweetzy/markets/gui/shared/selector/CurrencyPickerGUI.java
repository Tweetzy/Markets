package ca.tweetzy.markets.gui.shared.selector;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.gui.template.MaterialPickerGUI;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.Replacer;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import ca.tweetzy.markets.api.currency.IconableCurrency;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.impl.currency.FundsCurrency;
import ca.tweetzy.markets.impl.currency.ItemCurrency;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public final class CurrencyPickerGUI extends MarketsPagedGUI<AbstractCurrency> {

	private final Player player;
	private final BiConsumer<AbstractCurrency, ItemStack> selectedCurrency;

	public CurrencyPickerGUI(final Gui parent, @NonNull final Player player, @NonNull final BiConsumer<AbstractCurrency, ItemStack> selectedCurrency) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_CURRENCY_PICKER_TITLE), Settings.CURRENCY_USE_ITEM_ONLY.getBoolean() ? 4 : 6, Markets.getCurrencyManager().getManagerContent().stream().filter(currency -> !currency.getOwningPlugin().equalsIgnoreCase("markets")).collect(Collectors.toList()));
		this.player = player;
		this.selectedCurrency = selectedCurrency;
		setAcceptsItems(true);
		setDefaultItem(QuickItem.bg(Settings.GUI_CURRENCY_PICKER_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected void drawFixed() {
		// custom item
		if (Settings.ALLOW_BANK.getBoolean())
			setButton(Settings.CURRENCY_USE_ITEM_ONLY.getBoolean() ? 1 : getRows() - 1, 4, QuickItem
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
							this.selectedCurrency.accept(new ItemCurrency(), selected);
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
			quickItem.name(currency.getCurrencyName().equalsIgnoreCase("vault") ? "&a" + Settings.CURRENCY_VAULT_SYMBOL.getString() : "&e" + currency.getCurrencyName());
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
