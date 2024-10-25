package ca.tweetzy.markets.gui.user;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.market.Transaction;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TransactionsGUI extends MarketsPagedGUI<Transaction> {

	private final Player player;
	private boolean viewAll;

	public TransactionsGUI(Gui parent, @NonNull final Player player, boolean viewAll) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_TRANSACTIONS_TITLE), 6, new ArrayList<>());
		this.player = player;
		this.viewAll = viewAll;
		setAcceptsItems(true);
		setDefaultItem(QuickItem.bg(Settings.GUI_TRANSACTIONS_BACKGROUND.getItemStack()));

		draw();
	}

	@Override
	protected void prePopulate() {
		if (this.viewAll) {
			this.items = new ArrayList<>(Markets.getTransactionManager().getManagerContent());
		} else {
			this.items = new ArrayList<>(Markets.getTransactionManager().getOfflineTransactionsFor(this.player.getUniqueId()));
		}

		this.items.sort(Comparator.comparing(Transaction::getTimeCreated).reversed());
	}

	@Override
	protected void drawFixed() {
		if (!Settings.USE_ADDITIONAL_CONFIRMS.getBoolean()) {
			setTransactionViewButton();
		} else {
			if (this.player.hasPermission("markets.viewalltransactions"))
				setTransactionViewButton();
		}
	}

	private void setTransactionViewButton() {
		setButton(getRows() - 1, 8, QuickItem
				.of(Settings.GUI_TRANSACTIONS_VIEW_ALL_ITEM.getItemStack())
				.name(TranslationManager.string(Translations.GUI_TRANSACTIONS_ITEMS_VIEW_ALL_NAME))
				.lore(TranslationManager.list(Translations.GUI_TRANSACTIONS_ITEMS_VIEW_ALL_LORE, "is_true", TranslationManager.string(this.viewAll ? Translations.TRUE : Translations.FALSE), "left_click", TranslationManager.string(Translations.MOUSE_LEFT_CLICK)))
				.make(), click -> {

			this.viewAll = !this.viewAll;
			draw();
		});
	}

	@Override
	protected ItemStack makeDisplayItem(Transaction transaction) {
		final ItemStack item = transaction.getItem();

		return QuickItem
				.of(item)
				.amount(Math.min(transaction.getQuantity(), item.getMaxStackSize()))
				.lore(TranslationManager.list(this.player, Translations.GUI_TRANSACTIONS_ITEMS_ENTRY_LORE,
						"item_quantity", transaction.getQuantity(),
						"market_item_price", transaction.getPrice(),
						"market_item_currency", transaction.getCurrency(),
						"buyer_name", transaction.getBuyerName(),
						"transaction_date", transaction.getFormattedDate()
				)).make();
	}

	@Override
	protected void onClick(Transaction transaction, GuiClickEvent click) {

	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(5);
	}
}
