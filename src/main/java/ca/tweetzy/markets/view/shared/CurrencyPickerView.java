package ca.tweetzy.markets.view.shared;

import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.PagedGUI;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.currency.AbstractCurrency;
import org.bukkit.inventory.ItemStack;

public final class CurrencyPickerView extends PagedGUI<AbstractCurrency> {

	public CurrencyPickerView() {
		super(null, "&ePick a Currency", 6, Markets.getCurrencyManager().getManagerContent());
		draw();
	}

	@Override
	protected void drawAdditional() {

	}

	@Override
	protected ItemStack makeDisplayItem(AbstractCurrency currency) {


		return null;
	}

	@Override
	protected void onClick(AbstractCurrency currency, GuiClickEvent event) {

	}
}
