package me.chan.te.data;

public class Penalty implements Element {
	/**
	 * where3 = 1 if xi is a flagged mPenalty, otherwise = 0.
	 */
	private final boolean mFlag;

	/**
	 * where pi is the mPenalty at xi if ti=‘mPenalty’, otherwise pi= 0;
	 */
	private final float mPenalty;

	public Penalty(float penalty, boolean flag) {
		this.mPenalty = penalty;
		this.mFlag = flag;
	}

	public boolean isFlag() {
		return mFlag;
	}

	public float getPenalty() {
		return mPenalty;
	}

	@Override
	public float getWidth() {
		return 0;
	}
}
