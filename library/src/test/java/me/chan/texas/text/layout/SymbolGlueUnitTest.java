package me.chan.texas.text.layout;

import com.shanbay.lib.texas.TestUtils;
import com.shanbay.lib.texas.test.mock.MockTextPaint;

import me.chan.texas.measurer.MockMeasurer;

import org.junit.Assert;
import org.junit.Test;

public class SymbolGlueUnitTest {

	@Test
	public void test() {
		Glue.clean();
		SymbolGlue.clean();

		MockTextPaint mockTextPaint = new MockTextPaint();
		mockTextPaint.setMockTextSize(2);
		MockMeasurer measure = new MockMeasurer(mockTextPaint);

		TextBox box = TextBox.obtain("《", 0, 1, measure, null, null, null, null);
		Assert.assertEquals(box.getWidth(), 2, 0);

		box.addAttribute(TextBox.ATTRIBUTE_SQUISH_LEFT);
		Assert.assertEquals(box.getWidth(), 1, 0);


		SymbolGlue glue = SymbolGlue.obtain(box);
		Assert.assertTrue(glue.getWidth() <= box.getWidth());
		System.out.println(glue);

		glue.recycle();
		TestUtils.testRecycled(glue);

		Object tmp = glue;
		Glue o = Glue.obtain(1, 1, 1, 0);
		Assert.assertNotSame(o, tmp);

		glue = SymbolGlue.obtain(box);
		Assert.assertSame(tmp, glue);
	}
}
