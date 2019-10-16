package me.chan.te.hypher;

class IntTrieNodeArrayMap {
	private static final int MIN_KEY = 95;
	private static final int MAX_KEY = 122;

	private TrieNode[] mBuffer = new TrieNode[MAX_KEY - MIN_KEY + 1];

	void put(final int key, final TrieNode node) {
		int hash = getHash(key);
		if (hash == -1) {
			return;
		}
		mBuffer[hash] = node;
	}

	TrieNode get(final int key) {
		int hash = getHash(key);
		if (hash == -1) {
			return null;
		}

		return mBuffer[hash];
	}

	private int getHash(int key) {
		if (key < MIN_KEY || key > MAX_KEY) {
			return -1;
		}
		return key - MIN_KEY;
	}
}
