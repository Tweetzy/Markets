package ca.tweetzy.markets.model;

import ca.tweetzy.flight.comp.enums.ServerVersion;
import ca.tweetzy.flight.nbtapi.NBT;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.ChatUtil;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class BlacklistChecker {

	public boolean passesChecks(Player player, ItemStack itemStack) {
		boolean meets = true;

		for (String item : Settings.BLOCKED_ITEMS.getStringList()) {
			final String[] split = item.split(":");

			if (split.length == 1) {
				if (split[0].contains(itemStack.getType().name())) {
					Common.tell(player, TranslationManager.string(Translations.BLOCKED_ITEM, "item", ChatUtil.capitalize(itemStack.getType())));
					return false;
				}
			}

			if (split.length == 2 && isInt(split[1]) && ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
				if (split[0].contains(itemStack.getType().name()) && itemStack.getItemMeta() != null && itemStack.getItemMeta().getCustomModelData() == Integer.parseInt(split[1])) {
					Common.tell(player, TranslationManager.string(Translations.BLOCKED_ITEM, "item", ChatUtil.capitalize(itemStack.getType())));
					return false;
				}
			}
		}

		// Check NBT tags
		for (String nbtTag : Settings.BLOCKED_NBT_TAGS.getStringList()) {
			if (NBT.get(itemStack, nbt -> (boolean) nbt.hasTag(nbtTag))) {
				Common.tell(player, TranslationManager.string(Translations.BLOCKED_ITEM_TAG, ChatUtil.capitalize(itemStack.getType())));
				return false;
			}
		}

		String itemName = ChatColor.stripColor(getItemName(itemStack).toLowerCase());
		List<String> itemLore = getItemLore(itemStack).stream().map(line -> ChatColor.stripColor(line.toLowerCase())).toList();

		// Check for blocked names and lore
		for (String s : Settings.BLOCKED_ITEM_NAMES.getStringList()) {
			if (match(s, itemName)) {
				Common.tell(player, TranslationManager.string(Translations.BLOCKED_ITEM_NAME, "blacklisted_word", s));
				meets = false;
				break;
			}
		}

		if (!itemLore.isEmpty() && meets) {
			for (String s : Settings.BLOCKED_ITEM_LORES.getStringList()) {
				for (String line : itemLore) {
					if (match(s, line)) {
						Common.tell(player, TranslationManager.string(Translations.BLOCKED_ITEM_LORE, "blacklisted_word", s));
						meets = false;
						break;
					}
				}
			}
		}

		return meets;
	}

	private boolean match(String pattern, String sentence) {
		Pattern patt = Pattern.compile("\\b" + Pattern.quote(ChatColor.stripColor(pattern)) + "\\b", Pattern.CASE_INSENSITIVE);
		Matcher matcher = patt.matcher(sentence);
		return matcher.find();
	}

	private String getItemName(ItemStack stack) {
		Objects.requireNonNull(stack, "Item stack cannot be null when getting name");
		final String name = stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : ChatUtil.capitalizeFully(stack.getType());
		return name;
	}

	/**
	 * Used to get the lore from an item stack
	 *
	 * @param stack is the item being checked
	 * @return the item lore if available
	 */
	private List<String> getItemLore(ItemStack stack) {
		List<String> lore = new ArrayList<>();
		Objects.requireNonNull(stack, "Item stack cannot be null when getting lore");
		if (stack.hasItemMeta()) {
			if (stack.getItemMeta().hasLore() && stack.getItemMeta().getLore() != null) {
				lore.addAll(stack.getItemMeta().getLore());
			}
		}
		return lore;
	}

	private boolean isInt(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
