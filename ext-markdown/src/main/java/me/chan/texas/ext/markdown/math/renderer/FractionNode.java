package me.chan.texas.ext.markdown.math.renderer;


import androidx.annotation.NonNull;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;

public class FractionNode extends RendererNode implements OptimizableRendererNode {
	private RendererNode mNumerator;
	private RendererNode mDenominator;

	public FractionNode(MathPaint.Styles styles, RendererNode numerator, RendererNode denominator) {
		super(styles);
		mNumerator = numerator;
		mDenominator = denominator;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mNumerator.measure(paint);
		mDenominator.measure(paint);

		int width = (int) Math.ceil(Math.max(mDenominator.getWidth(), mNumerator.getWidth()) * 1.04f);
		int height = (int) Math.ceil(mDenominator.getHeight() + mNumerator.getHeight() +
				getThickness(paint) + getDenominatorVerticalGap(paint) + getNumeratorVerticalGap(paint));
		setMeasuredSize(width, height);
	}

	private float getDenominatorVerticalGap(MathPaint paint) {
		return MathFontOptions.FRACTION_DENOMINATOR_GAP_MIN / MathFontOptions.UNITS_PER_EM * paint.getTextSize();
	}

	private float getNumeratorVerticalGap(MathPaint paint) {
		return MathFontOptions.FRACTION_NUMERATOR_GAP_MIN / MathFontOptions.UNITS_PER_EM * paint.getTextSize();
	}

	private float getThickness(MathPaint paint) {
		return MathFontOptions.FRACTION_RULE_THICKNESS / MathFontOptions.UNITS_PER_EM * paint.getTextSize();
	}

	@Override
	protected void onLayoutChildren() {
		mNumerator.layout((getWidth() - mNumerator.getWidth()) / 2.0f, 0);
		mDenominator.layout((getWidth() - mDenominator.getWidth()) / 2.0f, getHeight() - mDenominator.getHeight());
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		mNumerator.draw(canvas, paint);
		mDenominator.draw(canvas, paint);

		float strokeWidth = paint.getStrokeWidth();
		float thickness = getThickness(paint);
		paint.setStrokeWidth(thickness);

		float y = (mNumerator.getBottom() + mDenominator.getTop()) / 2.0f;
		canvas.drawLine(0, y, getWidth(), y, paint);

		paint.setStrokeWidth(strokeWidth);
	}

	@Override
	protected String toPretty() {
		return "1/2";
	}

	@NonNull
	@Override
	public RendererNode optimize() {
		if (mDenominator instanceof OptimizableRendererNode) {
			mDenominator = ((OptimizableRendererNode) mDenominator).optimize();
		}
		if (mNumerator instanceof OptimizableRendererNode) {
			mNumerator = ((OptimizableRendererNode) mNumerator).optimize();
		}

		return this;
	}
}
