package me.chan.texas.misc;

public class BitBucket32 {
	private static final int SIZE_OF_BUCKET = 32;
	private int mBits;

	public BitBucket32() {
		this(0);
	}

	public BitBucket32(int bits) {
		mBits = bits;
	}

	/**
	 * @param index index
	 * @param v     值
	 */
	public void set(int index, boolean v) {
		if (index < 0 || index >= SIZE_OF_BUCKET) {
			throw new IllegalArgumentException("invalid index");
		}

		if (v) {
			mBits |= (1 << index);
		} else {
			mBits &= ~(1 << index);
		}
	}

	public void set(int index) {
		set(index, true);
	}

	public void clear(int index) {
		set(index, false);
	}

	/**
	 * @param index 获取对应位置
	 * @return true
	 */
	public boolean get(int index) {
		if (index < 0 || index >= SIZE_OF_BUCKET) {
			throw new IllegalArgumentException("invalid index");
		}

		return (mBits & (1 << index)) != 0;
	}

	/**
	 * 清除所有bit位
	 */
	public void clear() {
		reset(0);
	}

	public void reset(int bits) {
		mBits = bits;
	}

	public int getRange(int start, int end) {
		int range = end - start;
		if (start < 0 || start >= end || end > SIZE_OF_BUCKET || range > SIZE_OF_BUCKET) {
			throw new IllegalArgumentException("invalid range");
		}

		if (range == SIZE_OF_BUCKET) {
			return mBits;
		}

		int value = mBits;
		value = value >>> start;
		int mask = (1 << (range)) - 1;
		value = (value & mask);
		return value;
	}

	public int getBits() {
		return mBits;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BitBucket32 that = (BitBucket32) o;
		return mBits == that.mBits;
	}

	@Override
	public int hashCode() {
		return mBits;
	}

	public void copy(BitBucket32 other) {
		mBits = other.mBits;
	}
}
