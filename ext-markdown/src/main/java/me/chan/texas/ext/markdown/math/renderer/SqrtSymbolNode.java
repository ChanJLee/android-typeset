package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class SqrtSymbolNode extends RendererNode {

	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private float mTextSize;
	private float mContentWidth = 0;

	@Override
	protected void onMeasure(TexasPaint paint) {
		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		mTextSize = paint.getTextSize();

		int height = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent + getExtraAscender() + getRuleThickness());
		int width = (int) Math.ceil(paint.getRunAdvance("√", 0, 1, 0, 1, false, 1));

		setMeasuredSize(width, height);
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
		return MathFontOptions.RADICAL_KERN_AFTER_DEGREE / MathFontOptions.UNITS_PER_EM * mTextSize;
	}

	public float getDegreeBottomRaisePercent() {
		return MathFontOptions.RADICAL_DEGREE_BOTTOM_RAISE_PERCENT / 100;
	}

	public void setContentWidth(float contentWidth) {
		mContentWidth = contentWidth;
	}

	public float getContentWidth() {
		return mContentWidth;
	}

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint) {
		float y = getExtraAscender();
		canvas.drawText("√", 0, y, paint);

		float startX = getWidth();
		canvas.drawLine(startX, y, startX + mContentWidth, y, paint);
	}

	@Override
	protected String toPretty() {
		return "√";
	}
}