package me.chan.texas.text.layout;

import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.text.TextAttribute;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 代表一个空格
 */
@RestrictTo(LIBRARY)
public class Glue implements Element {
	public static final Glue TERMINAL = new Glue() {
		public float getWidth() {
			return 0;
		}

		public float getStretch() {
			return Texas.INFINITY_PENALTY;
		}

		public float getShrink() {
			return 0;
		}

		@Override
		public String toString() {
			return "TERMINAL_GLUE";
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

	public void setStretch(float stretch) {
		mStretch = stretch;
	}

	public void setShrink(float shrink) {
		mShrink = shrink;
	}

	public void setWidth(float width) {
		mWidth = width;
	}

	public static Glue obtain() {
		return obtain(FLAG_SHRINK | FLAG_STRETCH | FLAG_WIDTH);
	}

	public static Glue obtain(int flag) {
		Glue glue = new Glue();
		glue.mRefreshFlag = flag;
		return glue;
	}

	@Override
	public void measure(Measurer measurer, TextAttribute textAttribute) {
		float scale = (mRefreshFlag & FLAG_4X_SCALE) != 0 ? 4 : 1;
		if ((mRefreshFlag & FLAG_WIDTH) != 0) {
			mWidth = textAttribute.getSpaceWidth() * scale;
		}

		if ((mRefreshFlag & FLAG_SHRINK) != 0) {
			mShrink = textAttribute.getSpaceShrink() * scale;
		}

		if ((mRefreshFlag & FLAG_STRETCH) != 0) {
			mStretch = textAttribute.getSpaceStretch() * scale;
		}
	}

	public static final int FLAG_WIDTH = 1;
	public static final int FLAG_SHRINK = 1 << 1;
	public static final int FLAG_STRETCH = 1 << 2;
	public static final int FLAG_4X_SCALE = 1 << 3;
}
