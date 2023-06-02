package com.shanbay.lib.texas.renderer;

import com.shanbay.lib.texas.Texas;
import com.shanbay.lib.texas.TexasOption;
import com.shanbay.lib.texas.adapter.ParseException;
import com.shanbay.lib.texas.adapter.TextAdapter;
import com.shanbay.lib.texas.hyphenation.Hyphenation;
import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.source.SourceCloseException;
import com.shanbay.lib.texas.source.SourceOpenException;
import com.shanbay.lib.texas.measurer.MockMeasurer;
import com.shanbay.lib.texas.test.mock.MockTextAttribute;
import com.shanbay.lib.texas.test.mock.MockTextPaint;
import com.shanbay.lib.texas.text.layout.Box;
import com.shanbay.lib.texas.text.Document;
import com.shanbay.lib.texas.text.layout.Element;
import com.shanbay.lib.texas.text.layout.Glue;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.layout.Penalty;
import com.shanbay.lib.texas.text.layout.TextBox;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ParserUnitTest {

	@Test
	public void testEndElement() {
		Assert.assertEquals(Glue.TERMINAL.getShrink(), 0, 0);
		Assert.assertEquals(Glue.TERMINAL.getStretch(), Texas.INFINITY, 0);
		Assert.assertEquals(Glue.TERMINAL.getWidth(), 0, 0);

		Glue.TERMINAL.recycle();
		Assert.assertEquals(Glue.TERMINAL.getShrink(), 0, 0);
		Assert.assertEquals(Glue.TERMINAL.getStretch(), Texas.INFINITY, 0);
		Assert.assertEquals(Glue.TERMINAL.getWidth(), 0, 0);

		Assert.assertEquals(Penalty.FORCE_BREAK.getWidth(), 0, 0);
		Assert.assertEquals(Penalty.FORCE_BREAK.getHeight(), 0, 0);
		Assert.assertTrue(Penalty.FORCE_BREAK.isFlag());
		Assert.assertEquals(Penalty.FORCE_BREAK.getPenalty(), -Texas.INFINITY, 0);

		Penalty.FORCE_BREAK.recycle();
		Assert.assertEquals(Penalty.FORCE_BREAK.getWidth(), 0, 0);
		Assert.assertEquals(Penalty.FORCE_BREAK.getHeight(), 0, 0);
		Assert.assertTrue(Penalty.FORCE_BREAK.isFlag());
		Assert.assertEquals(Penalty.FORCE_BREAK.getPenalty(), -Texas.INFINITY, 0);
	}

	@Test
	public void testBase() throws InterruptedException, SourceCloseException, SourceOpenException, ParseException {
		MockTextPaint paint = new MockTextPaint();
		paint.setTextSize(18);
		MockTextAttribute mockTextAttribute = new MockTextAttribute(paint);
		RenderOption renderOption = new RenderOption();
		Measurer measurer = new MockMeasurer(paint);
		TextAdapter textParser = new TextAdapter();
		textParser.setData("hello\n\nworld\n\n");
		TexasOption texasOption = new TexasOption( Hyphenation.getInstance(),measurer, mockTextAttribute, renderOption);
		Document document = textParser.getDocument(texasOption);
		assertEquals(document.getSegmentCount(), 2);

		textParser.setData("hello\n\nworld\n");
		document = textParser.getDocument(texasOption);
		assertEquals(document.getSegmentCount(), 2);

		textParser.setData("");
		document = textParser.getDocument(texasOption);
		assertEquals(document.getSegmentCount(), 0);

		try {
			textParser.setData(null);
			textParser.getDocument(texasOption);
			Assert.fail("test parse null string");
		} catch (Throwable e) {
			assertFalse(e instanceof AssertionError);
		}

		textParser.setData(" hello");
		document = textParser.getDocument(texasOption);
		assertEquals(document.getSegmentCount(), 1);


		Paragraph paragraph = (Paragraph) document.getSegment(0);
		assertEquals(paragraph.getElementCount(), 3);

		assertEquals("check type, index 0", paragraph.getElement(0).getClass(), TextBox.class);
		assertEquals("check type, index 1", paragraph.getElement(1), Glue.TERMINAL);
		assertEquals("check type, index 2", paragraph.getElement(2), Penalty.FORCE_BREAK);

		Box box = (Box) paragraph.getElement(0);
		assertEquals("check box content: ", box.toString(), "hello");

		textParser.setData(" triangle\n\n\n");
		document = textParser.getDocument(texasOption);
		assertEquals(document.getSegmentCount(), 1);

		paragraph = (Paragraph) document.getSegment(0);
		assertEquals(paragraph.getElementCount(), 7);

		assertEquals("check type, index 0", paragraph.getElement(0).getClass(), TextBox.class);
		assertEquals("check type, index 1", paragraph.getElement(1).getClass(), Penalty.class);
		assertEquals("check type, index 2", paragraph.getElement(2).getClass(), TextBox.class);
		assertEquals("check type, index 3", paragraph.getElement(3).getClass(), Penalty.class);
		assertEquals("check type, index 4", paragraph.getElement(4).getClass(), TextBox.class);
		assertEquals("check type, index 5", paragraph.getElement(5), Glue.TERMINAL);
		assertEquals("check type, index 6", paragraph.getElement(6), Penalty.FORCE_BREAK);

		box = (Box) paragraph.getElement(0);
		assertEquals("check box content: ", box.toString(), "tri");

		box = (Box) paragraph.getElement(2);
		assertEquals("check box content: ", box.toString(), "an");

		box = (Box) paragraph.getElement(4);
		assertEquals("check box content: ", box.toString(), "gle");
	}

	@Test
	public void testParserRes() throws IOException, InterruptedException, SourceCloseException, SourceOpenException, ParseException {
		File file = new File("../app/src/main/assets/TheBookAndTheSword.txt");
		System.out.println(file.getAbsolutePath());
		assertTrue(file.exists());

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		String line = null;
		MockTextPaint paint = new MockTextPaint();
		Measurer measurer = new MockMeasurer(paint);
		RenderOption renderOption = new RenderOption();
		MockTextAttribute mockTextAttribute = new MockTextAttribute(paint);
		int lineNumber = 0;
		TextAdapter textParser = new TextAdapter();
		while ((line = bufferedReader.readLine()) != null) {
			++lineNumber;
			if (line == null || line.length() == 0) {
				continue;
			}

			String contentWithoutBlank = line.replaceAll("\\p{Z}+|\\t|\\r|\\n", "");
			StringBuilder stringBuilder = new StringBuilder();
			textParser.setData(line);
			TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), measurer, mockTextAttribute, renderOption);
			Document document = textParser.getDocument(texasOption);
			if (contentWithoutBlank.isEmpty()) {
				Assert.assertEquals(document.getSegmentCount(), 0);
				continue;
			}

			Paragraph paragraph = (Paragraph) document.getSegment(0);
			for (int i = 0; i < paragraph.getElementCount(); ++i) {
				Element element = paragraph.getElement(i);
				if (element instanceof Box) {
					stringBuilder.append(element);
				}
			}
			assertEquals("find exception at line: " + lineNumber,
					contentWithoutBlank, stringBuilder.toString());
		}
	}

	@Test
	public void testParserFull() throws IOException, InterruptedException, SourceCloseException, SourceOpenException, ParseException {
		File file = new File("../app/src/main/assets/TheBookAndTheSword.txt");
		System.out.println(file.getAbsolutePath());
		assertTrue(file.exists());

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		String line = null;
		MockTextPaint paint = new MockTextPaint();
		Measurer measurer = new MockMeasurer(paint);
		MockTextAttribute mocktextAttribute = new MockTextAttribute(paint);
		StringBuilder stringBuilder = new StringBuilder();
		TextAdapter textParser = new TextAdapter();
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}

		String content = stringBuilder.toString();
		stringBuilder = new StringBuilder();
		long timestamp = System.currentTimeMillis();
		RenderOption renderOption = new RenderOption();
		textParser.setData(content);
		TexasOption texasOption = new TexasOption(Hyphenation.getInstance(), measurer, mocktextAttribute, renderOption);
		Document document = textParser.getDocument(texasOption);
		System.out.println("used time: " + (System.currentTimeMillis() - timestamp));
		for (int i = 0; i < document.getSegmentCount(); ++i) {
			Paragraph paragraph = (Paragraph) document.getSegment(i);
			for (int j = 0; j < paragraph.getElementCount(); ++j) {
				Element element = paragraph.getElement(j);
				if (element instanceof Box) {
					stringBuilder.append(element);
				}
			}
		}
		assertEquals(content.replaceAll("\\p{Z}+|\\t|\\r|\\n", ""), stringBuilder.toString());
	}
}
