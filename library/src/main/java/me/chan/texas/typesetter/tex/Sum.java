package me.chan.texas.typesetter.tex;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;

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
	protected void onRecycle() {
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
