package me.chan.texas.misc;

import java.util.Arrays;

public class BitBucket {
	private static final int SIZE_OF_BUCKET = 32;
	private static final int SIZE_MASK = SIZE_OF_BUCKET - 1;
	private int[] mBits;

	public BitBucket() {
		this(SIZE_OF_BUCKET);
	}

	public BitBucket(int size) {
		mBits = new int[((size + SIZE_MASK) & ~SIZE_MASK) / SIZE_OF_BUCKET];
	}

	/**
	 * @param index index
	 * @param v     值
	 */
	public void set(int index, boolean v) {
		if (index < 0) {
			throw new IllegalArgumentException("invalid index");
		}

		int bucketIndex = index / SIZE_OF_BUCKET;
		if (bucketIndex >= mBits.length) {
			resize();
		}

		int bucketOffset = index % SIZE_OF_BUCKET;

		if (v) {
			mBits[bucketIndex] |= (1 << bucketOffset);
		} else {
			mBits[bucketIndex] &= ~(1 << bucketOffset);
		}
	}

	public void set(int index) {
		set(index, true);
	}

	public void clear(int index) {
		set(index, false);
	}

	private void resize() {
		int[] bits = new int[mBits.length * 2];
		System.arraycopy(mBits, 0, bits, 0, mBits.length);
		mBits = bits;
	}

	/**
	 * @param index 获取对应位置
	 * @return true
	 */
	public boolean get(int index) {
		if (index < 0) {
			throw new IllegalArgumentException("invalid index");
		}

		int bucketIndex = index / SIZE_OF_BUCKET;
		if (bucketIndex >= mBits.length) {
			throw new IllegalArgumentException("invalid index");
		}

		int bucketOffset = index % SIZE_OF_BUCKET;
		return (mBits[bucketIndex] & (1 << bucketOffset)) != 0;
	}

	/**
	 * 清除所有bit位
	 */
	public void clear() {
		Arrays.fill(mBits, 0);
	}

	/**
	 * @return 能存储的最大bit位
	 */
	public int size() {
		return mBits.length * SIZE_OF_BUCKET;
	}

	public int getRange(int start, int end) {
		if (end - start > SIZE_OF_BUCKET) {
			throw new IllegalArgumentException("too large range, max range is 32");
		}

		if (start < 0 || end < 0 || start >= end || end > size()) {
			throw new IllegalArgumentException("invalid range");
		}

		int range = end - start;
		// 获得start到end 之间的bit位
		int index = start / SIZE_OF_BUCKET;
		int offset = start % SIZE_OF_BUCKET;
		long value = mBits[index] & 0xFFFFFFFFL;
		if (offset + range > SIZE_OF_BUCKET) {
			if (++index >= mBits.length) {
				throw new IllegalArgumentException("invalid range");
			}
			long tmp = mBits[index];
			tmp = tmp << SIZE_OF_BUCKET;
			value |= tmp;
		}

		value = value >>> offset;
		long mask = (1L << (range)) - 1;
		value = (value & mask);
		return (int) value;
	}
}
