package me.chan.texas.text;

import android.graphics.drawable.Drawable;

import me.chan.texas.misc.ObjectFactory;

public class Foot extends Segment {
	private static final ObjectFactory<Foot> POOL = new ObjectFactory<>(4);

	private OnClickedListener mOnClickedListener;
	private Drawable mDrawable;

	public Foot(Drawable drawable, OnClickedListener onClickedListener) {
		mOnClickedListener = onClickedListener;
		mDrawable = drawable;
	}

	public Drawable getDrawable() {
		return mDrawable;
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
		mDrawable = null;
		POOL.release(this);
	}

	public static void clean() {
		POOL.clean();
	}

	public static Foot obtain(Drawable drawable, OnClickedListener onClickedListener) {
		Foot foot = POOL.acquire();
		if (foot == null) {
			return new Foot(drawable, onClickedListener);
		}

		foot.mDrawable = drawable;
		foot.mOnClickedListener = onClickedListener;
		foot.reuse();
		return foot;
	}
}
