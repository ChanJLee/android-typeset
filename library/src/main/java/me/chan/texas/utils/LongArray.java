package me.chan.texas.utils;

import static java.util.Arrays.fill;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.Arrays;
import java.util.Collection;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class LongArray {
	static final int DEFAULT_SIZE = 16;

	private long[] mContainer;
	private int mIndex = 0;

	public LongArray() {
		this(DEFAULT_SIZE);
	}

	public LongArray(@IntRange(from = 1) int size) {
		mContainer = new long[size <= 0 ? DEFAULT_SIZE : idealIntArraySize(size)];
		mIndex = 0;
	}

	
	public LongArray(@NonNull Collection<Long> collection) {
		this(collection.size());
		for (Long i : collection) {
			add(i);
		}
	}

	
	public void add(long v) {
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

	
	public long get(int index) {
		return mContainer[index];
	}

	
	public void reverse() {
		int size = size();
		for (int i = 0, mid = size >> 1, j = size - 1; i < mid; i++, j--) {
			long tmp = mContainer[i];
			mContainer[i] = mContainer[j];
			mContainer[j] = tmp;
		}
	}

	
	public void set(int index, long value) {
		mContainer[index] = value;
	}

	
	public void zero(int size) {
		size = idealByteArraySize(size);
		if (mContainer.length < size) {
			mContainer = new long[size];
			mIndex = size;
			return;
		}

		fill(mContainer, 0);
		mIndex = size;
	}

	
	public boolean contains(long value) {
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

	private static int idealIntArraySize(int need) {
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

	public long last() {
		return mContainer[mIndex - 1];
	}

	public void removeLast() {
		removeAt(mIndex - 1);
	}

	public void insertAt(int index, long v) {
		if (index >= mIndex) {
			add(v);
			return;
		}

		if (mIndex + 1 > mContainer.length) {
			mContainer = Arrays.copyOf(mContainer, mContainer.length * 2);
		}

		System.arraycopy(mContainer, index, mContainer, index + 1, mIndex - index);
		mContainer[index] = v;
		mIndex++;
	}
}
