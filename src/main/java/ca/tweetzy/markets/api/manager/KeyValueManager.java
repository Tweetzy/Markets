package ca.tweetzy.markets.api.manager;

import lombok.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class KeyValueManager<K, V> extends Manager {

	protected final Map<K, V> managerContent = Collections.synchronizedMap(new HashMap<>());

	public KeyValueManager(@NonNull String name) {
		super(name);
	}

	public V get(@NonNull final K k) {
		synchronized (this.managerContent) {
			return this.managerContent.getOrDefault(k, null);
		}
	}

	public void add(@NonNull final K k, @NonNull final V v) {
		synchronized (this.managerContent) {
			if (this.managerContent.containsKey(k)) return;
			this.managerContent.put(k, v);
		}
	}

	public void remove(@NonNull final K k) {
		synchronized (this.managerContent) {
			this.managerContent.remove(k);
		}
	}

	public void clear() {
		synchronized (this.managerContent) {
			this.managerContent.clear();
		}
	}

	public Map<K, V> getManagerContent() {
		synchronized (this.managerContent) {
			return Map.copyOf(this.managerContent);
		}
	}
}
