package me.chan.texas.text.layout;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextStyle;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 代表添加的'-'符号
 */
@RestrictTo(LIBRARY)
public class Penalty extends Element {
	private final static ObjectPool<Penalty> POOL = new ObjectPool<>(16);
	public final static Penalty FORCE_BREAK = new Penalty() {
		@Override
		public float getHeight() {
			return 0;
		}

		@Override
		public boolean isFlag() {
			return true;
		}

		@Override
		public float getPenalty() {
			return -Texas.INFINITY;
		}

		@Override
		public float getWidth() {
			return 0;
		}

		@Override
		protected void onRecycle() {
			/* NOOP */
		}

		@Override
		public void measure(Measurer measurer, TextAttribute textAttribute) {
		}

		@Override
		public TextStyle getTextStyle() {
			return null;
		}

		@Override
		public Object getTag() {
			return null;
		}

		@Override
		public String toString() {
			return "FORCE_BRK";
		}
	};
	public final static Element ADVISE_BREAK = Glue.EMPTY;

	/**
	 * 禁止断字
	 */
	public final static Penalty FORBIDDEN_BREAK = new Penalty() {
		@Override
		public float getHeight() {
			return 0;
		}

		@Override
		public boolean isFlag() {
			return false;
		}

		@Override
		public float getPenalty() {
			return Texas.INFINITY;
		}

		@Override
		public float getWidth() {
			return 0;
		}

		@Override
		protected void onRecycle() {
			/* NOOP */
		}

		@Override
		public void measure(Measurer measurer, TextAttribute textAttribute) {
		}

		@Override
		public TextStyle getTextStyle() {
			return null;
		}

		@Override
		public Object getTag() {
			return null;
		}

		@Override
		public String toString() {
			return "FORBIDDEN_BRK";
		}
	};

	private boolean mFlag;

	private float mPenalty;
	private float mWidth;
	private float mHeight;
	private Object mTag;
	private TextStyle mTextStyle;

	private Penalty() {
	}

	public float getHeight() {
		return mHeight;
	}

	public boolean isFlag() {
		return mFlag;
	}

	public float getPenalty() {
		return mPenalty;
	}

	public float getWidth() {
		return mWidth;
	}

	public Object getTag() {
		return mTag;
	}

	public TextStyle getTextStyle() {
		return mTextStyle;
	}

	@Override
	public String toString() {
		return "Penalty{" +
				"mFlag=" + mFlag +
				", mPenalty=" + mPenalty +
				", mWidth=" + mWidth +
				'}';
	}

	@Override
	protected void onRecycle() {
		mWidth = mHeight = 0;
		mPenalty = 0;
		mFlag = false;
		mTag = null;
		mTextStyle = null;
		POOL.release(this);
	}

	@Override
	public void measure(Measurer measurer, TextAttribute textAttribute) {
		if (!mFlag) {
			mWidth = mHeight = 0;
			return;
		}

		if (mTextStyle == null) {
			mWidth = textAttribute.getHyphenWidth();
			mHeight = textAttribute.getHyphenHeight();
			return;
		}

		Measurer.CharSequenceSpec spec = Measurer.CharSequenceSpec.obtain();
		measurer.measure("-", 0, 1, mTextStyle, mTag, spec);
		mWidth = spec.getWidth();
		mHeight = spec.getHeight();
		spec.recycle();
	}

	public static void clean() {
		POOL.clean();
	}

	public static Penalty obtainFakePenalty(float penalty) {
		return obtain(penalty, false, null, null, null, null);
	}

	public static Penalty obtain(float penalty,
								 Object tag, TextStyle textStyle,
								 Measurer measurer, TextAttribute textAttribute) {
		return obtain(penalty, true, tag, textStyle, measurer, textAttribute);
	}

	@NonNull
	public static Penalty obtain(float penalty, boolean flag/* 不是连字符 true, 连字符 false */,
								 Object tag, TextStyle textStyle, Measurer measurer, TextAttribute textAttribute) {
		Penalty p = POOL.acquire();
		if (p == null) {
			p = new Penalty();
		} else {
			p.reuse();
		}
		p.mFlag = flag;
		p.mPenalty = penalty;
		p.mTag = tag;
		p.mTextStyle = textStyle;
		p.measure(measurer, textAttribute);
		return p;
	}
}
