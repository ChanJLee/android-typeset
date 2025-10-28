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
		mCmdNode = isBrace() || isGlyph() ? new TextNode(scale, cmdToSymbol()) : null;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mContent.measure(paint);
		if (isBrace()) {
			measureBrace(paint);
			return;
		}

		if (isGlyph()) {
			measureGlyph(paint);
			return;
		}
	}

	private void measureGlyph(MathPaint paint) {
		mCmdNode.measure(paint);
		if ("check".equals(mCmd)) {
			setMeasuredSize(
					(int) Math.ceil(Math.max(mCmdNode.getWidth(), mContent.getWidth())),
					(int) Math.ceil(mCmdNode.getHeight() + mContent.getHeight())
			);
			return;
		}

		setMeasuredSize(
				(int) Math.ceil(Math.max(mCmdNode.getWidth(), mContent.getWidth())),
				(int) Math.ceil(mCmdNode.getHeight() / 2.0f + mContent.getHeight())
		);
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
			return;
		}

		if (isGlyph()) {
			layoutGlyph();
			return;
		}
	}

	@Override
	public float getBaseline() {
		return mContent.getBaseline();
	}

	private void layoutGlyph() {
		if ("hat".equals(mCmd) || "widehat".equals(mCmd) ||
				"dot".equals(mCmd) || "ddot".equals(mCmd) || "dddot".equals(mCmd) ||
				"acute".equals(mCmd) || "grave".equals(mCmd) || "breve".equals(mCmd)) {
			mCmdNode.layout((mContent.getWidth() - mCmdNode.getWidth()) / 2.0f, 0);
			mContent.layout(0, mCmdNode.getHeight() / 2.0f);
			return;
		}

		if ("tilde".equals(mCmd) || "widetilde".equals(mCmd)) {
			mCmdNode.layout((mContent.getWidth() - mCmdNode.getWidth()) / 2.0f, -mCmdNode.getHeight() / 2f);
			mContent.layout(0, mCmdNode.getBottom());
			return;
		}

		mCmdNode.layout((mContent.getWidth() - mCmdNode.getWidth()) / 2.0f, 0);
		mContent.layout(0, mCmdNode.getHeight());
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

	private boolean isGlyph() {
		return "hat".equals(mCmd) || "widehat".equals(mCmd) ||
				"tilde".equals(mCmd) || "widetilde".equals(mCmd) ||
				"dot".equals(mCmd) || "ddot".equals(mCmd) || "dddot".equals(mCmd)
				|| "acute".equals(mCmd) || "grave".equals(mCmd) || "breve".equals(mCmd) || "check".equals(mCmd);
	}

	private String cmdToSymbol() {
		if (isBrace()) {
			return MathFontOptions.formatSymbol("braceleft");
		}

		if ("hat".equals(mCmd) || "widehat".equals(mCmd)) {
			return MathFontOptions.formatSymbol("asciicircum");
		}

		if ("tilde".equals(mCmd) || "widetilde".equals(mCmd)) {
			return MathFontOptions.formatSymbol("asciitilde");
		}

		if ("dot".equals(mCmd)) {
			return MathFontOptions.formatSymbol("dotaccent");
		}

		if ("ddot".equals(mCmd)) {
			return MathFontOptions.formatSymbol("ddotaccent");
		}

		if ("dddot".equals(mCmd)) {
			return MathFontOptions.formatSymbol("dddotaccent");
		}

		if ("acute".equals(mCmd)) {
			return MathFontOptions.formatSymbol("acute");
		}

		if ("grave".equals(mCmd)) {
			return MathFontOptions.formatSymbol("grave");
		}

		if ("breve".equals(mCmd)) {
			return MathFontOptions.formatSymbol("breve");
		}

		if ("check".equals(mCmd)) {
			return MathFontOptions.formatSymbol("checkmark");
		}

		throw new RuntimeException("unknown cmd: " + mCmd);
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		mContent.draw(canvas, paint);
		if (isBrace()) {
			drawBrace(canvas, paint);
			return;
		}

		if (isGlyph()) {
			drawGlyph(canvas, paint);
			return;
		}
	}

	private void drawGlyph(MathCanvas canvas, MathPaint paint) {
		mCmdNode.draw(canvas, paint);
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
