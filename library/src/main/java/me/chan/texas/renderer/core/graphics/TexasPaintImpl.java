package me.chan.texas.renderer.core.graphics;

import android.graphics.BlendMode;
import android.graphics.ColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
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
import me.chan.texas.misc.RectF;
import me.chan.texas.misc.Rect;
import me.chan.texas.utils.CharArrayPool;
import me.chan.texas.utils.TexasUtils;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TexasPaintImpl implements TexasPaint {
	private TextPaint mPaint;
	@NonNull
	private PaintSet mPaintSet;
	private final TextPaint mWorkPaint = new TextPaint();
	private android.graphics.RectF mRawRectF;
	private android.graphics.Rect mRawRect;

	@Override
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
			mPaintSet.copyTo(mWorkPaint);
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
		getPaint(false).setAntiAlias(aa);
	}

	@Override
	public boolean isDither() {
		return getPaint(true).isDither();
	}

	@Override
	public void setDither(boolean dither) {
		getPaint(false).setDither(dither);
	}

	@Override
	public boolean isLinearText() {
		return getPaint(true).isLinearText();
	}

	@Override
	public void setLinearText(boolean linearText) {
		getPaint(false).setLinearText(linearText);
	}

	@Override
	public boolean isSubpixelText() {
		return getPaint(true).isSubpixelText();
	}

	@Override
	public void setSubpixelText(boolean subpixelText) {
		getPaint(false).setSubpixelText(subpixelText);
	}

	@Override
	public boolean isUnderlineText() {
		return getPaint(true).isUnderlineText();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getUnderlinePosition() {
		return getPaint(true).getUnderlinePosition();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getUnderlineThickness() {
		return getPaint(true).getUnderlineThickness();
	}

	@Override
	public void setUnderlineText(boolean underlineText) {
		getPaint(false).setUnderlineText(underlineText);
	}

	@Override
	public boolean isStrikeThruText() {
		return getPaint(true).isStrikeThruText();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getStrikeThruPosition() {
		return getPaint(true).getStrikeThruPosition();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getStrikeThruThickness() {
		return getPaint(true).getStrikeThruThickness();
	}

	@Override
	public void setStrikeThruText(boolean strikeThruText) {
		getPaint(false).setStrikeThruText(strikeThruText);
	}

	@Override
	public boolean isFakeBoldText() {
		return getPaint(true).isFakeBoldText();
	}

	@Override
	public void setFakeBoldText(boolean fakeBoldText) {
		getPaint(false).setFakeBoldText(fakeBoldText);
	}

	@Override
	public boolean isFilterBitmap() {
		return getPaint(true).isFilterBitmap();
	}

	@Override
	public void setFilterBitmap(boolean filter) {
		getPaint(false).setFilterBitmap(filter);
	}

	@Override
	public Paint.Style getStyle() {
		return getPaint(true).getStyle();
	}

	@Override
	public void setStyle(Paint.Style style) {
		getPaint(false).setStyle(style);
	}

	@Override
	public int getColor() {
		return getPaint(true).getColor();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public long getColorLong() {
		return getPaint(true).getColorLong();
	}

	@Override
	public void setColor(int color) {
		getPaint(false).setColor(color);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setColor(long color) {
		getPaint(false).setColor(color);
	}

	@Override
	public int getAlpha() {
		return getPaint(true).getAlpha();
	}

	@Override
	public void setAlpha(int a) {
		getPaint(false).setAlpha(a);
	}

	@Override
	public void setARGB(int a, int r, int g, int b) {
		getPaint(false).setARGB(a, r, g, b);
	}

	@Override
	public float getStrokeWidth() {
		return getPaint(true).getStrokeWidth();
	}

	@Override
	public void setStrokeWidth(float width) {
		getPaint(false).setStrokeWidth(width);
	}

	@Override
	public float getStrokeMiter() {
		return getPaint(true).getStrokeMiter();
	}

	@Override
	public void setStrokeMiter(float miter) {
		getPaint(false).setStrokeMiter(miter);
	}

	@Override
	public Paint.Cap getStrokeCap() {
		return getPaint(true).getStrokeCap();
	}

	@Override
	public void setStrokeCap(Paint.Cap cap) {
		getPaint(false).setStrokeCap(cap);
	}

	@Override
	public Paint.Join getStrokeJoin() {
		return getPaint(true).getStrokeJoin();
	}

	@Override
	public void setStrokeJoin(Paint.Join join) {
		getPaint(false).setStrokeJoin(join);
	}

	@Override
	public boolean getFillPath(Path src, Path dst) {
		return getPaint(true).getFillPath(src, dst);
	}

	@Override
	public Shader getShader() {
		return getPaint(true).getShader();
	}

	@Override
	public Shader setShader(Shader shader) {
		return getPaint(false).setShader(shader);
	}

	@Override
	public ColorFilter getColorFilter() {
		return getPaint(true).getColorFilter();
	}

	@Override
	public ColorFilter setColorFilter(ColorFilter filter) {
		return getPaint(false).setColorFilter(filter);
	}

	@Override
	public Xfermode getXfermode() {
		return getPaint(true).getXfermode();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	@Nullable
	public BlendMode getBlendMode() {
		return getPaint(true).getBlendMode();
	}

	@Override
	public Xfermode setXfermode(Xfermode xfermode) {
		return getPaint(false).setXfermode(xfermode);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setBlendMode(@Nullable BlendMode blendmode) {
		getPaint(false).setBlendMode(blendmode);
	}

	@Override
	public PathEffect getPathEffect() {
		return getPaint(true).getPathEffect();
	}

	@Override
	public PathEffect setPathEffect(PathEffect effect) {
		return getPaint(false).setPathEffect(effect);
	}

	@Override
	public MaskFilter getMaskFilter() {
		return getPaint(true).getMaskFilter();
	}

	@Override
	public MaskFilter setMaskFilter(MaskFilter maskfilter) {
		return getPaint(false).setMaskFilter(maskfilter);
	}

	@Override
	public Typeface getTypeface() {
		return getPaint(true).getTypeface();
	}

	@Override
	public Typeface setTypeface(Typeface typeface) {
		return getPaint(false).setTypeface(typeface);
	}

	@Override
	public void setShadowLayer(float radius, float dx, float dy, int shadowColor) {
		getPaint(false).setShadowLayer(radius, dx, dy, shadowColor);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setShadowLayer(float radius, float dx, float dy, long shadowColor) {
		getPaint(false).setShadowLayer(radius, dx, dy, shadowColor);
	}

	@Override
	public void clearShadowLayer() {
		getPaint(false).clearShadowLayer();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getShadowLayerRadius() {
		return getPaint(true).getShadowLayerRadius();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getShadowLayerDx() {
		return getPaint(true).getShadowLayerDx();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getShadowLayerDy() {
		return getPaint(true).getShadowLayerDy();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getShadowLayerColor() {
		return getPaint(true).getShadowLayerColor();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public long getShadowLayerColorLong() {
		return getPaint(true).getShadowLayerColorLong();
	}

	@Override
	public Paint.Align getTextAlign() {
		return getPaint(true).getTextAlign();
	}

	@Override
	public void setTextAlign(Paint.Align align) {
		getPaint(false).setTextAlign(align);
	}

	@Override
	@NonNull
	public Locale getTextLocale() {
		return getPaint(true).getTextLocale();
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	@NonNull
	public LocaleList getTextLocales() {
		return getPaint(true).getTextLocales();
	}

	@Override
	public void setTextLocale(@NonNull Locale locale) {
		getPaint(false).setTextLocale(locale);
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	@Override
	public void setTextLocales(@NonNull LocaleList locales) {
		getPaint(false).setTextLocales(locales);
	}

	@Override
	public boolean isElegantTextHeight() {
		return getPaint(true).isElegantTextHeight();
	}

	@Override
	public void setElegantTextHeight(boolean elegant) {
		getPaint(false).setElegantTextHeight(elegant);
	}

	@Override
	public float getTextSize() {
		return getPaint(true).getTextSize();
	}

	@Override
	public void setTextSize(float textSize) {
		getPaint(false).setTextSize(textSize);
	}

	@Override
	public float getTextScaleX() {
		return getPaint(true).getTextScaleX();
	}

	@Override
	public void setTextScaleX(float scaleX) {
		getPaint(false).setTextScaleX(scaleX);
	}

	@Override
	public float getTextSkewX() {
		return getPaint(true).getTextSkewX();
	}

	@Override
	public void setTextSkewX(float skewX) {
		getPaint(false).setTextSkewX(skewX);
	}

	@Override
	public float getLetterSpacing() {
		return getPaint(true).getLetterSpacing();
	}

	@Override
	public void setLetterSpacing(float letterSpacing) {
		getPaint(false).setLetterSpacing(letterSpacing);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getWordSpacing() {
		return getPaint(true).getWordSpacing();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setWordSpacing(float wordSpacing) {
		getPaint(false).setWordSpacing(wordSpacing);
	}

	@Override
	public String getFontFeatureSettings() {
		return getPaint(true).getFontFeatureSettings();
	}

	@Override
	public void setFontFeatureSettings(String settings) {
		getPaint(false).setFontFeatureSettings(settings);
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public String getFontVariationSettings() {
		return getPaint(true).getFontVariationSettings();
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	public boolean setFontVariationSettings(String fontVariationSettings) {
		return getPaint(false).setFontVariationSettings(fontVariationSettings);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getStartHyphenEdit() {
		return getPaint(true).getStartHyphenEdit();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getEndHyphenEdit() {
		return getPaint(true).getEndHyphenEdit();
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setStartHyphenEdit(int startHyphen) {
		getPaint(false).setStartHyphenEdit(startHyphen);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void setEndHyphenEdit(int endHyphen) {
		getPaint(false).setEndHyphenEdit(endHyphen);
	}

	@Override
	public float ascent() {
		return getPaint(true).ascent();
	}

	@Override
	public float descent() {
		return getPaint(true).descent();
	}

	@Override
	public float getFontMetrics(Paint.FontMetrics metrics) {
		return getPaint(true).getFontMetrics(metrics);
	}

	@Override
	public Paint.FontMetrics getFontMetrics() {
		return getPaint(true).getFontMetrics();
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void getFontMetricsForLocale(@NonNull Paint.FontMetrics metrics) {
		getPaint(true).getFontMetricsForLocale(metrics);
	}

	@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
	@Override
	public void getFontMetricsInt(@NonNull CharSequence text, int start, int count, int contextStart, int contextCount, boolean isRtl, @NonNull Paint.FontMetricsInt outMetrics) {
		getPaint(true).getFontMetricsInt(text, start, count, contextStart, contextCount, isRtl, outMetrics);
	}

	@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
	@Override
	public void getFontMetricsInt(@NonNull char[] text, int start, int count, int contextStart, int contextCount, boolean isRtl, @NonNull Paint.FontMetricsInt outMetrics) {
		getPaint(true).getFontMetricsInt(text, start, count, contextStart, contextCount, isRtl, outMetrics);
	}

	@Override
	public int getFontMetricsInt(Paint.FontMetricsInt fmi) {
		return getPaint(true).getFontMetricsInt(fmi);
	}

	@Override
	public Paint.FontMetricsInt getFontMetricsInt() {
		return getPaint(true).getFontMetricsInt();
	}

	@RequiresApi(api = Build.VERSION_CODES.VANILLA_ICE_CREAM)
	@Override
	public void getFontMetricsIntForLocale(@NonNull Paint.FontMetricsInt metrics) {
		getPaint(true).getFontMetricsIntForLocale(metrics);
	}

	@Override
	public float getFontSpacing() {
		return getPaint(true).getFontSpacing();
	}

	@Override
	public float measureText(char[] text, int index, int count) {
		return getPaint(true).measureText(text, index, count);
	}

	@Override
	public float measureText(String text, int start, int end) {
		return getPaint(true).measureText(text, start, end);
	}

	@Override
	public float measureText(String text) {
		return getPaint(true).measureText(text);
	}

	@Override
	public float measureText(CharSequence text, int start, int end) {
		return getPaint(true).measureText(text, start, end);
	}

	@Override
	public int breakText(char[] text, int index, int count, float maxWidth, float[] measuredWidth) {
		return getPaint(true).breakText(text, index, count, maxWidth, measuredWidth);
	}

	@Override
	public int breakText(CharSequence text, int start, int end, boolean measureForwards, float maxWidth, float[] measuredWidth) {
		return getPaint(true).breakText(text, start, end, measureForwards, maxWidth, measuredWidth);
	}

	@Override
	public int breakText(String text, boolean measureForwards, float maxWidth, float[] measuredWidth) {
		return getPaint(true).breakText(text, measureForwards, maxWidth, measuredWidth);
	}

	@Override
	public int getTextWidths(char[] text, int index, int count, float[] widths) {
		return getPaint(true).getTextWidths(text, index, count, widths);
	}

	@Override
	public int getTextWidths(CharSequence text, int start, int end, float[] widths) {
		return getPaint(true).getTextWidths(text, start, end, widths);
	}

	@Override
	public int getTextWidths(String text, int start, int end, float[] widths) {
		return getPaint(true).getTextWidths(text, start, end, widths);
	}

	@Override
	public int getTextWidths(String text, float[] widths) {
		return getPaint(true).getTextWidths(text, widths);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public float getTextRunAdvances(@NonNull char[] chars, int index, int count, int contextIndex, int contextCount, boolean isRtl, @Nullable float[] advances, int advancesIndex) {
		return getPaint(true).getTextRunAdvances(chars, index, count, contextIndex, contextCount, isRtl, advances, advancesIndex);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getTextRunCursor(@NonNull char[] text, int contextStart, int contextLength, boolean isRtl, int offset, int cursorOpt) {
		return getPaint(true).getTextRunCursor(text, contextStart, contextLength, isRtl, offset, cursorOpt);
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public int getTextRunCursor(@NonNull CharSequence text, int contextStart, int contextEnd, boolean isRtl, int offset, int cursorOpt) {
		return getPaint(true).getTextRunCursor(text, contextStart, contextEnd, isRtl, offset, cursorOpt);
	}

	@Override
	public void getTextPath(char[] text, int index, int count, float x, float y, Path path) {
		getPaint(true).getTextPath(text, index, count, x, y, path);
	}

	@Override
	public void getTextPath(String text, int start, int end, float x, float y, Path path) {
		getPaint(true).getTextPath(text, start, end, x, y, path);
	}

	@Override
	public void getTextBounds(String text, int start, int end, Rect bounds) {
		getPaint(true).getTextBounds(text, start, end, toRaw(bounds));
	}

	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void getTextBounds(@NonNull CharSequence text, int start, int end, @NonNull Rect bounds) {
		getPaint(true).getTextBounds(text, start, end, toRaw(bounds));
	}

	@Override
	public void getTextBounds(char[] text, int index, int count, Rect bounds) {
		getPaint(true).getTextBounds(text, index, count, toRaw(bounds));
	}

	@Override
	public boolean hasGlyph(String string) {
		return getPaint(true).hasGlyph(string);
	}

	@Override
	public float getRunAdvance(char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset) {
		return getPaint(true).getRunAdvance(text, start, end, contextStart, contextEnd, isRtl, offset);
	}

	private static final CharArrayPool POOL = new CharArrayPool();

	@Override
	public float getRunAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset) {
		int size = end - start;
		if (size <= 0) {
			return 0;
		}

		char[] buf = POOL.obtain(size);
		TexasUtils.getChars(text, start, end, buf, 0);
		try {
			return getPaint(true).getRunAdvance(buf, 0, size, 0, size, isRtl, offset);
		} finally {
			POOL.release(buf);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
	@Override
	public float getRunCharacterAdvance(@NonNull char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset, @Nullable float[] advances, int advancesIndex) {
		return getPaint(true).getRunCharacterAdvance(text, start, end, contextStart, contextEnd, isRtl, offset, advances, advancesIndex);
	}

	@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
	@Override
	public float getRunCharacterAdvance(@NonNull CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset, @Nullable float[] advances, int advancesIndex) {
		return getPaint(true).getRunCharacterAdvance(text, start, end, contextStart, contextEnd, isRtl, offset, advances, advancesIndex);
	}

	@Override
	public int getOffsetForAdvance(char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance) {
		return getPaint(true).getOffsetForAdvance(text, start, end, contextStart, contextEnd, isRtl, advance);
	}

	@Override
	public int getOffsetForAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance) {
		return getPaint(true).getOffsetForAdvance(text, start, end, contextStart, contextEnd, isRtl, advance);
	}

	@RequiresApi(api = Build.VERSION_CODES.P)
	@Override
	public boolean equalsForTextMeasurement(@NonNull Paint other) {
		return getPaint(true).equalsForTextMeasurement(other);
	}

	@Override
	public Paint getPaint() {
		return getPaint(true);
	}

	@Override
	public void set(Paint paint) {
		getPaint(false).set(paint);
	}

	private android.graphics.RectF toRaw(@Nullable RectF rect) {
		if (rect == null) {
			return null;
		}

		if (mRawRectF == null) {
			mRawRectF = new android.graphics.RectF();
		}

		mRawRectF.set(rect.left, rect.top, rect.right, rect.bottom);
		return mRawRectF;
	}

	private android.graphics.Rect toRaw(@Nullable me.chan.texas.misc.Rect rect) {
		if (rect == null) {
			return null;
		}

		if (mRawRect == null) {
			mRawRect = new android.graphics.Rect();
		}

		mRawRect.set(rect.left, rect.top, rect.right, rect.bottom);
		return mRawRect;
	}
}
