package me.chan.te.test;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.core.TextEngineCore;
import me.chan.te.data.Box;
import me.chan.te.data.DrawableBox;
import me.chan.te.data.TextBox;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
import me.chan.te.parser.TextParser;
import me.chan.te.parser.utils.PlainTextParserUtils;
import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockOption;
import me.chan.te.test.mock.MockTextPaint;
import me.chan.te.text.BreakStrategy;
import me.chan.te.text.Document;
import me.chan.te.text.Gravity;
import me.chan.te.text.Line;
import me.chan.te.text.Paragraph;
import me.chan.te.text.Segment;
import me.chan.te.typesetter.Typesetter;

import static me.chan.te.parser.utils.PlainTextParserUtils.findNewline;
import static me.chan.te.parser.utils.PlainTextParserUtils.skipBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class TypesetterUnitTest {
	@Mock
	private Rect mRect;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Rect rect = (Rect) invocation.getMock();
				return rect.right - rect.left;
			}
		}).when(mRect).width();

		Mockito.doAnswer(new Answer() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Rect rect = (Rect) invocation.getMock();
				return rect.bottom - rect.top;
			}
		}).when(mRect).height();

		Hypher.getInstance();
	}

	@Test
	public void testMockTextPaint() {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setTextSize(18);

		String msg = "hello";
		textPaint.getTextBounds(msg, 0, msg.length(), mRect);

		assertEquals(mRect.height(), textPaint.getMockTextHeight());
		assertEquals(mRect.width(), textPaint.getMockTextSize() * msg.length());

		msg = "";
		textPaint.getTextBounds(msg, 0, msg.length(), mRect);
	}

	@Test
	public void testTypesetter() throws IOException {
		File file = new File("../app/src/main/assets/TheBookAndTheSword.txt");
		System.out.println(file.getAbsolutePath());
		assertTrue(file.exists());

		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line)
					.append("\n");
		}

		String text = stringBuilder.toString();
		assertNotEquals(text.length(), 0);

		long timestamp = System.currentTimeMillis();

		checkContent(text, BreakStrategy.SIMPLE, 200, 1);
		checkContent(text, BreakStrategy.SIMPLE, 200, 18);
		checkContent(text, BreakStrategy.SIMPLE, 200, 100);
		checkContent(text, BreakStrategy.SIMPLE, 200, 200);
		checkContent(text, BreakStrategy.SIMPLE, 200, 201);

		checkContent(text, BreakStrategy.BALANCED, 200, 1);
		checkContent(text, BreakStrategy.BALANCED, 200, 18);
		checkContent(text, BreakStrategy.BALANCED, 200, 100);
		checkContent(text, BreakStrategy.BALANCED, 200, 200);
		checkContent(text, BreakStrategy.BALANCED, 200, 201);

		System.out.println("used time: " + (System.currentTimeMillis() - timestamp));
	}

	private void checkContent(String text, BreakStrategy breakStrategy, float lineWidth, int textSize) {
		System.out.println("check content, width: " + lineWidth + " text size: " + textSize + " " + breakStrategy);

		LineAttributes lineAttributes = new LineAttributes(new LineAttributes.Attribute(lineWidth, Gravity.LEFT, 10));
		MockTextPaint paint = new MockTextPaint();
		Measurer measurer = new MockMeasurer(paint);
		paint.setMockTextSize(textSize);
		Option option = new MockOption(paint);

		Assert.assertNotEquals(option.getHyphenWidth(), 0);
		Assert.assertNotEquals(option.getIndentWidth(), 0);
		Assert.assertNotEquals(option.getSpaceShrink(), 0);
		Assert.assertNotEquals(option.getSpaceStretch(), 0);
		Assert.assertNotEquals(option.getSpaceWidth(), 0);

		Typesetter texTypesetter = new Typesetter();
		TextParser textParser = new TextParser();
		Document document = textParser.parse(text, measurer, Hypher.getInstance(), option);
		assertNotEquals(document.getCount(), 0);

		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < document.getCount(); ++i) {
			Segment segment = document.getSegment(i);
			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			texTypesetter.typeset(paragraph, lineAttributes, breakStrategy);
			assertNotNull(paragraph);
			assertNotEquals(paragraph.getLineCount(), 0);

			for (int j = 0; j < paragraph.getLineCount(); ++j) {
				Line l = paragraph.getLine(j);

				for (int x = 0; x < l.getCount(); ++x) {
					Box box = l.getBox(x);
					if (!(box instanceof TextBox)) {
						continue;
					}

					String content = box.toString();
					if (((TextBox) box).isPenalty()) {
						Assert.assertEquals(content.charAt(content.length() - 1), '-');
						content = content.substring(0, content.length() - 1);
					}
					stringBuilder.append(content);
				}
			}
		}

		String origin = text.replaceAll("\\p{Z}+|\\t|\\r|\\n", "");
		String current = stringBuilder.toString();
		assertEquals(origin, current);
	}

	@Test
	public void testMixTypesetter() {
		testMixNormal();
		testMixFull();
		testMix();
	}

	private void testMix() {
		Document document = mockDocument(20, 20, 100, BreakStrategy.SIMPLE, "123", "123");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		Paragraph paragraph = (Paragraph) document.getSegment(0);

		Assert.assertNotNull(paragraph);
		Assert.assertEquals(paragraph.getLineCount(), 2);

		Line line1 = paragraph.getLine(0);
		Line line2 = paragraph.getLine(1);

		Assert.assertEquals(line1.getCount(), 2);
		Assert.assertEquals(line1.getBox(0).getClass(), TextBox.class);
		Assert.assertEquals(line1.getBox(1).getClass(), DrawableBox.class);
		Assert.assertEquals(line2.getCount(), 2);
		Assert.assertEquals(line2.getBox(0).getClass(), TextBox.class);
		Assert.assertEquals(line2.getBox(1).getClass(), DrawableBox.class);

		document = mockDocument(20, 20, 100, BreakStrategy.BALANCED, "123", "123");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		paragraph = (Paragraph) document.getSegment(0);

		Assert.assertNotNull(paragraph);
		Assert.assertEquals(paragraph.getLineCount(), 2);

		line1 = paragraph.getLine(0);
		line2 = paragraph.getLine(1);

		Assert.assertEquals(line1.getCount(), 2);
		Assert.assertEquals(line1.getBox(0).getClass(), TextBox.class);
		Assert.assertEquals(line1.getBox(1).getClass(), DrawableBox.class);
		Assert.assertEquals(line2.getCount(), 2);
		Assert.assertEquals(line2.getBox(0).getClass(), TextBox.class);
		Assert.assertEquals(line2.getBox(1).getClass(), DrawableBox.class);
	}

	private void testMixNormal() {
		Document document = mockDocument(20, 20, 100, BreakStrategy.SIMPLE, "123");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		Paragraph paragraph = (Paragraph) document.getSegment(0);

		Assert.assertNotNull(paragraph);
		Assert.assertEquals(paragraph.getLineCount(), 1);

		Line line1 = paragraph.getLine(0);

		Assert.assertEquals(line1.getCount(), 2);
		Assert.assertEquals(line1.getBox(0).getClass(), TextBox.class);
		Assert.assertEquals(line1.getBox(1).getClass(), DrawableBox.class);

		document = mockDocument(20, 20, 100, BreakStrategy.BALANCED, "123");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		paragraph = (Paragraph) document.getSegment(0);

		Assert.assertNotNull(paragraph);
		Assert.assertEquals(paragraph.getLineCount(), 1);

		line1 = paragraph.getLine(0);

		Assert.assertEquals(line1.getCount(), 2);
		Assert.assertEquals(line1.getBox(0).getClass(), TextBox.class);
		Assert.assertEquals(line1.getBox(1).getClass(), DrawableBox.class);
	}

	private void testMixFull() {
		Document document = mockDocument(20, 20, 100, BreakStrategy.SIMPLE, "12345");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		Paragraph paragraph = (Paragraph) document.getSegment(0);

		Assert.assertNotNull(paragraph);
		Assert.assertEquals(paragraph.getLineCount(), 2);

		Line line1 = paragraph.getLine(0);
		Line line2 = paragraph.getLine(1);

		Assert.assertEquals(line2.getCount(), 1);
		Assert.assertEquals(line2.getBox(0).getClass(), DrawableBox.class);
		Assert.assertEquals(line1.getCount(), 1);
		Assert.assertEquals(line1.getBox(0).getClass(), TextBox.class);

		document = mockDocument(20, 20, 100, BreakStrategy.BALANCED, "12345");
		Assert.assertNotNull(document);
		Assert.assertEquals(document.getCount(), 1);
		Assert.assertEquals(document.getSegment(0).getClass(), Paragraph.class);
		paragraph = (Paragraph) document.getSegment(0);

		Assert.assertNotNull(paragraph);
		Assert.assertEquals(paragraph.getLineCount(), 2);

		line1 = paragraph.getLine(0);
		line2 = paragraph.getLine(1);

		Assert.assertEquals(line2.getCount(), 1);
		Assert.assertEquals(line2.getBox(0).getClass(), DrawableBox.class);
		Assert.assertEquals(line1.getCount(), 1);
		Assert.assertEquals(line1.getBox(0).getClass(), TextBox.class);
	}

	private Document mockDocument(int textSize, float drawableWidth, float width, BreakStrategy breakStrategy, CharSequence... s) {
		MockTextPaint textPaint = new MockTextPaint(textSize);
		Measurer measurer = new MockMeasurer(textPaint);
		Document document = Document.obtain();
		Option option = new MockOption(textPaint);
		Paragraph.Builder builder = Paragraph.Builder.newBuilder(measurer, Hypher.getInstance(), option, null);
		for (int i = 0; i < s.length; ++i) {
			builder.text(s[i], 0, s[i].length(), null, null, null, null);
			builder.drawable(new ColorDrawable(10), drawableWidth, 20);
		}
		document.addSegment(builder.build());

		LineAttributes lineAttributes = new LineAttributes(new LineAttributes.Attribute(width, Gravity.LEFT, option.getSpaceWidth()));

		Typesetter typesetter = new Typesetter();
		for (int i = 0; i < document.getCount(); ++i) {
			typesetter.typeset(document.getSegment(i), lineAttributes, breakStrategy);
		}

		return document;
	}
}