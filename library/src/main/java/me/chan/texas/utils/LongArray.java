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

	/**
	 * @param collection 集合
	 */
	public LongArray(@NonNull Collection<Long> collection) {
		this(collection.size());
		for (Long i : collection) {
			add(i);
		}
	}

	/**
	 * @param v 元素
	 */
	public void add(long v) {
		if (mIndex + 1 > mContainer.length) {
			mContainer = Arrays.copyOf(mContainer, mContainer.length * 2);
		}
		mContainer[mIndex++] = v;
	}

	/**
	 * @return 是否为空
	 */
	public boolean empty() {
		return mIndex == 0;
	}

	/**
	 * 清空元素
	 */
	public void clear() {
		mIndex = 0;
	}

	/**
	 * @return 当前大小
	 */
	public int size() {
		return mIndex;
	}

	/**
	 * @param index 索引
	 * @return 索引对应的值
	 */
	public long get(int index) {
		return mContainer[index];
	}

	/**
	 * 反转内容
	 */
	public void reverse() {
		int size = size();
		for (int i = 0, mid = size >> 1, j = size - 1; i < mid; i++, j--) {
			long tmp = mContainer[i];
			mContainer[i] = mContainer[j];
			mContainer[j] = tmp;
		}
	}

	/**
	 * 设置指定位置的值
	 *
	 * @param index 索引
	 * @param value 值
	 */
	public void set(int index, long value) {
		mContainer[index] = value;
	}

	/**
	 * 内容清零并将大小调整到对应的size
	 *
	 * @param size 需要的大小
	 */
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

	/**
	 * @param value 元素
	 * @return 是否包含
	 */
	public boolean contains(long value) {
		for (int i = 0; i < mIndex; i++) {
			if (mContainer[i] == value) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param index 移除的位置
	 * @return 是否移除成功
	 */
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
}
