package me.chan.texas.text;

import android.graphics.Canvas;

import me.chan.texas.misc.RectF;

import me.chan.texas.renderer.RendererContext;
import me.chan.texas.renderer.core.graphics.TexasPaint;

/**
 * 批量绘制Appearance，会更有效率一点
 */
public abstract class BatchDrawAppearance extends Appearance {

	private final RectF mInner = new RectF();
	private final RectF mOuter = new RectF();

	private boolean mShouldReset = false;

	@Override
	public final void draw(Canvas canvas, TexasPaint paint, RectF inner, RectF outer, RendererContext context) {
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
			scheduleDraw(canvas, paint, context);
			mShouldReset = true;
		}
	}

	private void scheduleDraw(Canvas canvas, TexasPaint paint, RendererContext context) {
		onDraw(canvas, paint, mInner, mOuter, context);
	}

	protected abstract void onDraw(Canvas canvas, TexasPaint paint, RectF inner, RectF outer, RendererContext context);

	protected abstract boolean isSameGroup(RendererContext context);

	protected boolean isEnable() {
		return true;
	}
}
