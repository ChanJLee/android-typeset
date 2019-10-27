package me.chan.te.data;

import android.support.annotation.NonNull;

import me.chan.te.misc.ObjectFactory;

/**
 * 代表添加的'-'符号
 */
public final class Penalty implements Element {
	private final static ObjectFactory<Penalty> POOL = new ObjectFactory<>(20000);

	private boolean mFlag;

	private float mPenalty;
	private float mWidth;
	private float mHeight;

	private Penalty(float width, float height, float penalty, boolean flag) {
		reset(width, height, penalty, flag);
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
	public void recycle() {
		reset(-1, -1, -1, false);
		POOL.release(this);
	}

	public static void clean() {
		POOL.clean();
	}

	private void reset(float width, float height, float penalty, boolean flag) {
		mWidth = width;
		mHeight = height;
		mPenalty = penalty;
		mFlag = flag;
	}

	@NonNull
	public static Penalty obtain(float width, float height, float penalty, boolean flag) {
		Penalty p = POOL.acquire();
		if (p == null) {
			return new Penalty(width, height, penalty, flag);
		}
		p.reset(width, height, penalty, flag);
		return p;
	}
}
