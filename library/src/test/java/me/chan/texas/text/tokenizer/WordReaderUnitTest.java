package me.chan.texas.text.tokenizer;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class WordReaderUnitTest {

	@Test
	public void test() throws IOException {
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
			WordReader.read(text, 0, text.length(), (text1, start, end) -> builder.append(text1, start, end));

			Assert.assertEquals(builder.toString(), text);

			System.out.println("used time: " + (System.currentTimeMillis() - timestamp) + ", text len: " + text.length());
		}
	}
}
