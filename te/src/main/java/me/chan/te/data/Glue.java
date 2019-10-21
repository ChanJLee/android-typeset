package me.chan.te.data;

public final class Glue implements Element {

	/**
	 * stretch ability
	 */
	private float mStretch;
	/**
	 * shrink ability
	 */
	private float mShrink;

	private float mWidth;

	Glue(float width, float stretch, float shrink) {
		this.mStretch = stretch;
		this.mShrink = shrink;
		this.mWidth = width;
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
	public void release() {
		/* do nothing */
	}

	public void reset(float width, float stretch, float shrink) {
		mWidth = width;
		mStretch = stretch;
		mShrink = shrink;
	}
}
