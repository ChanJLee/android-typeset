package me.chan.texas.text.layout;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextStyle;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 代表添加的'-'符号
 */
@RestrictTo(LIBRARY)
public class Penalty implements Element {
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
			return -Texas.INFINITY_PENALTY;
		}

		@Override
		public float getWidth() {
			return 0;
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
			return Texas.INFINITY_PENALTY;
		}

		@Override
		public float getWidth() {
			return 0;
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

	public static Penalty obtainFakePenalty(float penalty) {
		return obtain(penalty, false, null, null);
	}

	public static Penalty obtain(float penalty,
								 Object tag, TextStyle textStyle) {
		return obtain(penalty, true, tag, textStyle);
	}

	@NonNull
	public static Penalty obtain(float penalty, boolean flag/* 不是连字符 true, 连字符 false */,
								 Object tag, TextStyle textStyle) {
		Penalty p = new Penalty();
		p.mFlag = flag;
		p.mPenalty = penalty;
		p.mTag = tag;
		p.mTextStyle = textStyle;
		return p;
	}
}
