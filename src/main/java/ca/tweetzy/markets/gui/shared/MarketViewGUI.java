package ca.tweetzy.markets.gui.shared;

import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.PagedGUI;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.Market;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketViewGUI extends PagedGUI<Category> {

	private final Player player;
	private final Market market;

	public MarketViewGUI(@NonNull final Player player, @NonNull final Market market) {
		super(null, market.getDisplayName(), 6, market.getCategories());
		this.player = player;
		this.market = market;

		setDefaultItem(market.getHomeLayout().getBackgroundItem());

		draw();
	}

	@Override
	protected void drawAdditional() {
		// decorations
		this.market.getHomeLayout().getDecoration().forEach(this::setItem);
	}

	@Override
	protected ItemStack makeDisplayItem(Category category) {
		return QuickItem.of(category.getIcon()).name(category.getDisplayName()).lore(category.getDescription()).make();
	}

	@Override
	protected void onClick(Category category, GuiClickEvent click) {

	}

	@Override
	protected List<Integer> fillSlots() {
		return this.market.getHomeLayout().getFillSlots();
	}

	@Override
	protected int getPreviousButtonSlot() {
		return this.market.getHomeLayout().getPrevPageButtonSlot();
	}

	@Override
	protected int getNextButtonSlot() {
		return this.market.getHomeLayout().getNextPageButtonSlot();
	}

	@Override
	protected int getBackExitButtonSlot() {
		return this.market.getHomeLayout().getExitButtonSlot();
	}
}
