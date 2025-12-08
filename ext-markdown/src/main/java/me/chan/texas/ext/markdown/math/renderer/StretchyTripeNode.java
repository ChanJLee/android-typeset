package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;

public class StretchyTripeNode extends RendererNode {
	private final SymbolNode mTop;
	private final SymbolNode mMiddle;
	private final SymbolNode mBottom;
	private final SymbolNode mExtension;
	private int mActualWidth;

	public StretchyTripeNode(MathPaint.Styles styles, Symbol top, Symbol middle, Symbol bottom, Symbol extension) {
		super(styles);
		mTop = new SymbolNode(styles, top);
		mMiddle = new SymbolNode(styles, middle);
		mBottom = new SymbolNode(styles, bottom);
		mExtension = new SymbolNode(styles, extension);
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mTop.measure(paint);
		mMiddle.measure(paint);
		mBottom.measure(paint);
		mExtension.measure(paint);

		float height = RendererNode.getSize(heightSpec);
		float width = RendererNode.getSize(widthSpec);

		if (RendererNode.getMode(heightSpec) == RendererNode.UNSPECIFIED) {
			height = mTop.getHeight() + mMiddle.getHeight() + mBottom.getHeight();
		} else if (RendererNode.getMode(heightSpec) == RendererNode.AT_MOST) {
			height = Math.min(height, mTop.getHeight() + mMiddle.getHeight() + mBottom.getHeight());
		}

		float centerY = height / 2.0f;
		mTop.layout(0);
		mMiddle.layout(centerY - mMiddle.getHeight() / 2.0f);
		mBottom.layout(height - mBottom.getHeight());

		// extension
		mExtension.layout(0);

		float left = Math.min(
				mTop.getLeft(),
				Math.min(
						mBottom.getLeft(),
						Math.min(mMiddle.getLeft(), mExtension.getLeft()))
		);
		float right = Math.max(
				mTop.getRight(),
				Math.max(
						mBottom.getRight(),
						Math.max(mMiddle.getRight(), mExtension.getRight()))
		);
		mActualWidth = (int) Math.ceil(right - left);
		if (RendererNode.getMode(widthSpec) == RendererNode.UNSPECIFIED) {
			width = mActualWidth;
		}

		float dx = -left;
		mTop.translate(dx, 0);
		mBottom.translate(dx, 0);
		mMiddle.translate(dx, 0);
		mExtension.translate(dx, 0);
		setMeasuredSize((int) Math.ceil(width), (int) Math.ceil(height));
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		int width = getWidth();
		if (width != mActualWidth) {
			canvas.save();
			canvas.scale(width * 1.0f / mActualWidth, 1f);
		}

		mTop.draw(canvas, paint);

		float top = mTop.getBottom();
		float bottom = mMiddle.getTop();
		float scaleY = (bottom - top) / mExtension.getHeight();
		if (scaleY > 0) {
			canvas.save();
			canvas.translate(0, top);
			canvas.scale(1, scaleY);
			mExtension.draw(canvas, paint);
			canvas.restore();
		}

		mMiddle.draw(canvas, paint);

		if (scaleY > 0) {
			canvas.save();
			canvas.translate(0, mMiddle.getBottom());
			canvas.scale(1, scaleY);
			mExtension.draw(canvas, paint);
			canvas.restore();
		}

		mBottom.draw(canvas, paint);

		if (width != mActualWidth) {
			canvas.restore();
		}
	}

	@Override
	protected void onDrawDebug(MathCanvas canvas, MathPaint paint) {
		super.onDrawDebug(canvas, paint);
		drawDebugBounds(canvas, paint);
	}

	@Override
	protected String toPretty() {
		return "stretchy";
	}
}
