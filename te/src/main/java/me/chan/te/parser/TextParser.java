package me.chan.te.parser;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.data.Element;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Glue;
import me.chan.te.data.Penalty;
import me.chan.te.data.Segment;
import me.chan.te.hypher.Hypher;

public class TextParser implements Parser {

	private Hypher mHypher;
	private Option mOption;

	public TextParser(Hypher hypher, Option option) {
		mHypher = hypher;
		mOption = option;
	}

	@Override
	@NonNull
	public List<Segment> parser(CharSequence charSequence, ElementFactory factory) {
		List<Segment> segments = new ArrayList<>();
		int len = charSequence.length();
		for (int i = 0; i < len; ) {
			int last = findNewline(charSequence, i, len);
			if (i != last) {
				segments.add(new Segment(charSequence, i, last,
						parserLine(charSequence, factory, i, last)));
			}
			i = skipBlank(charSequence, last, len);
		}
		return segments;
	}

	private List<? extends Element> parserLine(CharSequence paragraph, ElementFactory factory, int start, int end) {
		List<Element> list = new ArrayList<>();
		List<String> hyphenated = new ArrayList<>();

		for (int i = start; i < end;) {
			int last = findWord(paragraph, i, end);
			int first = i;
			i = skipBlank(paragraph, last, end);
			int len = last - first;
			if (len <= 0) {
				continue;
			}

			mHypher.hyphenate(String.valueOf(paragraph), first, len, hyphenated);
			int size = hyphenated.size();
			if (size == 0 || len < mOption.minHyperLen) {
				list.add(factory.obtainBox(paragraph, first, last));
			} else {
				for (int j = 0; j < size; ++j) {
					String item = hyphenated.get(j);
					list.add(factory.obtainBox(item));
					if (j != size - 1 && !item.isEmpty() && item.charAt(item.length() - 1) != '-') {
						list.add(new Penalty(mOption.hyphenWidth, mOption.hyphenPenalty, true));
					}
				}
			}
			hyphenated.clear();

			list.add(new Glue(mOption.spaceWidth, mOption.spaceStretch, mOption.spaceShrink));
		}

		if (!list.isEmpty()) {
			list.remove(list.size() - 1);
		}

		list.add(new Glue(0, mOption.infinity, 0));
		list.add(new Penalty(0, -mOption.infinity, true));

		return list;
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
