package me.chan.texas.ext.markdown.math.renderer;

import androidx.annotation.NonNull;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;
import me.chan.texas.ext.markdown.math.renderer.fonts.Symbol;

public class AccentNode extends RendererNode implements HorizontalCalibratedNode, OptimizableRendererNode {
	private final String mCmd;
	private RendererNode mContent;
	private final RendererNode mCmdNode;

	public AccentNode(MathPaint.Styles styles, String cmd, RendererNode content) {
		super(styles);

		mCmd = cmd;
		mContent = content;
		if (isBrace()) {
			mCmdNode = new StretchyTripeNode(styles, MathFontOptions.symbol("uni23A7"), MathFontOptions.symbol("uni23A8"), MathFontOptions.symbol("uni23A9"), MathFontOptions.symbol("uni23AA"));
		} else if (isGlyph()) {
			if ("check".equals(mCmd)) {
				mCmdNode = new SymbolNode(new MathPaint.Styles(styles).setTextSize(styles.getTextSize() * 0.5f), cmdToSymbol());
			} else {
				mCmdNode = new SymbolNode(styles, cmdToSymbol());
			}
		} else {
			throw new RuntimeException("unknown cmd: " + mCmd);
		}
	}

	public RendererNode getContent() {
		return mContent;
	}

	public RendererNode getCmdNode() {
		return mCmdNode;
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
		}
	}

	private void measureGlyph(MathPaint paint) {
		mCmdNode.measure(paint);
		if ("check".equals(mCmd) || "widetilde".equals(mCmd) || "underline".equals(mCmd) || "overline".equals(mCmd) ||
				"vec".equals(mCmd) || "bar".equals(mCmd)) {
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

		if ("underline".equals(mCmd)) {
			mContent.layout(0, 0);
			mCmdNode.layout(0, mContent.getBottom());
			return;
		}

		if ("overline".equals(mCmd) || "bar".equals(mCmd) || "vec".equals(mCmd)) {
			mCmdNode.layout(0, 0);
			mContent.layout(0, mCmdNode.getBottom());
			return;
		}

		if ("hat".equals(mCmd) ||
				"dot".equals(mCmd) || "ddot".equals(mCmd) || "dddot".equals(mCmd) ||
				"acute".equals(mCmd) || "grave".equals(mCmd) || "breve".equals(mCmd)
				|| "mathring".equals(mCmd)) {  // 添加 mathring
			mCmdNode.layout((mContent.getWidth() - mCmdNode.getWidth()) / 2.0f, 0);
			mContent.layout(0, mCmdNode.getHeight());
			return;
		}

		if ("tilde".equals(mCmd)) {
			mCmdNode.layout((mContent.getWidth() - mCmdNode.getWidth()) / 2.0f, -mCmdNode.getHeight() / 2f);
			mContent.layout(0, mCmdNode.getHeight());
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
				|| "acute".equals(mCmd) || "grave".equals(mCmd) || "breve".equals(mCmd) || "check".equals(mCmd)
				|| "mathring".equals(mCmd)
				|| "underline".equals(mCmd) || "overline".equals(mCmd) || "bar".equals(mCmd) || "vec".equals(mCmd);
	}

	private Symbol cmdToSymbol() {
		if ("hat".equals(mCmd) || "widehat".equals(mCmd)) {
			return MathFontOptions.symbol("asciicircum");
		}

		if ("tilde".equals(mCmd) || "widetilde".equals(mCmd)) {
			return MathFontOptions.symbol("asciitilde");
		}

		if ("dot".equals(mCmd)) {
			return MathFontOptions.symbol("dotaccent");
		}

		if ("ddot".equals(mCmd)) {
			return MathFontOptions.symbol("ddotaccent");
		}

		if ("dddot".equals(mCmd)) {
			return MathFontOptions.symbol("dddotaccent");
		}

		if ("acute".equals(mCmd)) {
			return MathFontOptions.symbol("acute");
		}

		if ("grave".equals(mCmd)) {
			return MathFontOptions.symbol("grave");
		}

		if ("breve".equals(mCmd)) {
			return MathFontOptions.symbol("breve");
		}

		if ("check".equals(mCmd)) {
			return MathFontOptions.symbol("checkmark");
		}

		if ("mathring".equals(mCmd)) {
			return MathFontOptions.symbol("ring");
		}

		if ("underline".equals(mCmd) || "overline".equals(mCmd) || "bar".equals(mCmd)) {
			return MathFontOptions.symbol("uni2015");
		}

		if ("vec".equals(mCmd)) {
			return MathFontOptions.symbol("uni22B8");
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

		boolean scaleX = "widehat".equals(mCmd) || "widetilde".equals(mCmd) ||
				"underline".equals(mCmd) || "overline".equals(mCmd);
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

	@Override
	public boolean supportAlignBaseline() {
		if (mContent instanceof HorizontalCalibratedNode) {
			HorizontalCalibratedNode node = (HorizontalCalibratedNode) mContent;
			return node.supportAlignBaseline();
		}

		return false;
	}

	@Override
	public float getBaseline() {
		HorizontalCalibratedNode node = (HorizontalCalibratedNode) mContent;
		return node.getBaseline() + getTop();
	}

	@Override
	public float getContentCenterY() {
		return mContent.getContentCenterY() + mContent.getTop();
	}

	@NonNull
	@Override
	public RendererNode optimize() {
		if (mContent instanceof OptimizableRendererNode) {
			mContent = ((OptimizableRendererNode) mContent).optimize();
		}

		return this;
	}
}
