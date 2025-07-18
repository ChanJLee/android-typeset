package me.chan.texas.text.layout;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.Texas;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.misc.RectF;
import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;
import me.chan.texas.text.HypeSpan;
import me.chan.texas.text.TextAttribute;

/**
 * 可绘制box，可以是图片，表情
 */
@RestrictTo(LIBRARY)
public class DrawableBox extends Box {
	private static final ObjectPool<DrawableBox> POOL = new ObjectPool<>(Texas.getMemoryOption().getEmoticonBufferSize());

	private HypeSpan mSpan;

	private DrawableBox() {
		super(0, 0);
	}

	@Override
	public void draw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states) {
		mSpan.draw(canvas, paint, inner, outer, baselineOffset, states);
	}

	@VisibleForTesting
	public HypeSpan getSpan() {
		return mSpan;
	}

	@Override
	protected void onRecycle() {
		super.onRecycle();
		mSpan = null;
		POOL.release(this);
	}

	public static DrawableBox obtain(@NonNull HypeSpan span, float width, float height) {
		DrawableBox drawableBox = POOL.acquire();
		if (drawableBox == null) {
			drawableBox = new DrawableBox();
		}

		drawableBox.mWidth = width;
		drawableBox.mHeight = height;
		drawableBox.mSpan = span;
		drawableBox.reuse();
		return drawableBox;
	}

	public static void clean() {
		POOL.clean();
	}

	public void resize(float width, float height) {
		mWidth = width;
		mHeight = height;
	}

	@Override
	public void measure(Measurer measurer, TextAttribute textAttribute) {
		/* do nothing */
	}
}
