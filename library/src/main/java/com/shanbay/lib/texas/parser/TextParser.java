package com.shanbay.lib.texas.parser;

import androidx.annotation.NonNull;

import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.hyphenation.Hyphenation;
import com.shanbay.lib.texas.measurer.Measurer;
import com.shanbay.lib.texas.renderer.RenderOption;
import com.shanbay.lib.texas.text.Document;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.TextAttribute;

/**
 * 最简单的文本解析器
 */
@Hidden
public class TextParser implements Parser<CharSequence> {
	@Override
	@NonNull
	public Document parse(CharSequence charSequence, Measurer measurer, Hyphenation hyphenation,
						  TextAttribute textAttribute, RenderOption renderOption) {
		Document document = Document.obtain();
		int len = charSequence.length();
		for (int i = skipBlank(charSequence, 0, len); i < len; ) {
			int last = findNewline(charSequence, i, len);
			if (i != last) {
				Paragraph.Builder builder = Paragraph.Builder.newBuilder(measurer, hyphenation, textAttribute);
				parse(charSequence, i, last, builder);
				document.addSegment(builder.build());
			}
			i = skipBlank(charSequence, last, len);
		}
		return document;
	}

	private static void parse(CharSequence paragraph, int start, int end, Paragraph.Builder builder) {
		for (int i = start; i < end; ) {
			int last = findWord(paragraph, i, end);
			int first = i;
			i = skipBlank(paragraph, last, end);
			if (first == last) {
				continue;
			}

			builder.text(paragraph, first, last);
		}
	}

	private static int findWord(CharSequence charSequence, int start, int end) {
		return skip(charSequence, start, end, true);
	}

	private static int skipBlank(CharSequence charSequence, int start, int end) {
		return skip(charSequence, start, end, false);
	}

	private static int findNewline(CharSequence charSequence, int start, int end) {
		for (; start < end; ++start) {
			char ch = charSequence.charAt(start);
			if (ch == '\n') {
				break;
			}
		}
		return start;
	}

	private static int skip(CharSequence charSequence, int start, int end, boolean untilBlank) {
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
