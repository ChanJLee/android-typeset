package me.chan.texas.ext.markdown.math.renderer.core;

import android.graphics.Paint;

import java.util.Objects;

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

	void setStrokeJoin(Paint.Join join);

	class Styles {
		private float mTextSize;
		private Paint.Style mStyle;
		private float mStrokeWidth;
		private int mColor;
		private boolean mIsBold;
		private boolean mIsItalic;
		private Paint.Cap mCap;
		private Paint.Join mJoin;

		public Styles(MathPaint paint) {
			mTextSize = paint.getTextSize();
			mStyle = paint.getStyle();
			mStrokeWidth = paint.getStrokeWidth();
			mColor = paint.getColor();
			mIsBold = paint.isBoldText();
			mIsItalic = paint.isItalicText();
			mCap = Paint.Cap.ROUND;
			mJoin = Paint.Join.ROUND;
		}

		public Styles(Styles other) {
			mTextSize = other.mTextSize;
			mStyle = other.mStyle;
			mStrokeWidth = other.mStrokeWidth;
			mColor = other.mColor;
			mIsBold = other.mIsBold;
			mIsItalic = other.mIsItalic;
			mCap = other.mCap;
			mJoin = other.mJoin;
		}

		public Styles copy() {
			return new Styles(this);
		}

		public Paint.Cap getCap() {
			return mCap;
		}

		public void setCap(Paint.Cap cap) {
			mCap = cap;
		}

		public Paint.Join getJoin() {
			return mJoin;
		}

		public void setJoin(Paint.Join join) {
			mJoin = join;
		}

		public float getTextSize() {
			return mTextSize;
		}

		public Styles setTextSize(float textSize) {
			mTextSize = textSize;
			return this;
		}

		public Styles setTextSizeFactor(float factor) {
			mTextSize *= factor;
			return this;
		}

		public Paint.Style getStyle() {
			return mStyle;
		}

		public Styles setStyle(Paint.Style style) {
			mStyle = style;
			return this;
		}

		public float getStrokeWidth() {
			return mStrokeWidth;
		}

		public Styles setStrokeWidth(float strokeWidth) {
			mStrokeWidth = strokeWidth;
			return this;
		}

		public int getColor() {
			return mColor;
		}

		public Styles setColor(int color) {
			mColor = color;
			return this;
		}

		public boolean isBold() {
			return mIsBold;
		}

		public Styles setBold(boolean bold) {
			mIsBold = bold;
			return this;
		}

		public boolean isItalic() {
			return mIsItalic;
		}

		public Styles setItalic(boolean italic) {
			mIsItalic = italic;
			return this;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;

			Styles styles = (Styles) o;
			return Float.compare(mTextSize, styles.mTextSize) == 0 &&
					Float.compare(mStrokeWidth, styles.mStrokeWidth) == 0 &&
					mColor == styles.mColor &&
					mIsBold == styles.mIsBold &&
					mIsItalic == styles.mIsItalic &&
					mStyle == styles.mStyle;
		}

		@Override
		public int hashCode() {
			int result = Float.hashCode(mTextSize);
			result = 31 * result + Objects.hashCode(mStyle);
			result = 31 * result + Float.hashCode(mStrokeWidth);
			result = 31 * result + mColor;
			result = 31 * result + Boolean.hashCode(mIsBold);
			result = 31 * result + Boolean.hashCode(mIsItalic);
			return result;
		}

		public void apply(MathPaint paint) {
			paint.setTextSize(mTextSize);
			paint.setStyle(mStyle);
			paint.setStrokeWidth(mStrokeWidth);
			paint.setColor(mColor);
			paint.setBoldText(mIsBold);
			paint.setItalicText(mIsItalic);
			paint.setStrokeCap(mCap);
			paint.setStrokeJoin(mJoin);
		}

		@Override
		public String toString() {
			return "Styles{" +
					"mTextSize=" + mTextSize +
					", mStyle=" + mStyle +
					", mStrokeWidth=" + mStrokeWidth +
					", mColor=" + String.format("#%08x", mColor) +
					", mIsBold=" + mIsBold +
					", mIsItalic=" + mIsItalic +
					'}';
		}
	}

	void setStrokeCap(Paint.Cap cap);
}
