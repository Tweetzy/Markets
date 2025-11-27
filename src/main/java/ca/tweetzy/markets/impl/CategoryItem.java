package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.database.annotations.Column;
import ca.tweetzy.flight.database.annotations.Id;
import ca.tweetzy.flight.database.annotations.Ignore;
import ca.tweetzy.flight.database.annotations.Nested;
import ca.tweetzy.flight.database.annotations.Table;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.ItemUtil;
import ca.tweetzy.flight.utils.PlayerUtil;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.currency.TransactionResult;
import ca.tweetzy.markets.api.event.MarketTransactionEvent;
import ca.tweetzy.markets.api.market.TransactionType;
import ca.tweetzy.markets.api.market.core.Category;
import ca.tweetzy.markets.api.market.core.Market;
import ca.tweetzy.markets.api.market.core.MarketItem;
import ca.tweetzy.markets.model.Taxer;
import ca.tweetzy.markets.model.sync.CrossServerNotificationManager;
import ca.tweetzy.markets.model.sync.NotificationEvent;
import ca.tweetzy.markets.model.sync.StockReservationManager;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Table("category_item")
public final class CategoryItem implements MarketItem {

	@Id
	@Column("id")
	private UUID id;
	
	@Column("owning_category")
	private UUID owningCategory;
	
	@Nested
	@Column("item")
	private ItemStack item;
	
	@Column("currency")
	private String currency;
	
	@Nested
	@Column("currency_item")
	private ItemStack currencyItem;
	
	@Column("price")
	private double price;
	
	@Column("stock")
	private volatile int stock;
	
	@Column("price_is_for_all")
	private boolean priceIsForAll;
	
	@Column("accepting_offers")
	private boolean acceptingOffers;
	
	@Column("infinite")
	private boolean infinite;

	@Ignore
	private boolean removeRequested = false;
	
	@Ignore
	private volatile boolean beingEdited;

	@Ignore
	private List<Player> viewingUsers;
	
	@Ignore
	private final Object editLock = new Object();

	public CategoryItem() {
		this.viewingUsers = new ArrayList<>();
	}

	public CategoryItem(
			@NonNull final UUID id,
			@NonNull final UUID owningCategory,
			@NonNull final ItemStack item,
			@NonNull final String currency,
			@NonNull final ItemStack currencyItem,
			final double price,
			final int stock,
			final boolean priceIsForAll,
			final boolean acceptingOffers,
			final boolean infinite
	) {
		this.id = id;
		this.owningCategory = owningCategory;
		this.item = item;
		this.currency = currency;
		this.currencyItem = currencyItem;
		this.price = price;
		this.stock = stock;
		this.priceIsForAll = priceIsForAll;
		this.acceptingOffers = acceptingOffers;
		this.infinite = infinite;
		this.viewingUsers = new ArrayList<>();
	}

	public CategoryItem(@NonNull final UUID owningCategory) {
		this(UUID.randomUUID(), owningCategory, CompMaterial.AIR.parseItem(), Settings.CURRENCY_USE_ITEM_ONLY.getBoolean() ? "Markets/Item" : Settings.CURRENCY_DEFAULT_SELECTED.getString(), CompMaterial.AIR.parseItem(), 1, 0, false, true, false);

		if (Settings.CURRENCY_USE_ITEM_ONLY.getBoolean())
			this.currencyItem = Settings.CURRENCY_ITEM_DEFAULT_SELECTED.getItemStack();
	}

	@Override
	public @NonNull UUID getId() {
		return this.id;
	}

	@Override
	public @NonNull UUID getOwningCategory() {
		return this.owningCategory;
	}

	@Override
	public @NonNull ItemStack getItem() {
		return this.item;
	}

	@Override
	public @NonNull String getCurrency() {
		return this.currency;
	}

	@Override
	public ItemStack getCurrencyItem() {
		return this.currencyItem;
	}

	@Override
	public double getPrice() {
		return this.price;
	}

	@Override
	public int getStock() {
		return this.stock;
	}

	@Override
	public boolean isPriceForAll() {
		return this.priceIsForAll;
	}

	@Override
	public boolean isAcceptingOffers() {
		return this.acceptingOffers;
	}

