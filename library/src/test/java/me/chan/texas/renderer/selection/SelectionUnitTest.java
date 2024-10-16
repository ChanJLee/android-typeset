package me.chan.texas.renderer.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import me.chan.texas.TexasOption;
import me.chan.texas.adapter.ParseException;
import me.chan.texas.adapter.TextAdapter;
import me.chan.texas.hyphenation.Hyphenation;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.measurer.MockMeasurer;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.source.SourceOpenException;
import me.chan.texas.test.mock.MockTextPaint;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.typesetter.ParagraphTypesetter;

public class SelectionUnitTest {
	private Document mDocument;

	@Before
	public void init() throws SourceOpenException, ParseException {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setMockTextSize(1);

		Measurer measurer = new MockMeasurer(textPaint);
		TextAttribute textAttribute = new TextAttribute(measurer);
		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), measurer, textAttribute, new RenderOption());

		MockTextPaint paint = new MockTextPaint();
		paint.setTextSize(1);
		TextAdapter textParser = new TextAdapter();
		textParser.setData("1 2 3 4 5 6 7 8 9\na b c d e f g h i\n一 二 三 四 五 六 七 八 九");
		mDocument = textParser.getDocument(texasOption);

		ParagraphTypesetter texTypesetter = new ParagraphTypesetter();

		for (int i = 0; i < mDocument.getSegmentCount(); ++i) {
			Segment segment = mDocument.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			texTypesetter.typeset(paragraph, BreakStrategy.SIMPLE, 5, 1);
			assertNotNull(paragraph);
			Layout layout = paragraph.getLayout();
			assertEquals(layout.getLineCount(), 3);
		}
	}

	@Test
	public void testBase() {

	}
}
