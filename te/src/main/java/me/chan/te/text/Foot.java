package me.chan.te.text;

import me.chan.te.misc.ObjectFactory;

public class Foot extends Segment {
	private static final ObjectFactory<Foot> POOL = new ObjectFactory<>(4);

	private OnClickedListener mOnClickedListener;

	public Foot(OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
	}

	public OnClickedListener getOnClickedListener() {
		return mOnClickedListener;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		super.recycle();
		mOnClickedListener = null;
		POOL.release(this);
	}

	public static Foot obtain(OnClickedListener onClickedListener) {
		Foot foot = POOL.acquire();
		if (foot == null) {
			return new Foot(onClickedListener);
		}

		foot.mOnClickedListener = onClickedListener;
		foot.reuse();
		return foot;
	}
}
