package me.chan.te.data;

import me.chan.te.misc.ObjectFactory;

/**
 * 代表一个空格
 */
public final class Glue implements Element {
	private final static ObjectFactory<Glue> POOL = new ObjectFactory<>(50000);

	/**
	 * stretch ability
	 */
	private float mStretch;
	/**
	 * shrink ability
	 */
	private float mShrink;

	private float mWidth;

	private Glue(float width, float stretch, float shrink) {
		reset(width, stretch, shrink);
	}

	public float getWidth() {
		return mWidth;
	}

	public float getStretch() {
		return mStretch;
	}

	public float getShrink() {
		return mShrink;
	}

	@Override
	public String toString() {
		return "Glue{" +
				"mStretch=" + mStretch +
				", mShrink=" + mShrink +
				", mWidth=" + mWidth +
				'}';
	}

	@Override
	public void recycle() {
		reset(-1, -1, -1);
		POOL.release(this);
	}

	private void reset(float width, float stretch, float shrink) {
		mWidth = width;
		mStretch = stretch;
		mShrink = shrink;
	}

	public static Glue obtain(float width, float stretch, float shrink) {
		Glue glue = POOL.acquire();
		if (glue == null) {
			return new Glue(width, stretch, shrink);
		}
		glue.reset(width, stretch, shrink);
		return glue;
	}
}
