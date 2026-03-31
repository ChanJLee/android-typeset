package me.chan.texas.text;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.layout.StateList;

/**
 * 颜文字
 */
public final class Emoticon extends HyperSpan {
	private static final int[] STATE_PRESSED = {
			android.R.attr.state_pressed,
	};
	private static final int[] STATE_NORMAL = {
			-android.R.attr.state_pressed
	};

	private Drawable mDrawable;
	private float mPaddingLeft = 0;
	private float mPaddingRight = 0;
	private final float mWidth;
	private final float mHeight;

	private Emoticon(@NonNull Drawable drawable, float width, float height, Object tag, Appearance background, Appearance foreground) {
		super(width, height);
		mWidth = width;
		mHeight = height;
		setBackground(background);
		setForeground(foreground);
		setTag(tag);
		mDrawable = drawable;
	}

	@VisibleForTesting
	Drawable getDrawable() {
		return mDrawable;
	}

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states) {
		float x = inner.left + mPaddingLeft;
		float y = inner.bottom - baselineOffset;
		Drawable drawable = mDrawable;
		if (mDrawable instanceof StateListDrawable) {
			StateListDrawable stateListDrawable = (StateListDrawable) mDrawable;
			stateListDrawable.setState(states.isSelected() ? STATE_PRESSED : STATE_NORMAL);
			drawable = stateListDrawable.getCurrent();
		}

		drawable.setBounds((int) x, (int) (y - mHeight), (int) (x + mWidth), (int) y);
		canvas.draw(drawable);
	}

	@Override
	protected void onMeasure(float lineHeight, float baselineOffset) {
		setMeasuredSize(mWidth + mPaddingRight + mPaddingLeft, mHeight);
	}

	/**
	 * 设置绘制对象
	 * <p>
	 * 如果新的绘制对象和之前的不一致，可能会导致绘制对象被拉伸或者压缩
	 *
	 * @param drawable 新的绘制对象
	 */
	public void setDrawable(@NonNull Drawable drawable) {
		if (mDrawable == null) {
			return;
		}

		if (drawable.getIntrinsicWidth() != getWidth() ||
				drawable.getIntrinsicHeight() != getHeight()) {
			Log.w("Emoticon", "drawable size changed, may be cause some problem");
		}
		mDrawable = drawable;
	}

	public void setPaddingLeft(float paddingLeft) {
		mPaddingLeft = paddingLeft;
	}

	public void setPaddingRight(float paddingRight) {
		mPaddingRight = paddingRight;
	}

	public void setHorizontalPadding(float padding) {
		mPaddingLeft = padding;
		mPaddingRight = padding;
	}

	/**
	 * @param drawable 绘制对象
	 * @return 颜文字对象
	 */
	public static Emoticon obtain(BitmapDrawable drawable) {
		return obtain(drawable, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
	}

	/**
	 * 获取Emoticon
	 *
	 * @param drawable 绘制对象
	 * @param width    宽，如果宽小于0，那么行为将是未定义的
	 * @param height   高，如果高小于0，那么行为将是未定义的
	 * @return 颜文字对象
	 */
	public static Emoticon obtain(Drawable drawable, float width, float height) {
		return obtain(drawable, width, height, null, null, null);
	}

	/**
	 * 获取Emoticon
	 *
	 * @param drawable 绘制对象
	 * @param width    宽，如果宽小于0，那么行为将是未定义的
	 * @param height   高，如果高小于0，那么行为将是未定义的
	 * @param tag      tag 唯一标识
	 * @return 颜文字对象
	 */
	public static Emoticon obtain(Drawable drawable, float width, float height, Object tag, Appearance background, Appearance foreground) {
		return new Emoticon(drawable, width, height, tag, background, foreground);
	}
}
