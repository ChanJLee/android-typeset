package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;

public class SqrtSymbolNode extends RendererNode {


	private static final float MAGIC_RADIO = 145 / 313f;

	private float mTextHeight;
	private float mTextWidth;
	private float mContentWidth = 0;
	private float mContentHeight = 0;

	public SqrtSymbolNode(MathPaint.Styles styles) {
		super(styles);
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mTextHeight = paint.getTextSize();
		mTextWidth = paint.getRunAdvance("√", 0, 1, 0, 1, false, 1);

		float width = mTextWidth + mContentHeight * MAGIC_RADIO - mTextHeight * MAGIC_RADIO;

		setMeasuredSize((int) Math.ceil(width), (int) Math.ceil(mContentHeight + getTopPadding()));
	}

	public float getVerticalGap() {
		return MathFontOptions.RADICAL_VERTICAL_GAP / MathFontOptions.UNITS_PER_EM * mTextHeight;
	}

	public float getRuleThickness() {
		return MathFontOptions.RADICAL_RULE_THICKNESS / MathFontOptions.UNITS_PER_EM * mTextHeight;
	}

	public float getExtraAscender() {
		return MathFontOptions.RADICAL_EXTRA_ASCENDER / MathFontOptions.UNITS_PER_EM * mTextHeight;
	}

	public float getKernBeforeDegree() {
		return MathFontOptions.RADICAL_KERN_BEFORE_DEGREE / MathFontOptions.UNITS_PER_EM * mTextHeight;
	}

	public float getKernAfterDegree() {
//		return MathFontOptions.RADICAL_KERN_AFTER_DEGREE / MathFontOptions.UNITS_PER_EM * mTextSize;
		return 0;
	}

	public float getDegreeBottomRaisePercent() {
		return MathFontOptions.RADICAL_DEGREE_BOTTOM_RAISE_PERCENT / 100;
	}

	public void setContentSize(float contentWidth, float contentHeight) {
		mContentWidth = contentWidth;
		mContentHeight = contentHeight;
	}

	public float getTopPadding() {
		return getExtraAscender();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		float thickness = getRuleThickness();
		canvas.drawText("√", 0, getHeight() - mTextHeight, paint);

		paint.setStrokeWidth(thickness);
		float startX = getWidth();
		float y = getTopPadding() - thickness / 2;
		canvas.drawLine(startX, y, startX + mContentWidth, y, paint);

		canvas.drawLine(mTextWidth, getHeight() - mTextHeight - thickness, startX, y - thickness / 2, paint);
	}

	@Override
	protected String toPretty() {
		return "√";
	}
}