	@Override
	public void setItem(@NonNull ItemStack item) {
		this.item = item;
	}

	@Override
	public void setCurrency(@NonNull String currency) {
		this.currency = currency;
	}

	@Override
	public void setCurrencyItem(@NonNull ItemStack currencyItem) {
		this.currencyItem = currencyItem;
	}

	@Override
	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public void setStock(int stock) {
		// Validate stock never goes negative
		if (stock < 0) {
			if (Markets.getTransactionLogger() != null) {
				Markets.getTransactionLogger().logWarning("STOCK_UPDATE", 
					"ItemID: " + this.id + ", Requested: " + stock, 
					"Attempted to set negative stock - setting to 0 instead");
			}
			stock = 0;
		}
		this.stock = stock;
	}

	@Override
	public void setPriceIsForAll(boolean priceIsForAll) {
		this.priceIsForAll = priceIsForAll;
	}

	@Override
	public void setIsAcceptingOffers(boolean acceptingOffers) {
		this.acceptingOffers = acceptingOffers;
	}

	@Override
	public boolean isInfinite() {
		return this.infinite;
	}

	@Override
	public void setInfinite(boolean infinite) {
		this.infinite = infinite;
	}

	@Override
	public List<Player> getViewingPlayers() {
		if (this.viewingUsers == null) {
			this.viewingUsers = new ArrayList<>();
		}
		return this.viewingUsers;
	}

