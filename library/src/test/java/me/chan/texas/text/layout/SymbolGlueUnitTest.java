package me.chan.texas.text.layout;

import me.chan.texas.test.mock.MockTextPaint;

import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.text.TextAttribute;

import org.junit.Assert;
import org.junit.Test;

public class SymbolGlueUnitTest {

	@Test
	public void test() {
		MockTextPaint mockTextPaint = new MockTextPaint();
		mockTextPaint.setMockTextSize(2);
		MockMeasurer measurer = new MockMeasurer(mockTextPaint);
		TextAttribute textAttribute = new TextAttribute(measurer);

		TextSpan span = TextSpan.obtain("《", 0, 1, null, null, null, null);
		span.measure(measurer, textAttribute);
		Assert.assertEquals(span.getWidth(), 2, 0);

		span.addAttribute(TextSpan.ATTRIBUTE_SQUISH_LEFT);
		Assert.assertEquals(span.getWidth(), 1, 0);


		SymbolGlue glue = SymbolGlue.obtain(span);
		glue.measure(measurer, textAttribute);
		Assert.assertTrue(glue.getWidth() <= span.getWidth());
		Assert.assertTrue(glue.getWidth() >= glue.getShrink());
		Assert.assertEquals(0, glue.getStretch(), 0);
		System.out.println(glue);

		glue.recycle();
//		TestUtils.testRecycled(glue);

		Object tmp = glue;
		Glue o = Glue.obtain();
		Assert.assertNotSame(o, tmp);

		glue = SymbolGlue.obtain(span);
		Assert.assertNotSame(tmp, glue);
	}
}
