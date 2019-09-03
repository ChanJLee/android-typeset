package me.chan.te.parser;

import android.graphics.Paint;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.data.Option;
import me.chan.te.data.Penalty;
import me.chan.te.hypher.Hypher;

public class TextParser implements Parser {

	private static final Pattern NEWLINE_PATTERN = Pattern.compile("\n");
	private static final Pattern BLANK_PATTERN = Pattern.compile("\\p{Z}+");
	private Hypher mHypher;
	private Paint mPaint;
	private Option mOption;
	private float mHyphenWidth;
	private float mSpaceWidth;
	private float mSpaceStretch;
	private float mSpaceShrink;

	public TextParser(Hypher hypher, Paint paint, Option option) {
		mHypher = hypher;
		mPaint = paint;
		mOption = option;
		mHyphenWidth = paint.measureText("-");
		mSpaceWidth = paint.measureText(" ");
		mSpaceStretch = (mSpaceWidth * 3) / 6;
		mSpaceShrink = (mSpaceWidth * 3) / 9;
	}

	@Override
	public List<? extends Element> parser(CharSequence charSequence) {

		List<Element> list = new ArrayList<>();
		String[] paras = NEWLINE_PATTERN.split(charSequence);

		for (int i = 0; paras != null && i < paras.length; ++i) {
			handleParas(paras[i], list);
		}

		return list;
	}

	private void handleParas(String para, List<Element> list) {
		List<String> hyphenated = new ArrayList<>();
		String[] spans = BLANK_PATTERN.split(para);
		for (int i = 0; i < spans.length; ++i) {
			String span = spans[i];
			if (TextUtils.isEmpty(span)) {
				continue;
			}

			mHypher.hyphenate(span, hyphenated);
			int size = hyphenated.size();
			if (size == 0) {
				list.add(new Box<>(span, mPaint.measureText(span)));
			} else {
				for (int j = 0; j < size; ++j) {
					String item = hyphenated.get(j);
					list.add(new Box<>(item, mPaint.measureText(item)));
					if (j != size - 1 && !item.isEmpty() && item.charAt(item.length() - 1) != '-') {
						list.add(new Penalty(mHyphenWidth, mOption.hyphenPenalty, true));
					}
				}
			}

			if (i == spans.length - 1) {
				list.add(new Glue(0, mOption.infinity, 0));
				list.add(new Penalty(0, -mOption.infinity, true));
			} else {
				list.add(new Glue(mSpaceWidth, mSpaceStretch, mSpaceShrink));
			}
		}
	}
}
