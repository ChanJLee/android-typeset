package me.chan.te.parser;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;

public class TextParser implements Parser {
	@Override
	@NonNull
	public List<Segment> parser(CharSequence charSequence, Measurer measurer, Hypher hypher, Option option) {
		List<Segment> segments = new LinkedList<>();
		int len = charSequence.length();
		for (int i = skipBlank(charSequence, 0, len); i < len; ) {
			int last = findNewline(charSequence, i, len);
			if (i != last) {
				segments.add(parseSegment(charSequence, i, last,
						new Segment.Builder(charSequence, i, last, measurer, hypher, option)));
			}
			i = skipBlank(charSequence, last, len);
		}
		return segments;
	}

	private Segment parseSegment(CharSequence paragraph, int start, int end, Segment.Builder builder) {
		for (int i = start; i < end; ) {
			int last = findWord(paragraph, i, end);
			int first = i;
			i = skipBlank(paragraph, last, end);
			if (first == last) {
				continue;
			}

			builder.text(paragraph, first, last);
		}
		return builder.build();
	}

	private int findWord(CharSequence charSequence, int start, int end) {
		return skip(charSequence, start, end, true);
	}

	private int skipBlank(CharSequence charSequence, int start, int end) {
		return skip(charSequence, start, end, false);
	}

	private int findNewline(CharSequence charSequence, int start, int end) {
		for (; start < end; ++start) {
			char ch = charSequence.charAt(start);
			if (ch == '\n') {
				break;
			}
		}
		return start;
	}

	private int skip(CharSequence charSequence, int start, int end, boolean untilBlank) {
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
