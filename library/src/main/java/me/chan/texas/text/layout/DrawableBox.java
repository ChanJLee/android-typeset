package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.text.HypeSpan;
import me.chan.texas.text.TextAttribute;

/**
 * 可绘制box，可以是图片，表情
 */
@RestrictTo(LIBRARY)
public class DrawableBox extends Box {
	private static final ObjectPool<DrawableBox> POOL = new ObjectPool<>(Texas.getMemoryOption().getEmoticonBufferSize());

	private HypeSpan mDrawable;

	private DrawableBox() {
		super(0, 0);
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

	public static DrawableBox obtain(@NonNull HypeSpan drawable, float width, float height) {
		DrawableBox drawableBox = POOL.acquire();
		if (drawableBox == null) {
			drawableBox = new DrawableBox();
		}

		drawableBox.mWidth = width;
		drawableBox.mHeight = height;
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
