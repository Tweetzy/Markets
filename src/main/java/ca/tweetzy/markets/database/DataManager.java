package ca.tweetzy.markets.database;

import ca.tweetzy.flight.comp.enums.CompMaterial;
import ca.tweetzy.flight.database.Callback;
import ca.tweetzy.flight.database.DataManagerAbstract;
import ca.tweetzy.flight.database.DatabaseConnector;
import ca.tweetzy.flight.database.UpdateCallback;
import ca.tweetzy.flight.database.query.QueryBuilder;
import ca.tweetzy.flight.database.sync.DatabaseEvent;
import ca.tweetzy.flight.utils.SerializeUtil;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.api.currency.Payment;
import ca.tweetzy.markets.database.repository.MarketsEntityMapper;
import ca.tweetzy.markets.database.repository.*;
import ca.tweetzy.markets.api.market.*;
import ca.tweetzy.markets.api.market.core.*;
import ca.tweetzy.markets.api.market.layout.Layout;
import ca.tweetzy.markets.api.market.offer.Offer;
import ca.tweetzy.markets.impl.*;
import ca.tweetzy.markets.impl.layout.HomeLayout;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class DataManager extends DataManagerAbstract {
	
	private QueryBuilder queryBuilder;

	public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
		super(databaseConnector, plugin);
		this.queryBuilder = new QueryBuilder(databaseConnector, getTablePrefix());
	}

	public void createMarket(@NonNull final AbstractMarket market, final Callback<AbstractMarket> callback) {
		// Use repository for create/update (upsert)
		if (market instanceof PlayerMarket) {
			Markets.getMarketRepository().save((PlayerMarket) market, (error, saved) -> {
				if (error == null && saved != null) {
					// Publish database event for cross-server sync
					publishEntityEvent(DatabaseEvent.EventType.INSERT, "markets", extractEntityData(market));
					
					if (callback != null) {
						// Fetch the saved market to return
						Markets.getMarketRepository().findById(market.getId(), (findError, found) -> {
							if (findError == null && found != null) {
								callback.accept(null, found);
							} else {
								// Fetch failed, but save succeeded - return original market to ensure cache is updated
								// This prevents markets from not appearing on the server where they were created
								if (findError != null) {
									Markets.getInstance().getLogger().warning("Failed to fetch saved market after creation due to connection error, using original market. Market ID: " + market.getId());
									Markets.getInstance().getLogger().warning("Error: " + findError.getMessage());
									if (isConnectionError(findError)) {
										findError.printStackTrace();
									}
								}
								// Return original market if fetch fails, since save succeeded
								callback.accept(null, market);
							}
						});
					}
				} else {
					if (error != null) {
						Markets.getInstance().getLogger().severe("Failed to create market. Market ID: " + market.getId() + ", Owner: " + market.getOwnerName());
						Markets.getInstance().getLogger().severe("Error: " + error.getMessage());
						if (error instanceof SQLException) {
							SQLException sqlEx = (SQLException) error;
							Markets.getInstance().getLogger().severe("SQL State: " + sqlEx.getSQLState());
							Markets.getInstance().getLogger().severe("Error Code: " + sqlEx.getErrorCode());
						}
						error.printStackTrace();
					}
					if (callback != null) {
						callback.accept(error, null);
					}
				}
			});
		} else {
			Exception unsupportedError = new Exception("Unsupported market type: " + (market != null ? market.getClass().getName() : "null"));
			Markets.getInstance().getLogger().severe("Failed to create market: " + unsupportedError.getMessage());
			if (callback != null) {
				callback.accept(unsupportedError, null);
			}
		}
	}

	public void updateMarket(@NonNull final AbstractMarket market, final Callback<Boolean> callback) {
		// Use repository save (upsert)
		if (market instanceof PlayerMarket) {
			Markets.getMarketRepository().save((PlayerMarket) market, (error, saved) -> {
				if (error == null && saved != null) {
					// Publish database event for cross-server sync
					publishEntityEvent(DatabaseEvent.EventType.UPDATE, "markets", extractEntityData(market));
				}
				
				if (callback != null) {
					callback.accept(error, error == null);
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported market type"), false);
			}
		}
	}

	public void deleteMarket(@NonNull final AbstractMarket market, Callback<Boolean> callback) {
		Markets.getMarketRepository().deleteById(market.getId(), (error, deleted) -> {
			if (error == null && deleted != null && deleted) {
				// Publish database event for cross-server sync
				Map<String, Object> data = new HashMap<>();
				data.put("id", market.getId().toString());
				publishEntityEvent(DatabaseEvent.EventType.DELETE, "markets", data);
			}
			
			if (callback != null) {
				callback.accept(error, deleted != null && deleted);
			}
		});
	}


	public void getMarkets(@NonNull final Callback<List<AbstractMarket>> callback) {
		Markets.getMarketRepository().findAll((error, markets) -> {
			if (callback != null) {
				if (error == null && markets != null) {
					@SuppressWarnings("unchecked")
					List<AbstractMarket> abstractMarkets = (List<AbstractMarket>) (List<?>) markets;
					callback.accept(null, abstractMarkets);
				} else {
					callback.accept(error, null);
				}
			}
		});
	}

	public void createCategory(@NonNull final Category category, final Callback<Category> callback) {
		if (category instanceof MarketCategory) {
			Markets.getCategoryRepository().save((MarketCategory) category, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.INSERT, "category", extractEntityData(category));
				}
				
				if (callback != null) {
					if (error == null) {
						Markets.getCategoryRepository().findById(category.getId(), (findError, found) -> {
							if (findError == null && found != null) {
								callback.accept(null, found);
							} else {
								if (findError != null) {
									Markets.getInstance().getLogger().severe("Failed to fetch saved category after creation. Category ID: " + category.getId());
									Markets.getInstance().getLogger().severe("Error: " + findError.getMessage());
									findError.printStackTrace();
								}
								callback.accept(findError, null);
							}
						});
					} else {
						Markets.getInstance().getLogger().severe("Failed to create category. Category ID: " + category.getId() + ", Name: " + category.getName() + ", Market: " + category.getOwningMarket());
						Markets.getInstance().getLogger().severe("Error: " + error.getMessage());
						if (error instanceof SQLException) {
							SQLException sqlEx = (SQLException) error;
							Markets.getInstance().getLogger().severe("SQL State: " + sqlEx.getSQLState());
							Markets.getInstance().getLogger().severe("Error Code: " + sqlEx.getErrorCode());
						}
						error.printStackTrace();
						callback.accept(error, null);
					}
				}
			});
		} else {
			Exception unsupportedError = new Exception("Unsupported category type: " + (category != null ? category.getClass().getName() : "null"));
			Markets.getInstance().getLogger().severe("Failed to create category: " + unsupportedError.getMessage());
			if (callback != null) {
				callback.accept(unsupportedError, null);
			}
		}
	}

	public void updateCategory(@NonNull final Category category, final Callback<Boolean> callback) {
		if (category instanceof MarketCategory) {
			Markets.getCategoryRepository().save((MarketCategory) category, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.UPDATE, "category", extractEntityData(category));
				}
				
				if (callback != null) {
					callback.accept(error, error == null);
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported category type"), false);
			}
		}
	}

	public void deleteCategory(@NonNull final Category category, Callback<Boolean> callback) {
		Markets.getCategoryRepository().deleteById(category.getId(), (error, deleted) -> {
			if (error == null && deleted != null && deleted) {
				Map<String, Object> data = new HashMap<>();
				data.put("id", category.getId().toString());
				publishEntityEvent(DatabaseEvent.EventType.DELETE, "category", data);
			}
			
			if (callback != null) {
				callback.accept(error, deleted != null && deleted);
			}
		});
	}

	public void getCategories(@NonNull final Callback<List<Category>> callback) {
		Markets.getCategoryRepository().findAll((error, categories) -> {
			if (callback != null) {
				if (error == null && categories != null) {
					@SuppressWarnings("unchecked")
					List<Category> categoryList = (List<Category>) (List<?>) categories;
					callback.accept(null, categoryList);
				} else {
					callback.accept(error, null);
				}
			}
		});
	}

	public void createMarketItem(@NonNull final MarketItem marketItem, final Callback<MarketItem> callback) {
		if (marketItem instanceof CategoryItem) {
			Markets.getMarketItemRepository().save((CategoryItem) marketItem, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.INSERT, "category_item", extractEntityData(marketItem));
				}
				
				if (callback != null) {
					if (error == null) {
						// Try to fetch the saved item, but if fetch fails, return the original item
						// This ensures items are added to cache even if there's a connection issue during fetch
						Markets.getMarketItemRepository().findById(marketItem.getId(), (findError, found) -> {
							if (findError == null && found != null) {
								callback.accept(null, found);
							} else {
								// Fetch failed, but save succeeded - return original item to ensure cache is updated
								// This prevents items from not appearing on the server where they were added
								if (findError != null && isConnectionError(findError)) {
									Markets.getInstance().getLogger().warning("Failed to fetch saved market item due to connection error, using original item. Item ID: " + marketItem.getId());
								}
								callback.accept(null, marketItem); // Return original item if fetch fails
							}
						});
					} else {
						callback.accept(error, null);
					}
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported market item type"), null);
			}
		}
	}

	public void updateMarketItem(@NonNull final MarketItem marketItem, final Callback<Boolean> callback) {
		if (marketItem instanceof CategoryItem) {
			Markets.getMarketItemRepository().save((CategoryItem) marketItem, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.UPDATE, "category_item", extractEntityData(marketItem));
				}
				
				if (callback != null) {
					callback.accept(error, error == null);
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported market item type"), false);
			}
		}
	}

	public void deleteMarketItem(@NonNull final MarketItem marketItem, Callback<Boolean> callback) {
		// Use retry logic for item deletion
		retryDatabaseOperation(
			(operationCallback) -> {
				Markets.getMarketItemRepository().deleteById(marketItem.getId(), (error, deleted) -> {
					if (error == null && deleted != null && deleted) {
						Map<String, Object> data = new HashMap<>();
						data.put("id", marketItem.getId().toString());
						publishEntityEvent(DatabaseEvent.EventType.DELETE, "category_item", data);
					}
					operationCallback.accept(error, deleted != null && deleted);
				});
			},
			(error, result) -> {
				if (callback != null) {
					callback.accept(error, (Boolean) result);
				}
			},
			3 // Max 3 retries
		);
	}

	public void deleteMarketItems(@NonNull final Category category, Callback<Boolean> callback) {
		// Use QueryBuilder for custom query
		queryBuilder.delete("category_item")
			.where("owning_category", category.getId().toString())
			.execute((error, affectedRows) -> {
				if (callback != null) {
					callback.accept(error, affectedRows != null && affectedRows > 0);
				}
			});
	}

	public void getMarketItems(@NonNull final Callback<List<MarketItem>> callback) {
		Markets.getMarketItemRepository().findAll((error, items) -> {
			if (callback != null) {
				if (error == null && items != null) {
					@SuppressWarnings("unchecked")
					List<MarketItem> marketItems = (List<MarketItem>) (List<?>) items;
					callback.accept(null, marketItems);
				} else {
					callback.accept(error, null);
				}
			}
		});
	}

	public void getMarketItemsByCategory(@NonNull final UUID categoryId, @NonNull final Callback<List<MarketItem>> callback) {
		// Use QueryBuilder for custom query
		queryBuilder.select("category_item")
			.where("owning_category", categoryId.toString())
			.fetch(rs -> {
				try {
					return new MarketsEntityMapper<>(CategoryItem.class).map(rs);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}, (error, items) -> {
				if (callback != null) {
					if (error == null && items != null) {
						@SuppressWarnings("unchecked")
						List<MarketItem> marketItems = (List<MarketItem>) (List<?>) items;
						callback.accept(null, marketItems);
					} else {
						callback.accept(error, null);
					}
				}
			});
	}

	public void createMarketUser(@NonNull final MarketUser marketUser, final Callback<MarketUser> callback) {
		if (marketUser instanceof MarketPlayer) {
			Markets.getMarketUserRepository().save((MarketPlayer) marketUser, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.INSERT, "user", extractEntityData(marketUser));
				}
				
				if (callback != null) {
					if (error == null) {
						Markets.getMarketUserRepository().findById(marketUser.getUUID(), (findError, found) -> {
							if (findError == null && found != null) {
								callback.accept(null, found);
							} else {
								callback.accept(findError, null);
							}
						});
					} else {
						callback.accept(error, null);
					}
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported market user type"), null);
			}
		}
	}

	public void updateMarketUser(@NonNull final MarketUser marketUser, final Callback<Boolean> callback) {
		if (marketUser instanceof MarketPlayer) {
			Markets.getMarketUserRepository().save((MarketPlayer) marketUser, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.UPDATE, "user", extractEntityData(marketUser));
				}
				
				if (callback != null) {
					callback.accept(error, error == null);
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported market user type"), false);
			}
		}
	}

	public void getMarketUsers(@NonNull final Callback<List<MarketUser>> callback) {
		Markets.getMarketUserRepository().findAll((error, users) -> {
			if (callback != null) {
				if (error == null && users != null) {
					@SuppressWarnings("unchecked")
					List<MarketUser> marketUsers = (List<MarketUser>) (List<?>) users;
					callback.accept(null, marketUsers);
				} else {
					callback.accept(error, null);
				}
			}
		});
	}

	public void createOfflineItemPayment(@NonNull final Payment payment, final Callback<Payment> callback) {
		if (payment instanceof OfflinePayment) {
			Markets.getPaymentRepository().save((OfflinePayment) payment, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.INSERT, "payment", extractEntityData(payment));
				}
				
				if (callback != null) {
					if (error == null) {
						Markets.getPaymentRepository().findById(payment.getId(), (findError, found) -> {
							if (findError == null && found != null) {
								callback.accept(null, found);
							} else {
								callback.accept(findError, null);
							}
						});
					} else {
						callback.accept(error, null);
					}
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported payment type"), null);
			}
		}
	}

	public void getOfflineItemPayments(@NonNull final Callback<List<Payment>> callback) {
		Markets.getPaymentRepository().findAll((error, payments) -> {
			if (callback != null) {
				if (error == null && payments != null) {
					@SuppressWarnings("unchecked")
					List<Payment> paymentList = (List<Payment>) (List<?>) payments;
					callback.accept(null, paymentList);
				} else {
					callback.accept(error, null);
				}
			}
		});
	}

	public void deleteOfflineItemPayment(@NonNull final Payment payment, Callback<Boolean> callback) {
		Markets.getPaymentRepository().deleteById(payment.getId(), (error, deleted) -> {
			if (error == null && deleted != null && deleted) {
				Map<String, Object> data = new HashMap<>();
				data.put("id", payment.getId().toString());
				publishEntityEvent(DatabaseEvent.EventType.DELETE, "payment", data);
			}
			
			if (callback != null) {
				callback.accept(error, deleted != null && deleted);
			}
		});
	}

	public void createOffer(@NonNull final Offer offer, final Callback<Offer> callback) {
		if (offer instanceof MarketOffer) {
			Markets.getOfferRepository().save((MarketOffer) offer, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.INSERT, "offer", extractEntityData(offer));
				}
				
				if (callback != null) {
					if (error == null) {
						Markets.getOfferRepository().findById(offer.getId(), (findError, found) -> {
							if (findError == null && found != null) {
								callback.accept(null, found);
							} else {
								// If findById fails but save succeeded, return the original offer
								callback.accept(null, (Offer) offer);
							}
						});
					} else {
						callback.accept(error, null);
					}
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported offer type"), null);
			}
		}
	}

	public void getOffers(@NonNull final Callback<List<Offer>> callback) {
		Markets.getOfferRepository().findAll((error, offers) -> {
			if (callback != null) {
				if (error == null && offers != null) {
					@SuppressWarnings("unchecked")
					List<Offer> offerList = (List<Offer>) (List<?>) offers;
					callback.accept(null, offerList);
				} else {
					callback.accept(error, null);
				}
			}
		});
	}

	public void deleteOffer(@NonNull final Offer offer, Callback<Boolean> callback) {
		Markets.getOfferRepository().deleteById(offer.getId(), (error, deleted) -> {
			if (error == null && deleted != null && deleted) {
				Map<String, Object> data = new HashMap<>();
				data.put("id", offer.getId().toString());
				publishEntityEvent(DatabaseEvent.EventType.DELETE, "offer", data);
			}
			
			if (callback != null) {
				callback.accept(error, deleted != null && deleted);
			}
		});
	}

	public void createBankEntry(@NonNull final BankEntry bankEntry, final Callback<BankEntry> callback) {
		if (bankEntry instanceof MarketBankEntry) {
			Markets.getBankEntryRepository().save((MarketBankEntry) bankEntry, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.INSERT, "bank_entry", extractEntityData(bankEntry));
				}
				
				if (callback != null) {
					if (error == null) {
						Markets.getBankEntryRepository().findById(bankEntry.getId(), (findError, found) -> {
							if (findError == null && found != null) {
								callback.accept(null, found);
							} else {
								callback.accept(findError, null);
							}
						});
					} else {
						callback.accept(error, null);
					}
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported bank entry type"), null);
			}
		}
	}

	public void getBankEntries(@NonNull final Callback<List<BankEntry>> callback) {
		Markets.getBankEntryRepository().findAll((error, entries) -> {
			if (callback != null) {
				if (error == null && entries != null) {
					@SuppressWarnings("unchecked")
					List<BankEntry> bankEntries = (List<BankEntry>) (List<?>) entries;
					callback.accept(null, bankEntries);
				} else {
					callback.accept(error, null);
				}
			}
		});
	}

	public void updateBankEntry(@NonNull final BankEntry entry, final Callback<Boolean> callback) {
		if (entry instanceof MarketBankEntry) {
			Markets.getBankEntryRepository().save((MarketBankEntry) entry, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.UPDATE, "bank_entry", extractEntityData(entry));
				}
				
				if (callback != null) {
					callback.accept(error, error == null);
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported bank entry type"), false);
			}
		}
	}

	public void deleteBankEntry(@NonNull final BankEntry entry, Callback<Boolean> callback) {
		Markets.getBankEntryRepository().deleteById(entry.getId(), (error, deleted) -> {
			if (error == null && deleted != null && deleted) {
				Map<String, Object> data = new HashMap<>();
				data.put("id", entry.getId().toString());
				publishEntityEvent(DatabaseEvent.EventType.DELETE, "bank_entry", data);
			}
			
			if (callback != null) {
				callback.accept(error, deleted != null && deleted);
			}
		});
	}

	public void createMarketRating(@NonNull final Rating rating, final Callback<Rating> callback) {
		if (rating instanceof MarketRating) {
			Markets.getRatingRepository().save((MarketRating) rating, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.INSERT, "review", extractEntityData(rating));
				}
				
				if (callback != null) {
					if (error == null) {
						Markets.getRatingRepository().findById(rating.getId(), (findError, found) -> {
							if (findError == null && found != null) {
								callback.accept(null, found);
							} else {
								callback.accept(findError, null);
							}
						});
					} else {
						callback.accept(error, null);
					}
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported rating type"), null);
			}
		}
	}

	public void getRatingsByMarket(@NonNull final UUID market, @NonNull final Callback<List<Rating>> callback) {
		// Use QueryBuilder for custom query
		queryBuilder.select("review")
			.where("market", market.toString())
			.fetch(rs -> {
				try {
					return new MarketsEntityMapper<>(MarketRating.class).map(rs);
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}, (error, ratings) -> {
				if (callback != null) {
					if (error == null && ratings != null) {
						@SuppressWarnings("unchecked")
						List<Rating> ratingList = (List<Rating>) (List<?>) ratings;
						callback.accept(null, ratingList);
					} else {
						callback.accept(error, null);
					}
				}
			});
	}

	public void createRequest(@NonNull final Request request, final Callback<Request> callback) {
		if (request instanceof MarketRequest) {
			Markets.getRequestRepository().save((MarketRequest) request, (error, saved) -> {
				if (error == null && saved != null) {
					publishEntityEvent(DatabaseEvent.EventType.INSERT, "request", extractEntityData(request));
				}
				
				if (callback != null) {
					if (error == null) {
						Markets.getRequestRepository().findById(request.getId(), (findError, found) -> {
							if (findError == null && found != null) {
								callback.accept(null, found);
							} else {
								callback.accept(findError, null);
							}
						});
					} else {
						callback.accept(error, null);
					}
				}
			});
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported request type"), null);
			}
		}
	}

	public void getRequests(@NonNull final Callback<List<Request>> callback) {
		Markets.getRequestRepository().findAll((error, requests) -> {
			if (callback != null) {
				if (error == null && requests != null) {
					@SuppressWarnings("unchecked")
					List<Request> requestList = (List<Request>) (List<?>) requests;
					callback.accept(null, requestList);
				} else {
					callback.accept(error, null);
				}
			}
		});
	}

	public void deleteRequest(@NonNull final Request request, Callback<Boolean> callback) {
		Markets.getRequestRepository().deleteById(request.getId(), (error, deleted) -> {
			if (error == null && deleted != null && deleted) {
				Map<String, Object> data = new HashMap<>();
				data.put("id", request.getId().toString());
				publishEntityEvent(DatabaseEvent.EventType.DELETE, "request", data);
			}
			
			if (callback != null) {
				callback.accept(error, deleted != null && deleted);
			}
		});
	}

	public void createTransaction(@NonNull final Transaction transaction, final Callback<Transaction> callback) {
		if (transaction instanceof MarketTransaction) {
			// Use retry logic for transaction storage
			retryDatabaseOperation(
				(operationCallback) -> {
					Markets.getTransactionRepository().save((MarketTransaction) transaction, (error, saved) -> {
						if (error == null && saved != null) {
							publishEntityEvent(DatabaseEvent.EventType.INSERT, "transaction", extractEntityData(transaction));
							// Fetch the saved transaction to return
							Markets.getTransactionRepository().findById(transaction.getId(), (findError, found) -> {
								if (findError == null && found != null) {
									operationCallback.accept(null, found);
								} else {
									// Fetch failed, but save succeeded - return original transaction to ensure it's tracked
									// This prevents transactions from being lost due to connection issues during fetch
									if (findError != null && isConnectionError(findError)) {
										Markets.getInstance().getLogger().warning("Failed to fetch saved transaction due to connection error, using original transaction. Transaction ID: " + transaction.getId());
									}
									operationCallback.accept(null, transaction); // Return original transaction if fetch fails
								}
							});
						} else {
							operationCallback.accept(error, null);
						}
					});
				},
				(error, result) -> {
					if (callback != null) {
						callback.accept(error, (Transaction) result);
					}
				},
				3 // Max 3 retries
			);
		} else {
			if (callback != null) {
				callback.accept(new Exception("Unsupported transaction type"), null);
			}
		}
	}

	public void getTransactions(@NonNull final Callback<List<Transaction>> callback) {
		Markets.getTransactionRepository().findAll((error, transactions) -> {
			if (callback != null) {
				if (error == null && transactions != null) {
					@SuppressWarnings("unchecked")
					List<Transaction> transactionList = (List<Transaction>) (List<?>) transactions;
					callback.accept(null, transactionList);
				} else {
					callback.accept(error, null);
				}
			}
		});
	}

	private Transaction extractTransaction(@NonNull final ResultSet resultSet) throws SQLException {
		return new MarketTransaction(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("buyer")),
				resultSet.getString("buyer_name"),
				UUID.fromString(resultSet.getString("seller")),
				resultSet.getString("seller_name"),
				TransactionType.valueOf(resultSet.getString("type")),
				SerializeUtil.decodeItem(resultSet.getString("item")),
				resultSet.getString("currency"),
				resultSet.getInt("quantity"),
				resultSet.getDouble("price"),
				resultSet.getLong("created_at")
		);
	}

	private Request extractRequest(@NonNull final ResultSet resultSet) throws SQLException {
		return new MarketRequest(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("owner")),
				resultSet.getString("owner_name"),
				SerializeUtil.decodeItem(resultSet.getString("requested_item")),
				resultSet.getString("currency"),
				SerializeUtil.decodeItem(resultSet.getString("currency_item")),
				resultSet.getDouble("price"),
				resultSet.getInt("requested_amount"),
				resultSet.getLong("requested_at")
		);
	}

	private Rating extractMarketRating(@NonNull final ResultSet resultSet) throws SQLException {
		return new MarketRating(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("market")),
				UUID.fromString(resultSet.getString("rater")),
				resultSet.getString("rater_name"),
				resultSet.getString("feedback"),
				resultSet.getInt("stars"),
				resultSet.getLong("posted_on")
		);
	}

	private BankEntry extractBankEntry(@NonNull final ResultSet resultSet) throws SQLException {

		final ItemStack currencyItem = resultSet.getString("currency_item") == null ? CompMaterial.AIR.parseItem() : SerializeUtil.decodeItem(resultSet.getString("currency_item"));

		return new MarketBankEntry(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("owner")),
				SerializeUtil.decodeItem(resultSet.getString("item")),
				resultSet.getInt("quantity"),
				resultSet.getString("currency"),
				currencyItem,
				resultSet.getDouble("price")
		);
	}

	private Offer extractOffer(@NonNull final ResultSet resultSet) throws SQLException {
		return new MarketOffer(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("sender")),
				resultSet.getString("sender_name"),
				UUID.fromString(resultSet.getString("offer_to")),
				UUID.fromString(resultSet.getString("market_item")),
				resultSet.getInt("request_amount"),
				resultSet.getString("currency"),
				SerializeUtil.decodeItem(resultSet.getString("currency_item")),
				resultSet.getDouble("offered_amount"),
				resultSet.getLong("offered_at")
		);
	}

	private AbstractMarket extractMarket(@NonNull final ResultSet resultSet) throws SQLException {
		final ArrayList<UUID> bannedUsers = new ArrayList<>();
		Layout homeLayout, categoryLayout;

		if (resultSet.getString("banned_users") != null) {
			final List<String> possibleUUIDS = Arrays.stream(resultSet.getString("banned_users").split(",")).toList();

			for (String id : possibleUUIDS) {
				try {
					bannedUsers.add(UUID.fromString(id));
				} catch (IllegalArgumentException ignored) {
					continue;
				}
			}
		}

		homeLayout = resultSet.getString("home_layout") != null ? MarketLayout.decodeJSON(resultSet.getString("home_layout")) : new HomeLayout();
		categoryLayout = resultSet.getString("category_layout") != null ? MarketLayout.decodeJSON(resultSet.getString("category_layout")) : new HomeLayout();

		// Handle null or invalid type - default to PLAYER
		String typeStr = resultSet.getString("type");
		MarketType marketType = MarketType.PLAYER;
		if (typeStr != null && !typeStr.isEmpty()) {
			try {
				marketType = Enum.valueOf(MarketType.class, typeStr.toUpperCase());
			} catch (IllegalArgumentException e) {
				Markets.getInstance().getLogger().warning("Invalid market type '" + typeStr + "' found in database, defaulting to PLAYER");
			}
		}

		if (marketType == MarketType.SERVER) {
			return new ServerMarket(
					UUID.fromString(resultSet.getString("id")),
					UUID.fromString(resultSet.getString("owner")),
					resultSet.getString("owner_name"),
					resultSet.getString("display_name"),
					new ArrayList<>(List.of(resultSet.getString("description").split(";;;"))),
					new ArrayList<>(),
					new ArrayList<>(),
					bannedUsers,
					resultSet.getBoolean("open"),
					resultSet.getBoolean("close_when_out_of_stock"),
					homeLayout,
					categoryLayout,
					resultSet.getLong("created_at"),
					resultSet.getLong("updated_at")
			);
		}

		return new PlayerMarket(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("owner")),
				resultSet.getString("owner_name"),
				resultSet.getString("display_name"),
				new ArrayList<>(List.of(resultSet.getString("description").split(";;;"))),
				new ArrayList<>(),
				new ArrayList<>(),
				bannedUsers,
				resultSet.getBoolean("open"),
				resultSet.getBoolean("close_when_out_of_stock"),
				homeLayout,
				categoryLayout,
				resultSet.getLong("created_at"),
				resultSet.getLong("updated_at")
		);
	}

	private Category extractCategory(@NonNull final ResultSet resultSet) throws SQLException {
		return new MarketCategory(
				UUID.fromString(resultSet.getString("owning_market")),
				UUID.fromString(resultSet.getString("id")),
				CompMaterial.matchCompMaterial(resultSet.getString("icon")).orElse(CompMaterial.CHEST).parseItem(),
				resultSet.getString("name"),
				resultSet.getString("display_name"),
				new ArrayList<>(List.of(resultSet.getString("description").split(";;;"))),
				new ArrayList<>(),
				resultSet.getLong("created_at"),
				resultSet.getLong("updated_at")
		);
	}

	private MarketItem extractMarketItem(@NonNull final ResultSet resultSet) throws SQLException {
		return new CategoryItem(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("owning_category")),
				SerializeUtil.decodeItem(resultSet.getString("item")),
				resultSet.getString("currency"),
				SerializeUtil.decodeItem(resultSet.getString("currency_item")),
				resultSet.getDouble("price"),
				resultSet.getInt("stock"),
				resultSet.getBoolean("price_is_for_all"),
				resultSet.getBoolean("accepting_offers"),
				resultSet.getBoolean("infinite")
		);
	}

	private MarketUser extractMarketUser(@NonNull final ResultSet resultSet) throws SQLException {
		return new MarketPlayer(
				UUID.fromString(resultSet.getString("id")),
				null,
				resultSet.getString("last_known_name"),
				new ArrayList<>(List.of(resultSet.getString("bio").split(";;;"))),
				resultSet.getString("preferred_language"),
				resultSet.getString("currency_format_country"),
				MarketSortType.NAME,
				resultSet.getLong("last_seen_at")
		);
	}

	private Payment extractOfflineItemPayment(@NonNull final ResultSet resultSet) throws SQLException {
		return new OfflinePayment(
				UUID.fromString(resultSet.getString("id")),
				UUID.fromString(resultSet.getString("payment_for")),
				SerializeUtil.decodeItem(resultSet.getString("currency")),
				resultSet.getDouble("amount"),
				resultSet.getString("reason"),
				resultSet.getLong("received_at")
		);
	}

