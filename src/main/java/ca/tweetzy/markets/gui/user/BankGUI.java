package ca.tweetzy.markets.gui.user;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.gui.Gui;
import ca.tweetzy.flight.gui.events.GuiClickEvent;
import ca.tweetzy.flight.gui.helper.InventoryBorder;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.*;
import ca.tweetzy.flight.utils.input.TitleInput;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.market.BankEntry;
import ca.tweetzy.markets.gui.MarketsPagedGUI;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;

public final class BankGUI extends MarketsPagedGUI<BankEntry> {

	private final Player player;
	private final boolean taxCollection;

	public BankGUI(Gui parent, @NonNull final Player player, boolean taxCollection) {
		super(
				parent,
				player,
				TranslationManager.string(player, taxCollection ? Translations.GUI_BANK_TAX_TITLE : Translations.GUI_BANK_TITLE),
				6,
				taxCollection ? Markets.getBankManager().getTaxEntries() : Markets.getBankManager().getEntriesByPlayer(player.getUniqueId())
		);

		this.player = player;
		this.taxCollection = taxCollection;
		setAcceptsItems(true);
		setDefaultItem(QuickItem.bg(Settings.GUI_BANK_BACKGROUND.getItemStack()));

		draw();
	}

	public BankGUI(Gui parent, @NonNull final Player player) {
		this(parent, player, false);
	}

	@Override
	protected void prePopulate() {
		this.items = this.taxCollection ? Markets.getBankManager().getTaxEntries() : Markets.getBankManager().getEntriesByPlayer(player.getUniqueId());
	}

	@Override
	protected void drawFixed() {
		if (!this.taxCollection)
			setButton(getRows() - 1, 4, QuickItem.of(Settings.GUI_BANK_ITEMS_ADD.getItemStack()).name(TranslationManager.string(this.player, Translations.GUI_BANK_ITEMS_ADD_NAME)).lore(TranslationManager.list(this.player, Translations.GUI_BANK_ITEMS_ADD_LORE, "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK))).make(), click -> {

				final ItemStack cursor = click.cursor;
				if (cursor != null && cursor.getType() != CompMaterial.AIR.get()) {

					final ItemStack currency = cursor.clone();
					final BankEntry locatedEntry = Markets.getBankManager().getEntryByPlayer(click.player.getUniqueId(), currency);

					if (locatedEntry != null) {
						locatedEntry.setQuantity(locatedEntry.getQuantity() + currency.getAmount());
						locatedEntry.sync(result -> {
							if (result == SynchronizeResult.SUCCESS) {
								click.player.setItemOnCursor(CompMaterial.AIR.parseItem());
								updateAndRedraw();
							}
						});

						return;
					}

					Markets.getBankManager().create(click.player, currency, currency.getAmount(), wasCreated -> {
						click.player.setItemOnCursor(CompMaterial.AIR.parseItem());
						updateAndRedraw();
					});
				}
			});
	}

	@Override
	protected ItemStack makeDisplayItem(BankEntry bankEntry) {
		if (this.taxCollection)
			return drawTaxEntry(bankEntry);

		return QuickItem.of(bankEntry.getItem()).lore(TranslationManager.list(this.player, Translations.GUI_BANK_ITEMS_ENTRY_LORE, "entry_quantity", bankEntry.getQuantity(), "left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK), "right_click", TranslationManager.string(this.player, Translations.MOUSE_RIGHT_CLICK))).make();
	}

	private ItemStack drawTaxEntry(final BankEntry bankEntry) {
		final QuickItem quickItem = bankEntry.isCurrencyOfItem() ? QuickItem.of(bankEntry.getCurrencyItem()) : QuickItem.of(Settings.GUI_TAX_BANK_ITEMS_MONEY.getString());

		quickItem.name(TranslationManager.string(Translations.GUI_BANK_TAX_ITEMS_ENTRY_NAME, "entry_name", bankEntry.isCurrencyOfItem() ? ItemUtil.getItemName(bankEntry.getCurrencyItem()) : bankEntry.getCurrencyDisplayName()));
		quickItem.lore(TranslationManager.list(Translations.GUI_BANK_TAX_ITEMS_ENTRY_LORE,
				"entry_quantity", bankEntry.isCurrencyOfItem() ? bankEntry.getQuantity() : bankEntry.getPrice(),
				"left_click", TranslationManager.string(this.player, Translations.MOUSE_LEFT_CLICK)
		));

		return quickItem.make();
	}

	@Override
	protected void onClick(BankEntry bankEntry, GuiClickEvent click) {
		if (click.clickType == ClickType.LEFT) {
			if (this.taxCollection) {
				deleteAndGiveTax(bankEntry, click);

			} else {
				deleteAndGiveEntry(bankEntry, click);
			}
		}

		if (!this.taxCollection)
			if (click.clickType == ClickType.RIGHT) {
				click.gui.exit();
				new TitleInput(Markets.getInstance(), click.player, TranslationManager.string(click.player, Translations.PROMPT_WITHDRAW_ENTRY_TITLE), TranslationManager.string(click.player, Translations.PROMPT_WITHDRAW_ENTRY_SUBTITLE)) {

					@Override
					public void onExit(Player player) {
						click.manager.showGUI(click.player, BankGUI.this);
					}

					@Override
					public boolean onResult(String string) {
						string = ChatColor.stripColor(string);

						if (!MathUtil.isInt(string)) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.NOT_A_NUMBER, "value", string));
							return false;
						}

						final int withdrawAmount = Integer.parseInt(string);

						if (withdrawAmount <= 0) {
							Common.tell(click.player, TranslationManager.string(click.player, Translations.MUST_BE_HIGHER_THAN_ZERO, "value", string));
							return false;
						}

						if (withdrawAmount == bankEntry.getQuantity()) {
							deleteAndGiveEntry(bankEntry, click);
							return true;
						}

					if (withdrawAmount > bankEntry.getQuantity()) {
						Common.tell(click.player, TranslationManager.string(click.player, Translations.INSUFFICIENT_ENTRY_AMOUNT));
						return false;
					}

					// Anti-dupe: Close GUI immediately
					click.gui.exit();
					
					// Update quantity and sync to database first (anti-dupe safeguard)
					bankEntry.setQuantity(bankEntry.getQuantity() - withdrawAmount);
					bankEntry.sync(result -> {
						if (result == SynchronizeResult.FAILURE) {
							if (Markets.getTransactionLogger() != null) {
								Markets.getTransactionLogger().logError("BANK_WITHDRAW", 
									"Player: " + click.player.getName() + ", EntryID: " + bankEntry.getId(), 
									"Failed to sync bank entry");
							}
							return;
						}
						
						// Database update successful - now give items using chunked method
						giveItemsChunked(click.player, bankEntry.getItem(), withdrawAmount);
					});
					return true;
					}
				};
			}
	}

