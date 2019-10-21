package me.chan.te.test;

import android.graphics.Rect;

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
import java.util.List;

import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.Box;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Line;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.parser.TextParser;
import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockOption;
import me.chan.te.test.mock.MockTextPaint;
import me.chan.te.text.BreakStrategy;
import me.chan.te.typesetter.CoreTypesetter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
	public void testBox() {
		MockTextPaint textPaint = new MockTextPaint();
		textPaint.setTextSize(18);

		ElementFactory elementFactory = new ElementFactory(new MockMeasurer(textPaint));

		assertNull(elementFactory.obtainTextBox(null, 0, 10, null));
		try {
			elementFactory.obtainTextBox(null);
			fail("obtain null box");
		} catch (Throwable throwable) {

		}

		String msg1 = "hello world";
		String msg2 = "hello";
		Box box1 = elementFactory.obtainTextBox(msg1);
		Box box2 = elementFactory.obtainTextBox(msg2);

		assertEquals(msg1, box1.toString());
		assertEquals(msg2, box2.toString());


		assertNotEquals(box1.getWidth(textPaint), 0);
		assertNotEquals(box1.getHeight(textPaint), 0);


		box2.copy(box1);
		assertEquals(box2, box1);

		Box[] boxes = box2.spilt(textPaint, msg2.length() * textPaint.getMockTextSize());
		assertNotNull(boxes);
		assertNotNull(boxes[0]);
		assertNotNull(boxes[1]);
		assertEquals(boxes[0].toString(), msg2);
		assertEquals(boxes[1].toString(), msg1.substring(msg2.length()));

		boxes = box2.spilt(textPaint, textPaint.getMockTextSize());
		assertNotNull(boxes);
		assertNotNull(boxes[0]);
		assertNotNull(boxes[1]);
		assertEquals(boxes[0].toString(), "h");
		assertEquals(boxes[1].toString(), msg1.substring(1));

		boxes = box2.spilt(textPaint, textPaint.getMockTextSize() * (msg1.length() - 1));
		assertNotNull(boxes);
		assertNotNull(boxes[0]);
		assertNotNull(boxes[1]);
		assertEquals(boxes[0].toString(), "hello worl");
		assertEquals(boxes[1].toString(), "d");

		boxes = box2.spilt(textPaint, -1);
		assertNull(boxes);

		boxes = box2.spilt(textPaint, 0);
		assertNull(boxes);

		boxes = box2.spilt(textPaint, msg1.length() * textPaint.getMockTextSize());
		assertNull(boxes);

		boxes = box2.spilt(textPaint, msg1.length() * textPaint.getMockTextSize() + 1);
		assertNull(boxes);
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

		LineAttributes lineAttributes = new LineAttributes(new LineAttributes.Attribute(lineWidth));
		MockTextPaint paint = new MockTextPaint();
		ElementFactory factory = new ElementFactory(new MockMeasurer(paint));
		paint.setMockTextSize(textSize);
		Option option = new MockOption(paint);

		Assert.assertNotEquals(option.getHyphenWidth(), 0);
		Assert.assertNotEquals(option.getIndentWidth(), 0);
		Assert.assertNotEquals(option.getLineSpacing(), 0);
		Assert.assertNotEquals(option.getSpaceShrink(), 0);
		Assert.assertNotEquals(option.getSpaceStretch(), 0);
		Assert.assertNotEquals(option.getSpaceWidth(), 0);

		CoreTypesetter texTypesetter = new CoreTypesetter(paint, factory);
		TextParser textParser = new TextParser();
		List<Segment> segments = textParser.parser(text, factory, Hypher.getInstance(), option);
		assertFalse(segments.isEmpty());

		StringBuilder stringBuilder = new StringBuilder();
		for (Segment segment : segments) {
			Paragraph paragraph = texTypesetter.typeset(segment, lineAttributes, breakStrategy);
			assertNotNull(paragraph);
			assertNotNull(paragraph.getLines());
			assertFalse(paragraph.getLines().isEmpty());

			for (Line l : paragraph.getLines()) {
				for (Box box : l.getBoxes()) {
					String content = box.toString();
					if (box.isPenalty()) {
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
}