package me.chan.texas.utils;

import me.chan.texas.text.icu.UnicodeUtils;

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

	// ============================================================
	// 原始测试 — 保留作为兼容性 / smoke 用例
	// ============================================================

	@Test
	public void test() {
		// EN
		Assert.assertFalse(UnicodeUtils.isCJKExtends('a'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends('z'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends('A'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends('Z'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends('0'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends('9'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends('-'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(','));

		Assert.assertTrue(UnicodeUtils.isCJKExtends('一'));

		Assert.assertTrue(UnicodeUtils.isSpace('　'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(','));

		Assert.assertTrue(UnicodeUtils.isSpace('　'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends('　'));
		Assert.assertTrue(UnicodeUtils.isSpace('　'));
		Assert.assertTrue(UnicodeUtils.isSpace('　'));
		Assert.assertTrue(UnicodeUtils.isSpace('　'));
		Assert.assertTrue(UnicodeUtils.isSpace('　'));

		Assert.assertTrue(UnicodeUtils.isSpace(' '));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(' '));
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
	public void testCn() {
		for (int i = 'a'; i < 'z'; i++) {
			Assert.assertFalse(UnicodeUtils.isCJKExtends(i));
		}
		for (int i = 'A'; i < 'Z'; i++) {
			Assert.assertFalse(UnicodeUtils.isCJKExtends(i));
		}
		for (int space : SPACES) {
			Assert.assertFalse(UnicodeUtils.isCJKExtends(space));
		}
		String emoji = "😀";
		Assert.assertTrue(Character.isHighSurrogate(emoji.charAt(0)));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(emoji.charAt(0)));

		Assert.assertFalse(UnicodeUtils.isCJKExtends('\t'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0xc0));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0xff));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x100));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x17f));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x180));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x24f));

		Assert.assertFalse(UnicodeUtils.isCJKExtends('-'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends('0'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends('9'));
		Assert.assertTrue(UnicodeUtils.isCJKExtends('你'));
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
		Assert.assertTrue(UnicodeUtils.isCJKExtends('你'));
		Assert.assertTrue(UnicodeUtils.isCJKExtends('぀'));
		Assert.assertTrue(UnicodeUtils.isCJKExtends('゠'));
		Assert.assertTrue(UnicodeUtils.isCJKExtends('｠'));
		Assert.assertTrue(UnicodeUtils.isCJKExtends('ㇰ'));
	}

	// ============================================================
	// isCJKExtends — 各 Unicode 区段的边界
	// ============================================================

	@Test
	public void testIsCJK_unifiedIdeographs_boundaries() {
		// CJK Unified: U+4E00 - U+9FFF
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x4DFF));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x4E00));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x9FFF));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0xA000));
	}

	@Test
	public void testIsCJK_extensionA_boundaries() {
		// CJK Extension A: U+3400 - U+4DBF
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x33FF));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x3400));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x4DBF));
		// 4DC0-4DFF (Yi Radicals area) 不在 CJK 范围
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x4DC0));
	}

	@Test
	public void testIsCJK_extensionB_supplementary() {
		// CJK Extension B: U+20000 - U+2A6DF (supplementary plane)
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x1FFFF));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x20000));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2A6DF));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x2A6E0));
	}

	@Test
	public void testIsCJK_extensionsCDEF() {
		// Ext C: U+2A700 - U+2B73F
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2A700));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2B73F));
		// Ext D: U+2B740 - U+2B81F
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2B740));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2B81F));
		// Ext E: U+2B820 - U+2CEAF
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2B820));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2CEAF));
		// Ext F: U+2CEB0 - U+2EBEF
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2CEB0));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2EBEF));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x2EBF0));
	}

	@Test
	public void testIsCJK_compatibilityIdeographs() {
		// U+F900 - U+FAFF
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0xF8FF));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0xF900));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0xFAFF));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0xFB00));
	}

	@Test
	public void testIsCJK_radicalsAndKangxi() {
		// Radicals Supplement U+2E80 - U+2EFF
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2E80));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2EFF));
		// Kangxi Radicals U+2F00 - U+2FDF
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2F00));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2FDF));
		// Ideographic Description Characters U+2FF0 - U+2FFF
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2FF0));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x2FFF));
		// 区段间隙
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x2E7F));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x2FE0));
	}

	@Test
	public void testIsCJK_kanaRanges() {
		// Hiragana U+3040 - U+309F
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x303F));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x3040));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x309F));
		// Katakana U+30A0 - U+30FF
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x30A0));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x30FF));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x3100));
		// Halfwidth Katakana U+FF60 - U+FF9F
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0xFF60));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0xFF9F));
		// Katakana Phonetic Extensions U+31F0 - U+31FF
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x31F0));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x31FF));
	}

	@Test
	public void testIsCJK_hangul() {
		// Hangul Syllables U+AC00 - U+D7AF
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0xABFF));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0xAC00));
		Assert.assertTrue(UnicodeUtils.isCJKExtends('한')); // U+D55C
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0xD7AF));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0xD7B0));
		// Hangul Jamo U+1100 - U+11FF
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x10FF));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x1100));
		Assert.assertTrue(UnicodeUtils.isCJKExtends(0x11FF));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x1200));
	}

	@Test
	public void testIsCJK_outsideAllRanges() {
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x10FFFF));
		Assert.assertFalse(UnicodeUtils.isCJKExtends('a'));
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x05D0)); // Hebrew
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x0627)); // Arabic
		Assert.assertFalse(UnicodeUtils.isCJKExtends(0x0400)); // Cyrillic
	}

	// ============================================================
	// isIdeographic — Java 内置；超出 BMP 也应识别
	// ============================================================

	@Test
	public void testIsIdeographic_basicAndSupplementary() {
		Assert.assertTrue(UnicodeUtils.isIdeographic('你'));
		Assert.assertTrue(UnicodeUtils.isIdeographic('一'));
		// Ext B: U+20000 (𠀀) is also ideographic per Java
		Assert.assertTrue(UnicodeUtils.isIdeographic(0x20000));

		Assert.assertFalse(UnicodeUtils.isIdeographic('a'));
		Assert.assertFalse(UnicodeUtils.isIdeographic('0'));
		Assert.assertFalse(UnicodeUtils.isIdeographic('-'));
		// 平/片假名不是 Ideographic（属于 Java 的 Letter 类），但属于 isCJKExtends
		Assert.assertFalse(UnicodeUtils.isIdeographic(0x3040));
	}

	// ============================================================
	// isDigit — 仅 ASCII 0-9
	// ============================================================

	@Test
	public void testIsDigit_asciiOnly() {
		for (int c = '0'; c <= '9'; ++c) {
			Assert.assertTrue("expected digit at " + c, UnicodeUtils.isDigit(c));
		}
		// ASCII 边界：'/' = 0x2F, ':' = 0x3A
		Assert.assertFalse(UnicodeUtils.isDigit('/'));
		Assert.assertFalse(UnicodeUtils.isDigit(':'));
	}

	@Test
	public void testIsDigit_excludesNonAsciiDigits() {
		// 阿拉伯-印度数字 ٠-٩
		Assert.assertFalse(UnicodeUtils.isDigit(0x0660));
		// 全宽数字 ０-９
		Assert.assertFalse(UnicodeUtils.isDigit(0xFF10));
		// 圈号数字 ①
		Assert.assertFalse(UnicodeUtils.isDigit(0x2460));
		// 罗马数字 Ⅰ
		Assert.assertFalse(UnicodeUtils.isDigit(0x2160));
		// 中文数字
		Assert.assertFalse(UnicodeUtils.isDigit('一'));
	}

	// ============================================================
	// isHyphen — 仅 ASCII 连字符 0x2D
	// ============================================================

	@Test
	public void testIsHyphen_onlyAsciiHyphen() {
		Assert.assertTrue(UnicodeUtils.isHyphen('-'));
		Assert.assertTrue(UnicodeUtils.isHyphen(0x2D));

		// 其他破折/减号符号都不是
		Assert.assertFalse(UnicodeUtils.isHyphen(0x2010)); // hyphen
		Assert.assertFalse(UnicodeUtils.isHyphen(0x2013)); // en dash
		Assert.assertFalse(UnicodeUtils.isHyphen(0x2014)); // em dash
		Assert.assertFalse(UnicodeUtils.isHyphen(0x2212)); // minus sign
		Assert.assertFalse(UnicodeUtils.isHyphen('_'));
		Assert.assertFalse(UnicodeUtils.isHyphen(' '));
		Assert.assertFalse(UnicodeUtils.isHyphen('a'));
	}

	// ============================================================
	// couldAffectRtl — 各 RTL/双向区段及边界
	// ============================================================

	@Test
	public void testCouldAffectRtl_hebrewArabicBlock() {
		// [0x0590, 0x08FF]
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0x058F));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0x0590));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0x05D0));   // Hebrew alef
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0x0627));   // Arabic alef
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0x08FF));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0x0900));  // Devanagari
	}

	@Test
	public void testCouldAffectRtl_bidiFormatChars() {
		// LRM / RLM (单独点)
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0x200D));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0x200E));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0x200F));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0x2010));

		// LRE/RLE/PDF/LRO/RLO [0x202A, 0x202E]
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0x2029));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0x202A));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0x202E));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0x202F));

		// LRI/RLI/FSI/PDI [0x2066, 0x2069]
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0x2065));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0x2066));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0x2069));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0x206A));
	}

	@Test
	public void testCouldAffectRtl_surrogateRange() {
		// [0xD800, 0xDFFF] 整段都被保守地标记
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0xD7FF));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0xD800));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0xDFFF));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0xE000));
	}

	@Test
	public void testCouldAffectRtl_presentationForms() {
		// Hebrew/Arabic Presentation Forms-A [0xFB1D, 0xFDFF]
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0xFB1C));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0xFB1D));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0xFDFF));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0xFE00));

		// Arabic Presentation Forms-B [0xFE70, 0xFEFE]
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0xFE6F));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0xFE70));
		Assert.assertTrue(UnicodeUtils.couldAffectRtl(0xFEFE));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(0xFEFF));
	}

	@Test
	public void testCouldAffectRtl_nonRtl() {
		Assert.assertFalse(UnicodeUtils.couldAffectRtl('a'));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl('A'));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl('0'));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl(' '));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl('一'));
		Assert.assertFalse(UnicodeUtils.couldAffectRtl('-'));
	}

	// ============================================================
	// isSymbolsAndPunctuation
	// ============================================================

	@Test
	public void testIsSymbolsAndPunctuation_punctuationCategories() {
		// Pc / Pd / Ps / Pe
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('_'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('-'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('('));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(')'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('['));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(']'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('{'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('}'));

		// Po
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('!'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('?'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(','));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('.'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(':'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(';'));

		// Pi / Pf
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(0x201C)); // “
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(0x201D)); // ”
	}

	@Test
	public void testIsSymbolsAndPunctuation_symbolCategories() {
		// Sm
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('+'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('<'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('='));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('>'));

		// Sc
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('$'));
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(0x00A5)); // ¥
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(0x20AC)); // €

		// Sk
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation('^'));

		// So
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(0x00A9)); // ©
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(0x00AE)); // ®
	}

	@Test
	public void testIsSymbolsAndPunctuation_modifierLetter() {
		// Lm — 也被算作"符号/标点"
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(0x02B0)); // ʰ
		Assert.assertTrue(UnicodeUtils.isSymbolsAndPunctuation(0x02C8)); // ˈ
	}

	@Test
	public void testIsSymbolsAndPunctuation_excludesLettersDigitsSpacesCJK() {
		Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation('a'));
		Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation('Z'));
		Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation('0'));
		Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation('9'));
		Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation(' '));
		Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation(0x3000)); // 全角空格
		Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation('一'));
		Assert.assertFalse(UnicodeUtils.isSymbolsAndPunctuation('你'));
	}

	// ============================================================
	// isRTLCharacter — 基于 Java directionality
	// ============================================================

	@Test
	public void testIsRTLCharacter_hebrewIsRtl() {
		Assert.assertTrue(UnicodeUtils.isRTLCharacter(0x05D0)); // alef
		Assert.assertTrue(UnicodeUtils.isRTLCharacter(0x05E2)); // ayin
	}

	@Test
	public void testIsRTLCharacter_arabicIsRtlArabic() {
		Assert.assertTrue(UnicodeUtils.isRTLCharacter(0x0627)); // alef
		Assert.assertTrue(UnicodeUtils.isRTLCharacter(0x0645)); // meem
	}

	@Test
	public void testIsRTLCharacter_excludesNonRtlScripts() {
		Assert.assertFalse(UnicodeUtils.isRTLCharacter('a'));
		Assert.assertFalse(UnicodeUtils.isRTLCharacter('A'));
		Assert.assertFalse(UnicodeUtils.isRTLCharacter('0'));
		Assert.assertFalse(UnicodeUtils.isRTLCharacter(' '));
		Assert.assertFalse(UnicodeUtils.isRTLCharacter('一'));
		Assert.assertFalse(UnicodeUtils.isRTLCharacter('-'));
		Assert.assertFalse(UnicodeUtils.isRTLCharacter(0x0400)); // Cyrillic
	}

	// ============================================================
	// isLatinLetter / isLetter (deprecated) / isLetterLatin0
	// ============================================================

	@Test
	@SuppressWarnings("deprecation")
	public void testIsLetter_isAliasOfIsLatinLetter() {
		int[] samples = {'a', 'Z', '0', ',', ' ', 0xC0, 0xD7, '一', 0x05D0};
		for (int c : samples) {
			Assert.assertEquals("mismatch at U+" + Integer.toHexString(c),
				UnicodeUtils.isLatinLetter(c),
				UnicodeUtils.isLetter(c));
		}
	}

	@Test
	public void testIsLatinLetter_includesNonLatinScripts_byCategory() {
		// 函数名是 "Latin letter"，但实现基于 Character 的 UPPERCASE/LOWERCASE 分类。
		// 只要属于这两个分类，函数都返回 true（包括希腊、西里尔等字母）。
		Assert.assertTrue(UnicodeUtils.isLatinLetter(0x03B1)); // 希腊 α
		Assert.assertTrue(UnicodeUtils.isLatinLetter(0x0410)); // 西里尔 А
	}

	@Test
	public void testIsLatinLetter_excludesOtherLetterCategories() {
		// 中文 / 日文假名等是 OTHER_LETTER，不会被这个函数收录
		Assert.assertFalse(UnicodeUtils.isLatinLetter('一'));
		Assert.assertFalse(UnicodeUtils.isLatinLetter(0x3040)); // ぁ
		Assert.assertFalse(UnicodeUtils.isLatinLetter(0x30A0));
	}

	@Test
	public void testIsLatinLetter_excludesPunctuationDigitsSpaces() {
		Assert.assertFalse(UnicodeUtils.isLatinLetter('-'));
		Assert.assertFalse(UnicodeUtils.isLatinLetter(','));
		Assert.assertFalse(UnicodeUtils.isLatinLetter('.'));
		Assert.assertFalse(UnicodeUtils.isLatinLetter('0'));
		Assert.assertFalse(UnicodeUtils.isLatinLetter('9'));
		Assert.assertFalse(UnicodeUtils.isLatinLetter(' '));
		Assert.assertFalse(UnicodeUtils.isLatinLetter('\t'));
	}

	@Test
	public void testIsLetterLatin0_byCategoryConstants() {
		Assert.assertTrue(UnicodeUtils.isLetterLatin0(Character.UPPERCASE_LETTER));
		Assert.assertTrue(UnicodeUtils.isLetterLatin0(Character.LOWERCASE_LETTER));

		// 其它字母分类不在此范围
		Assert.assertFalse(UnicodeUtils.isLetterLatin0(Character.TITLECASE_LETTER));
		Assert.assertFalse(UnicodeUtils.isLetterLatin0(Character.MODIFIER_LETTER));
		Assert.assertFalse(UnicodeUtils.isLetterLatin0(Character.OTHER_LETTER));

		// 完全不相关的分类
		Assert.assertFalse(UnicodeUtils.isLetterLatin0(Character.DECIMAL_DIGIT_NUMBER));
		Assert.assertFalse(UnicodeUtils.isLetterLatin0(Character.SPACE_SEPARATOR));
	}

	// ============================================================
	// isSpace vs isWhitespace — 它们覆盖的字符集不同
	// ============================================================

	@Test
	public void testIsSpaceVsIsWhitespace_divergeOnNbsp() {
		// 不可换行空格 U+00A0 — Unicode Zs，但 Java 不当作 whitespace
		int nbsp = 0x00A0;
		Assert.assertTrue(UnicodeUtils.isSpace(nbsp));
		Assert.assertFalse(UnicodeUtils.isWhitespace(nbsp));
	}

	@Test
	public void testIsSpaceVsIsWhitespace_divergeOnTabAndNewline() {
		// 制表符 / 换行 — Java whitespace，但不是 Unicode space char (Z*)
		Assert.assertFalse(UnicodeUtils.isSpace('\t'));
		Assert.assertTrue(UnicodeUtils.isWhitespace('\t'));

		Assert.assertFalse(UnicodeUtils.isSpace('\n'));
		Assert.assertTrue(UnicodeUtils.isWhitespace('\n'));

		Assert.assertFalse(UnicodeUtils.isSpace('\r'));
		Assert.assertTrue(UnicodeUtils.isWhitespace('\r'));

		Assert.assertFalse(UnicodeUtils.isSpace(0x000B)); // VT
		Assert.assertTrue(UnicodeUtils.isWhitespace(0x000B));

		Assert.assertFalse(UnicodeUtils.isSpace('\f'));
		Assert.assertTrue(UnicodeUtils.isWhitespace('\f'));
	}

	@Test
	public void testIsSpace_excludesPlainCharacters() {
		Assert.assertFalse(UnicodeUtils.isSpace('a'));
		Assert.assertFalse(UnicodeUtils.isSpace('Z'));
		Assert.assertFalse(UnicodeUtils.isSpace('0'));
		Assert.assertFalse(UnicodeUtils.isSpace('-'));
		Assert.assertFalse(UnicodeUtils.isSpace('一'));
	}
}