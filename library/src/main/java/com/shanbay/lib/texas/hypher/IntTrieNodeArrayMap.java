package com.shanbay.lib.texas.hypher;

class IntTrieNodeArrayMap {
	private static final int MIN_KEY = 95;
	private static final int MAX_KEY = 122;
	private static final int LOWER_A = 97;
	private static final int UPPER_A = 65;
	private static final int UPPER_Z = 90;

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
		if (key >= UPPER_A && key <= UPPER_Z) {
			key += (LOWER_A - UPPER_A);
		}
		if (key < MIN_KEY || key > MAX_KEY) {
			return -1;
		}
		return key - MIN_KEY;
	}
}