	@Override
	public void store(@NonNull Consumer<MarketItem> stored) {
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().createMarketItem(this, (error, created) -> {
			if (error == null && created != null)
				stored.accept(created);
			else if (error != null)
				stored.accept(null);
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		// Prevent deletion while item is being purchased (race condition protection)
		synchronized (this.editLock) {
			if (this.beingEdited) {
				// Item is currently being purchased - cannot delete
				if (Markets.getTransactionLogger() != null) {
					Markets.getTransactionLogger().logWarning("ITEM_REMOVE", 
						"ItemID: " + this.id, 
						"Cannot delete - item is currently being purchased");
				}
				if (syncResult != null) {
					syncResult.accept(SynchronizeResult.FAILURE);
				}
				return;
			}
			// Set beingEdited to prevent new purchases during deletion
			this.beingEdited = true;
		}
		
		// Check for active stock reservations (cross-server protection)
		final StockReservationManager reservationManager = Markets.getStockReservationManager();
		if (reservationManager != null && reservationManager.isReserved(this.id)) {
			// Stock is reserved - wait a bit and retry, or fail
			synchronized (this.editLock) {
				this.beingEdited = false;
			}
			if (Markets.getTransactionLogger() != null) {
				Markets.getTransactionLogger().logWarning("ITEM_REMOVE", 
					"ItemID: " + this.id, 
					"Cannot delete - stock reserved (purchase in progress on another server)");
			}
			if (syncResult != null) {
				syncResult.accept(SynchronizeResult.FAILURE);
			}
			return;
		}
		
		// Use DataManager to ensure retry logic and sync events are handled properly
		Markets.getDataManager().deleteMarketItem(this, (error, deleted) -> {
			// Clear beingEdited flag after deletion completes (success or failure)
			synchronized (this.editLock) {
				this.beingEdited = false;
			}
			
			if (error != null) {
				// Log the error for debugging
				Markets.getInstance().getLogger().severe("Failed to delete market item " + this.id + ": " + error.getMessage());
				if (error.getCause() != null) {
					Markets.getInstance().getLogger().severe("Caused by: " + error.getCause().getMessage());
					error.getCause().printStackTrace();
				} else {
					error.printStackTrace();
				}
			}
			
			if (deleted != null && deleted) {
				getViewingPlayers().forEach(viewingUser -> Common.tell(viewingUser, TranslationManager.string(viewingUser, Translations.ITEM_OUT_OF_STOCK)));

				Category category = Markets.getCategoryManager().getByUUID(this.owningCategory);
				if (category != null) {
					category.getItems().removeIf(categoryItem -> categoryItem.getId().equals(this.id));
				}
				Markets.getCategoryItemManager().remove(this);
			}

			if (syncResult != null)
				syncResult.accept(error == null && deleted != null && deleted ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}

	@Override
	public void sync(@Nullable Consumer<SynchronizeResult> syncResult) {
		// Use DataManager to ensure sync events are published
		Markets.getDataManager().updateMarketItem(this, (error, success) -> {
			if (syncResult != null)
				syncResult.accept(error == null && success ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE);
		});
	}

	@Override
	public void performPurchase(@NonNull final Market market, @NonNull Player buyer, int quantity, Consumer<TransactionResult> transactionResult) {

		// Atomically check and set beingEdited flag to prevent race conditions
		synchronized (this.editLock) {
			if (this.beingEdited) {
				transactionResult.accept(TransactionResult.ERROR);
				Common.tell(buyer, TranslationManager.string(buyer, Translations.ITEM_OUT_OF_STOCK));
				return;
			}
			this.beingEdited = true;
		}

		if (removeRequested) {
			synchronized (this.editLock) {
				this.beingEdited = false;
			}
			transactionResult.accept(TransactionResult.NO_LONGER_AVAILABLE);
			return;
		}

		// Acquire distributed lock for cross-server synchronization
		final StockReservationManager reservationManager = Markets.getStockReservationManager();
		boolean lockAcquired = reservationManager == null || reservationManager.reserveStock(this.id, quantity);
		
		if (!lockAcquired) {
			synchronized (this.editLock) {
				this.beingEdited = false;
			}
			transactionResult.accept(TransactionResult.FAILED_OUT_OF_STOCK);
			Common.tell(buyer, TranslationManager.string(buyer, Translations.ITEM_OUT_OF_STOCK));
			return;
		}
		
		try {
			// Initial stock check
			if (!this.infinite && this.stock == 0) {
				transactionResult.accept(TransactionResult.FAILED_OUT_OF_STOCK);
				Common.tell(buyer, TranslationManager.string(buyer, Translations.ITEM_OUT_OF_STOCK));
				if (reservationManager != null) {
					reservationManager.releaseReservation(this.id);
				}
				synchronized (this.editLock) {
					this.beingEdited = false;
				}
				return;
			}

			final int newPurchaseAmount = this.infinite ? quantity : Math.min(quantity, stock);

			final double subtotal = this.priceIsForAll ? this.price : this.price * newPurchaseAmount;
			final double total = subtotal;

			// Validate currency format before splitting
			if (this.currency == null || this.currency.isEmpty() || !this.currency.contains("/")) {
				transactionResult.accept(TransactionResult.ERROR);
				Common.tell(buyer, TranslationManager.string(buyer, Translations.ITEM_OUT_OF_STOCK));
				if (reservationManager != null) {
					reservationManager.releaseReservation(this.id);
				}
				synchronized (this.editLock) {
					this.beingEdited = false;
				}
				return;
			}

			final String[] currencyParts = this.currency.split("/");
			if (currencyParts.length < 2 || currencyParts[0].isEmpty() || currencyParts[1].isEmpty()) {
				transactionResult.accept(TransactionResult.ERROR);
				Common.tell(buyer, TranslationManager.string(buyer, Translations.ITEM_OUT_OF_STOCK));
				if (reservationManager != null) {
					reservationManager.releaseReservation(this.id);
				}
				synchronized (this.editLock) {
					this.beingEdited = false;
				}
				return;
			}

			final String currencyPlugin = currencyParts[0];
			final String currencyName = currencyParts[1];

			final boolean hasEnoughMoney = this.isCurrencyOfItem() ? Markets.getCurrencyManager().has(buyer, this.currencyItem, (int) Taxer.getTaxedTotal(total)) : Markets.getCurrencyManager().has(buyer, currencyPlugin, currencyName, Taxer.getTaxedTotal(total));

			if (!hasEnoughMoney) {
				transactionResult.accept(TransactionResult.FAILED_NO_MONEY);
				Common.tell(buyer, TranslationManager.string(buyer, Translations.NO_MONEY));
				if (reservationManager != null) {
					reservationManager.releaseReservation(this.id);
				}
				synchronized (this.editLock) {
					this.beingEdited = false;
				}
				return;
			}

			// Check inventory space before withdrawing money - account for item stacking
			final ItemStack updatedItem = this.item.clone();
			updatedItem.setAmount(1);
			int maxStackSize = updatedItem.getMaxStackSize();
			int totalNeeded = newPurchaseAmount;
			
			// Calculate how many slots are needed considering existing stacks
			for (int i = 0; i < buyer.getInventory().getSize(); i++) {
				ItemStack slot = buyer.getInventory().getItem(i);
				if (slot == null || slot.getType().isAir()) {
					// Empty slot can hold maxStackSize items
					totalNeeded -= maxStackSize;
					if (totalNeeded <= 0) break;
				} else if (slot.isSimilar(updatedItem)) {
					// Existing stack of same item - can add more
					int spaceInStack = maxStackSize - slot.getAmount();
					totalNeeded -= spaceInStack;
					if (totalNeeded <= 0) break;
				}
			}
			
			if (totalNeeded > 0) {
				transactionResult.accept(TransactionResult.ERROR);
				Common.tell(buyer, TranslationManager.string(buyer, Translations.ITEM_OUT_OF_STOCK));
				if (reservationManager != null) {
					reservationManager.releaseReservation(this.id);
				}
				synchronized (this.editLock) {
					this.beingEdited = false;
				}
				return;
			}

			final boolean withdrawResult = this.isCurrencyOfItem() ? Markets.getCurrencyManager().withdraw(buyer, this.currencyItem, (int) Taxer.getTaxedTotal(total)) : Markets.getCurrencyManager().withdraw(buyer, currencyPlugin, currencyName, Taxer.getTaxedTotal(total));
			final double tax = this.isCurrencyOfItem() ? (int) Taxer.calculateTaxAmount(total) : Taxer.calculateTaxAmount(total);

			if (!withdrawResult) {
				transactionResult.accept(TransactionResult.ERROR);
				if (reservationManager != null) {
					reservationManager.releaseReservation(this.id);
				}
				synchronized (this.editLock) {
					this.beingEdited = false;
				}
				return;
			}

			// Re-validate stock after money withdrawal (prevents race condition)
			// Also reload stock from database if Redis is enabled to get latest value
			if (!this.infinite) {
				// For cross-server sync, we need to check database stock
				MarketItem reloadedItem = null;
				if (reservationManager != null) {
					// Reload item from database to get latest stock
					reloadedItem = Markets.getCategoryItemManager().getByUUID(this.id);
					// The lock ensures no other server can modify it, but we should still verify
				}
				
				// Use reloaded stock if available, otherwise use current stock
				int currentStock = (reloadedItem != null) ? reloadedItem.getStock() : this.stock;
				
				if (currentStock < newPurchaseAmount) {
					// Rollback money withdrawal
					if (this.isCurrencyOfItem()) {
						Markets.getCurrencyManager().deposit(buyer, this.currencyItem, (int) Taxer.getTaxedTotal(total));
					} else {
						Markets.getCurrencyManager().deposit(buyer, currencyPlugin, currencyName, Taxer.getTaxedTotal(total));
					}
					transactionResult.accept(TransactionResult.FAILED_OUT_OF_STOCK);
					Common.tell(buyer, TranslationManager.string(buyer, Translations.ITEM_OUT_OF_STOCK));
					if (reservationManager != null) {
						reservationManager.releaseReservation(this.id);
					}
					synchronized (this.editLock) {
						this.beingEdited = false;
					}
					return;
				}
				
				// Update local stock from reloaded value if available
				if (reloadedItem != null) {
					this.stock = currentStock;
				}
			}

			// Store original stock for potential rollback
			final int originalStock = this.stock;
			final int newStock = this.infinite ? originalStock : (this.stock - newPurchaseAmount);
			final OfflinePlayer seller = Bukkit.getOfflinePlayer(market.getOwnerUUID());

			// Update stock if not infinite (before giving items to ensure consistency)
			if (!this.infinite) {
				setStock(newStock);
			}

			// Give items to buyer
			try {
				for (int i = 0; i < newPurchaseAmount; i++) {
					PlayerUtil.giveItem(buyer, updatedItem);
				}
			} catch (Exception e) {
				if (Markets.getTransactionLogger() != null) {
					Markets.getTransactionLogger().logError("ITEM_PURCHASE", 
						"Buyer: " + buyer.getName() + ", ItemID: " + this.id + ", Quantity: " + newPurchaseAmount, 
						"Failed to give items: " + e.getMessage());
				}
				e.printStackTrace();
				// Rollback stock if items couldn't be given
				if (!this.infinite) {
					setStock(originalStock);
				}
				// Rollback money
				if (this.isCurrencyOfItem()) {
					Markets.getCurrencyManager().deposit(buyer, this.currencyItem, (int) Taxer.getTaxedTotal(total));
				} else {
					Markets.getCurrencyManager().deposit(buyer, currencyPlugin, currencyName, Taxer.getTaxedTotal(total));
				}
				transactionResult.accept(TransactionResult.ERROR);
				Common.tell(buyer, TranslationManager.string(buyer, Translations.ITEM_OUT_OF_STOCK));
				return;
			}

			// Update stock if not infinite
			if (!this.infinite) {
				// If stock reached 0, notify viewing users and sync to database
				// Items remain in category but are hidden from non-owners (handled by getInStockItems)
				if (newStock <= 0) {
					getViewingPlayers().forEach(viewingUser -> {
						try {
							viewingUser.closeInventory();
							Common.tell(viewingUser, TranslationManager.string(viewingUser, Translations.ITEM_OUT_OF_STOCK));
						} catch (Exception e) {
							if (Markets.getTransactionLogger() != null) {
								Markets.getTransactionLogger().logWarning("STOCK_UPDATE", 
									"User: " + viewingUser.getName() + ", ItemID: " + this.id, 
									"Error notifying user of out of stock: " + e.getMessage());
							}
						}
					});
					
					// Always sync stock to 0 - never delete items
					sync(result -> {
						if (result == SynchronizeResult.FAILURE) {
							if (Markets.getTransactionLogger() != null) {
								Markets.getTransactionLogger().logError("STOCK_UPDATE", 
									"ItemID: " + this.id + ", Buyer: " + buyer.getName() + 
									", OriginalStock: " + originalStock + ", PurchaseAmount: " + newPurchaseAmount, 
									"CRITICAL: Failed to sync stock to 0 - attempting rollback");
							}
							// Attempt to restore stock to prevent duplication
							setStock(originalStock);
							sync(rollbackResult -> {
								if (rollbackResult == SynchronizeResult.FAILURE && Markets.getTransactionLogger() != null) {
									Markets.getTransactionLogger().logError("STOCK_UPDATE", 
										"ItemID: " + this.id, 
										"CRITICAL: Failed to rollback stock - manual intervention required!");
								}
							});
						} else {
							// Log successful purchase
							if (Markets.getTransactionLogger() != null) {
								Category owningCat = Markets.getCategoryManager().getByUUID(this.owningCategory);
								Market owningMarket = owningCat != null ? Markets.getMarketManager().getByUUID(owningCat.getOwningMarket()) : null;
								String marketName = owningMarket != null ? owningMarket.getDisplayName() : "Unknown";
								Markets.getTransactionLogger().logItemPurchase(buyer.getName(), 
									ItemUtil.getItemName(this.item), newPurchaseAmount, total, 
									this.currency, marketName, seller.getName(), true);
							}
						}
						if (!market.isServerMarket()) {
							alertOutOfStock(seller, buyer, newPurchaseAmount);
						}
					});
				} else {
					// Stock still available - sync the update
					sync(result -> {
						if (result == SynchronizeResult.FAILURE) {
							if (Markets.getTransactionLogger() != null) {
								Markets.getTransactionLogger().logError("STOCK_UPDATE", 
									"ItemID: " + this.id + ", Buyer: " + buyer.getName() + 
									", OriginalStock: " + originalStock + ", PurchaseAmount: " + newPurchaseAmount + 
									", ExpectedNewStock: " + newStock, 
									"CRITICAL: Failed to sync stock - attempting rollback");
							}
							// Attempt to restore stock to prevent duplication
							setStock(originalStock);
							sync(rollbackResult -> {
								if (rollbackResult == SynchronizeResult.FAILURE && Markets.getTransactionLogger() != null) {
									Markets.getTransactionLogger().logError("STOCK_UPDATE", 
										"ItemID: " + this.id, 
										"CRITICAL: Failed to rollback stock - manual intervention required!");
								}
							});
						} else {
							// Log successful purchase
							if (Markets.getTransactionLogger() != null) {
								Category owningCat = Markets.getCategoryManager().getByUUID(this.owningCategory);
								Market owningMarket = owningCat != null ? Markets.getMarketManager().getByUUID(owningCat.getOwningMarket()) : null;
								String marketName = owningMarket != null ? owningMarket.getDisplayName() : "Unknown";
								Markets.getTransactionLogger().logItemPurchase(buyer.getName(), 
									ItemUtil.getItemName(this.item), newPurchaseAmount, total, 
									this.currency, marketName, seller.getName(), true);
							}
						}
						if (!market.isServerMarket()) {
							// Send purchase notification (cross-server or local)
							Map<String, Object> notificationData = new HashMap<>();
							notificationData.put("buyer_name", buyer.getName());
							notificationData.put("item_name", ItemUtil.getItemName(this.item));
							notificationData.put("purchase_quantity", newPurchaseAmount);
							notificationData.put("purchase_price", isCurrencyOfItem() ? total : (int) total);
							
							CrossServerNotificationManager notificationManager = Markets.getNotificationManager();
							if (notificationManager != null) {
								notificationManager.sendNotification(seller.getUniqueId(), 
									NotificationEvent.NotificationType.PURCHASE, notificationData);
							} else if (seller.isOnline()) {
								// Fallback to local notification
								Common.tell(seller.getPlayer(), TranslationManager.string(seller.getPlayer(), Translations.MARKET_ITEM_BOUGHT_SELLER,
										"purchase_price", isCurrencyOfItem() ? total : (int) total,
										"purchase_quantity", newPurchaseAmount,
										"item_name", ItemUtil.getItemName(this.item),
										"buyer_name", buyer.getName()
								));
							}
						}
					});
				}
			} else {
				// Infinite stock - just send notification
				if (!market.isServerMarket()) {
					// Send purchase notification (cross-server or local)
					Map<String, Object> notificationData = new HashMap<>();
					notificationData.put("buyer_name", buyer.getName());
					notificationData.put("item_name", ItemUtil.getItemName(this.item));
					notificationData.put("purchase_quantity", newPurchaseAmount);
					notificationData.put("purchase_price", isCurrencyOfItem() ? total : (int) total);
					
					CrossServerNotificationManager notificationManager = Markets.getNotificationManager();
					if (notificationManager != null) {
						notificationManager.sendNotification(seller.getUniqueId(), 
							NotificationEvent.NotificationType.PURCHASE, notificationData);
					} else if (seller.isOnline()) {
						// Fallback to local notification
						Common.tell(seller.getPlayer(), TranslationManager.string(seller.getPlayer(), Translations.MARKET_ITEM_BOUGHT_SELLER,
								"purchase_price", isCurrencyOfItem() ? total : (int) total,
								"purchase_quantity", newPurchaseAmount,
								"item_name", ItemUtil.getItemName(this.item),
								"buyer_name", buyer.getName()
						));
					}
				}
			}

			// Pay seller
			if (!market.isServerMarket()) {
				try {
					if (isCurrencyOfItem()) {
						if (seller.isOnline() && seller.getPlayer() != null) {
							boolean depositSuccess = Markets.getCurrencyManager().deposit(seller.getPlayer(), this.currencyItem, (int) total);
							if (!depositSuccess && Markets.getTransactionLogger() != null) {
								Markets.getTransactionLogger().logWarning("ITEM_PURCHASE", 
									"Seller: " + seller.getName() + ", Item: " + ItemUtil.getItemName(this.item) + ", Amount: " + (int) total, 
									"Failed to deposit item currency to seller");
							}
						} else {
							Markets.getOfflineItemPaymentManager().create(
									seller.getUniqueId(),
									this.currencyItem,
									(int) total,
									TranslationManager.string(seller.getPlayer(), Translations.MARKET_ITEM_BOUGHT_SELLER,
											"purchase_price", isCurrencyOfItem() ? total : (int) total,
											"purchase_quantity", newPurchaseAmount,
											"item_name", ItemUtil.getItemName(this.item),
											"buyer_name", buyer.getName()
									), created -> {
										if (created == null && Markets.getTransactionLogger() != null) {
											Markets.getTransactionLogger().logError("ITEM_PURCHASE", 
												"Seller: " + seller.getUniqueId() + ", Item: " + ItemUtil.getItemName(this.item) + ", Amount: " + (int) total, 
												"Failed to create offline payment for seller");
										}
									});
						}
					} else {
						boolean depositSuccess = Markets.getCurrencyManager().deposit(seller, currencyPlugin, currencyName, total);
						if (!depositSuccess && Markets.getTransactionLogger() != null) {
							Markets.getTransactionLogger().logWarning("ITEM_PURCHASE", 
								"Seller: " + seller.getName() + ", Currency: " + currencyPlugin + "/" + currencyName + ", Amount: " + total, 
								"Failed to deposit currency to seller");
						}
					}
				} catch (Exception e) {
					if (Markets.getTransactionLogger() != null) {
						Markets.getTransactionLogger().logError("ITEM_PURCHASE", 
							"Seller: " + seller.getName(), 
							"Error paying seller: " + e.getMessage());
					}
					e.printStackTrace();
				}
			}

			// insert tax
			if (Settings.SEND_TAX_TO_SERVER_ACCOUNT.getBoolean()) {
				try {
					Markets.getBankManager().createTaxEntry(
							this,
							newPurchaseAmount,
							tax,
							created -> {
								if (created == null && Markets.getTransactionLogger() != null) {
									Markets.getTransactionLogger().logWarning("TAX_ENTRY_CREATE", 
										"Item: " + ItemUtil.getItemName(this.item) + ", Tax: " + tax, 
										"Failed to create tax entry for purchase");
								}
							}
					);
				} catch (Exception e) {
					if (Markets.getTransactionLogger() != null) {
						Markets.getTransactionLogger().logWarning("TAX_ENTRY_CREATE", 
							"Item: " + ItemUtil.getItemName(this.item), 
							"Error creating tax entry: " + e.getMessage());
					}
					// Don't fail the purchase if tax entry fails
				}
			}

			Common.tell(buyer, TranslationManager.string(buyer, Translations.MARKET_ITEM_BOUGHT_BUYER,
					"purchase_price", isCurrencyOfItem() ? total : (int) total,
					"purchase_quantity", newPurchaseAmount,
					"item_name", ItemUtil.getItemName(this.item),
					"seller_name", market.getOwnerName()
			));

			// call transaction event
			final double totalFixed = isCurrencyOfItem() ? (int) total : total;
			Bukkit.getServer().getPluginManager().callEvent(new MarketTransactionEvent(
					buyer,
					seller,
					TransactionType.ITEM_PURCHASE,
					this.item,
					getCurrencyDisplayName(),
					newPurchaseAmount,
					totalFixed
			));

			transactionResult.accept(TransactionResult.SUCCESS);
		} finally {
			// Always clear the beingEdited flag, even if an error occurred
			synchronized (this.editLock) {
				this.beingEdited = false;
			}
			
			// Release distributed lock
			if (reservationManager != null) {
				reservationManager.releaseReservation(this.id);
			}
		}
	}

	@Override
	public boolean removeRequested() {
		return this.removeRequested;
	}

	@Override
	public void setRemoveRequested() {
		this.removeRequested = true;
	}

	@Override
	public boolean isBeingEdited() {
		return this.beingEdited;
	}

	@Override
	public void setBeingEdited(boolean edited) {
		this.beingEdited = edited;
	}

	@Override
	public void addStock(@NonNull final ItemStack item, @NonNull final Consumer<SynchronizeResult> resultConsumer) {
		// Prevent stock addition while item is being purchased (race condition protection)
		synchronized (this.editLock) {
			if (this.beingEdited) {
				// Item is currently being purchased - cannot add stock
				if (Markets.getTransactionLogger() != null) {
					Markets.getTransactionLogger().logWarning("STOCK_ADD", 
						"ItemID: " + this.id, 
						"Cannot add stock - item is currently being purchased");
				}
				if (resultConsumer != null) {
					resultConsumer.accept(SynchronizeResult.FAILURE);
				}
				return;
			}
			// Set beingEdited to prevent new purchases during stock addition
			this.beingEdited = true;
		}
		
		// Check for active stock reservations (cross-server protection)
		final StockReservationManager reservationManager = Markets.getStockReservationManager();
		if (reservationManager != null && reservationManager.isReserved(this.id)) {
			// Stock is reserved - cannot add stock during purchase
			synchronized (this.editLock) {
				this.beingEdited = false;
			}
			if (Markets.getTransactionLogger() != null) {
				Markets.getTransactionLogger().logWarning("STOCK_ADD", 
					"ItemID: " + this.id, 
					"Cannot add stock - stock reserved (purchase in progress on another server)");
			}
			if (resultConsumer != null) {
				resultConsumer.accept(SynchronizeResult.FAILURE);
			}
			return;
		}
		
		try {
			if (getItem().isSimilar(item)) {
				// Validate stock won't go negative (safety check)
				int newStock = this.stock + item.getAmount();
				if (newStock < 0) {
					if (Markets.getTransactionLogger() != null) {
						Markets.getTransactionLogger().logWarning("STOCK_ADD", 
							"ItemID: " + this.id + ", Current: " + this.stock + ", Adding: " + item.getAmount(), 
							"Stock addition would result in negative - setting to 0");
					}
					newStock = 0;
				}
				
				setStock(newStock);
				sync(result -> {
					// Clear beingEdited flag after sync completes
					synchronized (this.editLock) {
						this.beingEdited = false;
					}
					
					if (resultConsumer != null) {
						resultConsumer.accept(result);
					}
				});
			} else {
				// Item doesn't match - release lock and fail
				synchronized (this.editLock) {
					this.beingEdited = false;
				}
				if (resultConsumer != null) {
					resultConsumer.accept(SynchronizeResult.FAILURE);
				}
			}
		} catch (Exception e) {
			// Ensure lock is released on error
			synchronized (this.editLock) {
				this.beingEdited = false;
			}
			if (Markets.getTransactionLogger() != null) {
				Markets.getTransactionLogger().logError("STOCK_ADD", 
					"ItemID: " + this.id, 
					"Error adding stock: " + e.getMessage());
			}
			e.printStackTrace();
			if (resultConsumer != null) {
				resultConsumer.accept(SynchronizeResult.FAILURE);
			}
		}
	}

	private void alertOutOfStock(final OfflinePlayer seller, @NonNull final Player buyer, final int newPurchaseAmount) {
		// Send purchase notification first
		Map<String, Object> purchaseData = new HashMap<>();
		purchaseData.put("buyer_name", buyer.getName());
		purchaseData.put("item_name", ItemUtil.getItemName(this.item));
		purchaseData.put("purchase_quantity", newPurchaseAmount);
		
		CrossServerNotificationManager notificationManager = Markets.getNotificationManager();
		if (notificationManager != null) {
			notificationManager.sendNotification(seller.getUniqueId(), 
				NotificationEvent.NotificationType.PURCHASE, purchaseData);
			
			// Send out of stock notification
			Map<String, Object> outOfStockData = new HashMap<>();
			outOfStockData.put("item_name", ItemUtil.getItemName(this.item));
			notificationManager.sendNotification(seller.getUniqueId(), 
				NotificationEvent.NotificationType.OUT_OF_STOCK, outOfStockData);
		} else if (seller.isOnline()) {
			// Fallback to local notification
			Common.tell(seller.getPlayer(), TranslationManager.string(seller.getPlayer(), Translations.MARKET_ITEM_BOUGHT_SELLER,
					"purchase_quantity", newPurchaseAmount,
					"item_name", ItemUtil.getItemName(this.item),
					"buyer_name", buyer.getName()
			));

			Common.tell(seller.getPlayer(), TranslationManager.string(seller.getPlayer(), Translations.MARKET_ITEM_OUT_OF_STOCK, "item_name", ItemUtil.getItemName(this.item)));
		}
	}
}
