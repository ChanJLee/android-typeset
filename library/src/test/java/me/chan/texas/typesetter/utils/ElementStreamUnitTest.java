package me.chan.texas.typesetter.utils;

import android.text.TextPaint;

import me.chan.texas.TexasOption;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.misc.PaintSet;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextAttribute;

import org.junit.Assert;
import org.junit.Test;

public class ElementStreamUnitTest {

	@Test
	public void test() {
		MockTextPaint paint = new MockTextPaint(20);
		MockMeasurer measurer = new MockMeasurer(paint);
		TextAttribute attribute = new TextAttribute(measurer);
		TexasOption texasOption = new TexasOption(new PaintSet(paint), Hyphenation.getInstance(), measurer, attribute, new RenderOption());
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
