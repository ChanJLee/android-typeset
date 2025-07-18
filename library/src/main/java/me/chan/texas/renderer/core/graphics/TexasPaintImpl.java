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
import android.os.Build;
import android.os.LocaleList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;

import java.util.Locale;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TexasPaintImpl implements TexasPaint {
	@NonNull
	private Paint mPaint;

	public TexasPaintImpl(@NonNull Paint paint) {
		mPaint = paint;
	}

	public final void setPaint(@NonNull Paint paint) {
		mPaint = paint;
	}

	@Override
	public void reset() {
		mPaint.reset();
	}

	@Override
	public int getFlags() {
		return mPaint.getFlags();
	}

	@Override
	public void setFlags(int flags) {
		mPaint.setFlags(flags);
	}

	@Override
	public int getHinting() {
		return mPaint.getHinting();
	}

	@Override
	public void setHinting(int mode) {
		mPaint.setHinting(mode);
	}

	@Override
	public boolean isAntiAlias() {
		return mPaint.isAntiAlias();
	}

	@Override
	public void setAntiAlias(boolean aa) {
		mPaint.setAntiAlias(aa);
	}

	@Override
	public boolean isDither() {
		return mPaint.isDither();
	}

	@Override
	public void setDither(boolean dither) {
		mPaint.setDither(dither);
	}

	@Override
	public boolean isLinearText() {
		return mPaint.isLinearText();
	}

	@Override
	public void setLinearText(boolean linearText) {
		mPaint.setLinearText(linearText);
	}

	@Override
	public boolean isSubpixelText() {
		return mPaint.isSubpixelText();
	}

	@Override
	public void setSubpixelText(boolean subpixelText) {
		mPaint.setSubpixelText(subpixelText);
	}

	@Override
	public boolean isUnderlineText() {
		return mPaint.isUnderlineText();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getUnderlinePosition() {
		return mPaint.getUnderlinePosition();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getUnderlineThickness() {
		return mPaint.getUnderlineThickness();
	}

	@Override
	public void setUnderlineText(boolean underlineText) {
		mPaint.setUnderlineText(underlineText);
	}

	@Override
	public boolean isStrikeThruText() {
		return mPaint.isStrikeThruText();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getStrikeThruPosition() {
		return mPaint.getStrikeThruPosition();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getStrikeThruThickness() {
		return mPaint.getStrikeThruThickness();
	}

	@Override
	public void setStrikeThruText(boolean strikeThruText) {
		mPaint.setStrikeThruText(strikeThruText);
	}

	@Override
	public boolean isFakeBoldText() {
		return mPaint.isFakeBoldText();
	}

	@Override
	public void setFakeBoldText(boolean fakeBoldText) {
		mPaint.setFakeBoldText(fakeBoldText);
	}

	@Override
	public boolean isFilterBitmap() {
		return mPaint.isFilterBitmap();
	}

	@Override
	public void setFilterBitmap(boolean filter) {
		mPaint.setFilterBitmap(filter);
	}

	@Override
	public Paint.Style getStyle() {
		return mPaint.getStyle();
	}

	@Override
	public void setStyle(Paint.Style style) {
		mPaint.setStyle(style);
	}

	@Override
	public int getColor() {
		return mPaint.getColor();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public long getColorLong() {
		return mPaint.getColorLong();
	}

	@Override
	public void setColor(int color) {
		mPaint.setColor(color);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setColor(long color) {
		mPaint.setColor(color);
	}

	@Override
	public int getAlpha() {
		return mPaint.getAlpha();
	}

	@Override
	public void setAlpha(int a) {
		mPaint.setAlpha(a);
	}

	@Override
	public void setARGB(int a, int r, int g, int b) {
		mPaint.setARGB(a, r, g, b);
	}

	@Override
	public float getStrokeWidth() {
		return mPaint.getStrokeWidth();
	}

	@Override
	public void setStrokeWidth(float width) {
		mPaint.setStrokeWidth(width);
	}

	@Override
	public float getStrokeMiter() {
		return mPaint.getStrokeMiter();
	}

	@Override
	public void setStrokeMiter(float miter) {
		mPaint.setStrokeMiter(miter);
	}

	@Override
	public Paint.Cap getStrokeCap() {
		return mPaint.getStrokeCap();
	}

	@Override
	public void setStrokeCap(Paint.Cap cap) {
		mPaint.setStrokeCap(cap);
	}

	@Override
	public Paint.Join getStrokeJoin() {
		return mPaint.getStrokeJoin();
	}

	@Override
	public void setStrokeJoin(Paint.Join join) {
		mPaint.setStrokeJoin(join);
	}

	@Override
	public boolean getFillPath(Path src, Path dst) {
		return mPaint.getFillPath(src, dst);
	}

	@Override
	public Shader getShader() {
		return mPaint.getShader();
	}

	@Override
	public Shader setShader(Shader shader) {
		return mPaint.setShader(shader);
	}

	@Override
	public ColorFilter getColorFilter() {
		return mPaint.getColorFilter();
	}

	@Override
	public ColorFilter setColorFilter(ColorFilter filter) {
		return mPaint.setColorFilter(filter);
	}

	@Override
	public Xfermode getXfermode() {
		return mPaint.getXfermode();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	@Nullable
	public BlendMode getBlendMode() {
		return mPaint.getBlendMode();
	}

	@Override
	public Xfermode setXfermode(Xfermode xfermode) {
		return mPaint.setXfermode(xfermode);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setBlendMode(@Nullable BlendMode blendmode) {
		mPaint.setBlendMode(blendmode);
	}

	@Override
	public PathEffect getPathEffect() {
		return mPaint.getPathEffect();
	}

	@Override
	public PathEffect setPathEffect(PathEffect effect) {
		return mPaint.setPathEffect(effect);
	}

	@Override
	public MaskFilter getMaskFilter() {
		return mPaint.getMaskFilter();
	}

	@Override
	public MaskFilter setMaskFilter(MaskFilter maskfilter) {
		return mPaint.setMaskFilter(maskfilter);
	}

	@Override
	public Typeface getTypeface() {
		return mPaint.getTypeface();
	}

	@Override
	public Typeface setTypeface(Typeface typeface) {
		return mPaint.setTypeface(typeface);
	}

	@Override
	public void setShadowLayer(float radius, float dx, float dy, int shadowColor) {
		mPaint.setShadowLayer(radius, dx, dy, shadowColor);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setShadowLayer(float radius, float dx, float dy, long shadowColor) {
		mPaint.setShadowLayer(radius, dx, dy, shadowColor);
	}

	@Override
	public void clearShadowLayer() {
		mPaint.clearShadowLayer();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getShadowLayerRadius() {
		return mPaint.getShadowLayerRadius();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getShadowLayerDx() {
		return mPaint.getShadowLayerDx();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getShadowLayerDy() {
		return mPaint.getShadowLayerDy();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getShadowLayerColor() {
		return mPaint.getShadowLayerColor();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public long getShadowLayerColorLong() {
		return mPaint.getShadowLayerColorLong();
	}

	@Override
	public Paint.Align getTextAlign() {
		return mPaint.getTextAlign();
	}

	@Override
	public void setTextAlign(Paint.Align align) {
		mPaint.setTextAlign(align);
	}

	@Override
	@NonNull
	public Locale getTextLocale() {
		return mPaint.getTextLocale();
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	@NonNull
	public LocaleList getTextLocales() {
		return mPaint.getTextLocales();
	}

	@Override
	public void setTextLocale(@NonNull Locale locale) {
		mPaint.setTextLocale(locale);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void setTextLocales(@NonNull LocaleList locales) {
		mPaint.setTextLocales(locales);
	}

	@Override
	public boolean isElegantTextHeight() {
		return mPaint.isElegantTextHeight();
	}

	@Override
	public void setElegantTextHeight(boolean elegant) {
		mPaint.setElegantTextHeight(elegant);
	}

	@Override
	public float getTextSize() {
		return mPaint.getTextSize();
	}

	@Override
	public void setTextSize(float textSize) {
		mPaint.setTextSize(textSize);
	}

	@Override
	public float getTextScaleX() {
		return mPaint.getTextScaleX();
	}

	@Override
	public void setTextScaleX(float scaleX) {
		mPaint.setTextScaleX(scaleX);
	}

	@Override
	public float getTextSkewX() {
		return mPaint.getTextSkewX();
	}

	@Override
	public void setTextSkewX(float skewX) {
		mPaint.setTextSkewX(skewX);
	}

	@Override
	public float getLetterSpacing() {
		return mPaint.getLetterSpacing();
	}

	@Override
	public void setLetterSpacing(float letterSpacing) {
		mPaint.setLetterSpacing(letterSpacing);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getWordSpacing() {
		return mPaint.getWordSpacing();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setWordSpacing(float wordSpacing) {
		mPaint.setWordSpacing(wordSpacing);
	}

	@Override
	public String getFontFeatureSettings() {
		return mPaint.getFontFeatureSettings();
	}

	@Override
	public void setFontFeatureSettings(String settings) {
		mPaint.setFontFeatureSettings(settings);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public String getFontVariationSettings() {
		return mPaint.getFontVariationSettings();
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean setFontVariationSettings(String fontVariationSettings) {
		return mPaint.setFontVariationSettings(fontVariationSettings);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getStartHyphenEdit() {
		return mPaint.getStartHyphenEdit();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getEndHyphenEdit() {
		return mPaint.getEndHyphenEdit();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setStartHyphenEdit(int startHyphen) {
		mPaint.setStartHyphenEdit(startHyphen);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setEndHyphenEdit(int endHyphen) {
		mPaint.setEndHyphenEdit(endHyphen);
	}

	@Override
	public float ascent() {
		return mPaint.ascent();
	}

	@Override
	public float descent() {
		return mPaint.descent();
	}

	@Override
	public float getFontMetrics(Paint.FontMetrics metrics) {
		return mPaint.getFontMetrics(metrics);
	}

	@Override
	public Paint.FontMetrics getFontMetrics() {
		return mPaint.getFontMetrics();
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void getFontMetricsForLocale(@NonNull Paint.FontMetrics metrics) {
		mPaint.getFontMetricsForLocale(metrics);
	}

	@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
	@Override
	public void getFontMetricsInt(@NonNull CharSequence text, int start, int count, int contextStart, int contextCount, boolean isRtl, @NonNull Paint.FontMetricsInt outMetrics) {
		mPaint.getFontMetricsInt(text, start, count, contextStart, contextCount, isRtl, outMetrics);
	}

	@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
	@Override
	public void getFontMetricsInt(@NonNull char[] text, int start, int count, int contextStart, int contextCount, boolean isRtl, @NonNull Paint.FontMetricsInt outMetrics) {
		mPaint.getFontMetricsInt(text, start, count, contextStart, contextCount, isRtl, outMetrics);
	}

	@Override
	public int getFontMetricsInt(Paint.FontMetricsInt fmi) {
		return mPaint.getFontMetricsInt(fmi);
	}

	@Override
	public Paint.FontMetricsInt getFontMetricsInt() {
		return mPaint.getFontMetricsInt();
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void getFontMetricsIntForLocale(@NonNull Paint.FontMetricsInt metrics) {
		mPaint.getFontMetricsIntForLocale(metrics);
	}

	@Override
	public float getFontSpacing() {
		return mPaint.getFontSpacing();
	}

	@Override
	public float measureText(char[] text, int index, int count) {
		return mPaint.measureText(text, index, count);
	}

	@Override
	public float measureText(String text, int start, int end) {
		return mPaint.measureText(text, start, end);
	}

	@Override
	public float measureText(String text) {
		return mPaint.measureText(text);
	}

	@Override
	public float measureText(CharSequence text, int start, int end) {
		return mPaint.measureText(text, start, end);
	}

	@Override
	public int breakText(char[] text, int index, int count, float maxWidth, float[] measuredWidth) {
		return mPaint.breakText(text, index, count, maxWidth, measuredWidth);
	}

	@Override
	public int breakText(CharSequence text, int start, int end, boolean measureForwards, float maxWidth, float[] measuredWidth) {
		return mPaint.breakText(text, start, end, measureForwards, maxWidth, measuredWidth);
	}

	@Override
	public int breakText(String text, boolean measureForwards, float maxWidth, float[] measuredWidth) {
		return mPaint.breakText(text, measureForwards, maxWidth, measuredWidth);
	}

	@Override
	public int getTextWidths(char[] text, int index, int count, float[] widths) {
		return mPaint.getTextWidths(text, index, count, widths);
	}

	@Override
	public int getTextWidths(CharSequence text, int start, int end, float[] widths) {
		return mPaint.getTextWidths(text, start, end, widths);
	}

	@Override
	public int getTextWidths(String text, int start, int end, float[] widths) {
		return mPaint.getTextWidths(text, start, end, widths);
	}

	@Override
	public int getTextWidths(String text, float[] widths) {
		return mPaint.getTextWidths(text, widths);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getTextRunAdvances(@NonNull char[] chars, int index, int count, int contextIndex, int contextCount, boolean isRtl, @Nullable float[] advances, int advancesIndex) {
		return mPaint.getTextRunAdvances(chars, index, count, contextIndex, contextCount, isRtl, advances, advancesIndex);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getTextRunCursor(@NonNull char[] text, int contextStart, int contextLength, boolean isRtl, int offset, int cursorOpt) {
		return mPaint.getTextRunCursor(text, contextStart, contextLength, isRtl, offset, cursorOpt);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getTextRunCursor(@NonNull CharSequence text, int contextStart, int contextEnd, boolean isRtl, int offset, int cursorOpt) {
		return mPaint.getTextRunCursor(text, contextStart, contextEnd, isRtl, offset, cursorOpt);
	}

	@Override
	public void getTextPath(char[] text, int index, int count, float x, float y, Path path) {
		mPaint.getTextPath(text, index, count, x, y, path);
	}

	@Override
	public void getTextPath(String text, int start, int end, float x, float y, Path path) {
		mPaint.getTextPath(text, start, end, x, y, path);
	}

	@Override
	public void getTextBounds(String text, int start, int end, Rect bounds) {
		mPaint.getTextBounds(text, start, end, bounds);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void getTextBounds(@NonNull CharSequence text, int start, int end, @NonNull Rect bounds) {
		mPaint.getTextBounds(text, start, end, bounds);
	}

	@Override
	public void getTextBounds(char[] text, int index, int count, Rect bounds) {
		mPaint.getTextBounds(text, index, count, bounds);
	}

	@Override
	public boolean hasGlyph(String string) {
		return mPaint.hasGlyph(string);
	}

	@Override
	public float getRunAdvance(char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset) {
		return mPaint.getRunAdvance(text, start, end, contextStart, contextEnd, isRtl, offset);
	}

	@Override
	public float getRunAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset) {
		return mPaint.getRunAdvance(text, start, end, contextStart, contextEnd, isRtl, offset);
	}

	@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
	@Override
	public float getRunCharacterAdvance(@NonNull char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset, @Nullable float[] advances, int advancesIndex) {
		return mPaint.getRunCharacterAdvance(text, start, end, contextStart, contextEnd, isRtl, offset, advances, advancesIndex);
	}

	@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
	@Override
	public float getRunCharacterAdvance(@NonNull CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset, @Nullable float[] advances, int advancesIndex) {
		return mPaint.getRunCharacterAdvance(text, start, end, contextStart, contextEnd, isRtl, offset, advances, advancesIndex);
	}

	@Override
	public int getOffsetForAdvance(char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance) {
		return mPaint.getOffsetForAdvance(text, start, end, contextStart, contextEnd, isRtl, advance);
	}

	@Override
	public int getOffsetForAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance) {
		return mPaint.getOffsetForAdvance(text, start, end, contextStart, contextEnd, isRtl, advance);
	}

	@RequiresApi(api = Build.VERSION_CODES.P)
	@Override
	public boolean equalsForTextMeasurement(@NonNull Paint other) {
		return mPaint.equalsForTextMeasurement(other);
	}
}
