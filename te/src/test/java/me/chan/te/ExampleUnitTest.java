package me.chan.te;

import android.text.TextPaint;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import me.chan.te.config.LineAttribute;
import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Glue;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.parser.TextParser;
import me.chan.te.typesetter.TexTypesetter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
	@Test
	public void testFoo() {
		String prefix = "-";
		if (prefix != null && prefix.length() >= 1) {
			prefix = prefix.substring(0, prefix.length() - 1);
		}
		System.out.println(prefix);
		assertEquals(4, 2 + 2);
	}

	@Test
	public void testHyphen() {
		List<String> result = new ArrayList<>();
		Hypher.getInstance().hyphenate("triangle", result);
		System.out.println(result);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0), "tri");
		assertEquals(result.get(0), "an");
		assertEquals(result.get(0), "gle");

		result = new ArrayList<>();
		Hypher.getInstance().hyphenate("ok", result);
		System.out.println(result);
		assertEquals(result.size(), 0);

		result = new ArrayList<>();
		Hypher.getInstance().hyphenate("oktriangle", 2, "oktriangle".length() - 2, result);
		System.out.println(result);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0), "tri");
		assertEquals(result.get(0), "an");
		assertEquals(result.get(0), "gle");

		result = new ArrayList<>();
		Hypher.getInstance().hyphenate("oktriangle", 0, 2, result);
		System.out.println(result);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0), "tri");
		assertEquals(result.get(0), "an");
		assertEquals(result.get(0), "gle");

		result = new ArrayList<>();
		Hypher.getInstance().hyphenate("oktriangleok", 2, "oktriangleok".length() - 4, result);
		System.out.println(result);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0), "tri");
		assertEquals(result.get(0), "an");
		assertEquals(result.get(0), "gle");
	}

	@Test
	public void testParser() {
		TextPaint paint = new TextPaint();
		paint.setTextSize(18);
		Option option = new Option(paint);
		ElementFactory factory = new ElementFactory();
		TextParser textParser = new TextParser(Hypher.getInstance(), option);
		List<Segment> segments = textParser.parser("hello\n\nworld\n\n", factory);
		List<? extends Element> list = segments.get(0).getElements();
		assertNotEquals(list.size(), 0);
		assertEquals("check last: ", list.get(list.size() - 1).getClass(), Penalty.class);
		assertEquals("check last - 1: ", list.get(list.size() - 2).getClass(), Glue.class);


		segments = textParser.parser("cross-legged", factory);
		assertNotEquals(list.size(), 0);
		for (Element element : list) {
			System.out.println(element);
		}

		list = segments.get(0).getElements();
		assertEquals("check last: ", list.get(list.size() - 1).getClass(), Penalty.class);
		assertEquals("check last - 1: ", list.get(list.size() - 2).getClass(), Glue.class);
	}

	@Test
	public void testParserWithPenalty() {
		TextPaint paint = new TextPaint();
		paint.setTextSize(18);
		Option option = new Option(paint);
		ElementFactory factory = new ElementFactory();
		TextParser textParser = new TextParser(Hypher.getInstance(), option);
		List<Segment> segments = textParser.parser("triangle", factory);
		List<? extends Element> list = segments.get(0).getElements();
		assertNotEquals(list.size(), 0);
		for (Element element : list) {
			System.out.println(element);
		}

		assertEquals("check last: ", list.get(list.size() - 1).getClass(), Penalty.class);
		assertEquals("check last - 1: ", list.get(list.size() - 2).getClass(), Glue.class);
	}

	@Test
	public void testParserContent() throws IOException {
		File file = new File("../app/src/main/assets/书剑恩仇录.txt");
		System.out.println(file.getAbsolutePath());
		assertTrue(file.exists());

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		String line = null;
		ElementFactory factory = new ElementFactory();
		TextPaint paint = new TextPaint();
		Option option = new Option(paint);
		int lineNumber = 0;
		TextParser textParser = new TextParser(Hypher.getInstance(), option);
		while ((line = bufferedReader.readLine()) != null) {
			++lineNumber;
			if (line == null || line.length() == 0) {
				continue;
			}

			String contentWithoutBlank = line.replaceAll("\\p{Z}+|\\t|\\r|\\n", "");
			StringBuilder stringBuilder = new StringBuilder();
			List<Segment> segments = textParser.parser(line, factory);
			List<? extends Element> list = segments.get(0).getElements();
			for (Element element : list) {
				if (element instanceof Box) {
					stringBuilder.append(((Box) element).getContentForDebug());
				}
			}
			assertEquals("find exception at line: " + lineNumber,
					contentWithoutBlank, stringBuilder.toString());
		}
	}

	@Test
	public void testLinesAttributes() {
		LineAttributes lineAttributes = new LineAttributes(new LineAttribute(10));
		lineAttributes.add(1, new LineAttribute(20));
		lineAttributes.add(2, new LineAttribute(30));

		assertEquals(lineAttributes.get(10).getLineWidth(), 10f, 0);
		assertEquals(lineAttributes.get(1).getLineWidth(), 20f, 0);

		lineAttributes.remove(2);
		assertEquals(lineAttributes.get(2).getLineWidth(), 10f, 0);
	}

	@Test
	public void testTypesetter() {
		LineAttributes lineAttributes = new LineAttributes(new LineAttribute(10));
		ElementFactory factory = new ElementFactory();
		TextPaint paint = new TextPaint();
		Option option = new Option(paint);
		TexTypesetter texTypesetter = new TexTypesetter(paint, option, factory);
		TextParser textParser = new TextParser(Hypher.getInstance(), option);
		List<Segment> segments = textParser.parser("hello\n\nworld\n\n", factory);
		Paragraph paragraph = texTypesetter.typeset(segments.get(0), lineAttributes);
		assertNotNull(paragraph);
		assertNotNull(paragraph.getLines());
	}

	@Test
	public void testHypherResource() throws IOException {
		File file = new File("../app/src/main/assets/test_words.txt");
		System.out.println(file.getAbsolutePath());
		assertTrue(file.exists());

		int count = 0;
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		String line = null;
		List<String> buffer = new ArrayList<>();
		Hypher hypher = Hypher.getInstance();
		long timeStamp = System.currentTimeMillis();
		while ((line = bufferedReader.readLine()) != null) {
			++count;
			assertEquals(buffer.size(), 0);
			hypher.hyphenate(line, buffer);
			int size = buffer.size();
			for (int i = 0; i < size; ++i) {
				String word = buffer.get(i);
				Assert.assertFalse(word.isEmpty());
				Assert.assertFalse(line, word.charAt(word.length() - 1) == '-' && i != size - 1);
			}
			System.out.println();
			buffer.clear();
		}

		System.out.println("count: " + count + " used time(ms): " + (System.currentTimeMillis() - timeStamp));
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