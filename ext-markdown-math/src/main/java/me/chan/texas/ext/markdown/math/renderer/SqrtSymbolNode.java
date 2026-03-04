package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Color;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;
import me.chan.texas.ext.markdown.math.renderer.fonts.MathFontOptions;

public class SqrtSymbolNode extends RendererNode {

	private float mTextSize;
	private float mContentWidth = 0;
	private float mContentHeight = 0;

	private final SymbolNode mSymbolNode;

	public SqrtSymbolNode(MathPaint.Styles styles) {
		super(styles);
		mSymbolNode = new SymbolNode(styles, MathFontOptions.symbol("radical"));
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mTextSize = paint.getTextSize();

		mSymbolNode.measure(paint);

		int height = (int) Math.ceil(mContentHeight + getTopPadding());
		float width = height * K - mSymbolNode.getHeight() * K + mSymbolNode.getWidth();

		setMeasuredSize((int) Math.ceil(width), height);
	}

	@Override
	protected void onLayoutChildren() {
		mSymbolNode.layout(0, getHeight() - mSymbolNode.getHeight());
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

	public void setContentSize(float contentWidth, float contentHeight) {
		mContentWidth = contentWidth;
		mContentHeight = contentHeight;
	}

	public float getTopPadding() {
		return getExtraAscender();
	}

	@Override
	protected void onDrawDebug(MathCanvas canvas, MathPaint paint) {
		paint.setColor(Color.YELLOW);
		super.onDrawDebug(canvas, paint);
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		float thickness = getRuleThickness();
		mSymbolNode.draw(canvas, paint);

		paint.setStrokeWidth(thickness);
		float startX = getWidth();
		float startY = getTopPadding() - thickness;
		float endX = startX + mContentWidth;
		float endY = startY;
		canvas.drawLine(startX, startY, endX, endY, paint);

		endX = startX;
		endY = 0;

		startX = mSymbolNode.getRight() * 0.95f;
		startY = (endX - startX) / K;
		float offsetY = thickness * 0.7f;
		canvas.drawLine(startX, startY - offsetY, endX, endY - offsetY, paint);
	}

	private static final float W = 338f;
	private static final float H = 441f;
	private static final float D = 126f;

	private static final float K = (W - D) / H;

	@Override
	protected String toPretty() {
		return "√";
	}
}