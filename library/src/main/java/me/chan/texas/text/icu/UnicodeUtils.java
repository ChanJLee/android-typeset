package me.chan.texas.text.icu;

import androidx.annotation.RestrictTo;

public class UnicodeUtils {
	/**
	 * cjkv字符
	 *
	 * @param codePoint unicode
	 * @return 是否是全角
	 */
	public static boolean isIdeographic(int codePoint) {
		/* 大部分的cjkv字符 */
		// https://www.compart.com/en/unicode/block/U+FF00
		return Character.isIdeographic(codePoint);
	}

	// Returns true if the character's presence could affect RTL layout.
	//
	// In order to be fast, the code is intentionally rough and quite conservative in its
	// considering inclusion of any non-BMP or surrogate characters or anything in the bidi
	// blocks or any bidi formatting characters with a potential to affect RTL layout.
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean couldAffectRtl(int c) {
		return (0x0590 <= c && c <= 0x08FF) ||  // RTL scripts
				c == 0x200E ||  // Bidi format character
				c == 0x200F ||  // Bidi format character
				(0x202A <= c && c <= 0x202E) ||  // Bidi format characters
				(0x2066 <= c && c <= 0x2069) ||  // Bidi format characters
				(0xD800 <= c && c <= 0xDFFF) ||  // Surrogate pairs
				(0xFB1D <= c && c <= 0xFDFF) ||  // Hebrew and Arabic presentation forms
				(0xFE70 <= c && c <= 0xFEFE);  // Arabic presentation forms
	}

	/**
	 * 判断 codePoint 是否属于需要按字符级切分的 CJK 系文字
	 * （汉字、日文假名、韩文谚文、CJK 部首/笔画/字符描述）。
	 *
	 * @param codePoint unicode
	 * @return 是否属于 CJK 系文字
	 */
	public static boolean isCJKScript(int codePoint) {
		// === Han 汉字 ===
		// CJK Unified Ideographs:                 U+4E00  - U+9FFF
		// CJK Unified Ideographs Extension A:     U+3400  - U+4DBF
		// CJK Unified Ideographs Extension B:     U+20000 - U+2A6DF
		// CJK Unified Ideographs Extension C:     U+2A700 - U+2B73F
		// CJK Unified Ideographs Extension D:     U+2B740 - U+2B81F
		// CJK Unified Ideographs Extension E:     U+2B820 - U+2CEAF
		// CJK Unified Ideographs Extension F:     U+2CEB0 - U+2EBEF
		// CJK Unified Ideographs Extension I:     U+2EBF0 - U+2EE5F (Unicode 15.1)
		// CJK Unified Ideographs Extension G:     U+30000 - U+3134F (Unicode 13.0)
		// CJK Unified Ideographs Extension H:     U+31350 - U+323AF (Unicode 15.0)
		// CJK Compatibility Ideographs:           U+F900  - U+FAFF
		// CJK Compatibility Ideographs Supplement:U+2F800 - U+2FA1F
		// === 部首 / 笔画 / 描述 ===
		// CJK Radicals Supplement:                U+2E80  - U+2EFF
		// Kangxi Radicals:                        U+2F00  - U+2FDF
		// Ideographic Description Characters:     U+2FF0  - U+2FFF
		// CJK Strokes:                            U+31C0  - U+31EF
		// === Kana 日文假名 ===
		// Hiragana:                               U+3040  - U+309F
		// Katakana:                               U+30A0  - U+30FF
		// Katakana Phonetic Extensions:           U+31F0  - U+31FF
		// Halfwidth Katakana:                     U+FF60  - U+FF9F
		// === Hangul 韩文谚文 ===
		// Hangul Jamo:                            U+1100  - U+11FF
		// Hangul Compatibility Jamo:              U+3130  - U+318F
		// Hangul Jamo Extended-A:                 U+A960  - U+A97F
		// Hangul Syllables:                       U+AC00  - U+D7AF
		// Hangul Jamo Extended-B:                 U+D7B0  - U+D7FF
		return
				// Han: Unified + Extension A
				(codePoint >= 0x4E00 && codePoint <= 0x9FFF) ||
				(codePoint >= 0x3400 && codePoint <= 0x4DBF) ||
				// Han: Extension B
				(codePoint >= 0x20000 && codePoint <= 0x2A6DF) ||
				// Han: Extensions C, D, E, F, I (contiguous)
				(codePoint >= 0x2A700 && codePoint <= 0x2EE5F) ||
				// Han: Extensions G, H (contiguous)
				(codePoint >= 0x30000 && codePoint <= 0x323AF) ||
				// Han: Compatibility Ideographs (BMP + Supplement)
				(codePoint >= 0xF900 && codePoint <= 0xFAFF) ||
				(codePoint >= 0x2F800 && codePoint <= 0x2FA1F) ||
				// 部首 / 笔画 / 描述
				(codePoint >= 0x2E80 && codePoint <= 0x2EFF) ||
				(codePoint >= 0x2F00 && codePoint <= 0x2FDF) ||
				(codePoint >= 0x2FF0 && codePoint <= 0x2FFF) ||
				(codePoint >= 0x31C0 && codePoint <= 0x31EF) ||
				// Kana
				(codePoint >= 0x3040 && codePoint <= 0x309F) ||
				(codePoint >= 0x30A0 && codePoint <= 0x30FF) ||
				(codePoint >= 0x31F0 && codePoint <= 0x31FF) ||
				(codePoint >= 0xFF60 && codePoint <= 0xFF9F) ||
				// Hangul: Jamo + Compatibility Jamo + Jamo Extended-A
				(codePoint >= 0x1100 && codePoint <= 0x11FF) ||
				(codePoint >= 0x3130 && codePoint <= 0x318F) ||
				(codePoint >= 0xA960 && codePoint <= 0xA97F) ||
				// Hangul: Syllables + Jamo Extended-B (contiguous)
				(codePoint >= 0xAC00 && codePoint <= 0xD7FF);
	}

