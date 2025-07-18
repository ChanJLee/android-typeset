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
import android.text.TextPaint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;

import java.util.Locale;

import me.chan.texas.misc.PaintSet;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TexasPaintImpl implements TexasPaint {
	private TextPaint mPaint;
	@NonNull
	private PaintSet mPaintSet;
	private final TextPaint mWorkPaint = new TextPaint();

	public final void reset(@NonNull PaintSet paintSet) {
		mPaintSet = paintSet;
		mPaint = paintSet.getPaint();
	}

	public boolean isModified() {
		return mPaint == mWorkPaint;
	}

	private Paint getPaint(boolean readOnly) {
		if (readOnly) {
			return mPaint;
		}

		if (mPaint != mWorkPaint) {
			mPaintSet.getWorkPaint(mWorkPaint);
			mPaint = mWorkPaint;
		}
		return mPaint;
	}

	@Override
	public void reset() {
		getPaint(false).reset();
	}

	@Override
	public int getFlags() {
		return getPaint(true).getFlags();
	}

	@Override
	public void setFlags(int flags) {
		getPaint(false).setFlags(flags);
	}

	@Override
	public int getHinting() {
		return getPaint(true).getHinting();
	}

	@Override
	public void setHinting(int mode) {
		getPaint(false).setHinting(mode);
	}

	@Override
	public boolean isAntiAlias() {
		return getPaint(true).isAntiAlias();
	}

	@Override
	public void setAntiAlias(boolean aa) {
		getPaint().setAntiAlias(aa);
	}

	@Override
	public boolean isDither() {
		return getPaint().isDither();
	}

	@Override
	public void setDither(boolean dither) {
		getPaint().setDither(dither);
	}

	@Override
	public boolean isLinearText() {
		return getPaint().isLinearText();
	}

	@Override
	public void setLinearText(boolean linearText) {
		getPaint().setLinearText(linearText);
	}

	@Override
	public boolean isSubpixelText() {
		return getPaint().isSubpixelText();
	}

	@Override
	public void setSubpixelText(boolean subpixelText) {
		getPaint().setSubpixelText(subpixelText);
	}

	@Override
	public boolean isUnderlineText() {
		return getPaint().isUnderlineText();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getUnderlinePosition() {
		return getPaint().getUnderlinePosition();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getUnderlineThickness() {
		return getPaint().getUnderlineThickness();
	}

	@Override
	public void setUnderlineText(boolean underlineText) {
		getPaint().setUnderlineText(underlineText);
	}

	@Override
	public boolean isStrikeThruText() {
		return getPaint().isStrikeThruText();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getStrikeThruPosition() {
		return getPaint().getStrikeThruPosition();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getStrikeThruThickness() {
		return getPaint().getStrikeThruThickness();
	}

	@Override
	public void setStrikeThruText(boolean strikeThruText) {
		getPaint().setStrikeThruText(strikeThruText);
	}

	@Override
	public boolean isFakeBoldText() {
		return getPaint().isFakeBoldText();
	}

	@Override
	public void setFakeBoldText(boolean fakeBoldText) {
		getPaint().setFakeBoldText(fakeBoldText);
	}

	@Override
	public boolean isFilterBitmap() {
		return getPaint().isFilterBitmap();
	}

	@Override
	public void setFilterBitmap(boolean filter) {
		getPaint().setFilterBitmap(filter);
	}

	@Override
	public Paint.Style getStyle() {
		return getPaint().getStyle();
	}

	@Override
	public void setStyle(Paint.Style style) {
		getPaint().setStyle(style);
	}

	@Override
	public int getColor() {
		return getPaint().getColor();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public long getColorLong() {
		return getPaint().getColorLong();
	}

	@Override
	public void setColor(int color) {
		getPaint().setColor(color);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setColor(long color) {
		getPaint().setColor(color);
	}

	@Override
	public int getAlpha() {
		return getPaint().getAlpha();
	}

	@Override
	public void setAlpha(int a) {
		getPaint().setAlpha(a);
	}

	@Override
	public void setARGB(int a, int r, int g, int b) {
		getPaint().setARGB(a, r, g, b);
	}

	@Override
	public float getStrokeWidth() {
		return getPaint().getStrokeWidth();
	}

	@Override
	public void setStrokeWidth(float width) {
		getPaint().setStrokeWidth(width);
	}

	@Override
	public float getStrokeMiter() {
		return getPaint().getStrokeMiter();
	}

	@Override
	public void setStrokeMiter(float miter) {
		getPaint().setStrokeMiter(miter);
	}

	@Override
	public Paint.Cap getStrokeCap() {
		return getPaint().getStrokeCap();
	}

	@Override
	public void setStrokeCap(Paint.Cap cap) {
		getPaint().setStrokeCap(cap);
	}

	@Override
	public Paint.Join getStrokeJoin() {
		return getPaint().getStrokeJoin();
	}

	@Override
	public void setStrokeJoin(Paint.Join join) {
		getPaint().setStrokeJoin(join);
	}

	@Override
	public boolean getFillPath(Path src, Path dst) {
		return getPaint().getFillPath(src, dst);
	}

	@Override
	public Shader getShader() {
		return getPaint().getShader();
	}

	@Override
	public Shader setShader(Shader shader) {
		return getPaint().setShader(shader);
	}

	@Override
	public ColorFilter getColorFilter() {
		return getPaint().getColorFilter();
	}

	@Override
	public ColorFilter setColorFilter(ColorFilter filter) {
		return getPaint().setColorFilter(filter);
	}

	@Override
	public Xfermode getXfermode() {
		return getPaint().getXfermode();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	@Nullable
	public BlendMode getBlendMode() {
		return getPaint().getBlendMode();
	}

	@Override
	public Xfermode setXfermode(Xfermode xfermode) {
		return getPaint().setXfermode(xfermode);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setBlendMode(@Nullable BlendMode blendmode) {
		getPaint().setBlendMode(blendmode);
	}

	@Override
	public PathEffect getPathEffect() {
		return getPaint().getPathEffect();
	}

	@Override
	public PathEffect setPathEffect(PathEffect effect) {
		return getPaint().setPathEffect(effect);
	}

	@Override
	public MaskFilter getMaskFilter() {
		return getPaint().getMaskFilter();
	}

	@Override
	public MaskFilter setMaskFilter(MaskFilter maskfilter) {
		return getPaint().setMaskFilter(maskfilter);
	}

	@Override
	public Typeface getTypeface() {
		return getPaint().getTypeface();
	}

	@Override
	public Typeface setTypeface(Typeface typeface) {
		return getPaint().setTypeface(typeface);
	}

	@Override
	public void setShadowLayer(float radius, float dx, float dy, int shadowColor) {
		getPaint().setShadowLayer(radius, dx, dy, shadowColor);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setShadowLayer(float radius, float dx, float dy, long shadowColor) {
		getPaint().setShadowLayer(radius, dx, dy, shadowColor);
	}

	@Override
	public void clearShadowLayer() {
		getPaint().clearShadowLayer();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getShadowLayerRadius() {
		return getPaint().getShadowLayerRadius();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getShadowLayerDx() {
		return getPaint().getShadowLayerDx();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getShadowLayerDy() {
		return getPaint().getShadowLayerDy();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getShadowLayerColor() {
		return getPaint().getShadowLayerColor();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public long getShadowLayerColorLong() {
		return getPaint().getShadowLayerColorLong();
	}

	@Override
	public Paint.Align getTextAlign() {
		return getPaint().getTextAlign();
	}

	@Override
	public void setTextAlign(Paint.Align align) {
		getPaint().setTextAlign(align);
	}

	@Override
	@NonNull
	public Locale getTextLocale() {
		return getPaint().getTextLocale();
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	@NonNull
	public LocaleList getTextLocales() {
		return getPaint().getTextLocales();
	}

	@Override
	public void setTextLocale(@NonNull Locale locale) {
		getPaint().setTextLocale(locale);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void setTextLocales(@NonNull LocaleList locales) {
		getPaint().setTextLocales(locales);
	}

	@Override
	public boolean isElegantTextHeight() {
		return getPaint().isElegantTextHeight();
	}

	@Override
	public void setElegantTextHeight(boolean elegant) {
		getPaint().setElegantTextHeight(elegant);
	}

	@Override
	public float getTextSize() {
		return getPaint().getTextSize();
	}

	@Override
	public void setTextSize(float textSize) {
		getPaint().setTextSize(textSize);
	}

	@Override
	public float getTextScaleX() {
		return getPaint().getTextScaleX();
	}

	@Override
	public void setTextScaleX(float scaleX) {
		getPaint().setTextScaleX(scaleX);
	}

	@Override
	public float getTextSkewX() {
		return getPaint().getTextSkewX();
	}

	@Override
	public void setTextSkewX(float skewX) {
		getPaint().setTextSkewX(skewX);
	}

	@Override
	public float getLetterSpacing() {
		return getPaint().getLetterSpacing();
	}

	@Override
	public void setLetterSpacing(float letterSpacing) {
		getPaint().setLetterSpacing(letterSpacing);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getWordSpacing() {
		return getPaint().getWordSpacing();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setWordSpacing(float wordSpacing) {
		getPaint().setWordSpacing(wordSpacing);
	}

	@Override
	public String getFontFeatureSettings() {
		return getPaint().getFontFeatureSettings();
	}

	@Override
	public void setFontFeatureSettings(String settings) {
		getPaint().setFontFeatureSettings(settings);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public String getFontVariationSettings() {
		return getPaint().getFontVariationSettings();
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean setFontVariationSettings(String fontVariationSettings) {
		return getPaint().setFontVariationSettings(fontVariationSettings);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getStartHyphenEdit() {
		return getPaint().getStartHyphenEdit();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getEndHyphenEdit() {
		return getPaint().getEndHyphenEdit();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setStartHyphenEdit(int startHyphen) {
		getPaint().setStartHyphenEdit(startHyphen);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setEndHyphenEdit(int endHyphen) {
		getPaint().setEndHyphenEdit(endHyphen);
	}

	@Override
	public float ascent() {
		return getPaint().ascent();
	}

	@Override
	public float descent() {
		return getPaint().descent();
	}

	@Override
	public float getFontMetrics(Paint.FontMetrics metrics) {
		return getPaint().getFontMetrics(metrics);
	}

	@Override
	public Paint.FontMetrics getFontMetrics() {
		return getPaint().getFontMetrics();
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void getFontMetricsForLocale(@NonNull Paint.FontMetrics metrics) {
		getPaint().getFontMetricsForLocale(metrics);
	}

	@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
	@Override
	public void getFontMetricsInt(@NonNull CharSequence text, int start, int count, int contextStart, int contextCount, boolean isRtl, @NonNull Paint.FontMetricsInt outMetrics) {
		getPaint().getFontMetricsInt(text, start, count, contextStart, contextCount, isRtl, outMetrics);
	}

	@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
	@Override
	public void getFontMetricsInt(@NonNull char[] text, int start, int count, int contextStart, int contextCount, boolean isRtl, @NonNull Paint.FontMetricsInt outMetrics) {
		getPaint().getFontMetricsInt(text, start, count, contextStart, contextCount, isRtl, outMetrics);
	}

	@Override
	public int getFontMetricsInt(Paint.FontMetricsInt fmi) {
		return getPaint().getFontMetricsInt(fmi);
	}

	@Override
	public Paint.FontMetricsInt getFontMetricsInt() {
		return getPaint().getFontMetricsInt();
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void getFontMetricsIntForLocale(@NonNull Paint.FontMetricsInt metrics) {
		getPaint().getFontMetricsIntForLocale(metrics);
	}

	@Override
	public float getFontSpacing() {
		return getPaint().getFontSpacing();
	}

	@Override
	public float measureText(char[] text, int index, int count) {
		return getPaint().measureText(text, index, count);
	}

	@Override
	public float measureText(String text, int start, int end) {
		return getPaint().measureText(text, start, end);
	}

	@Override
	public float measureText(String text) {
		return getPaint().measureText(text);
	}

	@Override
	public float measureText(CharSequence text, int start, int end) {
		return getPaint().measureText(text, start, end);
	}

	@Override
	public int breakText(char[] text, int index, int count, float maxWidth, float[] measuredWidth) {
		return getPaint().breakText(text, index, count, maxWidth, measuredWidth);
	}

	@Override
	public int breakText(CharSequence text, int start, int end, boolean measureForwards, float maxWidth, float[] measuredWidth) {
		return getPaint().breakText(text, start, end, measureForwards, maxWidth, measuredWidth);
	}

	@Override
	public int breakText(String text, boolean measureForwards, float maxWidth, float[] measuredWidth) {
		return getPaint().breakText(text, measureForwards, maxWidth, measuredWidth);
	}

	@Override
	public int getTextWidths(char[] text, int index, int count, float[] widths) {
		return getPaint().getTextWidths(text, index, count, widths);
	}

	@Override
	public int getTextWidths(CharSequence text, int start, int end, float[] widths) {
		return getPaint().getTextWidths(text, start, end, widths);
	}

	@Override
	public int getTextWidths(String text, int start, int end, float[] widths) {
		return getPaint().getTextWidths(text, start, end, widths);
	}

	@Override
	public int getTextWidths(String text, float[] widths) {
		return getPaint().getTextWidths(text, widths);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getTextRunAdvances(@NonNull char[] chars, int index, int count, int contextIndex, int contextCount, boolean isRtl, @Nullable float[] advances, int advancesIndex) {
		return getPaint().getTextRunAdvances(chars, index, count, contextIndex, contextCount, isRtl, advances, advancesIndex);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getTextRunCursor(@NonNull char[] text, int contextStart, int contextLength, boolean isRtl, int offset, int cursorOpt) {
		return getPaint().getTextRunCursor(text, contextStart, contextLength, isRtl, offset, cursorOpt);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getTextRunCursor(@NonNull CharSequence text, int contextStart, int contextEnd, boolean isRtl, int offset, int cursorOpt) {
		return getPaint().getTextRunCursor(text, contextStart, contextEnd, isRtl, offset, cursorOpt);
	}

	@Override
	public void getTextPath(char[] text, int index, int count, float x, float y, Path path) {
		getPaint().getTextPath(text, index, count, x, y, path);
	}

	@Override
	public void getTextPath(String text, int start, int end, float x, float y, Path path) {
		getPaint().getTextPath(text, start, end, x, y, path);
	}

	@Override
	public void getTextBounds(String text, int start, int end, Rect bounds) {
		getPaint().getTextBounds(text, start, end, bounds);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void getTextBounds(@NonNull CharSequence text, int start, int end, @NonNull Rect bounds) {
		getPaint().getTextBounds(text, start, end, bounds);
	}

	@Override
	public void getTextBounds(char[] text, int index, int count, Rect bounds) {
		getPaint().getTextBounds(text, index, count, bounds);
	}

	@Override
	public boolean hasGlyph(String string) {
		return getPaint().hasGlyph(string);
	}

	@Override
	public float getRunAdvance(char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset) {
		return getPaint().getRunAdvance(text, start, end, contextStart, contextEnd, isRtl, offset);
	}

	@Override
	public float getRunAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset) {
		return getPaint().getRunAdvance(text, start, end, contextStart, contextEnd, isRtl, offset);
	}

	@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
	@Override
	public float getRunCharacterAdvance(@NonNull char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset, @Nullable float[] advances, int advancesIndex) {
		return getPaint().getRunCharacterAdvance(text, start, end, contextStart, contextEnd, isRtl, offset, advances, advancesIndex);
	}

	@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
	@Override
	public float getRunCharacterAdvance(@NonNull CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset, @Nullable float[] advances, int advancesIndex) {
		return getPaint().getRunCharacterAdvance(text, start, end, contextStart, contextEnd, isRtl, offset, advances, advancesIndex);
	}

	@Override
	public int getOffsetForAdvance(char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance) {
		return getPaint().getOffsetForAdvance(text, start, end, contextStart, contextEnd, isRtl, advance);
	}

	@Override
	public int getOffsetForAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance) {
		return getPaint().getOffsetForAdvance(text, start, end, contextStart, contextEnd, isRtl, advance);
	}

	@RequiresApi(api = Build.VERSION_CODES.P)
	@Override
	public boolean equalsForTextMeasurement(@NonNull Paint other) {
		return getPaint().equalsForTextMeasurement(other);
	}
}
