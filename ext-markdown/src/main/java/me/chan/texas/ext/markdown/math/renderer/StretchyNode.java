package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;

public class StretchyNode extends RendererNode {
	private final SymbolNode mTop;
	private final SymbolNode mMiddle;
	private final SymbolNode mBottom;
	private final SymbolNode mExtension;

	public StretchyNode(float scale, Symbol top, Symbol middle, Symbol bottom, Symbol extension) {
		super(scale);
		mTop = new SymbolNode(scale, top);
		mMiddle = new SymbolNode(scale, middle);
		mBottom = new SymbolNode(scale, bottom);
		mExtension = new SymbolNode(scale, extension);
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mTop.measure(paint);
		mMiddle.measure(paint);
		mBottom.measure(paint);
		mExtension.measure(paint);

		float height = RendererNode.getSize(heightSpec);
		float width = RendererNode.getSize(widthSpec);
		if (RendererNode.getMode(widthSpec) == RendererNode.UNSPECIFIED) {
			width = mMiddle.getWidth();
		}

		if (RendererNode.getMode(heightSpec) != RendererNode.EXACTLY) {
			height = mTop.getHeight() + mMiddle.getHeight() + mBottom.getHeight() + mExtension.getHeight();
		}
		setMeasuredSize((int) Math.ceil(width), (int) Math.ceil(height));
	}

	@Override
	protected void onLayoutChildren() {
		float left = (getWidth() - mMiddle.getWidth()) / 2.0f;
		float centerY = getHeight() / 2.0f;

		mTop.layout(left, 0);
		mMiddle.layout(left, centerY - mMiddle.getHeight() / 2.0f);
		mBottom.layout(left, getHeight() - mBottom.getHeight());

		// extension
		mExtension.layout(left, 0);
	}

	@Override
	public float getBaseline() {
		return getCenterY();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
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
	}

	@Override
	protected String toPretty() {
		return "stretchy";
	}
}
