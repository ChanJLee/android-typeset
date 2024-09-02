package me.chan.texas.utils;

import android.icu.lang.UCharacter;
import android.icu.lang.UProperty;

import com.ibm.icu.text.Transliterator;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.text.UnicodeSetIterator;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class UCD {

	@Test
	public void testPs() throws IOException {
		write("[[:Ps:]]", "PsUnicodeSet");
	}

	@Test
	public void testPe() throws IOException {
		write("[[:Pe:]]", "PeUnicodeSet");
	}

	private void write(String pattern, String name) throws IOException {
		UnicodeSet unicodeSet = new UnicodeSet(pattern);
		// 遍历所有的字符
		UnicodeSetIterator iterator = new UnicodeSetIterator(unicodeSet);
		// 全角转半角
		List<Entry> entries = new java.util.ArrayList<>();
		while (iterator.next()) {
			int codepoint = iterator.codepoint;
			String str = new String(Character.toChars(codepoint));
			if (str.length() > 2) {
				throw new IllegalArgumentException("codepoint: " + codepoint + ", str: " + str);
			}
			entries.add(new Entry(codepoint, str));
		}

		entries.sort((o1, o2) -> Integer.compare(o1.code, o2.code));

		File file = new File("../library/src/main/java/me/chan/texas/text/icu/" + name + ".java");
		StringWriter writer = new StringWriter();
		writer.write("package me.chan.texas.text.icu;\n\n");
		writer.write("/* AUTO GEN, DO NOT MODIFY IT! */\n");
		writer.write("public class " + name + " {\n\n");
		writer.write("\tpublic final char[] HALF_WIDTH_CLOSE_PUNCTUATION = new char[] {\n");
		for (Entry entry : entries) {
			int esw = UCharacter.getIntPropertyValue(entry.raw.charAt(0), UProperty.EAST_ASIAN_WIDTH);
//			if (esw == UCharacter.EastAsianWidth.HALFWIDTH) {
				writer.write(String.format("\t\t\t'%s', /*U+%x*/\n", entry.raw, entry.code));
//			}
		}
		writer.write("};\n\n");

		writer.write("\tpublic final char[] FULL_WIDTH_CLOSE_PUNCTUATION = new char[] {\n");
		for (Entry entry : entries) {
			int esw = UCharacter.getIntPropertyValue(entry.raw.charAt(0), UProperty.EAST_ASIAN_WIDTH);
//			if (esw == UCharacter.EastAsianWidth.FULLWIDTH) {
				writer.write(String.format("\t\t\t'%s', /*U+%x*/\n", entry.raw, entry.code));
//			}
		}
		writer.write("};\n");
		writer.write("}\n");
		FileOutputStream os = new FileOutputStream(file);
		os.write(writer.toString().getBytes());
		os.flush();
		os.close();

		System.out.println(file.getAbsolutePath());
	}

	public static class Entry {
		int code;
		String raw;

		public Entry(int code, String raw) {
			this.code = code;
			this.raw = raw;
		}
	}
}
