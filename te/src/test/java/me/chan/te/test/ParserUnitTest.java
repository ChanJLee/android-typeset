package me.chan.te.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import me.chan.te.data.Box;
import me.chan.te.text.Document;
import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.text.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.data.TextBox;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;
import me.chan.te.parser.TextParser;
import me.chan.te.test.mock.MockMeasurer;
import me.chan.te.test.mock.MockOption;
import me.chan.te.test.mock.MockTextPaint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ParserUnitTest {

	@Test
	public void testBase() {
		MockTextPaint paint = new MockTextPaint();
		paint.setTextSize(18);
		MockOption MockOption = new MockOption(paint);
		Measurer measurer = new MockMeasurer(paint);
		TextParser textParser = new TextParser();
		Document document = textParser.parse("hello\n\nworld\n\n", measurer, Hypher.getInstance(), MockOption);
		assertEquals(document.getCount(), 2);

		document = textParser.parse("hello\n\nworld\n", measurer, Hypher.getInstance(), MockOption);
		assertEquals(document.getCount(), 2);

		document = textParser.parse("", measurer, Hypher.getInstance(), MockOption);
		assertEquals(document.getCount(), 0);

		try {
			textParser.parse(null, measurer, Hypher.getInstance(), MockOption);
			Assert.fail("test parse null string");
		} catch (Exception e) {
			/* do nothing */
		}

		document = textParser.parse(" hello", measurer, Hypher.getInstance(), MockOption);
		assertEquals(document.getCount(), 1);


		Paragraph paragraph = document.getSegment(0);
		List<? extends Element> list = paragraph.getElements();
		assertEquals(list.size(), 3);

		assertEquals("check type, index 0", list.get(0).getClass(), TextBox.class);
		assertEquals("check type, index 1", list.get(1).getClass(), Glue.class);
		assertEquals("check type, index 2", list.get(2).getClass(), Penalty.class);

		Box box = (Box) list.get(0);
		assertEquals("check box content: ", box.toString(), "hello");

		document = textParser.parse(" triangle\n\n\n", measurer, Hypher.getInstance(), MockOption);
		assertEquals(document.getCount(), 1);

		paragraph = document.getSegment(0);
		list = paragraph.getElements();
		assertEquals(list.size(), 7);

		for (Element element : list) {
			System.out.println(element);
		}

		assertEquals("check type, index 0", list.get(0).getClass(), TextBox.class);
		assertEquals("check type, index 1", list.get(1).getClass(), Penalty.class);
		assertEquals("check type, index 2", list.get(2).getClass(), TextBox.class);
		assertEquals("check type, index 3", list.get(3).getClass(), Penalty.class);
		assertEquals("check type, index 4", list.get(4).getClass(), TextBox.class);
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
		MockTextPaint paint = new MockTextPaint();
		Measurer measurer = new MockMeasurer(paint);
		MockOption MockOption = new MockOption(paint);
		int lineNumber = 0;
		TextParser textParser = new TextParser();
		while ((line = bufferedReader.readLine()) != null) {
			++lineNumber;
			if (line == null || line.length() == 0) {
				continue;
			}

			String contentWithoutBlank = line.replaceAll("\\p{Z}+|\\t|\\r|\\n", "");
			StringBuilder stringBuilder = new StringBuilder();
			Document document = textParser.parse(line, measurer, Hypher.getInstance(), MockOption);
			if (contentWithoutBlank.isEmpty()) {
				Assert.assertEquals(document.getCount(), 0);
				continue;
			}

			Paragraph paragraph = document.getSegment(0);
			List<? extends Element> list = paragraph.getElements();
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
		MockTextPaint paint = new MockTextPaint();
		Measurer measurer = new MockMeasurer(paint);
		MockOption MockOption = new MockOption(paint);
		StringBuilder stringBuilder = new StringBuilder();
		TextParser textParser = new TextParser();
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}

		String content = stringBuilder.toString();
		stringBuilder = new StringBuilder();
		long timestamp = System.currentTimeMillis();
		Document document = textParser.parse(content, measurer, Hypher.getInstance(), MockOption);
		System.out.println("used time: " + (System.currentTimeMillis() - timestamp));
		for (int i = 0; i < document.getCount(); ++i) {
			for (Element element : document.getSegment(i).getElements()) {
				if (element instanceof Box) {
					stringBuilder.append(element);
				}
			}
		}
		assertEquals(content.replaceAll("\\p{Z}+|\\t|\\r|\\n", ""), stringBuilder.toString());
	}
}
