package me.chan.te.test;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import me.chan.te.hypher.Hypher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HyphenUnitTest {

	@Test
	public void testBase() {
		List<String> result = new ArrayList<>();
		hyphenate("triangle", result);
		System.out.println(result);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0), "tri");
		assertEquals(result.get(1), "an");
		assertEquals(result.get(2), "gle");

		result = new ArrayList<>();
		hyphenate("ok", result);
		System.out.println(result);
		assertEquals(result.size(), 0);

		result = new ArrayList<>();
		hyphenate("oktriangle", 2, "oktriangle".length() - 2, result);
		System.out.println(result);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0), "tri");
		assertEquals(result.get(1), "an");
		assertEquals(result.get(2), "gle");

		result = new ArrayList<>();
		hyphenate("oktriangle", 0, 2, result);
		System.out.println(result);
		assertEquals(result.size(), 0);

		result = new ArrayList<>();
		hyphenate("oktriangleok", 2, "oktriangleok".length() - 4, result);
		System.out.println(result);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0), "tri");
		assertEquals(result.get(1), "an");
		assertEquals(result.get(2), "gle");

		result = new ArrayList<>();
		hyphenate("cos-triangleok", result);
		System.out.println(result);
		assertEquals(result.size(), 3);
		assertEquals(result.get(0), "cos-tri");
		assertEquals(result.get(1), "an");
		assertEquals(result.get(2), "gleok");

		result = new ArrayList<>();
		hyphenate("tri-angleok", result);
		System.out.println(result);
		assertEquals(result.size(), 2);
		assertEquals(result.get(0), "tri-an");
		assertEquals(result.get(1), "gleok");

		result = new ArrayList<>();
		hyphenate("", result);
		System.out.println(result);
		assertEquals(result.size(), 0);

		try {
			result = new ArrayList<>();
			hyphenate(null, result);
			Assert.fail("test null string");
		} catch (Exception e) {
			/* do nothing */
		}
	}

	@Test
	public void testHyphenRes() throws IOException {
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
			hyphenate(line, buffer);
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

	private void hyphenate(String text, List<String> result) {
		hyphenate(text, 0, text.length(), result);
	}

	private void hyphenate(String text, int start, int len, List<String> result) {
		Hypher hypher = Hypher.getInstance();
		List<Integer> list = new ArrayList<>();
		hypher.hyphenate(text, start, start + len, list);
		for (int p : list) {
			result.add(text.substring(start, p));
			start = p;
		}
	}
}
