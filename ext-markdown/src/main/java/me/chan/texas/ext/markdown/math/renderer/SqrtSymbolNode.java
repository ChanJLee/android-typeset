package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;

public class SqrtSymbolNode extends RendererNode {

	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private float mTextSize;
	private float mContentWidth = 0;

	public SqrtSymbolNode(float scale) {
		super(scale);
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		mTextSize = paint.getTextSize();

		int height = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent + getTopPadding());
		int width = (int) Math.ceil(paint.getRunAdvance("√", 0, 1, 0, 1, false, 1));

		setMeasuredSize(width, height);
	}

	@Override
	public float getBaseline() {
		return 0;
	}

	public float getVerticalGap() {
		return MathFontOptions.RADICAL_VERTICAL_GAP / MathFontOptions.UNITS_PER_EM * mTextSize;
	}

	public float getRuleThickness() {
		return MathFontOptions.RADICAL_RULE_THICKNESS / MathFontOptions.UNITS_PER_EM * mTextSize;
	}

	public float getExtraAscender() {
		return MathFontOptions.RADICAL_EXTRA_ASCENDER / MathFontOptions.UNITS_PER_EM * mTextSize;
	}

	public float getKernBeforeDegree() {
		return MathFontOptions.RADICAL_KERN_BEFORE_DEGREE / MathFontOptions.UNITS_PER_EM * mTextSize;
	}

	public float getKernAfterDegree() {
//		return MathFontOptions.RADICAL_KERN_AFTER_DEGREE / MathFontOptions.UNITS_PER_EM * mTextSize;
		return 0;
	}

	public float getDegreeBottomRaisePercent() {
		return MathFontOptions.RADICAL_DEGREE_BOTTOM_RAISE_PERCENT / 100;
	}

	public void setContentWidth(float contentWidth) {
		mContentWidth = contentWidth;
	}

	public float getTopPadding() {
		return getExtraAscender();
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		float thickness = getRuleThickness();
		float y = getTopPadding() + thickness / 2;
		canvas.drawText("√", 0, y, paint);

		float strokeWidth = paint.getStrokeWidth();
		paint.setStrokeWidth(thickness);
		float startX = getWidth();
		y = getTopPadding();
		canvas.drawLine(startX, y, startX + mContentWidth, y, paint);
		paint.setStrokeWidth(strokeWidth);
	}

	@Override
	protected String toPretty() {
		return "√";
	}
}