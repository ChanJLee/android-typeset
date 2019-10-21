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

	public Penalty(float width, float penalty, boolean flag) {
		this.mPenalty = penalty;
		this.mFlag = flag;
		mWidth = width;
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

	public void reset(float width, float penalty, boolean flag) {
		mWidth = width;
		mPenalty = penalty;
		mFlag = false;
	}
}
