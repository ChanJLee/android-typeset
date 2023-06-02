package me.chan.texas.typesetter.utils;

import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;

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
