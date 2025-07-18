package me.chan.texas.renderer.core.graphics;

import android.graphics.Color;

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
}
