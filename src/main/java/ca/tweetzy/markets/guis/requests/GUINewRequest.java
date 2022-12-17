package ca.tweetzy.markets.guis.requests;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.Gui;
import ca.tweetzy.core.gui.GuiUtils;
import ca.tweetzy.core.gui.SimplePagedGui;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.market.contents.BlockedItem;
import ca.tweetzy.markets.request.Request;
import ca.tweetzy.markets.request.RequestItem;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.utils.Common;
import ca.tweetzy.markets.utils.ConfigItemUtil;
import ca.tweetzy.markets.utils.InventorySafeMaterials;
import ca.tweetzy.markets.utils.Numbers;
import ca.tweetzy.markets.utils.input.TitleInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 03 2021
 * Time Created: 3:26 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class GUINewRequest extends Gui {

	private final Player player;

	private double itemPrice;
	private int requestAmount;

	private boolean useCustomCurrency;
	private boolean usedMaterialPicker;

	private ItemStack item;
	private ItemStack currency;

	public GUINewRequest(Player player, double itemPrice, int requestAmount, boolean useCustomCurrency, ItemStack item, ItemStack currency, boolean usedMaterialPicker) {
		this.player = player;
		this.itemPrice = itemPrice;
		this.requestAmount = requestAmount;
		this.useCustomCurrency = useCustomCurrency;
		this.item = item;
		this.currency = currency;
		this.usedMaterialPicker = usedMaterialPicker;
		setTitle(TextUtils.formatText(Settings.GUI_NEW_REQUEST_TITLE.getString()));
		setDefaultItem(GuiUtils.getBorderItem(Settings.GUI_NEW_REQUEST_FILL_ITEM.getMaterial()));
		setAllowDrops(true);
		setAcceptsItems(true);
		setUseLockedCells(true);
		setAllowShiftClick(false);
		setAllowClose(false);
		setRows(6);
		draw();
	}

	private void draw() {
		reset();

		// draw the border
		for (int i : Numbers.GUI_BORDER_6_ROWS) {
			setItem(i, GuiUtils.getBorderItem(Settings.GUI_NEW_REQUEST_BORDER_ITEM.getMaterial()));
			if (Settings.GUI_NEW_REQUEST_GLOW_BORDER.getBoolean()) highlightItem(i);
		}

		placePriceButton();
		placeAmountButton();
		placeUseCustomCurrencyButton();

		setUnlocked(2, 4, !this.usedMaterialPicker);
		setItem(2, 4, XMaterial.AIR.parseItem());

		setAction(2, 4, ClickType.SHIFT_RIGHT, e -> {
			if (this.item == null || getItem(2, 2) == null) {
				e.manager.showGUI(e.player, new RequestItemPicker());
			}
		});

		if (this.currency != null && this.currency != XMaterial.AIR.parseItem() && this.useCustomCurrency) {
			setItem(2, 2, this.currency);
		}

		if (this.item != null && this.item.getType() != XMaterial.AIR.parseMaterial()) {
			setItem(2, 4, this.item);
		}

		// CANCEL CONFIRM BUTTONS
		setItems(29, 30, GuiUtils.createButtonItem(Common.getItemStack(Settings.GUI_NEW_REQUEST_ITEMS_CANCEL_ADD_ITEM.getString()), TextUtils.formatText(Settings.GUI_NEW_REQUEST_ITEMS_CANCEL_ADD_NAME.getString()), TextUtils.formatText(Settings.GUI_NEW_REQUEST_ITEMS_CANCEL_ADD_LORE.getStringList())));
		setItems(32, 33, GuiUtils.createButtonItem(Common.getItemStack(Settings.GUI_NEW_REQUEST_ITEMS_CONFIRM_ADD_ITEM.getString()), TextUtils.formatText(Settings.GUI_NEW_REQUEST_ITEMS_CONFIRM_ADD_NAME.getString()), TextUtils.formatText(Settings.GUI_NEW_REQUEST_ITEMS_CONFIRM_ADD_LORE.getStringList())));
		setActionForRange(29, 30, ClickType.LEFT, e -> {
			setAllowClose(true);
			if (getItem(2, 2) != null && getItem(2, 2).getType() != XMaterial.AIR.parseMaterial() && this.useCustomCurrency) {
				PlayerUtils.giveItem(e.player, getItem(2, 2));
			}

			if (!this.usedMaterialPicker && getItem(2, 4) != null && getItem(2, 4).getType() != XMaterial.AIR.parseMaterial()) {
				PlayerUtils.giveItem(e.player, getItem(2, 4));
			}

			e.gui.close();
		});


		setActionForRange(32, 33, ClickType.LEFT, e -> {
			assignItemStacks();

			if (this.item == null || this.item.getType() == XMaterial.AIR.parseMaterial()) {
				Markets.getInstance().getLocale().getMessage("place_sell_item").sendPrefixedMessage(e.player);
				return;
			}

			if (Markets.getInstance().getMarketManager().getBlockedItems().size() != 0 && Markets.getInstance().getMarketManager().getBlockedItems().stream().map(BlockedItem::getItem).anyMatch(item -> item.isSimilar(this.item))) {
				Markets.getInstance().getLocale().getMessage("item_is_blocked").sendPrefixedMessage(player);
				return;
			}

			if (Settings.LIMIT_REQUESTS_BY_PERMISSION.getBoolean()) {
				int maxAllowedRequests = MarketsAPI.getInstance().maxAllowedRequestsItems(player);
				int totalItemsInMarket = Markets.getInstance().getRequestManager().getPlayerRequests(player).size();
				if (totalItemsInMarket >= maxAllowedRequests) {
					Markets.getInstance().getLocale().getMessage("at_max_request_limit").sendPrefixedMessage(player);
					return;
				}
			}

			if (this.requestAmount > Settings.MAX_REQUEST_AMOUNT.getInt()) {
				Markets.getInstance().getLocale().getMessage("max_request_amount").processPlaceholder("request_max_amount", Settings.MAX_REQUEST_AMOUNT.getInt()).sendPrefixedMessage(player);
				return;
			}


			if (this.currency == null || this.currency.getType() == XMaterial.AIR.parseMaterial() && this.useCustomCurrency) {
				Markets.getInstance().getLocale().getMessage("place_currency_item").sendPrefixedMessage(e.player);
				return;
			}

			if (this.itemPrice <= 0) { // Technically this should never be the case...
				Markets.getInstance().getLocale().getMessage("price_is_zero_or_less").sendPrefixedMessage(e.player);
				return;
			}

			double priceForAll = this.itemPrice;
			double pricePerItem = priceForAll / this.requestAmount;
			int maxStackSize = this.item.getMaxStackSize();
			int fullStacks = this.requestAmount / maxStackSize;
			int remainder = this.requestAmount % maxStackSize;

			Request request = new Request(player.getUniqueId(), null);
			List<RequestItem> requestItems = new ArrayList<>();

			for (int i = 0; i < fullStacks; i++) {
				requestItems.add(new RequestItem(request.getId(), this.item, this.currency, maxStackSize, pricePerItem * maxStackSize, false, this.useCustomCurrency));
			}

			if (remainder != 0) {
				requestItems.add(new RequestItem(request.getId(), this.item, this.currency, remainder, pricePerItem * remainder, false, this.useCustomCurrency));
			}

			request.setRequestedItems(requestItems);
			Markets.getInstance().getRequestManager().addRequest(request);
			Markets.getInstance().getLocale().getMessage("created_request").processPlaceholder("request_amount", requestAmount).processPlaceholder("request_item_name", Common.getItemName(this.item)).processPlaceholder("request_price", MarketsAPI.formatNumber(priceForAll)).sendPrefixedMessage(player);
			if (Settings.BROADCAST_REQUEST_CREATION.getBoolean()) {
				String prefix = Markets.getInstance().getLocale().getMessage("general.prefix").getMessage();
				String info = Markets.getInstance().getLocale().getMessage("created_request_broadcast")
						.processPlaceholder("player", player.getName())
						.processPlaceholder("request_amount", requestAmount)
						.processPlaceholder("request_item_name", Common.getItemName(item))
						.processPlaceholder("request_price", MarketsAPI.formatNumber(priceForAll))
						.getMessage();

				Bukkit.getOnlinePlayers().forEach(to -> MarketsAPI.getInstance().sendClickableCommand(to, prefix + " " + info, "markets show request " + player.getName() + " -L"));
			}

			setAllowClose(true);
			if (getItem(2, 2) != null && getItem(2, 2).getType() != XMaterial.AIR.parseMaterial() && this.useCustomCurrency) {
				PlayerUtils.giveItem(e.player, getItem(2, 2));
			}

			if (!this.usedMaterialPicker && getItem(2, 4) != null && getItem(2, 4).getType() != XMaterial.AIR.parseMaterial()) {
				PlayerUtils.giveItem(e.player, getItem(2, 4));
			}

			e.gui.close();
		});
	}

	private void placePriceButton() {
		setButton(3, 4, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_NEW_REQUEST_ITEMS_PRICE_ITEM.getString()), Settings.GUI_NEW_REQUEST_ITEMS_PRICE_NAME.getString(), this.useCustomCurrency ? Settings.GUI_NEW_REQUEST_ITEMS_PRICE_LORE_CUSTOM_CURRENCY.getStringList() : Settings.GUI_NEW_REQUEST_ITEMS_PRICE_LORE.getStringList(), 1, new HashMap<String, Object>() {{
			put("%market_payment_price%", useCustomCurrency ? MarketsAPI.formatNumber(itemPrice, true) : MarketsAPI.formatNumber(itemPrice));
			put("%market_request_currency%", useCustomCurrency && currency != null ? Common.getItemName(currency) : "");
		}}), ClickType.LEFT, e -> {
			assignItemStacks();
			setAllowClose(true);
			e.gui.exit();

			new TitleInput(
					Markets.getInstance(),
					e.player,
					Markets.getInstance().getLocale().getMessage("inputs.enter_market_item_price.title").getMessage(),
					Markets.getInstance().getLocale().getMessage("inputs.enter_market_item_price.subtitle").getMessage()) {

				@Override
				public void onExit(Player player) {
					reopen(player);
				}

				@Override
				public boolean onResult(String string) {
					String val = ChatColor.stripColor(string).trim();

					if (!(NumberUtils.isDouble(val) && Double.parseDouble(val) > 0)) return false;

					GUINewRequest.this.itemPrice = Double.parseDouble(val);
					reopen(player);
					return true;
				}
			};
		});
	}

	private void placeAmountButton() {
		setButton(2, 6, ConfigItemUtil.build(Common.getItemStack(Settings.GUI_NEW_REQUEST_ITEMS_AMOUNT_ITEM.getString()), Settings.GUI_NEW_REQUEST_ITEMS_AMOUNT_NAME.getString(), Settings.GUI_NEW_REQUEST_ITEMS_AMOUNT_LORE.getStringList(), 1, new HashMap<String, Object>() {{
			put("%request_amount%", requestAmount);
		}}), ClickType.LEFT, e -> {
			assignItemStacks();
			setAllowClose(true);
			e.gui.exit();

			new TitleInput(
					Markets.getInstance(),
					e.player,
					Markets.getInstance().getLocale().getMessage("inputs.enter_request_amount.title").getMessage(),
					Markets.getInstance().getLocale().getMessage("inputs.enter_request_amount.subtitle").getMessage()) {

				@Override
				public void onExit(Player player) {
					reopen(player);
				}

				@Override
				public boolean onResult(String string) {
					String msg = ChatColor.stripColor(string).trim();

					if (msg.length() != 0 && NumberUtils.isInt(msg) && Integer.parseInt(msg) > 0) {
						GUINewRequest.this.requestAmount = Integer.parseInt(msg);
						reopen(e.player);
					}
					return true;
				}
			};
		});
	}


	private void placeUseCustomCurrencyButton() {
		setButton(2, 3, ConfigItemUtil.build(
				this.useCustomCurrency ? Common.getItemStack(Settings.GUI_NEW_REQUEST_ITEMS_USE_CUSTOM_CURRENCY_ENABLED_ITEM.getString()) : Common.getItemStack(Settings.GUI_NEW_REQUEST_ITEMS_USE_CUSTOM_CURRENCY_DISABLED_ITEM.getString()),
				this.useCustomCurrency ? Settings.GUI_NEW_REQUEST_ITEMS_USE_CUSTOM_CURRENCY_ENABLED_NAME.getString() : Settings.GUI_NEW_REQUEST_ITEMS_USE_CUSTOM_CURRENCY_DISABLED_NAME.getString(),
				this.useCustomCurrency ? Settings.GUI_NEW_REQUEST_ITEMS_USE_CUSTOM_CURRENCY_ENABLED_LORE.getStringList() : Settings.GUI_NEW_REQUEST_ITEMS_USE_CUSTOM_CURRENCY_DISABLED_LORE.getStringList(),
				1, null), ClickType.LEFT, e -> {
			this.useCustomCurrency = !this.useCustomCurrency;
			placeUseCustomCurrencyButton();
			placePriceButton();
		});

		if (this.useCustomCurrency) {
			setItem(2, 2, XMaterial.AIR.parseItem());
			setUnlocked(2, 2, true);
		}
		if (!this.useCustomCurrency) {
			if (getItem(2, 2) != null && getItem(2, 2).getType() != XMaterial.AIR.parseMaterial()) {
				PlayerUtils.giveItem(this.player, getItem(2, 2));
			}
			setItem(2, 2, GuiUtils.createButtonItem(Common.getItemStack(Settings.GUI_NEW_REQUEST_ITEMS_CURRENCY_HOLDER_ITEM.getString()), TextUtils.formatText(Settings.GUI_NEW_REQUEST_ITEMS_CURRENCY_HOLDER_NAME.getString()), TextUtils.formatText(Settings.GUI_NEW_REQUEST_ITEMS_CURRENCY_HOLDER_LORE.getStringList())));
			setUnlocked(2, 2, false);
		}
	}

	private void reopen(Player player) {
		Markets.getInstance().getGuiManager().showGUI(player, new GUINewRequest(player, this.itemPrice, this.requestAmount, this.useCustomCurrency, this.item, this.currency, this.usedMaterialPicker));
	}

	private void assignItemStacks() {
		this.item = getItem(2, 4);
		this.currency = getItem(2, 2);
	}

	public final class RequestItemPicker extends SimplePagedGui {

		public RequestItemPicker() {
			setTitle(Settings.GUI_SELECT_MATERIAL_TITLE.getString());
			setUseLockedCells(true);
			setAllowShiftClick(false);
			setHeaderBackItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
			setFooterBackItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());
			setDefaultItem(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem());

			int slot = 9;
			for (XMaterial material : InventorySafeMaterials.get()) {
				setButton(slot++, GuiUtils.createButtonItem(material, material.name().toLowerCase(Locale.ROOT).replace("_", " ")), ClickType.LEFT, e -> {

					e.manager.showGUI(e.player,
							new GUINewRequest(e.player, GUINewRequest.this.itemPrice, GUINewRequest.this.requestAmount, GUINewRequest.this.useCustomCurrency, material.parseItem(), GUINewRequest.this.currency, true)
					);
				});
			}
		}
	}
}
