package me.chan.te.data;

public final class Penalty implements Element {
	/**
	 * where3 = 1 if xi is a flagged mPenalty, otherwise = 0.
	 */
	private boolean mFlag;

	/**
	 * where pi is the mPenalty at xi if ti=‘mPenalty’, otherwise pi= 0;
	 */
	private float mPenalty;
	private float mWidth;
	private float mHeight;

	Penalty(float width, float height, float penalty, boolean flag) {
		mPenalty = penalty;
		mFlag = flag;
		mWidth = width;
		mHeight = height;
	}

	public float getHeight() {
		return mHeight;
	}

	public boolean isFlag() {
		return mFlag;
	}

	public float getPenalty() {
		return mPenalty;
	}

	public float getWidth() {
		return mWidth;
	}

	@Override
	public String toString() {
		return "Penalty{" +
				"mFlag=" + mFlag +
				", mPenalty=" + mPenalty +
				", mWidth=" + mWidth +
				'}';
	}

	@Override
	public void release() {
		/* do nothing */
	}

	public void reset(float width, float height, float penalty, boolean flag) {
		mWidth = width;
		mHeight = height;
		mPenalty = penalty;
		mFlag = flag;
	}
}
