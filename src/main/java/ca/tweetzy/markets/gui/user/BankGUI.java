package ca.tweetzy.markets.gui.user;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.MathUtil;
import ca.tweetzy.flight.utils.PlayerUtil;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.BankEntry;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class BankGUI extends MarketsPagedGUI<BankEntry> {

	private final Player player;

	public BankGUI(Gui parent, @NonNull final Player player) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_BANK_TITLE), 6, Markets.getBankManager().getEntriesByPlayer(player.getUniqueId()));
		this.player = player;
		setAcceptsItems(true);
		setDefaultItem(QuickItem.bg(Settings.GUI_BANK_BACKGROUND.getItemStack()));

		draw();
	}

	@Override
	protected void drawAdditional() {
		setButton(getRows() - 1, 4, QuickItem.of(Settings.GUI_BANK_ITEMS_ADD.getItemStack()).name(TranslationManager.string(this.player, Translations.GUI_BANK_ITEMS_ADD_NAME)).lore(TranslationManager.list(this.player, Translations.GUI_BANK_ITEMS_ADD_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK))).make(), click -> {

			final ItemStack cursor = click.cursor;
			if (cursor != null && cursor.getType() != CompMaterial.AIR.parseMaterial()) {

				final ItemStack currency = cursor.clone();
				final BankEntry locatedEntry = Markets.getBankManager().getEntryByPlayer(click.player.getUniqueId(), currency);

				if (locatedEntry != null) {
					locatedEntry.setQuantity(locatedEntry.getQuantity() + currency.getAmount());
					locatedEntry.sync(result -> {
						if (result == SynchronizeResult.SUCCESS) {
							click.player.setItemOnCursor(CompMaterial.AIR.parseItem());
							updateAndRedraw();
						}
					});

					return;
				}

				Markets.getBankManager().create(click.player, currency, currency.getAmount(), wasCreated -> {
					click.player.setItemOnCursor(CompMaterial.AIR.parseItem());
					updateAndRedraw();
				});
			}
		});
	}

	@Override
	protected ItemStack makeDisplayItem(BankEntry bankEntry) {
		return QuickItem.of(bankEntry.getItem()).lore(TranslationManager.list(this.player, Translations.GUI_BANK_ITEMS_ENTRY_LORE, "entry_quantity", bankEntry.getQuantity(), "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK), "right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK))).make();
	}

	@Override
	protected void onClick(BankEntry bankEntry, GuiClickEvent click) {
		if (click.clickType == ClickType.LEFT) {
			deleteAndGiveEntry(bankEntry, click);
		}

		if (click.clickType == ClickType.RIGHT)
			new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(click.player, Translations.PROMPT_WITHDRAW_ENTRY_TITLE), TranslationManager.string(click.player, Translations.PROMPT_WITHDRAW_ENTRY_SUBTITLE)) {

				@Override
				public void onExit(Player player) {
					click.manager.showGUI(click.player, BankGUI.this);
				}

				@Override
				public boolean onResult(String string) {
					string = ChatColor.stripColor(string);

					if (!MathUtil.isInt(string)) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_A_NUMBER, "value", string));
						return false;
					}

					final int withdrawAmount = Integer.parseInt(string);

					if (withdrawAmount <= 0) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.MUST_BE_HIGHER_THAN_ZERO, "value", string));
						return false;
					}

					if (withdrawAmount == bankEntry.getQuantity()) {
						deleteAndGiveEntry(bankEntry, click);
						return true;
					}

					if (withdrawAmount > bankEntry.getQuantity()) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.INSUFFICIENT_ENTRY_AMOUNT));
						return false;
					}

					bankEntry.setQuantity(bankEntry.getQuantity() - withdrawAmount);
					bankEntry.sync(result -> {
						if (result == SynchronizeResult.FAILURE) return;
						for (int i = 0; i < withdrawAmount; i++)
							PlayerUtil.giveItem(click.player, bankEntry.getItem());

						click.manager.showGUI(click.player, new BankGUI(BankGUI.this.parent, click.player));
					});
					return true;
				}
			};
	}

	private void deleteAndGiveEntry(@NonNull final BankEntry bankEntry, @NonNull final GuiClickEvent click) {
		bankEntry.unStore(result -> {
			if (result == SynchronizeResult.FAILURE) return;
			for (int i = 0; i < bankEntry.getQuantity(); i++)
				PlayerUtil.giveItem(click.player, bankEntry.getItem());

			updateAndRedraw();
		});
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(5);
	}

	private void updateAndRedraw() {
		updateItems(Markets.getBankManager().getEntriesByPlayer(player.getUniqueId()));
		draw();
	}
}
