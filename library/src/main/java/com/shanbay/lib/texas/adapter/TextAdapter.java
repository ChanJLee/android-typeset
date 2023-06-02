package com.shanbay.lib.texas.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.TexasOption;
import com.shanbay.lib.texas.renderer.TexasView;
import com.shanbay.lib.texas.text.Document;
import com.shanbay.lib.texas.text.Paragraph;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

/**
 * 最简单的文本解析器
 */
@RestrictTo(LIBRARY)
public class TextAdapter extends TexasView.Adapter<CharSequence> {
	@Override
	@NonNull
	protected Document parse(CharSequence charSequence, TexasOption texasOption) {
		Document document = Document.obtain();
		int len = charSequence.length();
		for (int i = skipBlank(charSequence, 0, len); i < len; ) {
			int last = findNewline(charSequence, i, len);
			if (i != last) {
				Paragraph.Builder builder = Paragraph.Builder.newBuilder(texasOption);
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

	private static final char CN_SPACE = 0x3000;
	private static int skip(CharSequence charSequence, int start, int end, boolean untilBlank) {
		for (; start < end; ++start) {
			char ch = charSequence.charAt(start);
			boolean isBlank = ch == ' ' || ch == '\t' ||
					ch == '\r' || ch == '\n' || ch == CN_SPACE;
			if (isBlank == untilBlank) {
				break;
			}
		}
		return start;
	}
}
