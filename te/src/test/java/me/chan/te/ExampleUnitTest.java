package me.chan.te;

import android.text.TextPaint;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.data.LineAttribute;
import me.chan.te.data.LineAttributes;
import me.chan.te.data.Option;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.hypher.Hypher;
import me.chan.te.parser.TextParser;
import me.chan.te.typesetter.TexTypesetter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
	@Test
	public void addition_isCorrect() {
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
		TextParser textParser = new TextParser(Hypher.getInstance(), paint, option);
		List<? extends Element> list = textParser.parser("hello\n\nworld\n\n");
		assertNotEquals(list.size(), 0);
		for (Element element : list) {
			System.out.println(element);
		}
		assertEquals("check last: ", list.get(list.size() - 1).getClass(), Penalty.class);
		assertEquals("check last - 1: ", list.get(list.size() - 2).getClass(), Glue.class);
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

		TextPaint paint = new TextPaint();
		Option option = new Option(paint);
		TexTypesetter texTypesetter = new TexTypesetter(paint, option);
		TextParser textParser = new TextParser(Hypher.getInstance(), paint, option);
		List<? extends Element> list = textParser.parser("hello\n\nworld\n\n");
		Paragraph paragraph = texTypesetter.typeset(list, lineAttributes);
		assertNotNull(paragraph);
		assertNotNull(paragraph.getLines());
	}
}