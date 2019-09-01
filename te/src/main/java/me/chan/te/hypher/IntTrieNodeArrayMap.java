package me.chan.te.hypher;

class IntTrieNodeArrayMap {

	private static final int DEFAULT_INITIAL_CAPACITY = 16;

	private int[] keys;
	private TrieNode[] values;

	private int size = 0;

	IntTrieNodeArrayMap() {
		this(DEFAULT_INITIAL_CAPACITY);
	}

	private IntTrieNodeArrayMap(int capacity) {
		keys = new int[capacity];
		values = new TrieNode[capacity];
	}

	private int findIndex(final int key) {
		for (int i = 0; i < size; i++) {
			if (keys[i] == key) {
				return i;
			}
		}
		return -1;
	}

	TrieNode put(final int key, final TrieNode node) {
		final int oldIndex = findIndex(key);
		if (oldIndex >= 0) {
			final TrieNode oldValue = values[oldIndex];
			values[oldIndex] = node;
			return oldValue;
		}
		if (size == keys.length) {
			final int[] newKeys = new int[size == 0 ? DEFAULT_INITIAL_CAPACITY : size * 2];
			System.arraycopy(keys, 0, newKeys, 0, size);
			final TrieNode[] newValues = new TrieNode[size == 0 ? 2 : size * 2];
			System.arraycopy(values, 0, newValues, 0, size);
			keys = newKeys;
			values = newValues;
		}
		keys[size] = key;
		values[size] = node;
		size++;
		return null;
	}

	TrieNode get(final int key) {
		for (int i = 0; i < size; i++) {
			if (keys[i] == key)
				return values[i];
		}
		return null;
	}
}
