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

		paint.save();
		paint.setTextSize(mContent.getWidth());
		mCmdNode.measure(paint);
		paint.restore();
		mCmdNode.setBaselineOffset(mCmdNode.getBaselineOffset() * 1.28f);
		setMeasuredSize(mContent.getWidth(), mContent.getHeight() * 2);
	}

	@Override
	protected void onLayoutChildren() {
		mCmdNode.layout(0, 0);
		mContent.layout(0, mContent.getHeight());
	}

	private int getAccentCmdHeight() {
		return 0;
	}

	private boolean isUnderCmd() {
		return "underline".equals(mCmd) || "underbrace".equals(mCmd);
	}

	private String cmdToSymbol() {
		if ("underbrace".equals(mCmd)) {
			return "{";
		}
		return "";
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		mContent.draw(canvas, paint);

		canvas.save();
		canvas.rotate(90, mCmdNode.getRight(), mCmdNode.getCenterY());
		canvas.scale(mContent.getHeight() * 1.0f / mCmdNode.getWidth(), 1f);
		paint.save();
		paint.setTextSize(mContent.getWidth());
		mCmdNode.draw(canvas, paint);
		paint.restore();
		canvas.restore();
	}

	@Override
	protected String toPretty() {
		return "";
	}
}
