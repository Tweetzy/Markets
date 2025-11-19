# Race Condition Fix: Concurrent Purchase and Deletion

## Problem Description

A critical race condition existed where a seller could delete a market item while a buyer was simultaneously purchasing it. This could result in both operations succeeding, causing a duplication exploit:

1. **Buyer** initiates purchase → withdraws money → receives item
2. **Seller** deletes item → receives item back
3. **Result**: Both players have the item, but only the buyer paid (dupe exploit)

## Root Cause

The deletion operation (`unStore()`) did not check if the item was currently being purchased. The purchase operation (`performPurchase()`) had protection against concurrent purchases, but deletion could proceed independently, creating a race window.

## Solution Overview

The fix implements a **mutual exclusion mechanism** using the `beingEdited` flag and synchronized blocks to ensure that purchase and deletion operations cannot occur simultaneously.

## Implementation Details

### 1. Purchase Protection (Already Existed)

The purchase flow already had protection in place:

```java
// In CategoryItem.performPurchase()
synchronized (this.editLock) {
    if (this.beingEdited) {
        // Item is already being edited - reject purchase
        return;
    }
    this.beingEdited = true; // Lock the item
}

try {
    // ... purchase logic ...
} finally {
    synchronized (this.editLock) {
        this.beingEdited = false; // Release lock
    }
}
```

### 2. Deletion Protection (New Fix)

The deletion operation now checks and sets the `beingEdited` flag:

```java
// In CategoryItem.unStore()
synchronized (this.editLock) {
    if (this.beingEdited) {
        // Item is currently being purchased - cannot delete
        return;
    }
    this.beingEdited = true; // Lock the item for deletion
}

// Check for cross-server stock reservations
if (reservationManager != null && reservationManager.isReserved(this.id)) {
    // Stock is reserved on another server - cannot delete
    synchronized (this.editLock) {
        this.beingEdited = false; // Release lock
    }
    return;
}

// Perform deletion
Markets.getDataManager().deleteMarketItem(this, (error, deleted) -> {
    // Clear lock after deletion completes
    synchronized (this.editLock) {
        this.beingEdited = false;
    }
    // ... rest of deletion logic ...
});
```

### 3. GUI-Level Protection

The GUI also checks before allowing deletion:

```java
// In MarketCategoryEditGUI
if (marketItem.isBeingEdited()) {
    // Prevent deletion attempt
    Common.tell(click.player, TranslationManager.string(...));
    return;
}
```

### 4. Stock Withdrawal Protection

Stock withdrawal operations are also protected to prevent withdrawing stock while a purchase is in progress:

```java
// In MarketItemEditGUI - stock withdrawal
if (this.marketItem.isBeingEdited()) {
    // Item is being purchased - cannot withdraw stock
    return;
}

// Check for cross-server reservations
if (reservationManager != null && reservationManager.isReserved(this.marketItem.getId())) {
    return;
}
```

## How It Works

### Mutual Exclusion

Both purchase and deletion operations use the same `synchronized (this.editLock)` block, ensuring only one operation can proceed at a time:

1. **If purchase starts first:**
   - Purchase sets `beingEdited = true`
   - Deletion checks `beingEdited` → sees `true` → fails immediately
   - Purchase completes → sets `beingEdited = false`

2. **If deletion starts first:**
   - Deletion sets `beingEdited = true`
   - Purchase checks `beingEdited` → sees `true` → fails immediately
   - Deletion completes → sets `beingEdited = false`

### Cross-Server Protection

For multi-server setups with Redis:

- **Stock Reservation Manager**: Uses distributed locks to prevent concurrent purchases across servers
- **Deletion checks reservations**: Before deleting, checks if stock is reserved on any server
- **Purchase uses reservations**: Acquires reservation lock before proceeding

### Lock Lifecycle

The `beingEdited` flag is managed carefully:

- **Set**: Before any critical operation (purchase/deletion)
- **Cleared**: After operation completes (in `finally` block or callback)
- **Checked**: Before allowing any conflicting operation

## Additional Protections

### Stock Re-validation

After money withdrawal, the purchase flow re-validates stock from the database to prevent stale data issues:

```java
// Re-validate stock after money withdrawal
if (reservationManager != null) {
    MarketItem reloadedItem = Markets.getCategoryItemManager().getByUUID(this.id);
    int currentStock = (reloadedItem != null) ? reloadedItem.getStock() : this.stock;
    
    if (currentStock < newPurchaseAmount) {
        // Rollback money and fail
        return;
    }
}
```

### Error Handling

All operations include proper error handling and rollback mechanisms:

- Money withdrawal failures → transaction aborted
- Stock validation failures → money refunded
- Deletion failures → lock released, item remains

## Testing Recommendations

To verify the fix works correctly, test these scenarios:

1. **Concurrent Purchase and Deletion**
   - Have one player attempt to purchase an item
   - Simultaneously have the seller attempt to delete it
   - Verify only one operation succeeds

2. **Cross-Server Race Conditions**
   - With Redis enabled, have players on different servers attempt to purchase the same item
   - Verify only one purchase succeeds

3. **Stock Withdrawal During Purchase**
   - Have a buyer initiate a purchase
   - Have the seller attempt to withdraw stock
   - Verify withdrawal is blocked until purchase completes

4. **Rapid Sequential Operations**
   - Have multiple players rapidly click purchase on the same item
   - Verify only one purchase succeeds per item

## Files Modified

- `src/main/java/ca/tweetzy/markets/impl/CategoryItem.java`
  - Added `beingEdited` check in `unStore()` method
  - Added stock reservation check in `unStore()` method
  - Ensured lock is cleared after deletion completes

- `src/main/java/ca/tweetzy/markets/gui/user/category/MarketCategoryEditGUI.java`
  - Added `isBeingEdited()` check before deletion
  - Added check in both deletion paths (with and without confirmation)

- `src/main/java/ca/tweetzy/markets/gui/user/category/MarketItemEditGUI.java`
  - Added `isBeingEdited()` check before stock withdrawal
  - Added stock reservation check before stock withdrawal
  - Added stock re-validation after user input

## Conclusion

This fix ensures that purchase and deletion operations are mutually exclusive, preventing the duplication exploit. The implementation uses synchronized blocks and distributed locks (for cross-server setups) to guarantee thread safety and data integrity.

