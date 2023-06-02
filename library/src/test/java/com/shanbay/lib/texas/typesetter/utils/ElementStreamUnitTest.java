package com.shanbay.lib.texas.typesetter.utils;

import com.shanbay.lib.texas.TexasOption;
import com.shanbay.lib.texas.hyphenation.Hyphenation;
import com.shanbay.lib.texas.measurer.MockMeasurer;
import com.shanbay.lib.texas.renderer.RenderOption;
import com.shanbay.lib.texas.test.mock.MockTextPaint;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.TextAttribute;

import org.junit.Assert;
import org.junit.Test;

public class ElementStreamUnitTest {

	@Test
	public void test() {
		MockMeasurer measurer = new MockMeasurer(new MockTextPaint(20));
		TextAttribute attribute = new TextAttribute(measurer);
		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), measurer, attribute, new RenderOption());
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);

		builder.text("hello world");
		Paragraph paragraph = builder.build();

		final int size = 5;
		Assert.assertEquals(paragraph.getElementCount(), size);

		ElementStream stream = new ElementStream(paragraph);
		Assert.assertFalse(stream.eof());

		int save = stream.state();

		for (int i = 0; i < size; ++i) {
			Assert.assertFalse(stream.eof());
			Assert.assertEquals(i, stream.state());
			Assert.assertNotNull(stream.next());
		}

		Assert.assertTrue(stream.eof());

		stream.restore(save);
		Assert.assertFalse(stream.eof());

		for (int i = 0; i < size; ++i) {
			Assert.assertFalse(stream.eof());
			Assert.assertEquals(i, stream.state());
			Assert.assertNotNull(stream.next());
		}

		Assert.assertTrue(stream.eof());
	}
}
