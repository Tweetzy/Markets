package ca.tweetzy.markets.utils;

import ca.tweetzy.core.compatibility.ServerVersion;
import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.gui.events.GuiClickEvent;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.input.ChatPrompt;
import ca.tweetzy.core.utils.NumberUtils;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.MarketsAPI;
import ca.tweetzy.markets.api.events.MarketItemRemoveEvent;
import ca.tweetzy.markets.api.heads.HeadDatabaseHook;
import ca.tweetzy.markets.api.heads.SkullsHook;
import ca.tweetzy.markets.guis.category.GUICategorySettings;
import ca.tweetzy.markets.guis.items.GUIAllItems;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 04 2021
 * Time Created: 3:04 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */

@UtilityClass
public class Common {

	public boolean chargeCreationFee(Player player) {
		if (!Settings.USE_CREATION_FEE.getBoolean()) return true;
		if (!EconomyManager.hasBalance(player, Settings.CREATION_FEE_AMOUNT.getDouble())) {
			Markets.getInstance().getLocale().getMessage("not_enough_money_create").sendPrefixedMessage(player);
			return false;
		}
		EconomyManager.withdrawBalance(player, Settings.CREATION_FEE_AMOUNT.getDouble());
		Markets.getInstance().getLocale().getMessage("money_remove").processPlaceholder("price", MarketsAPI.formatNumber(Settings.CREATION_FEE_AMOUNT.getDouble())).sendPrefixedMessage(player);
		return true;
	}

	public void handleMarketItemEdit(GuiClickEvent e, Market market, MarketItem marketItem, MarketCategory marketCategory) {
		switch (e.clickType) {
			case LEFT:
				e.gui.exit();
				ChatPrompt.showPrompt(Markets.getInstance(), e.player, TextUtils.formatText(Markets.getInstance().getLocale().getMessage("prompt.enter_market_item_price").getMessage()), chat -> {
					String val = chat.getMessage().trim();
					if (NumberUtils.isDouble(val) && Double.parseDouble(val) > 0) {
						marketItem.setPrice(Double.parseDouble(val));
						market.setUpdatedAt(System.currentTimeMillis());
					}
				}).setOnCancel(() -> e.manager.showGUI(e.player, marketCategory == null ? new GUIAllItems(market, true) : new GUICategorySettings(market, marketCategory))).setOnClose(() -> e.manager.showGUI(e.player, marketCategory == null ? new GUIAllItems(market, true) : new GUICategorySettings(market, marketCategory)));
				break;
			case RIGHT:
				if (marketItem.getItemStack().getAmount() == 1) return;
				marketItem.setPriceForStack(!marketItem.isPriceForStack());
				market.setUpdatedAt(System.currentTimeMillis());
				e.manager.showGUI(e.player, marketCategory == null ? new GUIAllItems(market, true) : new GUICategorySettings(market, marketCategory));
				break;
			case DROP:
				MarketItemRemoveEvent marketItemRemoveEvent = new MarketItemRemoveEvent(market, marketItem);
				Bukkit.getPluginManager().callEvent(marketItemRemoveEvent);
				if (marketItemRemoveEvent.isCancelled()) return;

				if (marketCategory == null) {
					market.getCategories().stream().filter(category -> category.getId().equals(marketItem.getCategoryId())).findFirst().get().getItems().remove(marketItem);
				} else {
					marketCategory.getItems().remove(marketItem);
				}

				PlayerUtils.giveItem(e.player, marketItem.getItemStack());
				market.setUpdatedAt(System.currentTimeMillis());
				e.manager.showGUI(e.player, marketCategory == null ? new GUIAllItems(market, true) : new GUICategorySettings(market, marketCategory));
				break;
			case SHIFT_RIGHT:
				if (!marketItem.isUseItemCurrency()) return;
				e.event.setCancelled(true);
				Markets.getInstance().getMarketPlayerManager().addPlayerToCustomCurrencyItem(e.player.getUniqueId(), market, marketCategory, marketItem);
				Markets.getInstance().getLocale().getMessage("click_currency_item").sendPrefixedMessage(e.player);
				break;
		}
	}

