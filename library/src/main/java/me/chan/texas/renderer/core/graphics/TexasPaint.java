package me.chan.texas.renderer.core.graphics;

import android.graphics.BlendMode;
import android.graphics.ColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import android.os.LocaleList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

public interface TexasPaint {
	void reset();

	/* void set(Paint src); */

	int getFlags();

	void setFlags(int flags);

	int getHinting();

	void setHinting(int mode);

	boolean isAntiAlias();

	void setAntiAlias(boolean aa);

	boolean isDither();

	void setDither(boolean dither);

	boolean isLinearText();

	void setLinearText(boolean linearText);

	boolean isSubpixelText();

	void setSubpixelText(boolean subpixelText);

	boolean isUnderlineText();

	float getUnderlinePosition();

	float getUnderlineThickness();

	void setUnderlineText(boolean underlineText);

	boolean isStrikeThruText();

	float getStrikeThruPosition();

	float getStrikeThruThickness();

	void setStrikeThruText(boolean strikeThruText);

	boolean isFakeBoldText();

	void setFakeBoldText(boolean fakeBoldText);

	boolean isFilterBitmap();

	void setFilterBitmap(boolean filter);

	Paint.Style getStyle();

	void setStyle(Paint.Style style);

	int getColor();

	long getColorLong();

	void setColor(int color);

	void setColor(long color);

	int getAlpha();

	void setAlpha(int a);

	void setARGB(int a, int r, int g, int b);

	float getStrokeWidth();

	void setStrokeWidth(float width);

	float getStrokeMiter();

	void setStrokeMiter(float miter);

	Paint.Cap getStrokeCap();

	void setStrokeCap(Paint.Cap cap);

	Paint.Join getStrokeJoin();

	void setStrokeJoin(Paint.Join join);

	boolean getFillPath(Path src, Path dst);

	Shader getShader();

	Shader setShader(Shader shader);

	ColorFilter getColorFilter();

	ColorFilter setColorFilter(ColorFilter filter);

	Xfermode getXfermode();

	@Nullable
	BlendMode getBlendMode();

	Xfermode setXfermode(Xfermode xfermode);

	void setBlendMode(@Nullable BlendMode blendmode);

	PathEffect getPathEffect();

	PathEffect setPathEffect(PathEffect effect);

	MaskFilter getMaskFilter();

	MaskFilter setMaskFilter(MaskFilter maskfilter);

	Typeface getTypeface();

	Typeface setTypeface(Typeface typeface);

	void setShadowLayer(float radius, float dx, float dy, int shadowColor);

	void setShadowLayer(float radius, float dx, float dy, long shadowColor);

	void clearShadowLayer();

	float getShadowLayerRadius();

	float getShadowLayerDx();

	float getShadowLayerDy();

	int getShadowLayerColor();

	long getShadowLayerColorLong();

	Paint.Align getTextAlign();

	void setTextAlign(Paint.Align align);

	@NonNull
	Locale getTextLocale();

	@NonNull
	LocaleList getTextLocales();

	void setTextLocale(@NonNull Locale locale);

	void setTextLocales(@NonNull LocaleList locales);

	boolean isElegantTextHeight();

	void setElegantTextHeight(boolean elegant);

	float getTextSize();

	void setTextSize(float textSize);

	float getTextScaleX();

	void setTextScaleX(float scaleX);

	float getTextSkewX();

	void setTextSkewX(float skewX);

	float getLetterSpacing();

	void setLetterSpacing(float letterSpacing);

	float getWordSpacing();

	void setWordSpacing(float wordSpacing);

	String getFontFeatureSettings();

	void setFontFeatureSettings(String settings);

	String getFontVariationSettings();

	boolean setFontVariationSettings(String fontVariationSettings);

	int getStartHyphenEdit();

	int getEndHyphenEdit();

	void setStartHyphenEdit(int startHyphen);

	void setEndHyphenEdit(int endHyphen);

	float ascent();

	float descent();

	float getFontMetrics(Paint.FontMetrics metrics);

	Paint.FontMetrics getFontMetrics();

	void getFontMetricsForLocale(@NonNull Paint.FontMetrics metrics);

	void getFontMetricsInt(@NonNull CharSequence text, int start, int count, int contextStart, int contextCount, boolean isRtl, @NonNull Paint.FontMetricsInt outMetrics);

	void getFontMetricsInt(@NonNull char[] text, int start, int count, int contextStart, int contextCount, boolean isRtl, @NonNull Paint.FontMetricsInt outMetrics);

	int getFontMetricsInt(Paint.FontMetricsInt fmi);

	Paint.FontMetricsInt getFontMetricsInt();

	void getFontMetricsIntForLocale(@NonNull Paint.FontMetricsInt metrics);

	float getFontSpacing();

	float measureText(char[] text, int index, int count);

	float measureText(String text, int start, int end);

	float measureText(String text);

	float measureText(CharSequence text, int start, int end);

	int breakText(char[] text, int index, int count, float maxWidth, float[] measuredWidth);

	int breakText(CharSequence text, int start, int end, boolean measureForwards, float maxWidth, float[] measuredWidth);

	int breakText(String text, boolean measureForwards, float maxWidth, float[] measuredWidth);

	int getTextWidths(char[] text, int index, int count, float[] widths);

	int getTextWidths(CharSequence text, int start, int end, float[] widths);

	int getTextWidths(String text, int start, int end, float[] widths);

	int getTextWidths(String text, float[] widths);

	float getTextRunAdvances(@NonNull char[] chars, int index, int count, int contextIndex, int contextCount, boolean isRtl, @Nullable float[] advances, int advancesIndex);

	int getTextRunCursor(@NonNull char[] text, int contextStart, int contextLength, boolean isRtl, int offset, int cursorOpt);

	int getTextRunCursor(@NonNull CharSequence text, int contextStart, int contextEnd, boolean isRtl, int offset, int cursorOpt);

	void getTextPath(char[] text, int index, int count, float x, float y, Path path);

	void getTextPath(String text, int start, int end, float x, float y, Path path);

	void getTextBounds(String text, int start, int end, Rect bounds);

	void getTextBounds(@NonNull CharSequence text, int start, int end, @NonNull Rect bounds);

	void getTextBounds(char[] text, int index, int count, Rect bounds);

	boolean hasGlyph(String string);

	float getRunAdvance(char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset);

	float getRunAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset);

	float getRunCharacterAdvance(@NonNull char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset, @Nullable float[] advances, int advancesIndex);

	float getRunCharacterAdvance(@NonNull CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset, @Nullable float[] advances, int advancesIndex);

	int getOffsetForAdvance(char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance);

	int getOffsetForAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance);

	boolean equalsForTextMeasurement(@NonNull Paint other);
}
