package me.chan.te.parser;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import me.chan.te.config.Option;
import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.data.Penalty;
import me.chan.te.hypher.Hypher;

public class TextParser implements Parser {

	private static final Pattern BLANK_PATTERN = Pattern.compile("\\p{Z}+");
	private Hypher mHypher;
	private Option mOption;

	public TextParser(Hypher hypher, Option option) {
		mHypher = hypher;
		mOption = option;
	}

	@Override
	public List<? extends Element> parser(CharSequence paragraph) {
		List<Element> list = new ArrayList<>();
		List<String> hyphenated = new ArrayList<>();
		String[] spans = BLANK_PATTERN.split(paragraph);
		for (int i = 0; i < spans.length; ++i) {
			String span = spans[i];
			if (TextUtils.isEmpty(span)) {
				continue;
			}

			mHypher.hyphenate(span, hyphenated);
			int size = hyphenated.size();
			if (size == 0 || span.length() < mOption.minHyperLen) {
				list.add(new Box(span));
			} else {
				for (int j = 0; j < size; ++j) {
					String item = hyphenated.get(j);
					list.add(new Box(item));
					if (j != size - 1 && !item.isEmpty() && item.charAt(item.length() - 1) != '-') {
						list.add(new Penalty(mOption.hyphenWidth, mOption.hyphenPenalty, true));
					}
				}
				hyphenated.clear();
			}

			if (i == spans.length - 1) {
				list.add(new Glue(0, mOption.infinity, 0));
				list.add(new Penalty(0, -mOption.infinity, true));
			} else {
				list.add(new Glue(mOption.spaceWidth, mOption.spaceStretch, mOption.spaceShrink));
			}
		}
		return list;
	}
}
