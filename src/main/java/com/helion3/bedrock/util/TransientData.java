package com.helion3.bedrock.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Data that only needs to be held in memory and not written to disk
 */
public class TransientData {
	private final Map<String, Object> data = new HashMap<>();

	public <T> Optional<T> get(String node) {
		Object value = data.get(node);
		if (value != null) {
			try {
				@SuppressWarnings("unchecked")
				T t = (T) value;
				return Optional.of(t);
			} catch (ClassCastException e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

	public <T> T get(String node, Supplier<T> supplier) {
		Optional<T> result = get(node);
		if (!result.isPresent()) {
			T t = supplier.get();
			data.put(node, t);
			return t;
		}
		return result.get();
	}

	public boolean remove(String node) {
		return data.remove(node) != null;
	}
}
