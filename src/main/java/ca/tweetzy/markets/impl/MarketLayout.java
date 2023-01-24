package ca.tweetzy.markets.impl;

import ca.tweetzy.flight.utils.SerializeUtil;
import ca.tweetzy.markets.api.market.Layout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class MarketLayout implements Layout {

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
		final JsonObject object = new JsonObject();

		object.addProperty("exitButtonSlot", this.exitButtonSlot);
		object.addProperty("prevPageButtonSlot", this.prevPageButtonSlot);
		object.addProperty("nextPageButtonSlot", this.nextPageButtonSlot);
		object.addProperty("ownerProfileButtonSlot", this.ownerProfileButtonSlot);
		object.addProperty("reviewButtonSlot", this.reviewButtonSlot);
		object.addProperty("searchButtonSlot", this.searchButtonSlot);

		final JsonArray fillSlotsArray = new JsonArray();
		this.fillSlots.forEach(fillSlotsArray::add);

		object.addProperty("backgroundItem", SerializeUtil.encodeItem(this.background));
		object.add("fillSlots", fillSlotsArray);

		final JsonArray decorationsArray = new JsonArray();
		this.decorations.forEach((slot, item) -> {
			final JsonObject decoration = new JsonObject();

			decoration.addProperty("slot", slot);
			decoration.addProperty("item", SerializeUtil.encodeItem(item));

			decorationsArray.add(decoration);
		});

		object.add("decorations", decorationsArray);

		return object.toString();
	}

	public static MarketLayout decodeJSON(@NonNull final String json) {
		final JsonObject parentObject = JsonParser.parseString(json).getAsJsonObject();
		final JsonArray fillSlotsArray = parentObject.get("fillSlots").getAsJsonArray();
		final JsonArray decorationsArray = parentObject.get("decorations").getAsJsonArray();

		final List<Integer> fillSlots = new ArrayList<>();
		final Map<Integer, ItemStack> decoration = new HashMap<>();

		decorationsArray.forEach(element -> {
			final JsonObject decoObj = element.getAsJsonObject();
			decoration.put(decoObj.get("slot").getAsInt(), SerializeUtil.decodeItem(decoObj.get("item").getAsString()));
		});

		fillSlotsArray.forEach(element -> fillSlots.add(element.getAsInt()));

		return new MarketLayout(
				parentObject.get("exitButtonSlot").getAsInt(),
				parentObject.get("prevPageButtonSlot").getAsInt(),
				parentObject.get("nextPageButtonSlot").getAsInt(),
				parentObject.get("ownerProfileButtonSlot").getAsInt(),
				parentObject.get("reviewButtonSlot").getAsInt(),
				parentObject.get("searchButtonSlot").getAsInt(),
				fillSlots,
				decoration,
				SerializeUtil.decodeItem(parentObject.get("backgroundItem").getAsString())
		);
	}
}
