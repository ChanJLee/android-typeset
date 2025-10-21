package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class AccentNode extends RendererNode {
	private final String mCmd;
	private final RendererNode mContent;
	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();

	public AccentNode(float scale, String cmd, RendererNode content) {
		super(scale);

		mCmd = cmd;
		mContent = content;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		mContent.measure(paint);
		paint.getFontMetrics(mFontMetrics);
		setMeasuredSize(mContent.getWidth(), getMeasuredTextSize() + mContent.getHeight());
	}

	@Override
	protected void onLayoutChildren() {
		mContent.layout(0, getMeasuredTextSize());
	}

	private int getMeasuredTextSize() {
		return (int) Math.ceil(mFontMetrics.descent - mFontMetrics.ascent);
	}

	private int getBaselineOffset() {
		return (int) Math.ceil(mFontMetrics.descent);
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {

	}

	@Override
	protected String toPretty() {
		return "";
	}
}
