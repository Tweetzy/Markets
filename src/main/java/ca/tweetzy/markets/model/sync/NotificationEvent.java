package ca.tweetzy.markets.model.sync;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;
import java.util.UUID;

/**
 * Represents a cross-server notification event
 */
@Getter
public class NotificationEvent {
	
	public enum NotificationType {
		PURCHASE,
		OUT_OF_STOCK,
		OFFER_ACCEPTED,
		OFFER_REJECTED,
		OFFER_RECEIVED,
		PAYMENT_RECEIVED,
		REQUEST_FULFILLED,
		REQUEST_NEW,
		REVIEW_CREATED
	}
	
	private final String eventId;
	private final String serverId;
	private final NotificationType type;
	private final UUID targetPlayer;
	private final Map<String, Object> data;
	private final long timestamp;
	
	private static final Gson GSON = new GsonBuilder().create();
	
	public NotificationEvent(@NonNull String serverId,
	                        @NonNull NotificationType type,
	                        @NonNull UUID targetPlayer,
	                        @NonNull Map<String, Object> data) {
		this.eventId = UUID.randomUUID().toString();
		this.serverId = serverId;
		this.type = type;
		this.targetPlayer = targetPlayer;
		this.data = data;
		this.timestamp = System.currentTimeMillis();
	}
	
	/**
	 * Serialize this event to JSON
	 */
	@NonNull
	public String toJson() {
		return GSON.toJson(this);
	}
	
	/**
	 * Deserialize an event from JSON
	 */
	@NonNull
	public static NotificationEvent fromJson(@NonNull String json) {
		return GSON.fromJson(json, NotificationEvent.class);
	}
}

