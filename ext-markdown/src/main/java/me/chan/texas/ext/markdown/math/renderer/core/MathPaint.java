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

	void restore();

	boolean isBoldText();

	void setBoldText(boolean isBold);
}
