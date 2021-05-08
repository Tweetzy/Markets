package ca.tweetzy.markets.request;

import ca.tweetzy.markets.Markets;
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

    private final List<Request> requests = new ArrayList<>();

    public void addRequest(Request request) {
        Objects.requireNonNull(request, "Cannot add a null Request to request list");
        this.requests.add(request);
    }

    public void deleteRequest(Request request) {
        Objects.requireNonNull(request, "Cannot remove a null Request");
        this.requests.remove(request);
    }

    public void deletePlayerRequests(Player player) {
        Objects.requireNonNull(player, "Cannot remove request from null player");
        this.requests.removeIf(request -> request.getRequester().equals(player.getUniqueId()));
    }

    public List<Request> getRequestsByPlayer(Player player, boolean isFulfilled) {
        Objects.requireNonNull(player, "Cannot get requests for null player");
        return this.requests.stream().filter(request -> request.getRequester().equals(player.getUniqueId()) && request.isFulfilled() == isFulfilled).collect(Collectors.toList());
    }

    public List<Request> getRequestsByPlayer(UUID player) {
        Objects.requireNonNull(player, "Cannot get requests for null player");
        return this.requests.stream().filter(request -> request.getRequester().equals(player)).collect(Collectors.toList());
    }

    public List<Request> getNonFulfilledRequests() {
        return Collections.unmodifiableList(this.requests.stream().filter(request -> !request.isFulfilled()).collect(Collectors.toList()));
    }

    public List<Request> getRequests() {
        return Collections.unmodifiableList(this.requests);
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

    public void saveRequest(Request request) {
        Objects.requireNonNull(request, "Cannot save a null request");
        String node = "open requests." + request.getId().toString();
        Markets.getInstance().getData().set(node + ".requester", request.getRequester().toString());
        Markets.getInstance().getData().set(node + ".amount", request.getAmount());
        Markets.getInstance().getData().set(node + ".price", request.getPrice());
        Markets.getInstance().getData().set(node + ".fulfilled", request.isFulfilled());
        Markets.getInstance().getData().set(node + ".item", request.getItem());
    }

    public void loadRequests() {
        Markets.newChain().async(() -> {
            ConfigurationSection section = Markets.getInstance().getData().getConfigurationSection("open requests");
            if (section == null || section.getKeys(false).size() == 0) return;

            Markets.getInstance().getData().getConfigurationSection("open requests").getKeys(false).forEach(requestId -> {
                Request request = new Request(
                        UUID.fromString(requestId),
                        UUID.fromString(Markets.getInstance().getData().getString("open requests." + requestId + ".requester")),
                        Markets.getInstance().getData().getItemStack("open requests." + requestId + ".item"),
                        Markets.getInstance().getData().getInt("open requests." + requestId + ".amount"),
                        Markets.getInstance().getData().getDouble("open requests." + requestId + ".price"));
                request.setFulfilled(Markets.getInstance().getData().getBoolean("open requests." + requestId + ".fulfilled"));

                addRequest(request);
            });
        }).execute();
    }
}
