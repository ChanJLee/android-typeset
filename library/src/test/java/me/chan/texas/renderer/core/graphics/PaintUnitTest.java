package me.chan.texas.renderer.core.graphics;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.LocaleList;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

import me.chan.texas.misc.PaintSet;
import me.chan.texas.test.mock.MockTextPaint;

public class PaintUnitTest {

	@Test
	public void testBase() {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setMockTextSize(1);
		PaintSet paintSet = new PaintSet(textPaint);
		TexasPaintImpl texasPaint = new TexasPaintImpl();

		Assert.assertFalse(texasPaint.isModified());
		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.getColor();
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setColor(Color.RED);
		Assert.assertTrue(texasPaint.isModified());
		texasPaint.getColor();
		Assert.assertTrue(texasPaint.isModified());
	}

	/**
	 * 参考 {@link TexasPaint} 接口，按照顺序编写测试用例，根据 {@link TexasPaintImpl#getPaint(boolean)} 的实现，判断是否修改了 paint
	 */
	@Test
	public void testApi() {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setMockTextSize(1);
		PaintSet paintSet = new PaintSet(textPaint);
		TexasPaintImpl texasPaint = new TexasPaintImpl();

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.reset();
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFlags();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getHinting();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setHinting(Paint.HINTING_ON);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.isAntiAlias();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setAntiAlias(true);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.isDither();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setDither(true);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.isLinearText();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setLinearText(true);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.isSubpixelText();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setSubpixelText(true);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.isUnderlineText();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getUnderlinePosition();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getUnderlineThickness();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setUnderlineText(true);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.isStrikeThruText();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getStrikeThruPosition();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getStrikeThruThickness();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setStrikeThruText(true);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.isFakeBoldText();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setFakeBoldText(true);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.isFilterBitmap();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setFilterBitmap(true);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getStyle();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setStyle(Paint.Style.FILL);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getColor();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getColorLong();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setColor(Color.RED);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setColor(0xff00ff00);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getAlpha();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setAlpha(123);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setARGB(123, 123, 123, 123);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getStrokeWidth();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setStrokeWidth(123);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getStrokeMiter();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setStrokeMiter(123);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getStrokeCap();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setStrokeCap(Paint.Cap.BUTT);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getStrokeJoin();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setStrokeJoin(Paint.Join.BEVEL);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFillPath(null, null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getShader();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setShader(null);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getColorFilter();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setColorFilter(null);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getXfermode();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getBlendMode();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setXfermode(null);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setBlendMode(null);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getPathEffect();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setPathEffect(null);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getMaskFilter();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setMaskFilter(null);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTypeface();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setTypeface(null);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setShadowLayer(1, 1, 1, 1);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setShadowLayer(1, 1, 1, 1L);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.clearShadowLayer();
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getShadowLayerRadius();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getShadowLayerDx();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getShadowLayerDy();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getShadowLayerColor();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getShadowLayerColorLong();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextAlign();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setTextAlign(Paint.Align.CENTER);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextLocale();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setTextLocale(Locale.CHINA);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setTextLocales(LocaleList.forLanguageTags("zh-CN"));
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.isElegantTextHeight();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setElegantTextHeight(true);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextSize();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setTextSize(1);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextScaleX();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setTextScaleX(1);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextSkewX();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setTextSkewX(1);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getLetterSpacing();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setLetterSpacing(1);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getWordSpacing();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setWordSpacing(1);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontFeatureSettings();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setFontFeatureSettings("");
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontVariationSettings();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setFontVariationSettings("");
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getStartHyphenEdit();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getEndHyphenEdit();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setStartHyphenEdit(1);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.setEndHyphenEdit(1);
		Assert.assertTrue(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.ascent();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.descent();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontMetrics(null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontMetrics();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontMetricsForLocale(null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontMetricsInt("", 1, 1, 1, 1, true, null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontMetricsInt(new char[1], 1, 1, 1, 1, true, null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontMetricsInt(null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontMetricsInt();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontMetricsIntForLocale(null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getFontSpacing();
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.measureText(new char[1], 1, 1);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.measureText("", 1, 1);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.measureText("");
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.measureText((CharSequence) "", 1, 1);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.breakText(new char[1], 1, 1, 1, new float[1]);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.breakText("", false, 1, new float[1]);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.breakText("", 1, 1, false, 1, new float[1]);
		Assert.assertFalse(texasPaint.isModified());

		// @Override
		//	public int getTextWidths(char[] text, int index, int count, float[] widths) {
		//		return getPaint(true).getTextWidths(text, index, count, widths);
		//	}
		//
		//	@Override
		//	public int getTextWidths(CharSequence text, int start, int end, float[] widths) {
		//		return getPaint(true).getTextWidths(text, start, end, widths);
		//	}
		//
		//	@Override
		//	public int getTextWidths(String text, int start, int end, float[] widths) {
		//		return getPaint(true).getTextWidths(text, start, end, widths);
		//	}
		//
		//	@Override
		//	public int getTextWidths(String text, float[] widths) {
		//		return getPaint(true).getTextWidths(text, widths);
		//	}
		//
		//	@RequiresApi(api = Build.VERSION_CODES.Q)
		//	@Override
		//	public float getTextRunAdvances(@NonNull char[] chars, int index, int count, int contextIndex, int contextCount, boolean isRtl, @Nullable float[] advances, int advancesIndex) {
		//		return getPaint(true).getTextRunAdvances(chars, index, count, contextIndex, contextCount, isRtl, advances, advancesIndex);
		//	}
		//
		//	@RequiresApi(api = Build.VERSION_CODES.Q)
		//	@Override
		//	public int getTextRunCursor(@NonNull char[] text, int contextStart, int contextLength, boolean isRtl, int offset, int cursorOpt) {
		//		return getPaint(true).getTextRunCursor(text, contextStart, contextLength, isRtl, offset, cursorOpt);
		//	}
		//
		//	@RequiresApi(api = Build.VERSION_CODES.Q)
		//	@Override
		//	public int getTextRunCursor(@NonNull CharSequence text, int contextStart, int contextEnd, boolean isRtl, int offset, int cursorOpt) {
		//		return getPaint(true).getTextRunCursor(text, contextStart, contextEnd, isRtl, offset, cursorOpt);
		//	}
		//
		//	@Override
		//	public void getTextPath(char[] text, int index, int count, float x, float y, Path path) {
		//		getPaint(true).getTextPath(text, index, count, x, y, path);
		//	}
		//
		//	@Override
		//	public void getTextPath(String text, int start, int end, float x, float y, Path path) {
		//		getPaint(true).getTextPath(text, start, end, x, y, path);
		//	}
		//
		//	@Override
		//	public void getTextBounds(String text, int start, int end, Rect bounds) {
		//		getPaint(true).getTextBounds(text, start, end, toRaw(bounds));
		//	}
		//
		//	@RequiresApi(api = Build.VERSION_CODES.Q)
		//	@Override
		//	public void getTextBounds(@NonNull CharSequence text, int start, int end, @NonNull Rect bounds) {
		//		getPaint(true).getTextBounds(text, start, end, toRaw(bounds));
		//	}
		//
		//	@Override
		//	public void getTextBounds(char[] text, int index, int count, Rect bounds) {
		//		getPaint(true).getTextBounds(text, index, count, toRaw(bounds));
		//	}
		//
		//	@Override
		//	public boolean hasGlyph(String string) {
		//		return getPaint(true).hasGlyph(string);
		//	}
		//
		//	@Override
		//	public float getRunAdvance(char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset) {
		//		return getPaint(true).getRunAdvance(text, start, end, contextStart, contextEnd, isRtl, offset);
		//	}
		//
		//	@Override
		//	public float getRunAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset) {
		//		return getPaint(true).getRunAdvance(text, start, end, contextStart, contextEnd, isRtl, offset);
		//	}
		//
		//	@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
		//	@Override
		//	public float getRunCharacterAdvance(@NonNull char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset, @Nullable float[] advances, int advancesIndex) {
		//		return getPaint(true).getRunCharacterAdvance(text, start, end, contextStart, contextEnd, isRtl, offset, advances, advancesIndex);
		//	}
		//
		//	@RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
		//	@Override
		//	public float getRunCharacterAdvance(@NonNull CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, int offset, @Nullable float[] advances, int advancesIndex) {
		//		return getPaint(true).getRunCharacterAdvance(text, start, end, contextStart, contextEnd, isRtl, offset, advances, advancesIndex);
		//	}
		//
		//	@Override
		//	public int getOffsetForAdvance(char[] text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance) {
		//		return getPaint(true).getOffsetForAdvance(text, start, end, contextStart, contextEnd, isRtl, advance);
		//	}
		//
		//	@Override
		//	public int getOffsetForAdvance(CharSequence text, int start, int end, int contextStart, int contextEnd, boolean isRtl, float advance) {
		//		return getPaint(true).getOffsetForAdvance(text, start, end, contextStart, contextEnd, isRtl, advance);
		//	}
		//
		//	@RequiresApi(api = Build.VERSION_CODES.P)
		//	@Override
		//	public boolean equalsForTextMeasurement(@NonNull Paint other) {
		//		return getPaint(true).equalsForTextMeasurement(other);
		//	}

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextWidths(new char[1], 1, 1, new float[1]);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextWidths((CharSequence) "", 1, 1, new float[1]);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextWidths("", 1, 1, new float[1]);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextWidths("", new float[1]);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextRunAdvances(new char[1], 1, 1, 1, 1, true, null, 1);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextRunCursor(new char[1], 1, 1, true, 1, 1);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextRunCursor((CharSequence) "", 1, 1, true, 1, 1);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextPath(new char[1], 1, 1, 1, 1, null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextPath("", 1, 1, 1, 1, null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextBounds(new char[1], 1, 1, null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getTextBounds((CharSequence) "", 1, 1, null);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.hasGlyph("");
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getRunAdvance(new char[1], 1, 1, 1, 1, true, 1);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getRunAdvance((CharSequence) "", 1, 1, 1, 1, true, 1);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getOffsetForAdvance(new char[1], 1, 1, 1, 1, true, 1);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.getOffsetForAdvance((CharSequence) "", 1, 1, 1, 1, true, 1);
		Assert.assertFalse(texasPaint.isModified());

		texasPaint.reset(paintSet);
		Assert.assertFalse(texasPaint.isModified());
		texasPaint.equalsForTextMeasurement(null);
		Assert.assertFalse(texasPaint.isModified());
	}
}