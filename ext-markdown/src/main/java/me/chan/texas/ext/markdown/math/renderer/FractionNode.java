package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.renderer.core.graphics.TexasCanvas;
import me.chan.texas.renderer.core.graphics.TexasPaint;

public class FractionNode extends RendererNode {
	private final RendererNode mNumerator;
	private final RendererNode mDenominator;

	public FractionNode(float scale, RendererNode numerator, RendererNode denominator) {
		super(scale);
		mNumerator = numerator;
		mDenominator = denominator;
	}

	@Override
	protected void onMeasure(TexasPaint paint) {
		mNumerator.measure(paint);
		mDenominator.measure(paint);

		int width = (int) Math.ceil(Math.max(mDenominator.getWidth(), mNumerator.getWidth()) * 1.2f);
		int height = (int) Math.ceil(mDenominator.getHeight() + mNumerator.getHeight() +
				getThickness(paint) + getDenominatorVerticalGap(paint) + getNumeratorVerticalGap(paint));
		setMeasuredSize(width, height);
	}

	private float getDenominatorVerticalGap(TexasPaint paint) {
		return MathFontOptions.FRACTION_DENOMINATOR_GAP_MIN / MathFontOptions.UNITS_PER_EM * paint.getTextSize();
	}

	private float getNumeratorVerticalGap(TexasPaint paint) {
		return MathFontOptions.FRACTION_NUMERATOR_GAP_MIN / MathFontOptions.UNITS_PER_EM * paint.getTextSize();
	}

	private float getThickness(TexasPaint paint) {
		return MathFontOptions.FRACTION_RULE_THICKNESS / MathFontOptions.UNITS_PER_EM * paint.getTextSize();
	}

	@Override
	protected void onLayoutChildren() {
		mNumerator.layout((getWidth() - mNumerator.getWidth()) / 2.0f, 0);
		mDenominator.layout((getWidth() - mDenominator.getWidth()) / 2.0f, getHeight() - mDenominator.getHeight());
	}

	@Override
	protected void onDraw(TexasCanvas canvas, TexasPaint paint) {
		mNumerator.draw(canvas, paint);
		mDenominator.draw(canvas, paint);

		float strokeWidth = paint.getStrokeWidth();
		float thickness = getThickness(paint);
		paint.setStrokeWidth(thickness);

		float y = (mNumerator.getBottom() + mDenominator.getTop() - thickness) / 2.0f;
		canvas.drawLine(0, y, getWidth(), y, paint);

		paint.setStrokeWidth(strokeWidth);
	}

	@Override
	protected String toPretty() {
		return "1/2";
	}
}
