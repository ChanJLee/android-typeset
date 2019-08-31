package me.chan.library;

import org.junit.Test;

import java.util.List;

import me.chan.hypher.Hypher;

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
	public void testHypher() {
		Hypher hypher = Hypher.getInstance();
		List<String> list = hypher.hyphenate("Pattern");
		System.out.println(list);

		list = hypher.hyphenate("OK");
		System.out.println(list);
	}

	@Test
	public void testFoo() {
		String msg = "hello  world";
		String[] arr = msg.split("\\s");
		for (String i : arr) {
			System.out.println(i + " len: " + i.length());
		}
	}

	@Test
	public void testStream() {

	}
}