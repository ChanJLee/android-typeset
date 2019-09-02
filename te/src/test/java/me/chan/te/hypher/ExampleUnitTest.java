package me.chan.te.hypher;

import android.graphics.Paint;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.data.Element;
import me.chan.te.data.Option;
import me.chan.te.parser.TextParser;

import static org.junit.Assert.*;

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
		List<? extends Element> list = textParser.parser("hello world");
		assertNotEquals(list.size(), 0);
	}
}