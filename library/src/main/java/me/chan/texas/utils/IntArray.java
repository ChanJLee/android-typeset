package me.chan.texas.utils;

import static java.util.Arrays.fill;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Collection;

public class IntArray {
	static final int DEFAULT_SIZE = 16;

	private int[] mContainer;
	private int mIndex = 0;

	public IntArray() {
		this(DEFAULT_SIZE);
	}

	public IntArray(@IntRange(from = 1) int size) {
		mContainer = new int[size <= 0 ? DEFAULT_SIZE : idealIntArraySize(size)];
		mIndex = 0;
	}

	
	public IntArray(@NonNull Collection<Integer> collection) {
		this(collection.size());
		for (Integer i : collection) {
			add(i);
		}
	}

	
	public void add(int v) {
		if (mIndex + 1 > mContainer.length) {
			mContainer = Arrays.copyOf(mContainer, mContainer.length * 2);
		}
		mContainer[mIndex++] = v;
	}

	
	public boolean empty() {
		return mIndex == 0;
	}

	
	public void clear() {
		mIndex = 0;
	}

	
	public int size() {
		return mIndex;
	}

	
	public int get(int index) {
		return mContainer[index];
	}

	
	public void reverse() {
		int size = size();
		for (int i = 0, mid = size >> 1, j = size - 1; i < mid; i++, j--) {
			int tmp = mContainer[i];
			mContainer[i] = mContainer[j];
			mContainer[j] = tmp;
		}
	}

	
	public void set(int index, int value) {
		mContainer[index] = value;
	}

	
	public void zero(int size) {
		size = idealByteArraySize(size);
		if (mContainer.length < size) {
			mContainer = new int[size];
			mIndex = size;
			return;
		}

		fill(mContainer, 0);
		mIndex = size;
	}

	
	public boolean contains(int value) {
		for (int i = 0; i < mIndex; i++) {
			if (mContainer[i] == value) {
				return true;
			}
		}
		return false;
	}

	
	public boolean removeAt(int index) {
		if (index >= mIndex || index < 0) {
			return false;
		}

		if (index == mIndex - 1) {
			mIndex--;
			return true;
		}

		System.arraycopy(mContainer, index + 1, mContainer, index, mIndex - index - 1);
		mIndex--;
		return true;
	}

	static int idealIntArraySize(int need) {
		return idealByteArraySize(need * 4) / 4;
	}

	private static int idealByteArraySize(int need) {
		for (int i = 4; i < 32; i++)
			if (need <= (1 << i) - 12)
				return (1 << i) - 12;

		return need;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (int i = 0; i < mIndex; i++) {
			sb.append(mContainer[i]);
			if (i != mIndex - 1) {
				sb.append(", ");
			}
		}
		sb.append(']');
		return sb.toString();
	}
}
