package me.chan.te.hypher;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

		result = new ArrayList<>();
		Hypher.getInstance().hyphenate("ok", result);
		System.out.println(result);

		result = new ArrayList<>();
		Hypher.getInstance().hyphenate("oktriangle", 2, "oktriangle".length() - 2, result);
		System.out.println(result);

		result = new ArrayList<>();
		Hypher.getInstance().hyphenate("oktriangle", 0, 2, result);
		System.out.println(result);

		result = new ArrayList<>();
		Hypher.getInstance().hyphenate("oktriangleok", 2, "oktriangleok".length() - 4, result);
		System.out.println(result);
	}
}