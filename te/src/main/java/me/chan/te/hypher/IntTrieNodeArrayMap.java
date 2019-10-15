package me.chan.te.hypher;

import java.util.HashMap;
import java.util.Map;

class IntTrieNodeArrayMap {

	private Map<Integer, TrieNode> mMap = new HashMap<>();

	TrieNode put(final int key, final TrieNode node) {
		return mMap.put(key, node);
	}

	TrieNode get(final int key) {
		return mMap.get(key);
	}
}
