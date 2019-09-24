package me.chan.te.parser;

import java.util.ArrayList;
import java.util.List;

import me.chan.te.config.Option;
import me.chan.te.data.Element;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Glue;
import me.chan.te.data.Penalty;
import me.chan.te.hypher.Hypher;

public class TextParser implements Parser {

	private Hypher mHypher;
	private Option mOption;

	public TextParser(Hypher hypher, Option option) {
		mHypher = hypher;
		mOption = option;
	}

	@Override
	public List<? extends Element> parser(CharSequence paragraph, ElementFactory factory) {
		int start = 0;
		int end = paragraph.length();
		List<Element> list = new ArrayList<>();
		List<String> hyphenated = new ArrayList<>();

		for (int i = start; i < end; ++i) {
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
		return skip(charSequence, start, end, false) - 1;
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
