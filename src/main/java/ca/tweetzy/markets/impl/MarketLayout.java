package ca.tweetzy.markets.impl;

import ca.tweetzy.markets.api.market.Layout;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public final class MarketLayout implements Layout {

	private int exitButtonSlot;
	private int prevPageButtonSlot;
	private int nextPageButtonSlot;
	private int ownerProfileButtonSlot;
	private int reviewButtonSlot;
	private int searchButtonSlot;

	private List<Integer> fillSlots;
	private Map<Integer, ItemStack> decorations;
	private ItemStack background;

	@Override
	public int getExitButtonSlot() {
		return this.exitButtonSlot;
	}

	@Override
	public void setExitButtonSlot(int slot) {
		this.exitButtonSlot = slot;
	}

	@Override
	public int getPrevPageButtonSlot() {
		return this.prevPageButtonSlot;
	}

	@Override
	public void setPrevPageButtonSlot(int slot) {
		this.prevPageButtonSlot = slot;
	}

	@Override
	public int getOwnerProfileSlot() {
		return this.ownerProfileButtonSlot;
	}

	@Override
	public void setOwnerProfileSlot(int slot) {
		this.ownerProfileButtonSlot = slot;
	}

	@Override
	public int getNextPageButtonSlot() {
		return this.nextPageButtonSlot;
	}

	@Override
	public void setNextPageButtonSlot(int slot) {
		this.nextPageButtonSlot = slot;
	}

	@Override
	public int getReviewButtonSlot() {
		return this.reviewButtonSlot;
	}

	@Override
	public void setReviewButtonSlot(int slot) {
		this.reviewButtonSlot = slot;
	}

	@Override
	public int getSearchButtonSlot() {
		return this.searchButtonSlot;
	}

	@Override
	public void setSearchButtonSlot(int slot) {
		this.searchButtonSlot = slot;
	}

	@Override
	public List<Integer> getFillSlots() {
		return this.fillSlots;
	}

	@Override
	public void setFillSlots(List<Integer> slots) {
		this.fillSlots = slots;
	}

	@Override
	public Map<Integer, ItemStack> getDecoration(@NonNull Map<Integer, ItemStack> decoration) {
		return this.decorations = decoration;
	}

	@Override
	public ItemStack getBackgroundItem() {
		return this.background;
	}

	@Override
	public void setBackgroundItem(@NonNull ItemStack backgroundItem) {
		this.background = backgroundItem;
	}

	@Override
	public String getJSONString() {
		return null;
	}
}
