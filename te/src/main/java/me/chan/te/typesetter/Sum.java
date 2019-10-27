package me.chan.te.typesetter;

import me.chan.te.annotations.Hidden;
import me.chan.te.data.Glue;
import me.chan.te.data.Recyclable;
import me.chan.te.misc.ObjectFactory;

@Hidden
public class Sum implements Recyclable {
	private static final ObjectFactory<Sum> POOL = new ObjectFactory<>(2000);

	private float mWidth = 0;
	private float mStretch = 0;
	private float mShrink = 0;

	private Sum() {
	}

	public static void clean() {
		POOL.clean();
	}

	@Override
	public void recycle() {
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
}