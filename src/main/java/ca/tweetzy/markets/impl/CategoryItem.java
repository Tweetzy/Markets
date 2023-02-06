package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.settings.TranslationManager;
import ca.tweetzy.flight.utils.Common;
import ca.tweetzy.flight.utils.ItemUtil;
import ca.tweetzy.flight.utils.PlayerUtil;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.SynchronizeResult;
import ca.tweetzy.markets.api.currency.TransactionResult;
import ca.tweetzy.markets.api.market.Market;
import ca.tweetzy.markets.api.market.MarketItem;
import ca.tweetzy.markets.model.Taxer;
import ca.tweetzy.markets.settings.Settings;
import ca.tweetzy.markets.settings.Translations;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class CategoryItem implements MarketItem {

	private final UUID id;
	private final UUID owningCategory;
	private ItemStack item;
	private String currency;
	private ItemStack currencyItem;
	private double price;
	private int stock;
	private boolean priceIsForAll;
	private boolean acceptingOffers;

	private final List<Player> viewingUsers;

	public CategoryItem(
			@NonNull final UUID id,
			@NonNull final UUID owningCategory,
			@NonNull final ItemStack item,
			@NonNull final String currency,
			@NonNull final ItemStack currencyItem,
			final double price,
			final int stock,
			final boolean priceIsForAll,
			final boolean acceptingOffers
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
		this.viewingUsers = new ArrayList<>();
	}

	public CategoryItem(@NonNull final UUID owningCategory) {
		this(UUID.randomUUID(), owningCategory, CompMaterial.AIR.parseItem(), Settings.CURRENCY_DEFAULT_SELECTED.getString(), CompMaterial.AIR.parseItem(), 1, 0, false, true);
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
	public List<Player> getViewingPlayers() {
		return this.viewingUsers;
	}

	@Override
	public void store(@NonNull Consumer<MarketItem> stored) {
		Markets.getDataManager().createMarketItem(this, (error, created) -> {
			if (error == null)
				stored.accept(created);
		});
	}

	@Override
	public void unStore(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getDataManager().deleteMarketItem(this, (error, updateStatus) -> {
			if (updateStatus) {

				getViewingPlayers().forEach(viewingUser -> Common.tell(viewingUser, TranslationManager.string(viewingUser, Translations.ITEM_OUT_OF_STOCK)));

				Markets.getCategoryManager().getByUUID(this.owningCategory).getItems().removeIf(category -> category.getId().equals(this.id));
				Markets.getCategoryItemManager().remove(this);
			}

			if (syncResult != null)
				syncResult.accept(error == null ? updateStatus ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE : SynchronizeResult.FAILURE);
		});
	}

	@Override
	public void sync(@Nullable Consumer<SynchronizeResult> syncResult) {
		Markets.getDataManager().updateMarketItem(this, (error, updateStatus) -> {
			if (syncResult != null)
				syncResult.accept(error == null ? updateStatus ? SynchronizeResult.SUCCESS : SynchronizeResult.FAILURE : SynchronizeResult.FAILURE);
		});
	}

	@Override
	public void performPurchase(@NonNull final Market market, @NonNull Player buyer, int quantity, Consumer<TransactionResult> transactionResult) {
		if (this.stock == 0) {//todo add check to prevent multiple purchases
			transactionResult.accept(TransactionResult.FAILED_OUT_OF_STOCK);
			Common.tell(buyer, TranslationManager.string(buyer, Translations.ITEM_OUT_OF_STOCK));
			return;
		}

		final int newPurchaseAmount = Math.min(quantity, stock);

		final double subtotal = this.priceIsForAll ? this.price : this.price * newPurchaseAmount;
		final double total = subtotal;

		final String currencyPlugin = this.currency.split("/")[0];
		final String currencyName = this.currency.split("/")[1];

		final boolean hasEnoughMoney = this.isCurrencyOfItem() ? Markets.getCurrencyManager().has(buyer, this.currencyItem, (int) Taxer.getTaxedTotal(total)) : Markets.getCurrencyManager().has(buyer, currencyPlugin, currencyName, Taxer.getTaxedTotal(total));

		if (!hasEnoughMoney) {
			Common.tell(buyer, TranslationManager.string(buyer, Translations.NO_MONEY));
			transactionResult.accept(TransactionResult.FAILED_NO_MONEY);
			return;
		}

		final boolean withdrawResult = this.isCurrencyOfItem() ? Markets.getCurrencyManager().withdraw(buyer, this.currencyItem, (int) Taxer.getTaxedTotal(total)) : Markets.getCurrencyManager().withdraw(buyer, currencyPlugin, currencyName, Taxer.getTaxedTotal(total));

		if (withdrawResult) {
			final ItemStack updatedItem = this.item.clone();
			updatedItem.setAmount(1);

			for (int i = 0; i < newPurchaseAmount; i++)
				PlayerUtil.giveItem(buyer, updatedItem);

			final int newStock = this.stock - newPurchaseAmount;
			final OfflinePlayer seller = Bukkit.getOfflinePlayer(market.getOwnerUUID());

			if (newStock <= 0) {
				getViewingPlayers().forEach(viewingUser -> {
					viewingUser.closeInventory();
					Common.tell(viewingUser, TranslationManager.string(viewingUser, Translations.ITEM_OUT_OF_STOCK));
				});

				unStore(result -> {
					if (seller.isOnline()) {
						Common.tell(seller.getPlayer(), TranslationManager.string(seller.getPlayer(), Translations.MARKET_ITEM_BOUGHT_SELLER,
								"purchase_quantity", newPurchaseAmount,
								"item_name", ItemUtil.getStackName(this.item),
								"buyer_name", buyer.getName()
						));

						Common.tell(seller.getPlayer(), TranslationManager.string(seller.getPlayer(), Translations.MARKET_ITEM_OUT_OF_STOCK, "item_name", ItemUtil.getStackName(this.item)));
					}
				});
			} else {
				setStock(newStock);
				sync(result -> {
					if (seller.isOnline()) {
						Common.tell(seller.getPlayer(), TranslationManager.string(seller.getPlayer(), Translations.MARKET_ITEM_BOUGHT_SELLER,
								"purchase_quantity", newPurchaseAmount,
								"item_name", ItemUtil.getStackName(this.item),
								"buyer_name", buyer.getName()
						));
					}
				});
			}

			if (isCurrencyOfItem()) {
				if (seller.isOnline() && seller.getPlayer() != null)
					Markets.getCurrencyManager().deposit(seller.getPlayer(), this.currencyItem, (int) total);
				else
					Markets.getOfflineItemPaymentManager().create(
							seller.getUniqueId(),
							this.currencyItem,
							(int) total,
							TranslationManager.string(seller.getPlayer(), Translations.MARKET_ITEM_BOUGHT_SELLER,
									"purchase_quantity", newPurchaseAmount,
									"item_name", ItemUtil.getStackName(this.item),
									"buyer_name", buyer.getName()
							), created -> {
								// todo maybe do something here
							});
			} else {
				Markets.getCurrencyManager().deposit(seller, currencyPlugin, currencyName, total);
			}

			Common.tell(buyer, TranslationManager.string(buyer, Translations.MARKET_ITEM_BOUGHT_BUYER,
					"purchase_quantity", newPurchaseAmount,
					"item_name", ItemUtil.getStackName(this.item),
					"seller_name", market.getOwnerName()
			));

			transactionResult.accept(TransactionResult.SUCCESS);
			return;
		}

		transactionResult.accept(TransactionResult.ERROR);
	}
}
