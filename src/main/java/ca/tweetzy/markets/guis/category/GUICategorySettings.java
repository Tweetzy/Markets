package ca.tweetzy.markets.guis.category;

import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.input.PlayerChatInput;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.items.TItemBuilder;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.guis.GUIConfirm;
import ca.tweetzy.markets.guis.items.GUIAddItem;
import ca.tweetzy.markets.guis.items.GUIIconSelect;
import ca.tweetzy.markets.guis.market.GUIMarketEdit;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 01 2021
 * Time Created: 6:15 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUICategorySettings extends Gui {

	private final Market market;
	private final MarketCategory marketCategory;

	public GUICategorySettings(Market market, MarketCategory marketCategory) {
		this.market = market;
		this.marketCategory = marketCategory;
		setTitle(TextUtils.formatText(Settings.GUI_CATEGORY_EDIT_TITLE.getString().replace("%category_name%", this.marketCategory.getName())));
		setAllowDrops(false);
		setAcceptsItems(false);
		setUseLockedCells(true);
		setAllowShiftClick(false);
		setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_CATEGORY_EDIT_FILL_ITEM.getMaterial()));
		setRows(6);

		draw();
	}

	private void draw() {
		reset();
		pages = (int) Math.max(1, Math.ceil(this.marketCategory.getItems().size() / (double) 24));

		// make border
		Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 10, 17, 19, 26, 28, 35, 37, 44, 46, 47, 48, 49, 50, 51, 52, 53).forEach(i -> {
			setItem(i, GuiUtils.getBorderItem(Settings.GUI_CATEGORY_EDIT_BORDER_ITEM.getMaterial()));
			if (Settings.GUI_CATEGORY_EDIT_GLOW_BORDER.getBoolean()) highlightItem(i);
		});

		setPrevPage(5, 4, new TItemBuilder(Common.getItemStack(Settings.GUI_BACK_BTN_ITEM.getString())).setName(Settings.GUI_BACK_BTN_NAME.getString()).setLore(Settings.GUI_BACK_BTN_LORE.getStringList()).toItemStack());
		setNextPage(5, 5, new TItemBuilder(Common.getItemStack(Settings.GUI_NEXT_BTN_ITEM.getString())).setName(Settings.GUI_NEXT_BTN_NAME.getString()).setLore(Settings.GUI_NEXT_BTN_LORE.getStringList()).toItemStack());
		setOnPage(e -> draw());

		setButton(0, 0, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CATEGORY_EDIT_ITEMS_NAME_ITEM.getString()), Settings.GUI_CATEGORY_EDIT_ITEMS_NAME_NAME.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_NAME_LORE.getStringList(), 1, new HashMap<String, Object>() {{
			put("%category_display_name%", marketCategory.getDisplayName());
			put("%category_name%", marketCategory.getName());
		}}), ClickType.LEFT, e -> {
			e.gui.exit();
			PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Markets.getInstance(), e.player);
			builder.isValidInput((p, str) -> str.trim().length() >= 1);
			builder.sendValueMessage(TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_category_display_name").getMessage()));
			builder.toCancel("cancel");
			builder.onCancel(p -> e.manager.showGUI(e.player, new GUICategorySettings(this.market, this.marketCategory)));
			builder.setValue((p, value) -> value);
			builder.onFinish((p, value) -> {
				this.marketCategory.setDisplayName(value);
				this.market.setUpdatedAt(System.currentTimeMillis());
				Markets.getInstance().getLocale().getMessage("updated_category_name").sendPrefixedMessage(e.player);
				e.manager.showGUI(e.player, new GUICategorySettings(this.market, this.marketCategory));
			});

			PlayerChatInput<String> input = builder.build();
			input.start();
		});

		setButton(1, 0, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CATEGORY_EDIT_ITEMS_DESCRIPTION_ITEM.getString()), Settings.GUI_CATEGORY_EDIT_ITEMS_DESCRIPTION_NAME.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_DESCRIPTION_LORE.getStringList(), 1, new HashMap<String, Object>() {{
			put("%category_description%", marketCategory.getDescription());
		}}), ClickType.LEFT, e -> {
			e.gui.exit();
			PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Markets.getInstance(), e.player);
			builder.isValidInput((p, str) -> str.trim().length() >= 1);
			builder.sendValueMessage(TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_category_description").getMessage()));
			builder.toCancel("cancel");
			builder.onCancel(p -> e.manager.showGUI(e.player, new GUICategorySettings(this.market, this.marketCategory)));
			builder.setValue((p, value) -> value);
			builder.onFinish((p, value) -> {
				this.marketCategory.setDescription(value);
				this.market.setUpdatedAt(System.currentTimeMillis());
				Markets.getInstance().getLocale().getMessage("updated_category_description").sendPrefixedMessage(e.player);
				e.manager.showGUI(e.player, new GUICategorySettings(this.market, this.marketCategory));
			});

			PlayerChatInput<String> input = builder.build();
			input.start();
		});

		setButton(2, 0, new TItemBuilder(this.marketCategory.getIcon()).setName(Settings.GUI_CATEGORY_EDIT_ITEMS_ICON_NAME.getString()).setLore(Settings.GUI_CATEGORY_EDIT_ITEMS_ICON_LORE.getStringList()).toItemStack(), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIIconSelect(this.market, this.marketCategory)));

		setButton(3, 0, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CATEGORY_EDIT_ITEMS_EMPTY_ITEM.getString()), Settings.GUI_CATEGORY_EDIT_ITEMS_EMPTY_NAME.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_EMPTY_LORE.getStringList(), 1, null), ClickType.LEFT, e -> {
			PlayerUtils.giveItem(e.player, this.marketCategory.getItems().stream().map(MarketItem::getItemStack).collect(Collectors.toList()));
			this.marketCategory.getItems().clear();
			this.market.setUpdatedAt(System.currentTimeMillis());
			draw();
		});

		setButton(4, 0, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CATEGORY_EDIT_ITEMS_DELETE_ITEM.getString()), Settings.GUI_CATEGORY_EDIT_ITEMS_DELETE_NAME.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_DELETE_LORE.getStringList(), 1, null), ClickType.RIGHT, e -> {
			e.manager.showGUI(e.player, new GUIConfirm(this.market, this.marketCategory, GUIConfirm.ConfirmAction.DELETE_CATEGORY));
		});

		setButton(5, 0, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CLOSE_BTN_ITEM.getString()), Settings.GUI_CLOSE_BTN_NAME.getString(), Settings.GUI_CLOSE_BTN_LORE.getStringList(), 1, null), ClickType.LEFT, e -> e.manager.showGUI(e.player, new GUIMarketEdit(this.market)));
		setButton(5, 1, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_CATEGORY_EDIT_ITEMS_ADD_ITEM_ITEM.getString()), Settings.GUI_CATEGORY_EDIT_ITEMS_ADD_ITEM_NAME.getString(), Settings.GUI_CATEGORY_EDIT_ITEMS_ADD_ITEM_LORE.getStringList(), 1, null), ClickType.LEFT, e -> {
			e.manager.showGUI(e.player, new GUIAddItem(e.player, this.market, this.marketCategory, 1D, false, false, null, null));
		});

		Markets.newChain().async(() -> {
			List<MarketItem> data = this.marketCategory.getItems().stream().skip((page - 1) * 24L).limit(24L).collect(Collectors.toList());
			int slot = 11;
			for (MarketItem marketItem : data) {
				ItemStack item = marketItem.getItemStack().clone();

				List<String> lore = Common.getItemLore(item);
				lore.addAll(marketItem.isUseItemCurrency() ? Settings.GUI_CATEGORY_EDIT_ITEMS_ITEM_LORE_CUSTOM_CURRENCY.getStringList() : Settings.GUI_CATEGORY_EDIT_ITEMS_ITEM_LORE.getStringList());

				setButton(slot, ConfigItemUtil.build(item, Settings.GUI_CATEGORY_EDIT_ITEMS_ITEM_NAME.getString(), lore, item.getAmount(), new HashMap<String, Object>() {{
					put("%item_name%", Common.getItemName(item));
					put("%market_item_price%", marketItem.isUseItemCurrency() ? MarketsAPI.formatNumber(marketItem.getPrice(), true) : MarketsAPI.formatNumber(marketItem.getPrice()));
					put("%market_item_price_for_stack%", marketItem.getTranslatedPriceForStack());
					put("%market_item_currency%", marketItem.isUseItemCurrency() ? Common.getItemName(marketItem.getCurrencyItem()) : "");
				}}), e -> {
					Common.handleMarketItemEdit(e, this.market, marketItem, this.marketCategory);
				});

				slot = Arrays.asList(16, 25, 34).contains(slot) ? slot + 4 : slot + 1;
			}
		}).execute();
	}
}
