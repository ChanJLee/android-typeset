package me.chan.te.text;

import me.chan.te.misc.ObjectFactory;

public class Foot extends Segment {
	private static final ObjectFactory<Foot> POOL = new ObjectFactory<>(4);

	private OnClickedListener mOnClickedListener;
	private CharSequence mText;

	public Foot(CharSequence text, OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
		mText = text;
	}

	public OnClickedListener getOnClickedListener() {
		return mOnClickedListener;
	}

	public CharSequence getText() {
		return mText;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mOnClickedListener = null;
		mText = null;
		POOL.release(this);
	}

	public static Foot obtain(CharSequence text, OnClickedListener onClickedListener) {
		Foot foot = POOL.acquire();
		if (foot == null) {
			return new Foot(text, onClickedListener);
		}

		foot.mText = text;
		foot.mOnClickedListener = onClickedListener;
		foot.reuse();
		return foot;
	}
}
