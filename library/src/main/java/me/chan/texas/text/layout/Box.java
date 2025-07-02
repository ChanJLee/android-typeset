package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.BitBucket32;
import me.chan.texas.text.Appearance;
import me.chan.texas.utils.TexasUtils;


@RestrictTo(LIBRARY)
public abstract class Box extends Element {
	
	protected float mWidth;
	protected float mHeight;

	
	protected Object mTag;
	
	protected Appearance mBackground;
	
	protected Appearance mForeground;

	private int mSeq;


	
	protected Box(float width, float height) {
		mWidth = width;
		mHeight = height;
	}

	public void setTag(Object tag) {
		mTag = tag;
	}

	public void setBackground(Appearance background) {
		mBackground = background;
	}

	public void setForeground(Appearance foreground) {
		mForeground = foreground;
	}

	public float getWidth() {
		return mWidth;
	}

	public float getHeight() {
		return mHeight;
	}

	@CallSuper
	@Override
	protected void onRecycle() {
		mWidth = 0;
		mHeight = 0;
		mSeq = 0;
		mTag = null;

		mBackground = null;
		mForeground = null;
	}

	@RestrictTo(LIBRARY)
	public int getSeq() {
		return mSeq;
	}

	@RestrictTo(LIBRARY)
	public void setSeq(int seq) {
		mSeq = seq;
	}

	public Appearance getBackground() {
		return mBackground;
	}

	public Appearance getForeground() {
		return mForeground;
	}

	public Object getTag() {
		return mTag;
	}

	public abstract void draw(Canvas canvas, Paint paint, float x, float y, StateList states);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Box box = (Box) o;
		return Float.compare(box.mWidth, mWidth) == 0 &&
				Float.compare(box.mHeight, mHeight) == 0 &&
				TexasUtils.equals(mTag, box.mTag) &&
				TexasUtils.equals(mBackground, box.mBackground) &&
				TexasUtils.equals(mForeground, box.mForeground);
	}
}
