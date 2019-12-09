package me.chan.texas.text;

import androidx.annotation.Keep;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.misc.ObjectFactory;

/**
 * 代表一个空格
 */
@Hidden
public final class Glue extends Element {
	private final static ObjectFactory<Glue> POOL = new ObjectFactory<>(40960);

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
		if (isRecycled()) {
			return;
		}

		super.recycle();
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
		glue.reuse();
		return glue;
	}

	@Keep
	public static void clean() {
		POOL.clean();
	}
}
