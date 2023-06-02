package com.shanbay.lib.texas.hyphenation;

import android.annotation.SuppressLint;

import androidx.annotation.RestrictTo;
import androidx.collection.SparseArrayCompat;

import com.shanbay.lib.texas.utils.IntArray;

@RestrictTo(RestrictTo.Scope.LIBRARY)
class TrieNode {
	@SuppressLint("FieldCodeStyle")
	TrieNodes codePoint = new TrieNodes();

	@SuppressLint("FieldCodeStyle")
	IntArray points;

	public int capacity() {
		return capacity(this);
	}

	public static int capacity(TrieNode node) {
		int size = node.codePoint.size();
		int count = node.codePoint.capacity();
		for (int i = 0; i < size; ++i) {
			TrieNode child = node.codePoint.indexOf(i);
			if (child != null) {
				count += capacity(child);
			}
		}
		return count;
	}

	public int used() {
		return used(this);
	}

	public static int used(TrieNode node) {
		int size = node.codePoint.size();
		int count = size;
		for (int i = 0; i < size; ++i) {
			TrieNode child = node.codePoint.indexOf(i);
			if (child != null) {
				count += used(child);
			}
		}
		return count;
	}

	public int max() {
		return max(this);
	}

	public static int max(TrieNode node) {
		int size = node.codePoint.size();

		int value = 0;
		int count = 0;
		for (int i = 0; i < size; ++i) {
			TrieNode child = node.codePoint.indexOf(i);
			if (child != null) {
				value = Math.max(value, max(child));
				++count;
			}
		}

		return Math.max(value, count);
	}

	public SparseArrayCompat<Integer> status() {
		SparseArrayCompat<Integer> arrayCompat = new SparseArrayCompat<>();
		status(this, arrayCompat);
		return arrayCompat;
	}

	public static void status(TrieNode node, SparseArrayCompat<Integer> output) {
		int size = node.codePoint.size();
		int value = output.get(size, 0);
		output.put(size, value + 1);
		for (int i = 0; i < size; ++i) {
			TrieNode child = node.codePoint.indexOf(i);
			if (child != null) {
				status(child, output);
			}
		}
	}
}
