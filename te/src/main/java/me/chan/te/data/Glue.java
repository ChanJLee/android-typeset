package me.chan.te.data;

import me.chan.te.annotations.Hidden;

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
		/* do nothing */
	}

	@Hidden
	public void reset(float width, float stretch, float shrink) {
		mWidth = width;
		mStretch = stretch;
		mShrink = shrink;
	}
}
