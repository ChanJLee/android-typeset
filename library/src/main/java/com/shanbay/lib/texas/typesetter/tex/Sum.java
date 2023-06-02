package com.shanbay.lib.texas.typesetter.tex;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.Texas;
import com.shanbay.lib.texas.text.layout.Box;
import com.shanbay.lib.texas.text.layout.Glue;
import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.misc.ObjectPool;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

@RestrictTo(LIBRARY)
public class Sum extends DefaultRecyclable {
	private static final ObjectPool<Sum> POOL = new ObjectPool<>(64);
	static {
		Texas.registerLifecycleCallback(new Texas.LifecycleCallback() {
			@Override
			public void onClean() {
				POOL.clean();
			}
		});
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

	public void increase(Box box) {
		mWidth += box.getWidth();
	}

	public void increase(Glue glue) {
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

	@Override
	public String toString() {
		return "Sum{" +
				"mWidth=" + mWidth +
				", mStretch=" + mStretch +
				", mShrink=" + mShrink +
				'}';
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
}