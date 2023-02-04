package ca.tweetzy.markets.gui;

import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.template.PagedGUI;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class MarketsPagedGUI<T> extends PagedGUI<T> {

	@Getter
	protected final Player player;

	public MarketsPagedGUI(Gui parent, @NonNull final Player player, @NonNull String title, int rows, @NonNull List<T> items) {
		super(parent, title, rows, items);
		this.player = player;
	}

	public MarketsPagedGUI(@NonNull final Player player, @NonNull String title, int rows, @NonNull List<T> items) {
		super(title, rows, items);
		this.player = player;
	}
}
