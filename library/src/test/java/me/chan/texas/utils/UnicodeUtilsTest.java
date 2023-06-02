package me.chan.texas.utils;

import me.chan.texas.text.icu.UnicodeUtils;
import me.chan.texas.text.tokenizer.TokenStream;

import org.junit.Assert;
import org.junit.Test;

public class UnicodeUtilsTest {
	private static final int[] SPACES = {
			0x20,
			0xa0,
			0x1680,
			0x2000,
			0x2001,
			0x2002,
			0x2003,
			0x2004,
			0x2005,
			0x2006,
			0x2007,
			0x2008,
			0x2009,
			0x200a,
			0x202f,
			0x205f,
			0x3000
	};

	@Test
	public void test() {
		// EN
		Assert.assertFalse(UnicodeUtils.isCn('a'));
		Assert.assertFalse(UnicodeUtils.isCn('z'));
		Assert.assertFalse(UnicodeUtils.isCn('A'));
		Assert.assertFalse(UnicodeUtils.isCn('Z'));
		Assert.assertFalse(UnicodeUtils.isCn('0'));
		Assert.assertFalse(UnicodeUtils.isCn('9'));
		Assert.assertFalse(UnicodeUtils.isCn('-'));
		Assert.assertFalse(UnicodeUtils.isCn(','));

		Assert.assertTrue(UnicodeUtils.isCn('一'));

		Assert.assertTrue(UnicodeUtils.isSpace('　'));
		Assert.assertFalse(UnicodeUtils.isCn(','));

		Assert.assertTrue(UnicodeUtils.isSpace('　'));
		Assert.assertFalse(UnicodeUtils.isCn('　'));
		Assert.assertTrue(UnicodeUtils.isSpace('　'));
		Assert.assertTrue(UnicodeUtils.isSpace('　'));
		Assert.assertTrue(UnicodeUtils.isSpace('　'));
		Assert.assertTrue(UnicodeUtils.isSpace('　'));

		Assert.assertTrue(UnicodeUtils.isSpace(' '));
		Assert.assertFalse(UnicodeUtils.isCn(' '));
		Assert.assertTrue(UnicodeUtils.isSpace(' '));
		Assert.assertTrue(UnicodeUtils.isSpace(' '));
		Assert.assertTrue(UnicodeUtils.isSpace(' '));
		Assert.assertTrue(UnicodeUtils.isSpace(' '));

		Assert.assertTrue(UnicodeUtils.isLatinLetter('a'));
		Assert.assertTrue(UnicodeUtils.isLatinLetter('z'));
		Assert.assertTrue(UnicodeUtils.isLatinLetter('c'));
		Assert.assertTrue(UnicodeUtils.isLatinLetter('A'));
		Assert.assertTrue(UnicodeUtils.isLatinLetter('C'));
		Assert.assertTrue(UnicodeUtils.isLatinLetter('Z'));
		Assert.assertTrue(UnicodeUtils.isDigit('0'));
		Assert.assertTrue(UnicodeUtils.isDigit('5'));
		Assert.assertTrue(UnicodeUtils.isDigit('9'));
		Assert.assertFalse(UnicodeUtils.isLatinLetter(','));
	}

	@Test
	public void testSpace() {
		Assert.assertTrue(UnicodeUtils.isSpace(' '));
		for (int space : SPACES) {
			Assert.assertTrue(UnicodeUtils.isSpace(space));
		}
		Assert.assertFalse(UnicodeUtils.isSpace('\t'));
		Assert.assertFalse(UnicodeUtils.isSpace('\n'));
		Assert.assertFalse(UnicodeUtils.isSpace('a'));
		Assert.assertFalse(UnicodeUtils.isSpace('好'));
	}

	@Test
	public void testWhite() {
		Assert.assertTrue(UnicodeUtils.isWhitespace(' '));
		Assert.assertTrue(UnicodeUtils.isWhitespace('\t'));
		Assert.assertTrue(UnicodeUtils.isWhitespace('\n'));
		Assert.assertFalse(UnicodeUtils.isSpace('a'));
		Assert.assertFalse(UnicodeUtils.isSpace('好'));
	}

