package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.CallSuper;
import androidx.annotation.RestrictTo;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.Appearance;
import me.chan.texas.utils.TexasUtils;

/**
 * 一个box为排版中的绘制单元
 * <p>
 * 比如一个单词，一张图片
 */
@RestrictTo(LIBRARY)
public abstract class Box extends Element {
	/**
	 * 增加修改内容参考
	 * {@link #equals(Object)}
	 */
	protected float mWidth;
	protected float mHeight;

	/**
	 * 唯一标识
	 */
	protected Object mTag;
	/**
	 * 背景
	 */
	protected Appearance mBackground;
	/**
	 * 前景
	 */
	protected Appearance mForeground;

	private int mSeq;
	protected final RectF mInner = new RectF();
	protected final RectF mOuter = new RectF();

	/**
	 * @param width  宽度
	 * @param height 高度
	 */
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

	/**
	 * @param backward 向后遍历
	 * @param index    当前索引
	 * @param count    当前行的长度
	 * @return 是否是孤立的，孤立的意思是旁边是空格
	 */
	public abstract boolean isIsolate(boolean backward, int index, int count);

	@RestrictTo(LIBRARY)
	public final void setInnerBounds(RectF rectF) {
		TexasUtils.copyRect(mInner, rectF);
	}

	public RectF getInnerBounds() {
		return mInner;
	}

	@RestrictTo(LIBRARY)
	public final void setOuterBounds(RectF rectF) {
		TexasUtils.copyRect(mOuter, rectF);
	}

	public RectF getOuterBounds() {
		return mOuter;
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

	public abstract void draw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states);

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

	@RestrictTo(LIBRARY)
	public void linkBounds(Box current) {
		float mid = (mInner.right + current.mInner.left) / 2.0f;
		mOuter.right = current.mOuter.left = mid;
	}
}
