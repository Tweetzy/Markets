package ca.tweetzy.markets.view.shared;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.template.PagedGUI;
import ca.tweetzy.flight.utils.QuickItem;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.Category;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.settings.Settings;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public final class MarketOverviewView extends PagedGUI<Category> {

	private final Player player;
	private final Market market;

	public MarketOverviewView(@NonNull final Player player, @NonNull final Market market) {
		super(new MarketsMainView(player), Settings.GUI_MARKET_OVERVIEW_TITLE.getString(), 6, market.getCategories());
		this.player = player;
		this.market = market;

		draw();
	}

	@Override
	protected void drawAdditional() {

		// name
		setButton(1, 1, QuickItem
				.of(CompMaterial.NAME_TAG)
				.name("<GRADIENT:65B1B4>&LMarket Name</GRADIENT:2B6F8A>")
				.lore(
						"&7The display name of your market, this",
						"&7is what others will see in the search.",
						"",
						"&7Current&f: " + this.market.getDisplayName(),
						"",
						"&a&lClick &7to change display name"
				).make(), click -> new TitleInput(Markets.getInstance(), click.player, "<GRADIENT:65B1B4>&LMarket Name</GRADIENT:2B6F8A>", "&fEnter new name into chat") {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, MarketOverviewView.this);
			}

			@Override
			public boolean onResult(String string) {
				if (string.length() > 72) return false; // TODO tell them it's too long
				MarketOverviewView.this.market.setDisplayName(string);
				MarketOverviewView.this.market.sync(result -> {
					if (result == SynchronizeResult.SUCCESS)
						click.manager.showGUI(click.player, new MarketOverviewView(click.player, MarketOverviewView.this.market));
				});
				return true;
			}
		});

		// description
		setButton(2, 1, QuickItem
				.of(CompMaterial.ENCHANTED_BOOK)
				.name("<GRADIENT:65B1B4>&LMarket Description</GRADIENT:2B6F8A>")
				.lore(
						"&7A brief description of market, something",
						"&7to help catch someone's attention",
						"",
						"&7Current&f: ",
						this.market.getDescription().get(0),
						"",
						"&a&lClick &7to change description"
				).make(), click -> new TitleInput(Markets.getInstance(), click.player, "<GRADIENT:65B1B4>&LMarket Description</GRADIENT:2B6F8A>", "&fEnter new description into chat") {

			@Override
			public void onExit(Player player) {
				click.manager.showGUI(click.player, MarketOverviewView.this);
			}

			@Override
			public boolean onResult(String string) {
				MarketOverviewView.this.market.setDescription(List.of(string));
				MarketOverviewView.this.market.sync(result -> {
					if (result == SynchronizeResult.SUCCESS)
						click.manager.showGUI(click.player, new MarketOverviewView(click.player, MarketOverviewView.this.market));
				});
				return true;
			}
		});

		// settings button
		setButton(3, 1, QuickItem
				.of(CompMaterial.REPEATER)
				.name("<GRADIENT:65B1B4>&LMarket Settings</GRADIENT:2B6F8A>")
				.lore("&7This is used to configure market details.", "", "&a&lClick &7to adjust settings")
				.make(), click -> {

		});

		// create category
		setButton(getRows() - 1, 4, QuickItem
				.of(CompMaterial.LIME_DYE)
				.name("&A&LNew Category")
				.lore("&7Used to make a new category", "&a&lClick &7to create new category")
				.make(), click -> new TitleInput(Markets.getInstance(), click.player, "<GRADIENT:65B1B4>&LNew Category</GRADIENT:2B6F8A>", "&fEnter category name in chat") {

			@Override
			public boolean onResult(String string) {
				string = ChatColor.stripColor(string);
				if (string.length() > 32) return false; // todo tell them it's too long

				Markets.getCategoryManager().create(MarketOverviewView.this.market, string, created -> {
					click.manager.showGUI(click.player, new MarketOverviewView(click.player, MarketOverviewView.this.market));
				});

				return true;
			}
		});

		// delete button
		setButton(getRows() - 1, 8, QuickItem
				.of(CompMaterial.LAVA_BUCKET)
				.name("&c&lDelete Market")
				.lore("&7This action &4&lCANNOT &7be undone!", "", "&a&lClick &7to delete market")
				.make(), click -> {

		});
	}

	@Override
	protected ItemStack makeDisplayItem(Category category) {
		return QuickItem
				.of(CompMaterial.CHEST)
				.name(category.getDisplayName())
				.lore("id: " + category.getName())
				.lore(category.getDescription())
				.make();
	}

	@Override
	protected void onClick(Category category, GuiClickEvent clickEvent) {

	}

	@Override
	protected List<Integer> fillSlots() {
		return List.of(
				12, 13, 14, 15, 16,
				21, 22, 23, 24, 25,
				30, 31, 32, 33, 34
		);
	}
}
