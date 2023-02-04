package ca.tweetzy.markets.gui;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.template.BaseGUI;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

public abstract class MarketsBaseGUI extends BaseGUI {

	@Getter
	protected final Player player;

	public MarketsBaseGUI(Gui parent, @NonNull final Player player, @NonNull String title, int rows) {
		super(parent, title, rows);
		this.player = player;
	}

	public MarketsBaseGUI(Gui parent, @NonNull final Player player, @NonNull String title) {
		super(parent, title);
		this.player = player;
	}

	public MarketsBaseGUI(@NonNull final Player player, @NonNull String title) {
		super(title);
		this.player = player;
	}
}
