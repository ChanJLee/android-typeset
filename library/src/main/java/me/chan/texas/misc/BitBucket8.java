package me.chan.texas.misc;

public class BitBucket8 {
	private static final int SIZE_OF_BUCKET = 8;
	private byte mBits;

	public BitBucket8() {
		this((byte) 0);
	}

	public BitBucket8(byte bits) {
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
			mBits |= (byte) (1 << index);
		} else {
			mBits &= (byte) ~(1 << index);
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

		return (mBits & (byte) (1 << index)) != 0;
	}

	/**
	 * 清除所有bit位
	 */
	public void clear() {
		reset((byte) 0);
	}

	public void reset(byte bits) {
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

	public byte getBits() {
		return mBits;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BitBucket8 that = (BitBucket8) o;
		return mBits == that.mBits;
	}

	@Override
	public int hashCode() {
		return mBits;
	}

	public void copy(BitBucket8 other) {
		mBits = other.mBits;
	}
}