	/**
	 * Gives items to a player in batched stacks and chunks over multiple ticks if needed.
	 * This method prevents server lag from large withdrawals and includes anti-dupe safeguards.
	 *
	 * @param player The player to give items to
	 * @param baseItem The item template to give (amount will be set per stack)
	 * @param totalQuantity Total number of items to give
	 */
	private void giveItemsChunked(@NonNull final Player player, @NonNull final ItemStack baseItem, final int totalQuantity) {
		final int threshold = Settings.BANK_WITHDRAWAL_CHUNK_THRESHOLD.getInt();
		final int chunkSize = Settings.BANK_WITHDRAWAL_CHUNK_SIZE.getInt();
		
		// Convert quantity into stacks
		final List<ItemStack> stacks = createBatchedStacks(baseItem, totalQuantity);
		
		// Check if we should chunk (threshold check: -1 = never, 0 = always, >0 = when exceeds threshold)
		final boolean shouldChunk = threshold >= 0 && (threshold == 0 || totalQuantity > threshold);
		
		if (!shouldChunk) {
			// Give all items immediately
			for (ItemStack stack : stacks) {
				givePlayerItems(player, stack);
			}
			if (Markets.getTransactionLogger() != null) {
				Markets.getTransactionLogger().logBankWithdrawal(player.getName(), 
					ca.tweetzy.flight.utils.ItemUtil.getItemName(baseItem), totalQuantity, false);
			}
			return;
		}
		
		// Chunk the withdrawal over multiple ticks
		final int itemsPerChunk = Math.max(1, chunkSize);
		int stackIndex = 0;
		int tickDelay = 0;
		
		if (Markets.getTransactionLogger() != null) {
			Markets.getTransactionLogger().logBankWithdrawal(player.getName(), 
				ca.tweetzy.flight.utils.ItemUtil.getItemName(baseItem), totalQuantity, true);
		}
		
		while (stackIndex < stacks.size()) {
			final List<ItemStack> chunk = new java.util.ArrayList<>();
			int chunkItemCount = 0;
			
			// Build chunk - add stacks until we reach the items per chunk limit
			while (stackIndex < stacks.size() && chunkItemCount < itemsPerChunk) {
				final ItemStack stack = stacks.get(stackIndex);
				chunk.add(stack);
				chunkItemCount += stack.getAmount();
				stackIndex++;
			}
			
			final int finalChunkItemCount = chunkItemCount;
			final int finalChunkNumber = (tickDelay / 1) + 1;
			final int finalTotalChunks = (int) Math.ceil((double) totalQuantity / itemsPerChunk);
			
			// Schedule this chunk on the main thread
			org.bukkit.Bukkit.getScheduler().runTaskLater(Markets.getInstance(), () -> {
				// Safety check: verify player is still online
				if (!player.isOnline()) {
					// Player logged out - drop items at their last known location
					final org.bukkit.Location dropLocation = player.getLocation();
					if (Markets.getTransactionLogger() != null) {
						Markets.getTransactionLogger().logWarning("BANK_WITHDRAW", 
							"Player: " + player.getName() + ", Stacks: " + chunk.size(), 
							"Player logged out during withdrawal - items dropped at last location");
					}
					for (ItemStack stack : chunk) {
						dropLocation.getWorld().dropItemNaturally(dropLocation, stack);
					}
					return;
				}
				
				// Give items in this chunk
				for (ItemStack stack : chunk) {
					givePlayerItems(player, stack);
				}
				
				// Log progress
				if (Markets.getTransactionLogger() != null) {
					Markets.getTransactionLogger().logBankWithdrawalChunk(player.getName(), 
						finalChunkNumber, finalTotalChunks, finalChunkItemCount);
				}
			}, tickDelay);
			
			tickDelay += 1; // 1 tick delay between chunks
		}
		
		// Log completion (scheduled after all chunks)
		final int finalTickDelay = tickDelay;
		org.bukkit.Bukkit.getScheduler().runTaskLater(Markets.getInstance(), () -> {
			if (player.isOnline() && Markets.getTransactionLogger() != null) {
				Markets.getTransactionLogger().logBankWithdrawalComplete(player.getName(), totalQuantity);
			}
		}, finalTickDelay);
	}
	
