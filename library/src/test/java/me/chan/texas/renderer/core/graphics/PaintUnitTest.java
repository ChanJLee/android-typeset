package me.chan.texas.renderer.core.graphics;

import android.graphics.Color;
import android.graphics.Paint;

import org.junit.Assert;
import org.junit.Test;

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
	}
}