	/**
	 * Create a custom textured skull
	 *
	 * @param texture is the texture URL (http://textures.minecraft.net/texture/???) or the base64 encoded value
	 * @param base64  is the entered texture a base64 string?
	 * @return the created head
	 */
	public ItemStack getCustomTextureHead(String texture, boolean base64) {
		ItemStack head = XMaterial.PLAYER_HEAD.parseItem();
		SkullMeta meta = (SkullMeta) Objects.requireNonNull(head).getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), "");
		if (base64) {
			profile.getProperties().put("textures", new Property("textures", texture));
		} else {
			byte[] encoded = Base64.getEncoder().encode(String.format("{\"textures\": {\"SKIN\": {\"url\": \"%s\"}}}", texture).getBytes());
			profile.getProperties().put("textures", new Property("textures", new String(encoded)));
		}
		Field profileField;
		try {
			profileField = Objects.requireNonNull(meta).getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(meta, profile);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		head.setItemMeta(meta);
		return head;
	}

	private boolean isMinecraftTextureString(String value) {
		return value.startsWith("https://textures.minecraft.net/texture/");
	}

	private boolean isSkullsTexture(String value) {
		return value.toLowerCase().startsWith("skulls:");
	}

	private boolean isHeadDatabaseTexture(String value) {
		return value.toLowerCase().startsWith("hdb:");
	}

	public ItemStack getItemStack(String value) {
		if (isMinecraftTextureString(value)) {
			return getCustomTextureHead(value, false);
		}

		if (isSkullsTexture(value)) {
			final String[] parts = value.split(":");
			if (NumberUtils.isInt(parts[1]))
				return SkullsHook.getHead(Integer.parseInt(parts[1]));
			else
				return XMaterial.STONE.parseItem();
		}

		if (isHeadDatabaseTexture(value)) {
			final String[] parts = value.split(":");
			if (NumberUtils.isInt(parts[1]))
				return HeadDatabaseHook.getHead(Integer.parseInt(parts[1]));
			else
				return XMaterial.STONE.parseItem();
		}

		return XMaterial.matchXMaterial(value.toUpperCase()).orElse(XMaterial.STONE).parseItem();
	}

	/**
	 * Used to get a specific player head
	 *
	 * @param player is the name of the player
	 * @return the player's head/skull
	 */
	public ItemStack getPlayerHead(Player player) {
		ItemStack stack = XMaterial.PLAYER_HEAD.parseItem();
		SkullMeta meta = (SkullMeta) stack.getItemMeta();
		meta.setOwner(player.getName());
		stack.setItemMeta(meta);
		return stack;
	}

	/**
	 * Used to get a specific player head
	 *
	 * @param player is the name of the player
	 * @return the player's head/skull
	 */
	public ItemStack getPlayerHead(String player) {
		ItemStack stack = XMaterial.PLAYER_HEAD.parseItem();
		SkullMeta meta = (SkullMeta) stack.getItemMeta();
		meta.setOwner(player);
		stack.setItemMeta(meta);
		return stack;
	}

	public String getItemName(ItemStack stack) {
		Objects.requireNonNull(stack, "Item stack cannot be null when getting name");
		return stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : TextUtils.formatText("&f" + WordUtils.capitalize(stack.getType().name().toLowerCase().replace("_", " ")));
	}

	public List<String> getItemLore(ItemStack stack) {
		List<String> lore = new ArrayList<>();
		Objects.requireNonNull(stack, "Item stack cannot be null when getting lore");
		if (stack.hasItemMeta()) {
			if (stack.getItemMeta().hasLore() && stack.getItemMeta().getLore() != null) {
				lore.addAll(stack.getItemMeta().getLore());
			}
		}
		return lore;
	}

	public ItemStack getItemInHand(Player player) {
		return ServerVersion.isServerVersionBelow(ServerVersion.V1_9) ? player.getInventory().getItemInHand() : player.getInventory().getItemInMainHand();
	}

	/**
	 * Used to convert milliseconds (usually System.currentMillis) into a readable date format
	 *
	 * @param milliseconds is the total milliseconds
	 * @return a readable date format
	 */
	public String convertMillisToDate(long milliseconds) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm aa");
		Date date = new Date(milliseconds);
		return simpleDateFormat.format(date);
	}

	/**
	 * Used to convert seconds to days, hours, minutes, and seconds
	 *
	 * @param seconds is the amount of seconds to convert
	 * @return an array containing the total number of days, hours, minutes, and seconds remaining
	 */
	public long[] getRemainingTimeValues(long seconds) {
		long[] vals = new long[4];
		int day = (int) TimeUnit.SECONDS.toDays(seconds);
		long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24L);
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
		long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
		vals[0] = day;
		vals[1] = hours;
		vals[2] = minute;
		vals[3] = second;
		return vals;
	}
}
