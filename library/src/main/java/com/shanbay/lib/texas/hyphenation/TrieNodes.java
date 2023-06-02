package com.shanbay.lib.texas.hyphenation;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.utils.IntMap;

@RestrictTo(RestrictTo.Scope.LIBRARY)
class TrieNodes {
	private IntMap<TrieNode> mChildren;

	public void put(final int key, final TrieNode node) {
		if (mChildren == null) {
			mChildren = new IntMap<>(8);
		}

		mChildren.put(key, node);
	}

	public TrieNode get(final int key) {
		return mChildren == null ? null : mChildren.get(key);
	}

	public int size() {
		return mChildren == null ? 0 : mChildren.size();
	}

	public int capacity() {
		return mChildren == null ? 0 : mChildren.capacity();
	}

	public TrieNode indexOf(int index) {
		return mChildren.valueAt(index);
	}
}
