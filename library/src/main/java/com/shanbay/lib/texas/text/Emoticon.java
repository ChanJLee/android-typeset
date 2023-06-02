package com.shanbay.lib.texas.text;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import com.shanbay.lib.log.Log;
import com.shanbay.lib.texas.Texas;
import com.shanbay.lib.texas.misc.DefaultRecyclable;
import com.shanbay.lib.texas.misc.ObjectPool;
import com.shanbay.lib.texas.text.layout.DrawableBox;

/**
 * 颜文字
 */
public final class Emoticon extends DefaultRecyclable {
	private static final ObjectPool<Emoticon> POOL = new ObjectPool<>(Texas.getMemoryOption().getEmoticonBufferSize());

	@VisibleForTesting
	public static boolean hasBuffered() {
		return !POOL.isEmpty();
	}

	private DrawableBox mDrawableBox;

	private Emoticon(@NonNull Drawable drawable, float width, float height, Object tag, Appearance background, Appearance foreground) {
		mDrawableBox = DrawableBox.obtain(drawable, width, height, this, tag, background, foreground);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public DrawableBox getDrawableBox() {
		return mDrawableBox;
	}

	@Override
	public void recycle() {
		if (isRecycled()) {
			return;
		}

		mDrawableBox = null;
		super.recycle();
		POOL.release(this);
	}

	/**
	 * @return 颜文字的宽
	 */
	public float getWidth() {
		return mDrawableBox == null ? 0 : mDrawableBox.getWidth();
	}

	/**
	 * @return 颜文字的高
	 */
	public float getHeight() {
		return mDrawableBox == null ? 0 : mDrawableBox.getHeight();
	}

	/**
	 * @return drawable绘制对象
	 */
	public Drawable getDrawable() {
		return mDrawableBox == null ? null : mDrawableBox.getDrawable();
	}

	/**
	 * 设置绘制对象
	 * <p>
	 * 如果新的绘制对象和之前的不一致，可能会导致绘制对象被拉伸或者压缩
	 *
	 * @param drawable 新的绘制对象
	 */
	public void setDrawable(@NonNull Drawable drawable) {
		if (mDrawableBox == null) {
			return;
		}

		if (drawable.getIntrinsicWidth() != getWidth() ||
				drawable.getIntrinsicHeight() != getHeight()) {
			Log.w("Emoticon", "drawable size changed, may be cause some problem");
		}
		mDrawableBox.setDrawable(drawable);
	}

	/**
	 * 设置唯一标识
	 *
	 * @param tag tag
	 */
	public void setTag(Object tag) {
		if (mDrawableBox == null) {
			return;
		}

		mDrawableBox.setTag(tag);
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
		Emoticon emoticon = POOL.acquire();
		if (emoticon == null) {
			return new Emoticon(drawable, width, height, tag, background, foreground);
		}

		emoticon.mDrawableBox = DrawableBox.obtain(drawable, width, height, emoticon, tag, background, foreground);
		emoticon.reuse();
		return emoticon;
	}

	public static void clean() {
		POOL.clean();
	}
}
