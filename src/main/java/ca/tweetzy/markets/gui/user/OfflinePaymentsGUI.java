package ca.tweetzy.markets.gui.user;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.TimeUtil;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.currency.Payment;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class OfflinePaymentsGUI extends MarketsPagedGUI<Payment> {

	private final Player player;

	public OfflinePaymentsGUI(Gui parent, @NonNull final Player player) {
		super(parent, player, TranslationManager.string(player, Translations.GUI_OFFLINE_PAYMENTS_TITLE), 6, Markets.getOfflineItemPaymentManager().getPaymentsFor(player.getUniqueId()));
		this.player = player;
		setDefaultItem(QuickItem.bg(Settings.GUI_OFFLINE_PAYMENTS_BACKGROUND.getItemStack()));
		draw();
	}

	@Override
	protected ItemStack makeDisplayItem(Payment payment) {
		return QuickItem
				.of(payment.getCurrency())
				.lore(TranslationManager.list(
						this.player,
						Translations.GUI_OFFLINE_PAYMENTS_ITEMS_PROFILE_LORE,
						"payment_total", payment.getAmount(),
						"payment_reason", payment.getReason(),
						"payment_date", TimeUtil.convertToReadableDate(payment.getTimeCreated(), Settings.DATETIME_FORMAT.getString()),
						"left_click", TranslationManager.string(Translations.MOUSE_LEFT_CLICK)
				))
				.make();
	}

	@Override
	protected void onClick(Payment payment, GuiClickEvent click) {
		payment.unStore(result -> {
			if (result == SynchronizeResult.FAILURE) return;
			click.manager.showGUI(click.player, new OfflinePaymentsGUI(this.parent, click.player));
			Markets.getCurrencyManager().deposit(click.player, payment.getCurrency(), (int) payment.getAmount());
		});
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(5);
	}
}
