package me.chan.texas.ext.markdown.math.renderer;

import android.graphics.Paint;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

public class TextNode extends RendererNode {

	private final String mContent;
	private final Paint.FontMetrics mFontMetrics = new Paint.FontMetrics();
	private float mBaselineOffset;
	private TextStyle mTextStyle;

	public TextNode(String content) {
		this(1, content);
	}

	public TextNode(float scale, String content) {
		super(scale);
		mContent = content;
	}

	public void setTextStyle(TextStyle textStyle) {
		mTextStyle = textStyle;
	}

	@Override
	protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
		if (mTextStyle != null) {
			paint.save();
			mTextStyle.update(paint);
		}

		paint.getFontMetrics(mFontMetrics);
		Paint.FontMetrics fontMetrics = mFontMetrics;
		int height = (int) Math.ceil(fontMetrics.descent - fontMetrics.ascent);
		mBaselineOffset = (float) Math.ceil(fontMetrics.descent);

		int size = mContent.length();
		int width = (int) Math.ceil(paint.getRunAdvance(mContent, 0, size, 0, size, false, size));

		setMeasuredSize(width, height);

		if (mTextStyle != null) {
			paint.restore();
		}
	}

	@Override
	public float getBaseline() {
		return getBottom() - mBaselineOffset;
	}

	@Override
	protected void onDraw(MathCanvas canvas, MathPaint paint) {
		if (mTextStyle != null) {
			paint.save();
			mTextStyle.update(paint);
		}

		canvas.drawText(mContent, 0, getHeight() - mBaselineOffset, paint);

		if (mTextStyle != null) {
			paint.restore();
		}
	}

	@Override
	protected String toPretty() {
		return "text: " + mContent;
	}

	public interface TextStyle {
		void update(MathPaint paint);
	}

	public static class DefaultTextStyle implements TextStyle {
		public static final int FLAG_BOLD = 1;
		public static final int FLAG_ITALIC = 2;

		private final int mFlags;

		public DefaultTextStyle(int flags) {
			mFlags = flags;
		}

		@Override
		public void update(MathPaint paint) {
			if ((mFlags & FLAG_BOLD) != 0) {
				paint.setBoldText(true);
			}

			if ((mFlags & FLAG_ITALIC) != 0) {
				paint.setItalicText(true);
			}
		}
	}
}
