package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;

public class AccentNode extends RendererNode {
	private final String mCmd;
	private final RendererNode mContent;
	private final RendererNode mCmdNode;

	public AccentNode(float scale, String cmd, RendererNode content) {
		super(scale);

		mCmd = cmd;
		mContent = content;
		if (isBrace()) {
			mCmdNode = new StretchyNode(1, MathFontOptions.formatSymbol("uni23A7"), MathFontOptions.formatSymbol("uni23A8"), MathFontOptions.formatSymbol("uni23A9"), MathFontOptions.formatSymbol("uni23AA"));
		} else if (isGlyph()) {
			if ("check".equals(mCmd)) {
				mCmdNode = new TextNode(scale * 0.5f, cmdToSymbol());
			} else {
				mCmdNode = new TextNode(scale, cmdToSymbol());
			}
		} else {
			mCmdNode = null;
		}
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

	// TODO
	private void measureBrace(MathPaint paint) {
		mCmdNode.measure(paint,
				RendererNode.makeMeasureSpec(
						mContent.getHeight(), RendererNode.EXACTLY
				),
				RendererNode.makeMeasureSpec(
						mContent.getWidth(), RendererNode.EXACTLY
				));
		setMeasuredSize(mContent.getWidth(), mContent.getHeight() + mCmdNode.getWidth());
	}

	@Override
	protected void onLayoutChildren() {
		if (isBrace()) {
			layoutBrace();
			return;
		}

		if (isGlyph()) {
			layoutGlyph();
		}
	}

	@Override
	public float getBaseline() {
		return mContent.getBaseline();
	}

	private void layoutGlyph() {
		if ("widehat".equals(mCmd)) {
			mCmdNode.layout(0, 0);
			mContent.layout(0, mCmdNode.getHeight() / 2.0f);
			return;
		}

		if ("widetilde".equals(mCmd)) {
			mCmdNode.layout(0, -mCmdNode.getHeight() / 2f);
			mContent.layout(0, mCmdNode.getBottom());
			return;
		}

		if ("hat".equals(mCmd) ||
				"dot".equals(mCmd) || "ddot".equals(mCmd) || "dddot".equals(mCmd) ||
				"acute".equals(mCmd) || "grave".equals(mCmd) || "breve".equals(mCmd)) {
			mCmdNode.layout((mContent.getWidth() - mCmdNode.getWidth()) / 2.0f, 0);
			mContent.layout(0, mCmdNode.getHeight() / 2.0f);
			return;
		}

		if ("tilde".equals(mCmd)) {
			mCmdNode.layout((mContent.getWidth() - mCmdNode.getWidth()) / 2.0f, -mCmdNode.getHeight() / 2f);
			mContent.layout(0, mCmdNode.getBottom());
			return;
		}

		mCmdNode.layout((mContent.getWidth() - mCmdNode.getWidth()) / 2.0f, 0);
		mContent.layout(0, mCmdNode.getHeight());
	}

	private void layoutBrace() {
		float x = 0;
		float y = mContent.getHeight();
		if ("overbrace".equals(mCmd)) {
			mCmdNode.layout(x - mCmdNode.getWidth(), y - mCmdNode.getHeight());
			mContent.layout(0, mContent.getHeight() /* brace 高度会被压缩 */);
			return;
		}

		mContent.layout(0, 0);
		mCmdNode.layout(x - mCmdNode.getWidth(), y);
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

	private boolean isWideGlyph() {
		return "widehat".equals(mCmd) || "widetilde".equals(mCmd);
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		mContent.draw(canvas, paint);
		if (isBrace()) {
			drawBrace(canvas, paint);
			return;
		}

		boolean scaleX = "widehat".equals(mCmd) || "widetilde".equals(mCmd);
		if (scaleX) {
			canvas.save();
			canvas.scale(mContent.getWidth() * 1.0f / mCmdNode.getWidth(), 1);
		}

		if (isGlyph()) {
			drawGlyph(canvas, paint);
		}

		if (scaleX) {
			canvas.restore();
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
		canvas.rotate(270, mCmdNode.getRight(), mCmdNode.getTop());
		mCmdNode.draw(canvas, paint);
		canvas.restore();
	}

	private void drawOverBrace(MathCanvas canvas, MathPaint paint) {
		canvas.save();
		canvas.rotate(90, mCmdNode.getRight(), mCmdNode.getBottom());
		mCmdNode.draw(canvas, paint);
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
