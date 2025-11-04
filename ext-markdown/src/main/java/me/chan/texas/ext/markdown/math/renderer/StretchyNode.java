package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class StretchyNode extends RendererNode {
	private final OpNode mTop;
	private final OpNode mMiddle;
	private final OpNode mBottom;
	private final OpNode mExtension;

	public StretchyNode(float scale, String top, String middle, String bottom, String extension) {
		super(scale);
		mTop = new OpNode(scale, top, false);
		mMiddle = new OpNode(scale, middle, true);
		mBottom = new OpNode(scale, bottom, false);
		mExtension = new OpNode(scale, extension, false);
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
