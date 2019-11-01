package me.chan.te.parser;

import me.chan.te.text.Background;
import me.chan.te.text.Foreground;
import me.chan.te.text.Paragraph;
import me.chan.te.text.TextStyle;

public class PlainTextParserUtils {
	public static void parse(CharSequence paragraph, int start, int end, Paragraph.Builder builder) {
		parse(paragraph, start, end, builder, null, null, null, null);
	}

	public static void parse(CharSequence paragraph, int start, int end, Paragraph.Builder builder,
							 TextStyle textStyle, Background background, Foreground foreground, Object extra) {
		for (int i = start; i < end; ) {
			int last = findWord(paragraph, i, end);
			int first = i;
			i = skipBlank(paragraph, last, end);
			if (first == last) {
				continue;
			}

			builder.text(paragraph, first, last, textStyle, background, foreground, extra);
		}
	}

	public static int findWord(CharSequence charSequence, int start, int end) {
		return skip(charSequence, start, end, true);
	}

	public static int skipBlank(CharSequence charSequence, int start, int end) {
		return skip(charSequence, start, end, false);
	}

	public static int findNewline(CharSequence charSequence, int start, int end) {
		for (; start < end; ++start) {
			char ch = charSequence.charAt(start);
			if (ch == '\n') {
				break;
			}
		}
		return start;
	}

	public static int skip(CharSequence charSequence, int start, int end, boolean untilBlank) {
		for (; start < end; ++start) {
			char ch = charSequence.charAt(start);
			boolean isBlank = ch == ' ' || ch == '\t' ||
					ch == '\r' || ch == '\n';
			if (isBlank == untilBlank) {
				break;
			}
		}
		return start;
	}
}
