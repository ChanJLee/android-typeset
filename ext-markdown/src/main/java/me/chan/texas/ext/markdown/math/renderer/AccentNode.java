package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class AccentNode extends RendererNode {
	private final String mCmd;
	private final RendererNode mContent;
	private final TextNode mCmdNode;

	public AccentNode(float scale, String cmd, RendererNode content) {
		super(scale);

		mCmd = cmd;
		mContent = content;
		mCmdNode = new TextNode(scale, cmdToSymbol());
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mContent.measure(paint);
		if (isBrace()) {
			measureBrace(paint);
		}
	}

	private void measureBrace(MathPaint paint) {
		paint.save();
		paint.setTextSize(mContent.getWidth());
		mCmdNode.measure(paint);
		paint.restore();
		mCmdNode.setBaselineOffset(mCmdNode.getBaselineOffset() * 1.28f);
		setMeasuredSize(mContent.getWidth(), mContent.getHeight() * 2);
	}

	@Override
	protected void onLayoutChildren() {
		if (isBrace()) {
			layoutBrace();
		}
	}

	private void layoutBrace() {
		if ("overbrace".equals(mCmd)) {
			mCmdNode.layout(0, 0);
			mContent.layout(0, mContent.getHeight() /* brace 高度会被压缩 */);
			return;
		}

		mContent.layout(0, 0);
		mCmdNode.layout(0, mContent.getHeight());
	}

	private boolean isBrace() {
		return "underbrace".equals(mCmd) || "overbrace".equals(mCmd);
	}

	private String cmdToSymbol() {
		if (isBrace()) {
			return "{";
		}
		return "";
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		mContent.draw(canvas, paint);
		if (isBrace()) {
			drawBrace(canvas, paint);
		}
	}

	@Override
	protected String toPretty() {
		return "acc[" + mContent.toPretty() + "]";
	}

	private void drawUnderBrace(MathCanvas canvas, MathPaint paint) {
		canvas.save();
		canvas.translate(0, mContent.getHeight());
		canvas.rotate(270, mCmdNode.getLeft(), mContent.getBottom());
		canvas.scale(mContent.getHeight() * 1.0f / mCmdNode.getWidth(), 1f);
		paint.save();
		paint.setTextSize(mContent.getWidth());
		mCmdNode.draw(canvas, paint);
		paint.restore();
		canvas.restore();
	}

	private void drawOverBrace(MathCanvas canvas, MathPaint paint) {
		canvas.save();
		canvas.rotate(90, mCmdNode.getRight(), mCmdNode.getCenterY());
		canvas.scale(mContent.getHeight() * 1.0f / mCmdNode.getWidth(), 1f);
		paint.save();
		paint.setTextSize(mContent.getWidth());
		mCmdNode.draw(canvas, paint);
		paint.restore();
		canvas.restore();
	}

	private void drawBrace(MathCanvas canvas, MathPaint paint) {
		if ("overbrace".equals(mCmd)) {
			drawOverBrace(canvas, paint);
			return;
		}

		drawUnderBrace(canvas, paint);
	}
}
