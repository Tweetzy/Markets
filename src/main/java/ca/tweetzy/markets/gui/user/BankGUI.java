package ca.tweetzy.markets.gui.user;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.markets.gui.MarketsBaseGUI;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;

public final class BankGUI extends MarketsBaseGUI {

	private final Player player;

	public BankGUI(Gui parent, @NonNull final Player player) {
		super(parent, player, "&eyour bank", 6);
		this.player = player;
		draw();
	}

	@Override
	protected void draw() {

		applyBackExit();
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(5);
	}
}
