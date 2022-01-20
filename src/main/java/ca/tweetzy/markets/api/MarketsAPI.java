package ca.tweetzy.markets.api;

import ca.tweetzy.core.compatibility.XMaterial;
import ca.tweetzy.core.hooks.EconomyManager;
import ca.tweetzy.core.utils.PlayerUtils;
import ca.tweetzy.core.utils.TextUtils;
import ca.tweetzy.core.utils.nms.NBTEditor;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.events.MarketCategoryRemoveEvent;
import ca.tweetzy.markets.api.events.MarketDeleteEvent;
import ca.tweetzy.markets.guis.GUIMain;
import ca.tweetzy.markets.guis.market.GUIMarketEdit;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.market.contents.MarketCategory;
import ca.tweetzy.markets.market.contents.MarketItem;
import ca.tweetzy.markets.settings.Settings;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 05 2021
 * Time Created: 1:42 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MarketsAPI {

	private MarketsAPI() {
	}

	private static MarketsAPI instance;
	private final Pattern maxAllowedMarketItemPattern = Pattern.compile("markets\\.maxalloweditems\\.(\\d+)");
	private final Pattern maxAllowedRequestsPattern = Pattern.compile("markets\\.maxallowedrequests\\.(\\d+)");
	private final Pattern alphaNumericPattern = Pattern.compile("^[a-zA-Z0-9]*$");

	public static MarketsAPI getInstance() {
		if (instance == null) {
			instance = new MarketsAPI();
		}
		return instance;
	}

	/**
	 * Get the total amount of an item in the player's inventory
	 *
	 * @param player is the player being checked
	 * @param stack  is the item you want to find
	 * @return the total count of the item(s)
	 */
	public int getItemCountInPlayerInventory(Player player, ItemStack stack) {
		int total = 0;
		if (stack.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
			for (ItemStack item : player.getInventory().getContents()) {
				if (item == null || item.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) continue;
				if (NBTEditor.getTexture(item).equals(NBTEditor.getTexture(stack))) total += item.getAmount();
			}
		} else {
			for (ItemStack item : player.getInventory().getContents()) {
				if (item == null || !item.isSimilar(stack)) continue;
				total += item.getAmount();
			}
		}
		return total;
	}

	/**
	 * Removes a set amount of a specific item from the player inventory
	 *
	 * @param player is the player you want to remove the item from
	 * @param stack  is the item that you want to remove
	 * @param amount is the amount of items you want to remove.
	 */
	public void removeSpecificItemQuantityFromPlayer(Player player, ItemStack stack, int amount) {
		int i = amount;
		for (int j = 0; j < player.getInventory().getSize(); j++) {
			ItemStack item = player.getInventory().getItem(j);
			if (item == null) continue;
			if (stack.getType() == XMaterial.PLAYER_HEAD.parseMaterial() && item.getType() == XMaterial.PLAYER_HEAD.parseMaterial()) {
				if (!NBTEditor.getTexture(item).equals(NBTEditor.getTexture(stack))) continue;
			} else {
				if (!item.isSimilar(stack)) continue;

			}

			if (i >= item.getAmount()) {
				player.getInventory().clear(j);
				i -= item.getAmount();
			} else if (i > 0) {
				item.setAmount(item.getAmount() - i);
				i = 0;
			} else {
				break;
			}
		}
	}

	/**
	 * Used to get command flags (ex. -h, -f, -t, etc)
	 *
	 * @param args is the arguments passed when running a command
	 * @return any command flags if any
	 */
	public List<String> getCommandFlags(String... args) {
		List<String> flags = new ArrayList<>();
		for (String arg : args) {
			if (arg.startsWith("-") && arg.length() >= 2) {
				flags.add(arg.substring(0, 2));
			}
		}
		return flags;
	}

	/**
	 * Get the max allowed items based on the permission
	 *
	 * @param player is the player being checked
	 * @return the max allowed items from the permission node.
	 */
	public int maxAllowedMarketItems(Player player) {
		int maxAllowedItems = Settings.DEFAULT_MAX_ALLOWED_MARKET_ITEMS.getInt();
		int max = player.getEffectivePermissions().stream().map(i -> {
			Matcher matcher = maxAllowedMarketItemPattern.matcher(i.getPermission());
			if (matcher.matches()) {
				return Integer.parseInt(matcher.group(1));
			}
			return 0;
		}).max(Integer::compareTo).orElse(0);

		if (player.hasPermission("markets.maxalloweditems.*")) {
			maxAllowedItems = Integer.MAX_VALUE;
		}

		if (max > maxAllowedItems) {
			maxAllowedItems = max;
		}

		return maxAllowedItems;
	}

	/**
	 * Get the max allowed requests a player can have based on permission
	 *
	 * @param player is the player being checked
	 * @return the max allowed requests from the permission node.
	 */
	public int maxAllowedRequestsItems(Player player) {
		int maxAllowedRequests = Settings.DEFAULT_MAX_ALLOWED_REQUESTS_ITEMS.getInt();
		int max = player.getEffectivePermissions().stream().map(i -> {
			Matcher matcher = maxAllowedRequestsPattern.matcher(i.getPermission());
			if (matcher.matches()) {
				return Integer.parseInt(matcher.group(1));
			}
			return 0;
		}).max(Integer::compareTo).orElse(0);

		if (player.hasPermission("markets.maxallowedrequests.*")) {
			maxAllowedRequests = Integer.MAX_VALUE;
		}

		if (max > maxAllowedRequests) {
			maxAllowedRequests = max;
		}

		return maxAllowedRequests;
	}

	/**
	 * Check if a string is alphanumeric
	 *
	 * @param string is the phrase being checked
	 * @return true if the provided string is alphanumeric
	 */
	public boolean isAlphaNumeric(String string) {
		Matcher matcher = alphaNumericPattern.matcher(string);
		return matcher.matches();
	}

	public static String formatNumber(double number, boolean round) {
		if (round)
			return String.valueOf(Math.round(number));
		else
			return String.format(Settings.CURRENCY_FORMAT.getString(), number);
	}

	public static String formatNumber(double number) {
		return formatNumber(number, false);
	}

	/**
	 * Used to convert a serializable object into a base64 string
	 *
	 * @param object is the class that implements Serializable
	 * @return the base64 encoded string
	 */
	public String convertToBase64(Serializable object) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream;
		try {
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
	}

	/**
	 * Used to convert a base64 string into an object
	 *
	 * @param string is the base64 string
	 * @return an object
	 */
	public Object convertBase64ToObject(String string) {
		byte[] data = Base64.getDecoder().decode(string);
		ObjectInputStream objectInputStream;
		Object object = null;
		try {
			objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
			object = objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * Deserialize a byte array into an ItemStack.
	 *
	 * @param data Data to deserialize.
	 * @return Deserialized ItemStack.
	 */
	public ItemStack deserializeItem(byte[] data) {
		ItemStack item = null;
		try (BukkitObjectInputStream stream = new BukkitObjectInputStream(new ByteArrayInputStream(data))) {
			item = (ItemStack) stream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return item;
	}

	/**
	 * Serialize an ItemStack into a byte array.
	 *
	 * @param item Item to serialize.
	 * @return Serialized data.
	 */
	public byte[] serializeItem(ItemStack item) {
		try (ByteArrayOutputStream stream = new ByteArrayOutputStream(); BukkitObjectOutputStream bukkitStream = new BukkitObjectOutputStream(stream)) {
			bukkitStream.writeObject(item);
			return stream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Get the name of an item stack
	 *
	 * @param stack is the item you want to get name from
	 * @return the item name
	 */
	public String getItemName(ItemStack stack) {
		Objects.requireNonNull(stack, "Item stack cannot be null when getting name");
		return stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : TextUtils.formatText("&f" + WordUtils.capitalize(stack.getType().name().toLowerCase().replace("_", " ")));
	}

	/**
	 * Used to get the lore from an item stack
	 *
	 * @param stack is the item being checked
	 * @return the item lore if available
	 */
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

	/**
	 * Used to get the names of all the enchantments on an item
	 *
	 * @param stack is the itemstack being checked
	 * @return a list of all the enchantment names
	 */
	public List<String> getItemEnchantments(ItemStack stack) {
		List<String> enchantments = new ArrayList<>();
		Objects.requireNonNull(stack, "Item Stack cannot be null when getting enchantments");
		if (!stack.getEnchantments().isEmpty()) {
			stack.getEnchantments().forEach((k, i) -> {
				enchantments.add(k.getName());
			});
		}
		return enchantments;
	}

	public boolean checkSearchCriteria(String phrase, ItemStack stack) {
		return match(phrase, MarketsAPI.getInstance().getItemName(stack)) ||
				match(phrase, Inflector.getInstance().pluralize(stack.getType().name())) ||
				match(phrase, Inflector.getInstance().singularize(stack.getType().name())) ||
				match(phrase, MarketsAPI.getInstance().getItemLore(stack)) ||
				match(phrase, MarketsAPI.getInstance().getItemEnchantments(stack));
	}

	/**
	 * Used to match patterns
	 *
	 * @param pattern  is the keyword being searched for
	 * @param sentence is the sentence you're checking
	 * @return whether the keyword is found
	 */
	public boolean match(String pattern, String sentence) {
		Pattern patt = Pattern.compile(ChatColor.stripColor(pattern), Pattern.CASE_INSENSITIVE);
		Matcher matcher = patt.matcher(sentence);
		return matcher.find();
	}

	/**
	 * @param pattern is the keyword that you're currently searching for
	 * @param lines   is the lines being checked for the keyword
	 * @return whether the keyword was found in any of the lines provided
	 */
	public boolean match(String pattern, List<String> lines) {
		for (String line : lines) {
			if (match(pattern, line)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sends a clickable message to a player that runs a command when clicked.
	 *
	 * @param message The clickable message!
	 * @param command The command without the slash to make the user perform.
	 * @param player  player to send to.
	 */
	public void sendClickableCommand(Player player, String message, String command) {
		// Make a new component (Bungee API).
		TextComponent component = new TextComponent(TextComponent.fromLegacyText(TextUtils.formatText(message)));
		// Add a click event to the component.
		component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + command));
		// Send it!
		player.spigot().sendMessage(component);
	}

	public void deleteMarket(Player player, Market market) {
		MarketDeleteEvent marketDeleteEvent = new MarketDeleteEvent(player, market);
		Bukkit.getPluginManager().callEvent(marketDeleteEvent);
		if (marketDeleteEvent.isCancelled()) {
			Markets.getInstance().getGuiManager().showGUI(player, new GUIMain(player));
			return;
		}

		if (Settings.GIVE_ITEMS_ON_MARKET_DELETE.getBoolean() && !market.getCategories().isEmpty()) {
			List<ItemStack> items = new ArrayList<>();
			Markets.newChain().async(() -> market.getCategories().forEach(category -> category.getItems().forEach(item -> items.add(item.getItemStack())))).sync(() -> PlayerUtils.giveItem(player, items)).execute();
		}
		Markets.getInstance().getMarketManager().deleteMarket(market);
		Markets.getInstance().getLocale().getMessage("removed_market").sendPrefixedMessage(player);
		Markets.getInstance().getGuiManager().showGUI(player, new GUIMain(player));
	}

	public void deleteMarketCategory(Player player, Market market, MarketCategory marketCategory) {
		MarketCategoryRemoveEvent marketCategoryRemoveEvent = new MarketCategoryRemoveEvent(market, marketCategory);
		Bukkit.getPluginManager().callEvent(marketCategoryRemoveEvent);
		if (marketCategoryRemoveEvent.isCancelled()) {
			Markets.getInstance().getGuiManager().showGUI(player, new GUIMarketEdit(market));
			return;
		}

		if (Settings.GIVE_ITEMS_ON_CATEGORY_DELETE.getBoolean()) {
			PlayerUtils.giveItem(player, marketCategory.getItems().stream().map(MarketItem::getItemStack).collect(Collectors.toList()));
		}

		market.getCategories().remove(marketCategory);
		market.setUpdatedAt(System.currentTimeMillis());
		Markets.getInstance().getGuiManager().showGUI(player, new GUIMarketEdit(market));
		Markets.getInstance().getLocale().getMessage("removed_category").processPlaceholder("market_category_name", marketCategory.getName()).sendPrefixedMessage(player);
	}

	public void featureMarket(Player player, Market market) {
		if (Markets.getInstance().getMarketManager().getFeaturedMarkets().containsKey(market.getId())) {
			Markets.getInstance().getGuiManager().showGUI(player, new GUIMarketEdit(market));
			return;
		}

		if (!EconomyManager.hasBalance(player, Settings.FEATURE_COST.getDouble())) {
			Markets.getInstance().getLocale().getMessage("not_enough_money").sendPrefixedMessage(player);
			Markets.getInstance().getGuiManager().showGUI(player, new GUIMarketEdit(market));
			return;
		}

		EconomyManager.withdrawBalance(player, Settings.FEATURE_COST.getDouble());
		Markets.getInstance().getLocale().getMessage("featured_market").sendPrefixedMessage(player);
		Markets.getInstance().getMarketManager().getFeaturedMarkets().put(market.getId(), System.currentTimeMillis() + 1000L * Settings.FEATURE_TIME.getInt());
		Markets.getInstance().getGuiManager().showGUI(player, new GUIMarketEdit(market));
	}
}
