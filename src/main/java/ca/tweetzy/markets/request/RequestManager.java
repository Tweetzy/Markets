package ca.tweetzy.markets.request;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: May 07 2021
 * Time Created: 2:17 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class RequestManager {

	private final List<Request> requests = Collections.synchronizedList(new ArrayList<>());

	public void addRequest(Request request) {
		Objects.requireNonNull(request, "Cannot add a null request to request list");
		this.requests.add(request);
	}

	public void deleteRequest(Request request) {
		Objects.requireNonNull(request, "Cannot remove a null request from request list");
		this.requests.remove(request);
	}

	public List<Request> getRequests() {
		return Collections.unmodifiableList(this.requests);
	}

	public List<Request> getNonFulfilledRequests() {
		return Collections.unmodifiableList(this.requests.stream().filter(req -> req.getRequestedItems().size() != 0).collect(Collectors.toList()));
	}

	public List<Request> getPlayerRequests(Player player) {
		return Collections.unmodifiableList(this.requests.stream().filter(req -> req.getRequestedItems().size() != 0 && req.getRequester().equals(player.getUniqueId())).collect(Collectors.toList()));
	}

	public void setRequestsOnFile() {
		Markets.getInstance().getData().set("open requests", null);
		for (Request request : this.requests) {
			saveRequest(request);
		}
	}

	public void saveRequests(Request... requests) {
		Markets.newChain().sync(() -> {
			Markets.getInstance().getData().set("open requests", null);
			for (Request request : requests) {
				saveRequest(request);
			}
			Markets.getInstance().getData().save();
		}).execute();
	}

	public void export() {
		Markets.getInstance().getExport().set("open requests", null);
		for (Request request : this.requests) {
			exportRequest(request);
		}

		Markets.getInstance().getExport().save();
	}

	public void exportRequest(Request request) {
		Objects.requireNonNull(request, "Cannot save a null request");
		String node = "open requests." + request.getId().toString();
		Markets.getInstance().getExport().set(node + ".requester", request.getRequester().toString());
		Markets.getInstance().getExport().set(node + ".date", request.getDate());
		request.getRequestedItems().forEach(requestItem -> {
			String tempId = UUID.randomUUID().toString();
			Markets.getInstance().getExport().set(node + ".items." + tempId + ".item", requestItem.getItem());
			Markets.getInstance().getExport().set(node + ".items." + tempId + ".currency", requestItem.getCurrency());
			Markets.getInstance().getExport().set(node + ".items." + tempId + ".amount", requestItem.getAmount());
			Markets.getInstance().getExport().set(node + ".items." + tempId + ".price", requestItem.getPrice());
			Markets.getInstance().getExport().set(node + ".items." + tempId + ".use custom currency", requestItem.isUseCustomCurrency());
			Markets.getInstance().getExport().set(node + ".items." + tempId + ".fulfilled", requestItem.isFulfilled());
		});
	}

	public void saveRequest(Request request) {
		Objects.requireNonNull(request, "Cannot save a null request");
		String node = "open requests." + request.getId().toString();
		Markets.getInstance().getData().set(node + ".requester", request.getRequester().toString());
		Markets.getInstance().getData().set(node + ".date", request.getDate());
		request.getRequestedItems().forEach(requestItem -> {
			String tempId = UUID.randomUUID().toString();
			Markets.getInstance().getData().set(node + ".items." + tempId + ".item", requestItem.getItem());
			Markets.getInstance().getData().set(node + ".items." + tempId + ".currency", requestItem.getCurrency());
			Markets.getInstance().getData().set(node + ".items." + tempId + ".amount", requestItem.getAmount());
			Markets.getInstance().getData().set(node + ".items." + tempId + ".price", requestItem.getPrice());
			Markets.getInstance().getData().set(node + ".items." + tempId + ".use custom currency", requestItem.isUseCustomCurrency());
			Markets.getInstance().getData().set(node + ".items." + tempId + ".fulfilled", requestItem.isFulfilled());
		});
	}

	public void loadRequests() {
		if (Settings.DATABASE_USE.getBoolean()) {
			Markets.getInstance().getDataManager().getRequests(callback -> callback.forEach(this::addRequest));
		} else {
			ConfigurationSection section = Markets.getInstance().getData().getConfigurationSection("open requests");
			if (section == null || section.getKeys(false).size() == 0) return;

			Markets.newChain().async(() -> Markets.getInstance().getData().getConfigurationSection("open requests").getKeys(false).forEach(requestId -> {
				Request request = new Request(
						UUID.fromString(requestId),
						UUID.fromString(Markets.getInstance().getData().getString("open requests." + requestId + ".requester")),
						Markets.getInstance().getData().getLong("open requests." + requestId + ".date"),
						null
				);

				List<RequestItem> requestItems = new ArrayList<>();

				Markets.getInstance().getData().getConfigurationSection("open requests." + requestId + ".items").getKeys(false).forEach(rItem -> requestItems.add(new RequestItem(
						request.getId(),
						Markets.getInstance().getData().getItemStack("open requests." + requestId + ".items." + rItem + ".item"),
						Markets.getInstance().getData().getItemStack("open requests." + requestId + ".items." + rItem + ".currency"),
						Markets.getInstance().getData().getInt("open requests." + requestId + ".items." + rItem + ".amount"),
						Markets.getInstance().getData().getDouble("open requests." + requestId + ".items." + rItem + ".price"),
						Markets.getInstance().getData().getBoolean("open requests." + requestId + ".items." + rItem + ".fulfilled"),
						Markets.getInstance().getData().getBoolean("open requests." + requestId + ".items." + rItem + ".use custom currency")
				)));

				request.setRequestedItems(requestItems);
				addRequest(request);
			})).execute();
		}
	}
}


