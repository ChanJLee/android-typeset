package me.chan.te.parser;

import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.data.Option;
import me.chan.te.data.Penalty;
import me.chan.te.hypher.Hypher;

public class TextParser implements Parser {

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
	public List<? extends Element> parser(CharSequence paragraph) {
		List<Element> list = new ArrayList<>();
		List<String> hyphenated = new ArrayList<>();
		String[] spans = BLANK_PATTERN.split(paragraph);
		Rect bound = new Rect();
		for (int i = 0; i < spans.length; ++i) {
			String span = spans[i];
			if (TextUtils.isEmpty(span)) {
				continue;
			}

			mHypher.hyphenate(span, hyphenated);
			int size = hyphenated.size();
			if (size == 0 || span.length() < mOption.minHyperLen) {
				mPaint.getTextBounds(span, 0, span.length(), bound);
				list.add(new Box<>(span, bound.width(), bound.height()));
			} else {
				for (int j = 0; j < size; ++j) {
					String item = hyphenated.get(j);
					mPaint.getTextBounds(item, 0, item.length(), bound);
					list.add(new Box<>(item, bound.width(), bound.height()));
					if (j != size - 1 && !item.isEmpty() && item.charAt(item.length() - 1) != '-') {
						list.add(new Penalty(mHyphenWidth, mOption.hyphenPenalty, true));
					}
				}
				hyphenated.clear();
			}

			if (i == spans.length - 1) {
				list.add(new Glue(0, mOption.infinity, 0));
				list.add(new Penalty(0, -mOption.infinity, true));
			} else {
				list.add(new Glue(mSpaceWidth, mSpaceStretch, mSpaceShrink));
			}
		}
		return list;
	}
}