//	final String query = "INSERT INTO " + this.getTablePrefix() + "user (id, last_known_name, bio, preferred_language, currency_format_country, last_seen_at) VALUES (?, ?, ?, ?, ?, ?)";

	private void resolveUpdateCallback(@Nullable UpdateCallback callback, @Nullable Exception ex) {
		if (callback != null) {
			callback.accept(ex);
		} else if (ex != null) {
			ex.printStackTrace();
		}
	}

	private void resolveCallback(@Nullable Callback<?> callback, @NotNull Exception ex) {
		if (callback != null) {
			callback.accept(ex, null);
		} else {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Extract entity data to a Map for event publishing
	 */
	private Map<String, Object> extractEntityData(@NonNull Object entity) {
		Map<String, Object> data = new HashMap<>();
		
		try {
			Class<?> clazz = entity.getClass();
			for (Field field : clazz.getDeclaredFields()) {
				try {
					// Try to get Column annotation using reflection
					Class<?> columnAnnotationClass = Class.forName("ca.tweetzy.flight.database.annotations.Column");
					Annotation column = field.getAnnotation((Class<? extends Annotation>) columnAnnotationClass);
					if (column != null) {
						field.setAccessible(true);
						Object value = field.get(entity);
						
						if (value != null) {
							// Get column name from annotation
							String columnName;
							try {
								Method valueMethod = columnAnnotationClass.getMethod("value");
								String columnValue = (String) valueMethod.invoke(column);
								columnName = columnValue.isEmpty() ? field.getName() : columnValue;
							} catch (Exception ex) {
								columnName = field.getName();
							}
							
							// Convert value to string representation
							if (value instanceof UUID) {
								data.put(columnName, value.toString());
							} else if (value instanceof Enum) {
								data.put(columnName, value.toString());
							} else if (value instanceof List) {
								// For lists, join with separator
								List<?> list = (List<?>) value;
								if (list.isEmpty()) {
									data.put(columnName, "");
								} else if (list.get(0) instanceof String) {
									data.put(columnName, String.join(";;;", (List<String>) list));
								} else if (list.get(0) instanceof UUID) {
									data.put(columnName, list.stream().map(u -> u.toString()).reduce((a, b) -> a + "," + b).orElse(""));
								} else {
									data.put(columnName, value.toString());
								}
							} else {
								data.put(columnName, value.toString());
							}
						}
					}
					
					// Also check for @Id annotation
					Class<?> idAnnotationClass = Class.forName("ca.tweetzy.flight.database.annotations.Id");
					Annotation idAnnotation = field.getAnnotation((Class<? extends Annotation>) idAnnotationClass);
					if (idAnnotation != null) {
						field.setAccessible(true);
						Object value = field.get(entity);
						if (value != null) {
							data.put("id", value.toString());
						}
					}
				} catch (ClassNotFoundException ignored) {
					// Annotation classes not found, skip
				}
			}
		} catch (Exception e) {
			// If extraction fails, at least include the ID if it's an Identifiable
			try {
				Class<?> identifiableClass = Class.forName("ca.tweetzy.markets.api.Identifiable");
				if (identifiableClass.isInstance(entity)) {
					Method getIdMethod = identifiableClass.getMethod("getId");
					Object id = getIdMethod.invoke(entity);
					if (id != null) {
						data.put("id", id.toString());
					}
				}
			} catch (Exception ex) {
				// Ignore
			}
		}
		
		return data;
	}
	
	/**
	 * Publish a database event for cross-server synchronization
	 */
	private void publishEntityEvent(@NonNull DatabaseEvent.EventType eventType, @NonNull String tableName, @NonNull Map<String, Object> data) {
		// Use the parent class method to publish event
		publishDatabaseEvent(eventType, tableName, data);
	}
	
	/**
	 * Check if an exception is a connection-related error that should be retried
	 */
	private boolean isConnectionError(@NonNull Exception error) {
		String message = error.getMessage();
		if (message == null) {
			return false;
		}
		message = message.toLowerCase();
		return message.contains("connection closed") ||
		       message.contains("no operations allowed") ||
		       message.contains("communications link failure") ||
		       message.contains("connection reset") ||
		       message.contains("broken pipe") ||
		       (error.getCause() != null && isConnectionError((Exception) error.getCause()));
	}
	
	/**
	 * Retry a database operation with exponential backoff
	 * @param operation The operation to retry (should call the callback with result)
	 * @param callback The final callback to call with result
	 * @param maxRetries Maximum number of retries (default 3)
	 */
	private <T> void retryDatabaseOperation(
		@NonNull Consumer<BiConsumer<Exception, T>> operation,
		@NonNull Callback<T> callback,
		int maxRetries
	) {
		retryDatabaseOperationInternal(operation, callback, maxRetries, 0);
	}
	
	private <T> void retryDatabaseOperationInternal(
		@NonNull Consumer<BiConsumer<Exception, T>> operation,
		@NonNull Callback<T> callback,
		int maxRetries,
		int currentAttempt
	) {
		operation.accept((error, result) -> {
			if (error == null) {
				// Success
				callback.accept(null, result);
			} else if (isConnectionError(error) && currentAttempt < maxRetries) {
				// Connection error - retry with exponential backoff
				long delayMs = (long) Math.pow(2, currentAttempt) * 100; // 100ms, 200ms, 400ms
				Markets.getInstance().getLogger().warning("Database connection error (attempt " + (currentAttempt + 1) + "/" + (maxRetries + 1) + "): " + error.getMessage() + ". Retrying in " + delayMs + "ms...");
				
				// Schedule retry on async thread
				Bukkit.getScheduler().runTaskLaterAsynchronously(
					Markets.getInstance(),
					() -> retryDatabaseOperationInternal(operation, callback, maxRetries, currentAttempt + 1),
					delayMs / 50 // Convert ms to ticks (20 ticks per second)
				);
			} else {
				// Non-retryable error or max retries reached
				if (currentAttempt >= maxRetries && isConnectionError(error)) {
					Markets.getInstance().getLogger().severe("Failed to execute database operation after " + (maxRetries + 1) + " attempts. Last error: " + error.getMessage());
					error.printStackTrace();
				}
				callback.accept(error, result);
			}
		});
	}
}
