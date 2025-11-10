package me.chan.texas.ext.markdown.math.renderer.core;

import android.graphics.Paint;

import me.chan.texas.renderer.core.graphics.TexasPaint;

public interface MathPaint {
	float getTextSize();

	Paint.Style getStyle();

	void setStyle(Paint.Style style);

	void setTextSize(float textSize);

	float getStrokeWidth();

	void setStrokeWidth(float width);

	int getColor();

	void setColor(int color);

	void getFontMetrics(Paint.FontMetrics fontMetrics);

	float getRunAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset);

	TexasPaint getCore();

	default float toPixelSize(float units, float unitsPerEm) {
		return units / unitsPerEm * getTextSize();
	}

	void save();

	void save(Styles styles);

	void restore();

	boolean isBoldText();

	void setBoldText(boolean isBold);

	boolean isItalicText();

	void setItalicText(boolean isItalic);

	class Styles {
		private float mTextSize;
		private Paint.Style mStyle;
		private float mStrokeWidth;
		private int mColor;
		private boolean mIsBold;
		private boolean mIsItalic;

		public Styles(MathPaint paint) {
			mTextSize = paint.getTextSize();
			mStyle = paint.getStyle();
			mStrokeWidth = paint.getStrokeWidth();
			mColor = paint.getColor();
			mIsBold = paint.isBoldText();
			mIsItalic = paint.isItalicText();
		}

		public Styles(Styles other) {
			mTextSize = other.mTextSize;
			mStyle = other.mStyle;
			mStrokeWidth = other.mStrokeWidth;
			mColor = other.mColor;
			mIsBold = other.mIsBold;
			mIsItalic = other.mIsItalic;
		}

		public float getTextSize() {
			return mTextSize;
		}

		public void setTextSize(float textSize) {
			mTextSize = textSize;
		}

		public Paint.Style getStyle() {
			return mStyle;
		}

		public void setStyle(Paint.Style style) {
			mStyle = style;
		}

		public float getStrokeWidth() {
			return mStrokeWidth;
		}

		public void setStrokeWidth(float strokeWidth) {
			mStrokeWidth = strokeWidth;
		}

		public int getColor() {
			return mColor;
		}

		public void setColor(int color) {
			mColor = color;
		}

		public boolean isBold() {
			return mIsBold;
		}

		public void setBold(boolean bold) {
			mIsBold = bold;
		}

		public boolean isItalic() {
			return mIsItalic;
		}

		public void setItalic(boolean italic) {
			mIsItalic = italic;
		}

		public void apply(MathPaint paint) {
			paint.setTextSize(mTextSize);
			paint.setStyle(mStyle);
			paint.setStrokeWidth(mStrokeWidth);
			paint.setColor(mColor);
			paint.setBoldText(mIsBold);
			paint.setItalicText(mIsItalic);
		}
	}
}
