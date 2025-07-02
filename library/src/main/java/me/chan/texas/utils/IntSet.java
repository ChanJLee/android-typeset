package me.chan.texas.utils;

import static me.chan.texas.utils.IntArray.idealIntArraySize;

import androidx.annotation.VisibleForTesting;

import java.util.Arrays;

public class IntSet {
	public static final int DEFAULT_SIZE = 16;

	private int[] mBuckets;
	private int mSize = 0;

	public IntSet() {
		this(DEFAULT_SIZE);
	}

	public IntSet(int size) {
		mBuckets = new int[size <= 0 ? DEFAULT_SIZE : idealIntArraySize(size)];
		mSize = 0;
	}

	public boolean add(int v) {
		int index = find(v);
		if (index > 0 && index < mSize) {
			return false;
		}

		index = ~index;
		if (mSize + 1 > mBuckets.length) {
			mBuckets = Arrays.copyOf(mBuckets, mBuckets.length * 2);
		}

		// 将index后的元素向后移动一位，从后往前
		System.arraycopy(mBuckets, index, mBuckets, index + 1, mSize - index);
		mBuckets[index] = v;
		++mSize;
		return true;
	}

	public boolean remove(int v) {
		int index = find(v);
		if (index >= 0 && index < mSize) {
			System.arraycopy(mBuckets, index + 1, mBuckets, index, mSize - index - 1);
			--mSize;
			return true;
		}
		return false;
	}

	public boolean contains(int v) {
		int index = find(v);
		return index >= 0 && index < mSize;
	}

	@VisibleForTesting
	int find(int v) {
		return IntMap.binarySearch(mBuckets, mSize, v);
	}

	@VisibleForTesting
	void mock(int[] values) {
		mBuckets = new int[idealIntArraySize(values.length)];
		System.arraycopy(values, 0, mBuckets, 0, values.length);
		mSize = values.length;
	}

	@VisibleForTesting
	int[] getBuckets() {
		return mBuckets;
	}

	public void clear() {
		mSize = 0;
	}

	public boolean isEmpty() {
		return mSize == 0;
	}

	public int size() {
		return mSize;
	}
}
