package me.chan.texas.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import me.chan.texas.renderer.RendererContext;

/**
 * 批量绘制Appearance，会更有效率一点
 */
public abstract class BatchDrawAppearance extends Appearance {

	private final RectF mInner = new RectF();
	private final RectF mOuter = new RectF();

	private boolean mShouldReset = false;

	@Override
	public final void draw(Canvas canvas, Paint paint, RectF inner, RectF outer, RendererContext context) {
		if (!isEnable()) {
			return;
		}

		if (context.checkLocation(RendererContext.LOCATION_LINE_START) || mShouldReset) {
			mInner.set(inner);
			mOuter.set(outer);
			mShouldReset = false;
		}

		mInner.right = inner.right;
		mOuter.right = outer.right;
		mInner.top = Math.max(mInner.top, inner.top);
		mInner.bottom = Math.max(mInner.bottom, inner.bottom);
		mOuter.top = Math.max(mOuter.top, outer.top);
		mOuter.bottom = Math.max(mOuter.bottom, outer.bottom);
		if (context.checkLocation(RendererContext.LOCATION_LINE_END) ||
				!isSameGroup(context)) {
			scheduleDraw(canvas, paint);
			mShouldReset = true;
		}
	}

	private void scheduleDraw(Canvas canvas, Paint paint) {
		onDraw(canvas, paint, mInner, mOuter);
	}

	protected abstract void onDraw(Canvas canvas, Paint paint, RectF inner, RectF outer);

	protected abstract boolean isSameGroup(RendererContext context);

	// protected abstract boolean shouldDraw(RendererContext context);

	protected boolean isEnable() {
		return true;
	}
}
