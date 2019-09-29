package me.chan.te;

import android.graphics.Rect;
import android.text.TextPaint;

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
import java.util.regex.Pattern;

import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.Box;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.parser.TextParser;
import me.chan.te.typesetter.TexTypesetter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
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
	public void testBox() {
		TextPaint textPaint = new MockTextPaint();
		textPaint.setTextSize(18);

		// width 976
		// text size 13

		ElementFactory elementFactory = new ElementFactory(new MockMeasurer());

		assertNull(elementFactory.obtainBox(null, 0, 10, null));
		try {
			elementFactory.obtainBox(null);
			fail("obtain null box");
		} catch (Throwable throwable) {

		}

		String msg1 = "hello world";
		String msg2 = "hello";
		Box box1 = elementFactory.obtainBox(msg1);
		Box box2 = elementFactory.obtainBox(msg2);

		assertEquals(msg1, box1.toString());
		assertEquals(msg2, box2.toString());

		box2.copy(box1);
		assertEquals(box2, box1);

		Box[] boxes = box2.spilt(textPaint, msg2.length() * MockTextPaint.MOCK_TEXT_SIZE);
		assertNotNull(boxes);
		assertNotNull(boxes[0]);
		assertNotNull(boxes[1]);
		assertEquals(boxes[0].toString(), msg2);
		assertEquals(boxes[1].toString(), msg1.substring(msg2.length()));

		boxes = box2.spilt(textPaint, MockTextPaint.MOCK_TEXT_SIZE);
		assertNotNull(boxes);
		assertNotNull(boxes[0]);
		assertNotNull(boxes[1]);
		assertEquals(boxes[0].toString(), "h");
		assertEquals(boxes[1].toString(), msg1.substring(1));

		boxes = box2.spilt(textPaint, MockTextPaint.MOCK_TEXT_SIZE * (msg1.length() - 1));
		assertNotNull(boxes);
		assertNotNull(boxes[0]);
		assertNotNull(boxes[1]);
		assertEquals(boxes[0].toString(), "hello worl");
		assertEquals(boxes[1].toString(), "d");

		boxes = box2.spilt(textPaint, -1);
		assertNull(boxes);

		boxes = box2.spilt(textPaint, 0);
		assertNull(boxes);

		boxes = box2.spilt(textPaint, msg1.length() * MockTextPaint.MOCK_TEXT_SIZE);
		assertNull(boxes);

		boxes = box2.spilt(textPaint, msg1.length() * MockTextPaint.MOCK_TEXT_SIZE + 1);
		assertNull(boxes);
	}

	@Test
	public void testMockTextPaint() {
		TextPaint textPaint = new MockTextPaint();
		textPaint.setTextSize(18);

		String msg = "hello";
		textPaint.getTextBounds(msg, 0, msg.length(), mRect);

		assertEquals(mRect.height(), MockTextPaint.MOCK_TEXT_HEIGHT);
		assertEquals(mRect.width(), MockTextPaint.MOCK_TEXT_SIZE * msg.length());

		msg = "";
		textPaint.getTextBounds(msg, 0, msg.length(), mRect);
	}

	@Test
	public void testTypesetter() {
		LineAttributes lineAttributes = new LineAttributes(new LineAttributes.Attribute(10));
		ElementFactory factory = new ElementFactory();
		TextPaint paint = new TextPaint();
		Option option = new Option(paint);
		TexTypesetter texTypesetter = new TexTypesetter(paint, option, factory);
		TextParser textParser = new TextParser(Hypher.getInstance(), option);
		List<Segment> segments = textParser.parser("hello\n\nworld\n\n", factory);
		Paragraph paragraph = texTypesetter.typeset(segments.get(0), lineAttributes, TexTypesetter.Policy.FILL);
		assertNotNull(paragraph);
		assertNotNull(paragraph.getLines());
	}

	@Test
	public void testLinkBreak() throws IOException {
		File file = new File("../app/src/main/assets/书剑恩仇录.txt");
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

		String msg = stringBuilder.toString();
		long timestamp = System.currentTimeMillis();
		Pattern pattern = Pattern.compile("\n");
		String[] spans = pattern.split(msg);
		System.out.println("pattern split used time: " + (System.currentTimeMillis() - timestamp));

		timestamp = System.currentTimeMillis();
		int len = msg.length();
		for (int i = 0; i < len; ++i) {
			if (msg.charAt(i) == '\n') {
				continue;
			}
		}
		System.out.println("loop used time: " + (System.currentTimeMillis() - timestamp));
	}
}