	@Test
	public void testPunctuation() {
		Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation('a'));
		for (int space : SPACES) {
			Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation(space));
		}

		String emoji = "😀";
		Assert.assertTrue(Character.isHighSurrogate(emoji.charAt(0)));
		Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation(emoji.charAt(0)));

		for (int cp : TokenStream.KINSOKU_AVOID_HEADER_MAP) {
			Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(cp));
		}
		for (int cp : TokenStream.KINSOKU_AVOID_TAIL_MAP) {
			Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(cp));
		}
		for (int cp : TokenStream.SQUISH_LEFT_MAP) {
			Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(cp));
		}
		for (int cp : TokenStream.SQUISH_RIGHT_MAP) {
			Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(cp));
		}
		for (int cp : TokenStream.STRETCH_LEFT_MAP) {
			Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(cp));
		}
		for (int cp : TokenStream.STRETCH_RIGHT_MAP) {
			Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(cp));
		}
	}

	@Test
	public void testCn() {
		for (int i = 'a'; i < 'z'; i++) {
			Assert.assertFalse(UnicodeUtils.isCn(i));
		}
		for (int i = 'A'; i < 'Z'; i++) {
			Assert.assertFalse(UnicodeUtils.isCn(i));
		}
		for (int space : SPACES) {
			Assert.assertFalse(UnicodeUtils.isCn(space));
		}
		String emoji = "😀";
		Assert.assertTrue(Character.isHighSurrogate(emoji.charAt(0)));
		Assert.assertFalse(UnicodeUtils.isCn(emoji.charAt(0)));

		for (int cp : TokenStream.KINSOKU_AVOID_HEADER_MAP) {
			Assert.assertFalse(UnicodeUtils.isCn(cp));
		}
		for (int cp : TokenStream.KINSOKU_AVOID_TAIL_MAP) {
			Assert.assertFalse(UnicodeUtils.isCn(cp));
		}
		for (int cp : TokenStream.SQUISH_LEFT_MAP) {
			Assert.assertFalse(UnicodeUtils.isCn(cp));
		}
		for (int cp : TokenStream.SQUISH_RIGHT_MAP) {
			Assert.assertFalse(UnicodeUtils.isCn(cp));
		}
		for (int cp : TokenStream.STRETCH_LEFT_MAP) {
			Assert.assertFalse(UnicodeUtils.isCn(cp));
		}
		for (int cp : TokenStream.STRETCH_RIGHT_MAP) {
			Assert.assertFalse(UnicodeUtils.isCn(cp));
		}

		Assert.assertFalse(UnicodeUtils.isCn('\t'));

		Assert.assertFalse(UnicodeUtils.isCn(0xc0));
		Assert.assertFalse(UnicodeUtils.isCn(0xff));
		Assert.assertFalse(UnicodeUtils.isCn(0x100));
		Assert.assertFalse(UnicodeUtils.isCn(0x17f));
		Assert.assertFalse(UnicodeUtils.isCn(0x180));
		Assert.assertFalse(UnicodeUtils.isCn(0x24f));

		Assert.assertFalse(UnicodeUtils.isCn('-'));
		Assert.assertFalse(UnicodeUtils.isCn('0'));
		Assert.assertFalse(UnicodeUtils.isCn('9'));
		Assert.assertTrue(UnicodeUtils.isCn('你'));
	}

	@Test
	public void testEn() {
		for (int i = 'a'; i < 'z'; i++) {
			Assert.assertTrue(UnicodeUtils.isLatinLetter(i));
		}
		for (int i = 'A'; i < 'Z'; i++) {
			Assert.assertTrue(UnicodeUtils.isLatinLetter(i));
		}
		for (int space : SPACES) {
			Assert.assertFalse(UnicodeUtils.isLatinLetter(space));
		}
		String emoji = "😀";
		Assert.assertTrue(Character.isHighSurrogate(emoji.charAt(0)));
		Assert.assertFalse(UnicodeUtils.isLatinLetter(emoji.charAt(0)));

		for (int cp : TokenStream.KINSOKU_AVOID_HEADER_MAP) {
			Assert.assertFalse(UnicodeUtils.isLatinLetter(cp));
		}
		for (int cp : TokenStream.KINSOKU_AVOID_TAIL_MAP) {
			Assert.assertFalse(UnicodeUtils.isLatinLetter(cp));
		}
		for (int cp : TokenStream.SQUISH_LEFT_MAP) {
			Assert.assertFalse(UnicodeUtils.isLatinLetter(cp));
		}
		for (int cp : TokenStream.SQUISH_RIGHT_MAP) {
			Assert.assertFalse(UnicodeUtils.isLatinLetter(cp));
		}
		for (int cp : TokenStream.STRETCH_LEFT_MAP) {
			Assert.assertFalse(UnicodeUtils.isLatinLetter(cp));
		}
		for (int cp : TokenStream.STRETCH_RIGHT_MAP) {
			Assert.assertFalse(UnicodeUtils.isLatinLetter(cp));
		}

		Assert.assertFalse(UnicodeUtils.isLatinLetter('\t'));

		Assert.assertTrue(UnicodeUtils.isLatinLetter(0xc0));
		Assert.assertTrue(UnicodeUtils.isLatinLetter(0xff));
		Assert.assertTrue(UnicodeUtils.isLatinLetter(0x100));
		Assert.assertTrue(UnicodeUtils.isLatinLetter(0x17f));
		Assert.assertTrue(UnicodeUtils.isLatinLetter(0x180));
		Assert.assertTrue(UnicodeUtils.isLatinLetter(0x24f));

		Assert.assertFalse(UnicodeUtils.isLatinLetter('-'));
		Assert.assertFalse(UnicodeUtils.isLatinLetter('0'));
		Assert.assertFalse(UnicodeUtils.isLatinLetter('9'));
		Assert.assertFalse(UnicodeUtils.isLatinLetter('你'));
	}

	@Test
	public void testIdeographic() {
		Assert.assertTrue(UnicodeUtils.isIdeographic('你'));
	}
}