	/**
	 * Converts a quantity of items into properly batched stacks.
	 * For example: 40,000 diamonds = 625 stacks of 64 diamonds.
	 *
	 * @param baseItem The item template (amount will be overridden)
	 * @param totalQuantity Total number of items to convert into stacks
	 * @return List of ItemStacks with proper stack amounts
	 */
	private List<ItemStack> createBatchedStacks(@NonNull final ItemStack baseItem, final int totalQuantity) {
		final List<ItemStack> stacks = new java.util.ArrayList<>();
		final int maxStackSize = baseItem.getMaxStackSize();
		
		int remaining = totalQuantity;
		
		// Create full stacks
		while (remaining > 0) {
			final int stackAmount = Math.min(remaining, maxStackSize);
			final ItemStack stack = baseItem.clone();
			stack.setAmount(stackAmount);
			stacks.add(stack);
			remaining -= stackAmount;
		}
		
		return stacks;
	}

	private void givePlayerItems(Player player, ItemStack itemToGive) {
		PlayerInventory inventory = player.getInventory();
		HashMap<Integer, ItemStack> leftoverItems = inventory.addItem(itemToGive);

		if (!leftoverItems.isEmpty()) {
			for (ItemStack leftover : leftoverItems.values()) {
				player.getWorld().dropItemNaturally(player.getLocation(), leftover);
			}
		}
	}

	private void deleteAndGiveEntry(@NonNull final BankEntry bankEntry, @NonNull final GuiClickEvent click) {
		// Anti-dupe: Close GUI immediately to prevent double-clicks
		click.gui.exit();
		
		// Delete from database first (anti-dupe safeguard)
		bankEntry.unStore(result -> {
			if (result == SynchronizeResult.FAILURE) {
				if (Markets.getTransactionLogger() != null) {
					Markets.getTransactionLogger().logError("BANK_ENTRY_DELETE", 
						"Player: " + click.player.getName() + ", EntryID: " + bankEntry.getId(), 
						"Failed to delete bank entry");
				}
				return;
			}
			
			// Database deletion successful - now give items using chunked method
			Markets.newChain().sync(() -> {
				if (Markets.getTransactionLogger() != null) {
					Markets.getTransactionLogger().logBankEntryDelete(click.player.getName(), 
						bankEntry.getId().toString(), "Player withdrew all items");
				}
				giveItemsChunked(click.player, bankEntry.getItem(), bankEntry.getQuantity());
			}).execute();
		});
	}

	private void deleteAndGiveTax(@NonNull final BankEntry bankEntry, @NonNull final GuiClickEvent click) {
		// Anti-dupe: Close GUI immediately to prevent double-clicks
		click.gui.exit();
		
		// Delete from database first (anti-dupe safeguard)
		bankEntry.unStore(result -> {
			if (result == SynchronizeResult.FAILURE) {
				if (Markets.getTransactionLogger() != null) {
					Markets.getTransactionLogger().logError("BANK_ENTRY_DELETE", 
						"Admin: " + click.player.getName() + ", TaxEntryID: " + bankEntry.getId(), 
						"Failed to delete tax bank entry");
				}
				return;
			}
			
			if (Markets.getTransactionLogger() != null) {
				Markets.getTransactionLogger().logBankEntryDelete(click.player.getName(), 
					bankEntry.getId().toString(), "Admin collected tax");
			}
			
			if (bankEntry.isCurrencyOfItem()) {
				// Database deletion successful - now give items using chunked method
				Markets.newChain().sync(() -> {
					giveItemsChunked(click.player, bankEntry.getItem(), bankEntry.getQuantity());
				}).execute();
			} else {
				// For non-item currencies, deposit directly (no chunking needed)
				final String[] currencyData = bankEntry.getCurrency().split("/");
				Markets.getCurrencyManager().deposit(click.player, currencyData[0], currencyData[1], bankEntry.getPrice());
			}
		});
	}

	@Override
	protected List<Integer> fillSlots() {
		return InventoryBorder.getInsideBorders(5);
	}

	private void updateAndRedraw() {
		draw();
	}
}
