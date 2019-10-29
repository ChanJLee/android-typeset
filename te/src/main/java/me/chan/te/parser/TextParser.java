package me.chan.te.parser;

import android.support.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.hypher.Hypher;
import me.chan.te.measurer.Measurer;

import static me.chan.te.parser.PlainTextParserUtils.findNewline;
import static me.chan.te.parser.PlainTextParserUtils.skipBlank;

public class TextParser implements Parser {
	@Override
	@NonNull
	public List<Segment> parse(CharSequence charSequence, Measurer measurer, Hypher hypher, Option option) {
		List<Segment> segments = new LinkedList<>();
		Segment.Builder builder = new Segment.Builder(measurer, hypher, option);
		int len = charSequence.length();
		for (int i = skipBlank(charSequence, 0, len); i < len; ) {
			int last = findNewline(charSequence, i, len);
			if (i != last) {
				builder.newSegment(charSequence, i, last);
				PlainTextParserUtils.parse(charSequence, i, last, builder);
				segments.add(builder.build());
			}
			i = skipBlank(charSequence, last, len);
		}
		return segments;
	}
}
