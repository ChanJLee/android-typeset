package me.chan.te.hypher;

import android.support.v4.util.SparseArrayCompat;

class IntTrieNodeArrayMap {

	private SparseArrayCompat<TrieNode> mMap = new SparseArrayCompat<>();

	void put(final int key, final TrieNode node) {
		mMap.put(key, node);
	}

	TrieNode get(final int key) {
		return mMap.get(key);
	}
}
