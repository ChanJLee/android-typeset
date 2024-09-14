package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.util.concurrent.atomic.AtomicInteger;

import me.chan.texas.text.Appearance;
import me.chan.texas.utils.TexasUtils;

/**
 * 一个box为排版中的绘制单元
 * <p>
 * 比如一个单词，一张图片
 */
@RestrictTo(LIBRARY)
public abstract class Box extends Element {
	private static final AtomicInteger UUID = new AtomicInteger(0);

	public static final int STATUS_SELECTED = 1;
	public static final int STATUS_HIGHLIGHT = 1 << 1;

	@IntDef({STATUS_SELECTED, STATUS_HIGHLIGHT})
	public @interface StatusType {
	}

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

	private int mId;

	private int mStatus = 0;

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
		mId = 0;
		mTag = null;
		removeAllStatus();

		mBackground = null;
		mForeground = null;
	}

	public int getId() {
		return mId;
	}

	@CallSuper
	@Override
	protected void onReuse() {
		removeAllStatus();
		mId = UUID.incrementAndGet();
	}

	public Appearance getBackground() {
		return mBackground;
	}

	public Appearance getForeground() {
		return mForeground;
	}

	@RestrictTo(LIBRARY)
	public void addStatus(@StatusType int status) {
		mStatus |= status;
	}

	@RestrictTo(LIBRARY)
	public void removeStatus(@StatusType int status) {
		mStatus &= ~status;
	}

	@RestrictTo(LIBRARY)
	public void removeAllStatus() {
		mStatus = 0;
	}

	@RestrictTo(LIBRARY)
	public boolean containsStatus(int statusHighlight) {
		return (mStatus & statusHighlight) != 0;
	}

	public Object getTag() {
		return mTag;
	}

	public abstract void draw(Canvas canvas, Paint paint, float x, float y, boolean isSelected);

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
