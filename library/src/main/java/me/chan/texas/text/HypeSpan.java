package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.Paint;

import me.chan.texas.text.layout.StateList;

public abstract class HypeSpan {
	private Object mTag;
	private Appearance background;
	private Appearance foreground;
	private final float mWidth;
	private final float mHeight;

	public HypeSpan(float width, float height) {
		mWidth = width;
		mHeight = height;
	}

	/**
	 * @return 颜文字的宽
	 */
	public final float getWidth() {
		return mWidth;
	}

	/**
	 * @return 颜文字的高
	 */
	public final float getHeight() {
		return mHeight;
	}

	public final void draw(Canvas canvas, Paint paint, float x, float y, StateList states) {
		onDraw(canvas, paint, x, y, states, mTag);
	}

	protected abstract void onDraw(Canvas canvas, Paint paint, float x, float y, StateList states, Object tag);

	/**
	 * 设置唯一标识
	 *
	 * @param tag tag
	 */
	public final void setTag(Object tag) {
		mTag = tag;
	}

	public final Object getTag() {
		return mTag;
	}

	public final Appearance getBackground() {
		return background;
	}

	public final void setBackground(Appearance background) {
		this.background = background;
	}

	public final Appearance getForeground() {
		return foreground;
	}

	public final void setForeground(Appearance foreground) {
		this.foreground = foreground;
	}
}
