package me.chan.texas.text.layout;

import androidx.annotation.CallSuper;
import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.TextAttribute;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.annotation.SuppressLint;

/**
 * 代表一个空格
 */
@RestrictTo(LIBRARY)
public class Glue extends Element {
	private final static ObjectPool<Glue> POOL = new ObjectPool<>(16);
	public static final Glue TERMINAL = new Glue() {
		public float getWidth() {
			return 0;
		}

		public float getStretch() {
			return Texas.INFINITY;
		}

		public float getShrink() {
			return 0;
		}

		@Override
		public String toString() {
			return "TERMINAL_GLUE";
		}

		@SuppressLint("MissingSuperCall")
		@Override
		protected void onRecycle() {

		}
	};

	public static final Glue EMPTY = new Glue() {
		public float getWidth() {
			return 0;
		}

		public float getStretch() {
			return 0;
		}

		public float getShrink() {
			return 0;
		}

		@Override
		public String toString() {
			return "ADVICE_BRK";
		}

		@SuppressLint("MissingSuperCall")
		@Override
		protected void onRecycle() {

		}
	};

	/**
	 * stretch ability
	 */
	protected float mStretch;
	/**
	 * shrink ability
	 */
	protected float mShrink;

	protected float mWidth;

	protected int mRefreshFlag;

	protected Glue() {
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

	@CallSuper
	@Override
	protected void onRecycle() {
		mWidth = mShrink = mStretch = 0;
		mRefreshFlag = 0;
		POOL.release(this);
	}

	public static Glue obtain(TextAttribute textAttribute) {
		Glue glue = POOL.acquire();
		if (glue == null) {
			glue = new Glue();
		} else {
			glue.reuse();
		}
		glue.mRefreshFlag = FLAG_WIDTH | FLAG_SHRINK | FLAG_STRETCH;
		glue.measure(null, textAttribute);
		return glue;
	}

	public static Glue obtain(float width, float shrink, float stretch, int flag) {
		Glue glue = POOL.acquire();
		if (glue == null) {
			glue = new Glue();
		} else {
			glue.reuse();
		}
		glue.mWidth = width;
		glue.mShrink = shrink;
		glue.mStretch = stretch;
		glue.mRefreshFlag = flag;
		return glue;
	}

	public static void clean() {
		POOL.clean();
	}

	@Override
	public void measure(Measurer measurer, TextAttribute textAttribute) {
		if ((mRefreshFlag & FLAG_WIDTH) != 0) {
			mWidth = textAttribute.getSpaceWidth();
		}

		if ((mRefreshFlag & FLAG_SHRINK) != 0) {
			mShrink = textAttribute.getSpaceShrink();
		}

		if ((mRefreshFlag & FLAG_STRETCH) != 0) {
			mStretch = textAttribute.getSpaceStretch();
		}
	}

	public static final int FLAG_WIDTH = 1;
	public static final int FLAG_SHRINK = 1 << 1;
	public static final int FLAG_STRETCH = 1 << 2;
}
