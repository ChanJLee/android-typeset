package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.HypeDrawable;
import me.chan.texas.text.TextAttribute;

/**
 * 可绘制box，可以是图片，表情
 */
@RestrictTo(LIBRARY)
public class DrawableBox extends Box {
	private static final ObjectPool<DrawableBox> POOL = new ObjectPool<>(Texas.getMemoryOption().getEmoticonBufferSize());

	private HypeDrawable mDrawable;

	public DrawableBox(@NonNull HypeDrawable drawable) {
		super(drawable.getWidth(), drawable.getHeight());
		mDrawable = drawable;
	}

	@Override
	public void draw(Canvas canvas, Paint paint, float x, float y, StateList states) {
		mDrawable.draw(canvas, paint, x, y, states);
	}

	@Override
	protected void onRecycle() {
		super.onRecycle();
		mDrawable = null;
		POOL.release(this);
	}

	public static DrawableBox obtain(HypeDrawable drawable) {
		DrawableBox drawableBox = POOL.acquire();
		if (drawableBox == null) {
			drawableBox = new DrawableBox(drawable);
		}

		drawableBox.mWidth = drawable.getWidth();
		drawableBox.mHeight = drawable.getHeight();
		drawableBox.mTag = drawable.getTag();
		drawableBox.mBackground = drawable.getBackground();
		drawableBox.mForeground = drawable.getForeground();
		drawableBox.mDrawable = drawable;
		drawableBox.reuse();
		return drawableBox;
	}

	public static void clean() {
		POOL.clean();
	}

	@Override
	public void measure(Measurer measurer, TextAttribute textAttribute) {
		/* do nothing */
	}
}
