package me.chan.typeset.core;

import android.graphics.Paint;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import me.chan.hypher.Hypher;
import me.chan.typeset.elements.Box;
import me.chan.typeset.elements.Element;
import me.chan.typeset.elements.Glue;
import me.chan.typeset.elements.Penalty;

public class Typeset {

	public static List<Break> linkbreak(String content, Option option, Paint paint) {
		List<Break> breaks = new ArrayList<>();
		List<Element> elements = createElements(content, option, paint);
		return breaks;
	}

	private static List<Element> createElements(String content, Option option, Paint paint) {
		List<Element> elements = new ArrayList<>();
		Hypher hypher = Hypher.getInstance();
		String[] spans = content.split("\\s");
		for (int i = 0; i < spans.length; ++i) {
			String span = spans[i];
			if (TextUtils.isEmpty(span)) {
				continue;
			}

			if (span.length() < option.getMinHyperLength()) {
				elements.add(new Box(paint.measureText(span), span));
				continue;
			}

			List<String> hyphenated = hypher.hyphenate(span);
			if (hyphenated.isEmpty() || hyphenated.size() == 1) {
				elements.add(new Box(paint.measureText(span), span));
				continue;
			}

			int size = hyphenated.size();
			for (int j = 0; j < size; ++j) {
				String item = hyphenated.get(j);
				elements.add(new Box(paint.measureText(item), item));
				if (j != size - 1) {
					elements.add(new Penalty(option.getHyphenWidth(), option.getHyphenPenalty(), true));
				}
			}

			if (i == spans.length - 1) {
				elements.add(new Glue(0, option.getInfinity(), 0));
				elements.add(new Penalty(0, -option.getInfinity(), true));
			} else {
				elements.add(new Glue(option.getSpaceWidth(), option.getSpaceStretch(), option.getSpaceShrink()));
			}
		}
		return elements;
	}
}
