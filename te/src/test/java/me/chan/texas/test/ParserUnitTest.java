package me.chan.texas.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import me.chan.texas.text.RenderOption;
import me.chan.texas.text.Box;
import me.chan.texas.text.Document;
import me.chan.texas.text.Glue;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Penalty;
import me.chan.texas.text.TextBox;
import me.chan.texas.hypher.Hypher;
import me.chan.texas.measurer.Measurer;
import me.chan.texas.parser.TextParser;
import me.chan.texas.test.mock.MockMeasurer;
import me.chan.texas.test.mock.MockTextAttribute;
import me.chan.texas.test.mock.MockTextPaint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParserUnitTest {

	@Test
	public void testBase() {
		MockTextPaint paint = new MockTextPaint();
		paint.setTextSize(18);
		MockTextAttribute mockTextAttribute = new MockTextAttribute(paint);
		RenderOption renderOption = new RenderOption();
		Measurer measurer = new MockMeasurer(paint);
		TextParser textParser = new TextParser();
		Document document = textParser.parse("hello\n\nworld\n\n", measurer, Hypher.getInstance(), mockTextAttribute, renderOption);
		assertEquals(document.getSegmentCount(), 2);

		document = textParser.parse("hello\n\nworld\n", measurer, Hypher.getInstance(), mockTextAttribute, renderOption);
		assertEquals(document.getSegmentCount(), 2);

		document = textParser.parse("", measurer, Hypher.getInstance(), mockTextAttribute, renderOption);
		assertEquals(document.getSegmentCount(), 0);

		try {
			textParser.parse(null, measurer, Hypher.getInstance(), mockTextAttribute, renderOption);
			Assert.fail("test parse null string");
		} catch (Throwable e) {
			assertFalse(e instanceof AssertionError);
		}

		document = textParser.parse(" hello", measurer, Hypher.getInstance(), mockTextAttribute, renderOption);
		assertEquals(document.getSegmentCount(), 1);


		Paragraph paragraph = (Paragraph) document.getSegment(0);
		assertEquals(paragraph.getElementCount(), 3);

		assertEquals("check type, index 0", paragraph.getElement(0).getClass(), TextBox.class);
		assertEquals("check type, index 1", paragraph.getElement(1).getClass(), Glue.class);
		assertEquals("check type, index 2", paragraph.getElement(2).getClass(), Penalty.class);

		Box box = (Box) paragraph.getElement(0);
		assertEquals("check box content: ", box.toString(), "hello");

		document = textParser.parse(" triangle\n\n\n", measurer, Hypher.getInstance(), mockTextAttribute, renderOption);
		assertEquals(document.getSegmentCount(), 1);

		paragraph = (Paragraph) document.getSegment(0);
		assertEquals(paragraph.getElementCount(), 7);

		assertEquals("check type, index 0", paragraph.getElement(0).getClass(), TextBox.class);
		assertEquals("check type, index 1", paragraph.getElement(1).getClass(), Penalty.class);
		assertEquals("check type, index 2", paragraph.getElement(2).getClass(), TextBox.class);
		assertEquals("check type, index 3", paragraph.getElement(3).getClass(), Penalty.class);
		assertEquals("check type, index 4", paragraph.getElement(4).getClass(), TextBox.class);
		assertEquals("check type, index 5", paragraph.getElement(5).getClass(), Glue.class);
		assertEquals("check type, index 6", paragraph.getElement(6).getClass(), Penalty.class);

		box = (Box) paragraph.getElement(0);
		assertEquals("check box content: ", box.toString(), "tri");

		box = (Box) paragraph.getElement(2);
		assertEquals("check box content: ", box.toString(), "an");

		box = (Box) paragraph.getElement(4);
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
		RenderOption renderOption = new RenderOption();
		MockTextAttribute MockTextAttribute = new MockTextAttribute(paint);
		int lineNumber = 0;
		TextParser textParser = new TextParser();
		while ((line = bufferedReader.readLine()) != null) {
			++lineNumber;
			if (line == null || line.length() == 0) {
				continue;
			}

			String contentWithoutBlank = line.replaceAll("\\p{Z}+|\\t|\\r|\\n", "");
			StringBuilder stringBuilder = new StringBuilder();
			Document document = textParser.parse(line, measurer, Hypher.getInstance(), MockTextAttribute, renderOption);
			if (contentWithoutBlank.isEmpty()) {
				Assert.assertEquals(document.getSegmentCount(), 0);
				continue;
			}

			Paragraph paragraph = (Paragraph) document.getSegment(0);
			for (int i = 0; i < paragraph.getElementCount(); ++i) {
				Paragraph.Element element = paragraph.getElement(i);
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
		MockTextAttribute MockTextAttribute = new MockTextAttribute(paint);
		StringBuilder stringBuilder = new StringBuilder();
		TextParser textParser = new TextParser();
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}

		String content = stringBuilder.toString();
		stringBuilder = new StringBuilder();
		long timestamp = System.currentTimeMillis();
		RenderOption renderOption = new RenderOption();
		Document document = textParser.parse(content, measurer, Hypher.getInstance(), MockTextAttribute, renderOption);
		System.out.println("used time: " + (System.currentTimeMillis() - timestamp));
		for (int i = 0; i < document.getSegmentCount(); ++i) {
			Paragraph paragraph = (Paragraph) document.getSegment(i);
			for (int j = 0; j < paragraph.getElementCount(); ++j) {
				Paragraph.Element element = paragraph.getElement(j);
				if (element instanceof Box) {
					stringBuilder.append(element);
				}
			}
		}
		assertEquals(content.replaceAll("\\p{Z}+|\\t|\\r|\\n", ""), stringBuilder.toString());
	}
}
