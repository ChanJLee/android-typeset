package me.chan.te.test;

import org.junit.Before;
import org.junit.Test;

import me.chan.te.measurer.Measurer;
import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockTextPaint;
import me.chan.te.text.Paragraph;
import me.chan.te.text.TextAttribute;

public class ParagraphUnitTest {

	private Measurer mMeasurer;
	private TextAttribute mTextAttribute;

	@Before
	public void setup() {
		mMeasurer = new MockMeasurer(new MockTextPaint(20));
		mTextAttribute = new TextAttribute(mMeasurer);
	}

	@Test
	public void testBuilder() {

		Paragraph.Builder builder = Paragraph.Builder.newBuilder(mMeasurer, )
	}

	@Test
	public void testParagraph() {

	}
}
