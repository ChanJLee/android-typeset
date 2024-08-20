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
			while (stream.next(builder::append)) {
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

		while (stream.next((text, start, end) -> System.out.println(text.subSequence(start, end))));

		System.out.println("====================================");
		stream.setText(msg, 1, 3);
		while (stream.next((text, start, end) -> System.out.println(text.subSequence(start, end))));
	}
}
