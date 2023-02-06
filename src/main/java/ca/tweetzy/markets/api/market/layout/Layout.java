package ca.tweetzy.markets.api.market.layout;

import ca.tweetzy.markets.api.Jsonable;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface Layout extends Jsonable {

	int getExitButtonSlot();

	void setExitButtonSlot(final int slot);

	int getPrevPageButtonSlot();

	void setPrevPageButtonSlot(final int slot);

	int getOwnerProfileSlot();

	void setOwnerProfileSlot(final int slot);

	int getNextPageButtonSlot();

	void setNextPageButtonSlot(final int slot);

	int getReviewButtonSlot();

	void setReviewButtonSlot(final int slot);

	int getSearchButtonSlot();

	void setSearchButtonSlot(final int slot);

	List<Integer> getFillSlots();

	void setFillSlots(final List<Integer> slots);

	Map<Integer, ItemStack> getDecoration();

	void setDecoration(@NonNull final Map<Integer, ItemStack> decoration);

	ItemStack getBackgroundItem();

	void setBackgroundItem(@NonNull final ItemStack backgroundItem);
}
