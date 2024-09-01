package me.chan.texas.text.tokenizer;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.ibm.icu.text.BreakIterator;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class WordReaderUnitTest {

	@Test
	public void testBase() throws IOException {
		for (int i = 1; i <= 6; ++i) {
			File file = new File("../app/src/main/assets/harry" + i + ".txt");
			System.out.println(file.getAbsolutePath());
			assertTrue(file.exists());

			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line)
						.append("\n");
			}

			String text = stringBuilder.toString();
			assertNotEquals(text.length(), 0);

			long timestamp = System.currentTimeMillis();

			StringBuilder builder = new StringBuilder();
			BreakIterator boundary = BreakIterator.getWordInstance();
			boundary.setText(text);
			int start = boundary.first();
			for (int end = boundary.next();
				 end != BreakIterator.DONE;
				 start = end, end = boundary.next()) {
				builder.append(text, start, end);
			}

			Assert.assertEquals(builder.toString(), text);

			System.out.println("used time: " + (System.currentTimeMillis() - timestamp) + ", text len: " + text.length());
		}
	}

	@Test
	public void test() throws IOException {
		WordStream stream = new WordStream();
		for (int i = 1; i <= 6; ++i) {
			File file = new File("../app/src/main/assets/harry" + i + ".txt");
			System.out.println(file.getAbsolutePath());
			assertTrue(file.exists());

			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line)
						.append("\n");
			}

			String text = stringBuilder.toString();
			assertNotEquals(text.length(), 0);

			long timestamp = System.currentTimeMillis();

			StringBuilder builder = new StringBuilder();
			stream.setText(text, 0, text.length());
			while (stream.next(new WordStream.Listener() {
				@Override
				public void onValue(CharSequence text, int start, int end, int reason) {
					builder.append(text, start, end);
				}
			})) {
				/* noop */
			}
			Assert.assertEquals(builder.toString(), text);

			System.out.println("used time: " + (System.currentTimeMillis() - timestamp) + ", text len: " + text.length());
		}
	}

	@Test
	public void testSimple() {
		WordStream stream = new WordStream();
		String msg = "0 2 4";
		stream.setText(msg, 0, msg.length());

		while (stream.next((text, start, end, reason) -> System.out.println(text.subSequence(start, end) + " -- " + reason)))
			;

		System.out.println("====================================");
		stream.setText(msg, 1, 3);
		while (stream.next((text, start, end, reason) -> System.out.println(text.subSequence(start, end) + "--" + reason)))
			;
	}

	@Test
	public void testSave() {
		String[] array = {"0", " ", "1", " ", "2", " ", "3", " ", "4", " ", "5", " ", "6", " ", "7", " ", "8", " ", "9"};
		List<String> list = new ArrayList<>();
		Collections.addAll(list, array);
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : list) {
			stringBuilder.append(s);
		}
		String text = stringBuilder.toString();

		WordStream stream = new WordStream();
		Assert.assertFalse(stream.prev(null));
		Assert.assertFalse(stream.next(null));
		Assert.assertEquals(stream.save(), 0);

		stream.setText(text, 0, text.length());
		Assert.assertFalse(stream.prev(null));
		Iterator<String> iterator = list.iterator();
		while (stream.next((text1, start, end, reason) -> {
			String except = iterator.next();
			String actual = text1.subSequence(start, end).toString();
			Assert.assertEquals(except, actual);
		})) ;
		Assert.assertEquals(stream.save(), list.size());

		// 倒序List
		Collections.reverse((List<?>) list);
		Iterator<String> iterator2 = list.iterator();
		while (stream.prev((text1, start, end, reason) -> {
			String except = iterator2.next();
			String actual = text1.subSequence(start, end).toString();
			Assert.assertEquals(except, actual);
		})) ;
		Assert.assertEquals(stream.save(), 0);
	}
}
