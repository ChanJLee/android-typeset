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

	public static boolean isCJKExtends(int codePoint) {
		//CJK Unified Ideographs: U+4E00 - U+9FFF
		//CJK Unified Ideographs Extension A: U+3400 - U+4DBF
		//CJK Unified Ideographs Extension B: U+20000 - U+2A6DF
		//CJK Unified Ideographs Extension C: U+2A700 - U+2B73F
		//CJK Unified Ideographs Extension D: U+2B740 - U+2B81F
		//CJK Unified Ideographs Extension E: U+2B820 - U+2CEAF
		//CJK Unified Ideographs Extension F: U+2CEB0 - U+2EBEF
		//CJK Compatibility Ideographs: U+F900 - U+FAFF, U+FF00 – U+FFEF
		//CJK Radicals Supplement: U+2E80 - U+2EFF
		//Kangxi Radicals: U+2F00 - U+2FDF
		//Ideographic Description Characters: U+2FF0 - U+2FFF
		// 平假名（Hiragana）：U+3040 – U+309F
		// 片假名（Katakana）：U+30A0 – U+30FF
		// 半角片假名（Halfwidth Katakana）：U+FF60 – U+FF9F
		// 扩展片假名：U+31F0 – U+31FF
		// 韩文字母 (Hangul)：U+AC00 – U+D7AF：Hangul 音节
		// Hangul 字母: U+1100 – U+11FF
		return (codePoint >= 0x4e00 && codePoint <= 0x9fff) ||
				(codePoint >= 0x3400 && codePoint <= 0x4dbf) ||
				(codePoint >= 0x20000 && codePoint <= 0x2a6df) ||
				(codePoint >= 0x2a700 && codePoint <= 0x2b73f) ||
				(codePoint >= 0x2b740 && codePoint <= 0x2b81f) ||
				(codePoint >= 0x2b820 && codePoint <= 0x2ceaf) ||
				(codePoint >= 0x2ceb0 && codePoint <= 0x2ebef) ||
				(codePoint >= 0xf900 && codePoint <= 0xfaff) ||
				(codePoint >= 0x2e80 && codePoint <= 0x2eff) ||
				(codePoint >= 0x2f00 && codePoint <= 0x2fdf) ||
				(codePoint >= 0x2ff0 && codePoint <= 0x2fff) ||
				(codePoint >= 0x3040 && codePoint <= 0x309f) ||
				(codePoint >= 0x30a0 && codePoint <= 0x30ff) ||
				(codePoint >= 0xff60 && codePoint <= 0xff9f) ||
				(codePoint >= 0x31f0 && codePoint <= 0x31ff) ||
				(codePoint >= 0xac00 && codePoint <= 0xd7af) ||
				(codePoint >= 0x1100 && codePoint <= 0x11ff);
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

	/**
	 * @param codePoint unicode
	 * @return 是否是内部分割符
	 */
	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isControlCharacter(int codePoint) {
		// white 和 space 是有交叉的 0x20 就是
		/* 优先自主判断 */
		return codePoint == ' ' ||
				codePoint == '\n' ||
				codePoint == '\t' ||
				codePoint == '\r' ||
				isSpace(codePoint) ||
				isWhitespace(codePoint);
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

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isContextSensitiveCharacter(int c) {
		// Unicode ranges for Arabic, Hebrew, etc.
		// Arabic: \u0600 - \u06FF
		// Arabic Supplement: \u0750 - \u077F
		// Hebrew: \u0590 - \u05FF
		return ((c >= '\u0600' && c <= '\u06FF') ||  // Arabic
				(c >= '\u0750' && c <= '\u077F') ||  // Arabic Supplement
				(c >= '\u0590' && c <= '\u05FF'));  // Hebrew

	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static boolean isContextFreeCharacter(int c) {
		return !isContextSensitiveCharacter(c);
	}
}
