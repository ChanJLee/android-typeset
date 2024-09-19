package me.chan.texas.misc;

import java.util.Arrays;

public class BitBucket {
	private static final int BITS_SIZE_OF_INT = 32;
	private static final int SIZE_MASK = BITS_SIZE_OF_INT - 1;
	private int[] mBits;

	public BitBucket() {
		this(32);
	}

	public BitBucket(int size) {
		mBits = new int[((size + SIZE_MASK) & ~SIZE_MASK) / BITS_SIZE_OF_INT];
	}

	/**
	 * @param index index
	 * @param v     值
	 * @return 是否写入成功
	 */
	public boolean set(int index, boolean v) {
		if (index < 0) {
			return false;
		}

		int bucketIndex = index / BITS_SIZE_OF_INT;
		if (bucketIndex >= mBits.length) {
			int[] bits = new int[mBits.length * 2];
			System.arraycopy(mBits, 0, bits, 0, mBits.length);
			mBits = bits;
		}

		int bucketOffset = index % BITS_SIZE_OF_INT;

		if (v) {
			mBits[bucketIndex] |= (1 << bucketOffset);
		} else {
			mBits[bucketIndex] &= ~(1 << bucketOffset);
		}

		return true;
	}

	/**
	 * @param index 获取对应位置
	 * @return true
	 */
	public boolean get(int index) {
		if (index < 0) {
			return false;
		}

		int bucketIndex = index / BITS_SIZE_OF_INT;
		if (bucketIndex >= mBits.length) {
			return false;
		}

		int bucketOffset = index % BITS_SIZE_OF_INT;
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
		return mBits.length * BITS_SIZE_OF_INT;
	}
}
