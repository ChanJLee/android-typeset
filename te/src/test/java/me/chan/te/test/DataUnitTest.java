package me.chan.te.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import me.chan.te.data.ElementFactory;
import me.chan.te.data.Glue;
import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockTextPaint;

public class DataUnitTest {
	private ElementFactory mElementFactory;

	@Before
	public void setup() {
		MockTextPaint mockTextPaint = new MockTextPaint();
		MockMeasurer mockMeasurer = new MockMeasurer(mockTextPaint);
		mElementFactory = new ElementFactory(mockMeasurer);

		Assert.assertNotEquals(mockTextPaint.getMockTextSize(), 0);
		Assert.assertNotEquals(mockTextPaint.getMockTextHeight(), 0);

		String hello = "hello";
		Assert.assertNotEquals(mockMeasurer.getFontSpacing(), 0);
		Assert.assertEquals("check measure text", mockMeasurer.getDesiredWidth(hello,
				0, hello.length()), hello.length() * mockTextPaint.getMockTextSize(), 0);
		Assert.assertEquals("check measure text", mockMeasurer.getDesiredWidth(hello,
				1, 2), mockTextPaint.getMockTextSize(), 0);
	}

	@Test
	public void testGlue() {
		Glue glue = mElementFactory.obtainGlue(1, 2, 3);
		Assert.assertEquals("check width: ", glue.getWidth(), 1, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), 2, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), 3, 0);

		Glue previous = glue;
		mElementFactory.recycle(glue);

		glue = mElementFactory.obtainGlue(4, 5, 6);
		if (previous != glue) {
			Assert.fail("check recycle reference failed");
		}

		Assert.assertEquals("check width: ", glue.getWidth(), 4, 0);
		Assert.assertEquals("check stretch: ", glue.getStretch(), 5, 0);
		Assert.assertEquals("check shrink: ", glue.getShrink(), 6, 0);
	}
}
