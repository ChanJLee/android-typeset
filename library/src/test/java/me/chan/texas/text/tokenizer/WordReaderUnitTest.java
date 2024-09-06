package me.chan.texas.text.tokenizer;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.UnicodeSet;

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
		WordStream wordStream = new WordStream();
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

			wordStream.setText(text, 0, text.length(), true);
			StringBuilder builder = new StringBuilder();
			BreakIterator boundary = BreakIterator.getWordInstance();
			boundary.setText(text);
			int start = boundary.first();
			boundary.getRuleStatus();
			for (int end = boundary.next();
				 end != BreakIterator.DONE;
				 start = end, end = boundary.next()) {
				builder.append(text, start, end);
				int reason = boundary.getRuleStatus();
				Token token = wordStream.next();
				String actual = text.subSequence(start, end).toString();
				Assert.assertEquals(token.getCharSequence().subSequence(token.getStart(), token.getEnd()), actual);
				Assert.assertEquals(token.getReason(), reason);
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
			Token token = null;
			while ((token = stream.next()) != null) {
				builder.append(token.getCharSequence(), token.getStart(), token.getEnd());
			}
			Assert.assertEquals(builder.toString(), text);

			System.out.println("used time: " + (System.currentTimeMillis() - timestamp) + ", text len: " + text.length());
		}
	}

	@Test
	public void testSimple() {
		WordStream stream = new WordStream();
		Assert.assertNull(stream.next());
		Assert.assertNull(stream.tryGet(stream.save(), -1));
		String msg = "0 2 4";
		stream.setText(msg, 0, msg.length());
		Assert.assertNull(stream.tryGet(stream.save(), -1));
		Assert.assertNull(stream.tryGet(stream.save(), 5));
		Token token = null;
		token = stream.tryGet(stream.save(), 4);
		Assert.assertEquals(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString(), "4");

		while ((token = stream.next()) != null) {
			System.out.println(token.getCharSequence().subSequence(token.getStart(), token.getEnd()));
		}
		token = stream.tryGet(stream.save(), -5);
		Assert.assertEquals(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString(), "0");
		Assert.assertNull(stream.tryGet(stream.save(), -6));

		stream.reset();
		Assert.assertNull(stream.tryGet(stream.save(), -1));
		Assert.assertNull(stream.tryGet(stream.save(), 5));
		token = stream.tryGet(stream.save(), 4);
		Assert.assertEquals(token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString(), "4");

		System.out.println("====================================");
		stream.setText(msg, 1, 3);
		while ((token = stream.next()) != null) {
			System.out.println(token.getCharSequence().subSequence(token.getStart(), token.getEnd()));
		}
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
		Assert.assertNull(stream.next());
		Assert.assertEquals(stream.save(), 0);

		stream.setText(text, 0, text.length());

		int save = stream.save();
		Iterator<String> iterator = list.iterator();
		Token token = null;
		while ((token = stream.next()) != null) {
			String except = iterator.next();
			String actual = token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString();
			Assert.assertEquals(except, actual);
		}
		Assert.assertEquals(stream.save(), list.size());

		stream.restore(save);
		iterator = list.iterator();
		while ((token = stream.next()) != null) {
			String except = iterator.next();
			String actual = token.getCharSequence().subSequence(token.getStart(), token.getEnd()).toString();
			Assert.assertEquals(except, actual);
		}
		Assert.assertEquals(stream.save(), list.size());
	}

	@Test
	public void testWs() {
		BreakIterator boundary = WordStream.getWhiteSpaceBreakIterator();
		String text = "1 abc 3 4";
		boundary.setText(text);
		int start = boundary.first();
		boundary.getRuleStatus();
		for (int end = boundary.next();
			 end != BreakIterator.DONE;
			 start = end, end = boundary.next()) {
			int reason = boundary.getRuleStatus();
			String actual = text.subSequence(start, end).toString();
			System.out.println(actual);
		}
	}

	@Test
	public void unit() {
		UnicodeSet unicodeSet = new UnicodeSet("\\p{White_Space}");
		List<Integer> points = new ArrayList<>();
		unicodeSet.forEach(codePoint -> {
			if (codePoint.length() > 2) {
				throw new IllegalStateException("codePoint: " + codePoint);
			}
			points.add((int) codePoint.charAt(0));
		});
		points.sort((o1, o2) -> o1 - o2);

		StringBuilder stringBuilder = new StringBuilder("private static final int WHITE_SPACE = {");
		for (int i = 0; i < points.size(); ++i) {
			stringBuilder.append(String.format("0x%04x,", points.get(i).intValue()));
		}
		stringBuilder.append("};");
		System.out.println(stringBuilder.toString());
	}

	@Test
	public void testLong() {
		long v = addBrk(200, 2);
		int reason = (int) (v >>> 32);
		Assert.assertEquals(reason, 200);
	}

	private long addBrk(int reason, int index) {
		long v = reason;
		v <<= 32;
		v += index;
		return v;
	}

}
