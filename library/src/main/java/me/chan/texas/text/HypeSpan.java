package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.RestrictTo;

import me.chan.texas.text.layout.DrawableBox;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.StateList;

public abstract class HypeSpan {
	private final DrawableBox mDrawableBox;

	public HypeSpan(float width, float height) {
		mDrawableBox = DrawableBox.obtain(this, width, height);
	}

	/**
	 * @return 颜文字的宽
	 */
	public final float getWidth() {
		return mDrawableBox.getWidth();
	}

	/**
	 * @return 颜文字的高
	 */
	public final float getHeight() {
		return mDrawableBox.getHeight();
	}

	public final void draw(Canvas canvas, Paint paint, float x, float y, StateList states) {
		onDraw(canvas, paint, x, y, states, mDrawableBox.getTag());
	}

	protected abstract void onDraw(Canvas canvas, Paint paint, float x, float y, StateList states, Object tag);

	/**
	 * 设置唯一标识
	 *
	 * @param tag tag
	 */
	public final void setTag(Object tag) {
		mDrawableBox.setTag(tag);
	}

	public final void setBackground(Appearance background) {
		mDrawableBox.setBackground(background);
	}

	public final void setForeground(Appearance foreground) {
		mDrawableBox.setForeground(foreground);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public Element getDrawableBox() {
		return mDrawableBox;
	}
}