	/**
	 * @deprecated 命名歧义且漏掉若干区段。改用 {@link #isCJKScript(int)}。
	 */
	@Deprecated
	public static boolean isCJKExtends(int codePoint) {
		return isCJKScript(codePoint);
	}


	/**
	 * use {@link #isLatinLetter(int)} instead
	 *
	 * @param codePoint unicode
	 * @return 是否是空白字符
	 */
	@Deprecated
	public static boolean isLetter(int codePoint) {
		return isLatinLetter(codePoint);
	}

	/**
	 * @param codePoint unicode
	 * @return 是否是拉丁字母 a-z A-Z
	 */
	public static boolean isLatinLetter(int codePoint) {
		/*
		 * 优先自主判断
		 * */
		return (codePoint >= 'a' && codePoint <= 'z') ||
				(codePoint >= 'A' && codePoint <= 'Z') ||
				isLetterLatin0(Character.getType(codePoint));
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isLetterLatin0(int type) {
		return type == Character.UPPERCASE_LETTER ||
				type == Character.LOWERCASE_LETTER;
	}

	/**
	 * @param codePoint unicode
	 * @return 是否是数字
	 */
	public static boolean isDigit(int codePoint) {
		// https://www.compart.com/en/unicode/block/U+0000
		return (0x30 <= codePoint && codePoint <= 0x39);
	}

	/**
	 * @param codePoint unicode
	 * @return 是否是符号
	 */
	public static boolean isSymbolsAndPunctuation(int codePoint) {
		final int type = Character.getType(codePoint);
		return isSymbolsAndPunctuation0(type);
	}

	private static boolean isSymbolsAndPunctuation0(int type) {
		return (type == Character.CONNECTOR_PUNCTUATION
				|| type == Character.DASH_PUNCTUATION
				|| type == Character.END_PUNCTUATION
				|| type == Character.FINAL_QUOTE_PUNCTUATION
				|| type == Character.INITIAL_QUOTE_PUNCTUATION
				|| type == Character.OTHER_PUNCTUATION
				|| type == Character.START_PUNCTUATION
				/* https://www.compart.com/en/unicode/category/Sm */
				|| type == Character.MATH_SYMBOL
				/* https://www.compart.com/en/unicode/category/Sc */
				|| type == Character.CURRENCY_SYMBOL
				/* https://www.compart.com/en/unicode/category/Sk */
				|| type == Character.MODIFIER_SYMBOL
				/* https://www.compart.com/en/unicode/category/So */
				|| type == Character.OTHER_SYMBOL
				/* https://www.compart.com/en/unicode/category/Lm */
				|| type == Character.MODIFIER_LETTER);
	}

	public static boolean isSpace(int codePoint) {
		// https://www.compart.com/en/unicode/category/Zs
		// https://www.compart.com/en/unicode/category/Zp
		// https://www.compart.com/en/unicode/category/Zl
		// etc
		return Character.isSpaceChar(codePoint);
	}

	public static boolean isWhitespace(int codePoint) {
		return Character.isWhitespace(codePoint);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isHyphen(int codePoint) {
		return codePoint == '-';
	}


	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isRTLCharacter(int c) {
		byte directionality = Character.getDirectionality(c);
		return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
				directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
	}
}
