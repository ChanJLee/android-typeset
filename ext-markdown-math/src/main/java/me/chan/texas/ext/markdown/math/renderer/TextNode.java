package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Color;
import android.graphics.Paint;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class TextNode extends RendererNode implements HorizontalCalibratedNode {

	private final String mContent;
	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private float mBaselineOffset;

	public TextNode(MathPaint.Styles styles, String content) {
		super(styles);
		mContent = content;
	}

	public String getContent() {
		return mContent;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		int height = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
		mBaselineOffset = (float) Math.ceil(fontMetrics.descent);

		int size = mContent.length();
		int width = (int) Math.ceil(paint.getRunAdvance(mContent, 0, size, 0, size, false, size));

		setMeasuredSize(width, height);
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		canvas.drawText(mContent, 0, getHeight() - mBaselineOffset, paint);
	}

	@Override
	protected void onDrawDebug(MathCanvas canvas, MathPaint paint) {
		super.onDrawDebug(canvas, paint);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.FILL);
		float y = getHeight() - mBaselineOffset;
		canvas.drawLine(0, y, getWidth(), y, paint);
	}

	@Override
	protected String toPretty() {
		return mContent;
	}

	@Override
	public boolean supportAlignBaseline() {
		return true;
	}

	@Override
	public float getBaseline() {
		return getBottom() - mBaselineOffset;
	}
}
