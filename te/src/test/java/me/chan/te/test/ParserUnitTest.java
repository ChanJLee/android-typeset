package me.chan.te.test;

import android.text.TextPaint;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Glue;
import me.chan.te.data.Penalty;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.parser.TextParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserUnitTest {

	@Test
	public void testBase() {
		TextPaint paint = new TextPaint();
		paint.setTextSize(18);
		Option option = new Option(paint);
		ElementFactory factory = new ElementFactory();
		TextParser textParser = new TextParser(Hypher.getInstance(), option);
		List<Segment> segments = textParser.parser("hello\n\nworld\n\n", factory);
		assertEquals(segments.size(), 2);

		segments = textParser.parser("hello\n\nworld\n", factory);
		assertEquals(segments.size(), 2);

		segments = textParser.parser("", factory);
		assertEquals(segments.size(), 0);

		try {
			textParser.parser(null, factory);
			Assert.fail("test parser null string");
		} catch (Exception e) {
			/* do nothing */
		}

		segments = textParser.parser(" hello", factory);
		assertEquals(segments.size(), 1);

		Segment segment = segments.get(0);
		List<? extends Element> list = segment.getElements();
		assertEquals(list.size(), 3);

		assertEquals("check type, index 0", list.get(0).getClass(), Box.class);
		assertEquals("check type, index 1", list.get(1).getClass(), Glue.class);
		assertEquals("check type, index 2", list.get(2).getClass(), Penalty.class);

		Box box = (Box) list.get(0);
		assertEquals("check box content: ", box.toString(), "hello");

		segments = textParser.parser(" triangle\n\n\n", factory);
		assertEquals(segments.size(), 1);

		segment = segments.get(0);
		list = segment.getElements();
		assertEquals(list.size(), 7);

		for (Element element : list) {
			System.out.println(element);
		}

		assertEquals("check type, index 0", list.get(0).getClass(), Box.class);
		assertEquals("check type, index 1", list.get(1).getClass(), Penalty.class);
		assertEquals("check type, index 2", list.get(2).getClass(), Box.class);
		assertEquals("check type, index 3", list.get(3).getClass(), Penalty.class);
		assertEquals("check type, index 4", list.get(4).getClass(), Box.class);
		assertEquals("check type, index 5", list.get(5).getClass(), Glue.class);
		assertEquals("check type, index 6", list.get(6).getClass(), Penalty.class);

		box = (Box) list.get(0);
		assertEquals("check box content: ", box.toString(), "tri");

		box = (Box) list.get(2);
		assertEquals("check box content: ", box.toString(), "an");

		box = (Box) list.get(4);
		assertEquals("check box content: ", box.toString(), "gle");
	}

	@Test
	public void testParserRes() throws IOException {
		File file = new File("../app/src/main/assets/TheBookAndTheSword.txt");
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
			if (contentWithoutBlank.isEmpty()) {
				assertTrue(segments.isEmpty());
				continue;
			}

			List<? extends Element> list = segments.get(0).getElements();
			for (Element element : list) {
				if (element instanceof Box) {
					stringBuilder.append(element);
				}
			}
			assertEquals("find exception at line: " + lineNumber,
					contentWithoutBlank, stringBuilder.toString());
		}
	}

	@Test
	public void testParserFull() throws IOException {
		File file = new File("../app/src/main/assets/TheBookAndTheSword.txt");
		System.out.println(file.getAbsolutePath());
		assertTrue(file.exists());

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		String line = null;
		ElementFactory factory = new ElementFactory();
		TextPaint paint = new TextPaint();
		Option option = new Option(paint);
		StringBuilder stringBuilder = new StringBuilder();
		TextParser textParser = new TextParser(Hypher.getInstance(), option);
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}

		String content = stringBuilder.toString();
		stringBuilder = new StringBuilder();
		List<Segment> segments = textParser.parser(content, factory);
		for (Segment segment : segments) {
			for (Element element : segment.getElements()) {
				if (element instanceof Box) {
					stringBuilder.append(element);
				}
			}
		}
		assertEquals(content.replaceAll("\\p{Z}+|\\t|\\r|\\n", ""), stringBuilder.toString());
	}
}
