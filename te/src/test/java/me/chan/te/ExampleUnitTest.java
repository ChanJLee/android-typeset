package me.chan.te;

import android.graphics.Rect;
import android.text.TextPaint;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;

import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.parser.TextParser;
import me.chan.te.typesetter.TexTypesetter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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
	}

	@Test
	public void testBox() {
		// width 976
		// text size 13
		ElementFactory elementFactory = new ElementFactory();
	}

	@Test
	public void testMockTextPaint() {
		TextPaint textPaint = new MockTextPaint();
		textPaint.setTextSize(18);

		String msg = "hello";
		textPaint.getTextBounds(msg, 0, msg.length(), mRect);

		// 100 x 100 square for available width and height
		when(mRect.width()).thenReturn(mRect.right - mRect.left);
		when(mRect.height()).thenReturn(mRect.bottom - mRect.top);

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