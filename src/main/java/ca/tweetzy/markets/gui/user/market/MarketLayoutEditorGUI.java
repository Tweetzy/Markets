package ca.tweetzy.markets.gui.user.market;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.PagedGUI;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.markets.api.market.Layout;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketLayoutType;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class MarketLayoutEditorGUI extends PagedGUI<Integer> {

	private final Player player;
	private final Market market;
	private final Layout layout;

	public MarketLayoutEditorGUI(@NonNull final Player player, @NonNull final Market market, @NonNull final MarketLayoutType layoutType) {
		super(new MarketSettingsGUI(player, market), "Market Layout Edit", 6, IntStream.rangeClosed(0, 53).boxed().collect(Collectors.toList()));
		this.player = player;
		this.market = market;
		this.layout = layoutType == MarketLayoutType.HOME ? market.getHomeLayout() : market.getCategoryLayout();

		draw();
	}

	@Override
	protected void drawAdditional() {
	}

	@Override
	protected ItemStack makeDisplayItem(Integer slot) {
		QuickItem quickItem;

		if (this.layout.getFillSlots().contains(slot))
			quickItem = QuickItem.of(CompMaterial.LIME_STAINED_GLASS_PANE);
		else if (this.layout.getDecoration().containsKey(slot))
			quickItem = QuickItem.of(this.market.getHomeLayout().getDecoration().get(slot));
		else
			quickItem = QuickItem.of(CompMaterial.WHITE_STAINED_GLASS_PANE);

		return quickItem.make();
	}

	@Override
	protected void onClick(Integer object, GuiClickEvent clickEvent) {

	}

	@Override
	protected List<Integer> fillSlots() {
		return IntStream.rangeClosed(0, 53).boxed().collect(Collectors.toList());
	}
}
