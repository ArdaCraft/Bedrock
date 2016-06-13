package com.helion3.bedrock.util;

import java.util.ArrayDeque;

/**
 * A bounded, first in last out, array-backed Deque that evicts from the head when full.
 */
public class BoundedDeque<T> extends ArrayDeque<T> {
	private static final long serialVersionUID = 8884216011915712340L;

	private final int capacity;

	public BoundedDeque(int size) {
		this.capacity = size;
	}

	@Override
	public boolean add(T t) {
		if (this.size() > capacity) {
			this.removeFirst();
		}
		return super.add(t);
	}

	@Override
	public T poll() {
		return super.pollLast();
	}

	@Override
	public T peek() {
		return super.peekLast();
	}

	@Override
	public T pop() {
		return super.removeLast();
	}

	@Override
	public T remove() {
		return super.removeLast();
	}
}
