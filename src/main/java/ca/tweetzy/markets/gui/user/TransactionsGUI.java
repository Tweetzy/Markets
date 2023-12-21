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

import java.util.List;

public final class TransactionsGUI extends MarketsPagedGUI<Transaction> {

	private final Player player;

	public TransactionsGUI(Gui parent, @NonNull final Player player) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_TRANSACTIONS_TITLE), 6, Markets.getTransactionManager().getOfflineTransactionsFor(player.getUniqueId()));
		this.player = player;
		setAcceptsItems(true);
		setDefaultItem(QuickItem.bg(Settings.GUI_TRANSACTIONS_BACKGROUND.getItemStack()));

		draw();
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
