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
import me.chan.texas.text.HyperSpan;
import me.chan.texas.text.TextAttribute;

/**
 * 可绘制box，可以是图片，表情
 */
public class DrawableBox extends Box {
	private static final ObjectPool<DrawableBox> POOL = new ObjectPool<>(Texas.getMemoryOption().getEmoticonBufferSize());

	private HyperSpan mSpan;

	private DrawableBox() {
		super(0, 0);
	}

	@Override
	public void draw(TexasCanvas canvas, TexasPaint paint, RectF inner, RectF outer, float baselineOffset, StateList states) {
		mSpan.draw(canvas, paint, inner, outer, baselineOffset, states);
	}

	@VisibleForTesting
	@RestrictTo(LIBRARY)
	public HyperSpan getSpan() {
		return mSpan;
	}

	@Override
	protected void onRecycle() {
		super.onRecycle();
		mSpan = null;
		POOL.release(this);
	}

	@Override
	public final boolean isIsolate(boolean backward) {
		return true;
	}

	public static DrawableBox obtain(@NonNull HyperSpan span, float width, float height) {
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

	@RestrictTo(LIBRARY)
	public static void clean() {
		POOL.clean();
	}

	@RestrictTo(LIBRARY)
	public void resize(float width, float height) {
		mWidth = width;
		mHeight = height;
	}

	@Override
	protected void onMeasure(Measurer measurer, TextAttribute textAttribute) {
		mSpan.measure(textAttribute.getLineHeight(), textAttribute.getBaselineOffset());
	}
}
