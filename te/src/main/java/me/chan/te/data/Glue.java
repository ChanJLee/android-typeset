package me.chan.te.data;

public final class Glue implements Element {

	/**
	 * stretchability
	 */
	private float mStretch;
	/**
	 * shrinkability
	 */
	private float mShrink;

	private float mWidth;

	public Glue(float width, float stretch, float shrink) {
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
}
