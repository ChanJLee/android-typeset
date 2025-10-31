package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class StretchyNode extends RendererNode {
	private final RendererNode mTop;
	private final RendererNode mMiddle;
	private final RendererNode mBottom;
	private final RendererNode mExtension;

	public StretchyNode(float scale, String top, String middle, String extension, String bottom) {
		super(scale);
		mTop = new TextNode(scale, top);
		mMiddle = new TextNode(scale, middle);
		mBottom = new TextNode(scale, bottom);
		mExtension = new TextNode(scale, extension);
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
			width = Math.max(mTop.getWidth(), Math.max(mMiddle.getWidth(), mBottom.getWidth()));
		}

		if (RendererNode.getMode(heightSpec) != RendererNode.EXACTLY) {
			throw new IllegalArgumentException("height must be EXACTLY");
		}
		setMeasuredSize((int) Math.ceil(width), (int) Math.ceil(height));
	}

	@Override
	protected void onLayoutChildren() {
		float centerX = getCenterX();
		float centerY = getCenterY();

		mTop.layout(centerX - mTop.getWidth() / 2.0f, 0);
		mMiddle.layout(centerX - mMiddle.getWidth() / 2.0f, centerY - mMiddle.getHeight() / 2.0f);
		mBottom.layout(centerX - mBottom.getWidth() / 2.0f, centerY + mBottom.getHeight());

		// extension
		mExtension.layout(centerX - mExtension.getWidth() / 2.0f, 0);
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
