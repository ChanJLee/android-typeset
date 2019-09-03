package me.chan.te.hypher;

import android.graphics.Paint;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.data.Lines;
import me.chan.te.data.Option;
import me.chan.te.data.Penalty;
import me.chan.te.parser.TextParser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
	@Test
	public void addition_isCorrect() {
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
		Paint paint = new Paint();
		paint.setTextSize(18);
		Option option = new Option();
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
	public void testLines() {
		Lines lines = new Lines(10);
		lines.add(1, 20)
				.add(2, 30);

		assertEquals("check normal width", lines.get(10), 10);
		assertEquals("check special width", lines.get(1), 20);

		lines.remove(2);
		assertEquals("check remove", lines.get(2), 10);
	}
}