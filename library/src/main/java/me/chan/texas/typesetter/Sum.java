package me.chan.texas.typesetter;

import androidx.annotation.Keep;

import me.chan.texas.Texas;
import me.chan.texas.annotations.Hidden;
import me.chan.texas.text.Glue;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectFactory;

@Hidden
class Sum extends DefaultRecyclable {
	private static final ObjectFactory<Sum> POOL = new ObjectFactory<>(1024);
	static {
		Texas.register(Sum.class);
	}

	private float mWidth = 0;
	private float mStretch = 0;
	private float mShrink = 0;

	private Sum() {
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mWidth = mShrink = mStretch = 0;
		POOL.release(this);
	}

	public void increaseWidth(float width) {
		mWidth += width;
	}

	public void increaseGlue(Glue glue) {
		mWidth += glue.getWidth();
		mShrink += glue.getShrink();
		mStretch += glue.getStretch();
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

	public static Sum obtain() {
		Sum sum = POOL.acquire();
		if (sum != null) {
			sum.reuse();
			return sum;
		}
		return new Sum();
	}

	public static Sum obtain(Sum other) {
		Sum sum = obtain();
		sum.mWidth = other.mWidth;
		sum.mShrink = other.mShrink;
		sum.mStretch = other.mStretch;
		return sum;
	}

	@Keep
	public static void clean() {
		POOL.clean();
	}
}