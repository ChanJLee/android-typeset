package me.chan.typeset.core;

import android.graphics.Paint;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.chan.hypher.Hypher;
import me.chan.typeset.elements.Box;
import me.chan.typeset.elements.Element;
import me.chan.typeset.elements.Glue;
import me.chan.typeset.elements.Penalty;

public class Typeset {

	public static List<Break> linkBreak(String content, List<Float> lineLengths, Option option, Paint paint) {
		List<Break> breaks = new ArrayList<>();

		// create elements
		List<Element> elements = createElements(content, option, paint);

		Sum sum = new Sum();
		List<Node> activeNodes = new ArrayList<>();
		activeNodes.add(new Node(new Break(), null, null));
		for (int i = 0; i < elements.size(); ++i) {
			Element element = elements.get(i);
			if (element instanceof Box) {
				sum.width += element.width;
			} else if (element instanceof Glue) {
				if (i > 0 && elements.get(i - 1) instanceof Box) {
					mainLoop(element, i, elements);
				}

				Glue glue = (Glue) element;
				sum.width += glue.width;
				sum.shrink += glue.shrink;
				sum.stretch += glue.stretch;
			} else if (element instanceof Penalty && ((Penalty) element).penalty != option.getInfinity()) {
				mainLoop(element, i, elements);
			}
		}

		if (activeNodes.isEmpty()) {
			return breaks;
		}

		Node tempNode = null;
		for (Node node : activeNodes) {
			if (tempNode == null || tempNode.data.demerits >= node.data.demerits) {
				tempNode = node;
			}
		}

		while (tempNode != null) {
			// TODO 确定break的数据类型
			breaks.add(new Break());
		}

		Collections.reverse(breaks);
		return breaks;
	}

	private static void mainLoop(Element element, int index, List<Element> elements) {

	}

	private static List<Element> createElements(String content, Option option, Paint paint) {
		List<Element> elements = new ArrayList<>();
		Hypher hypher = Hypher.getInstance();
		String[] spans = content.split("\\s");
		for (int i = 0; i < spans.length; ++i) {
			String span = spans[i];
			if (TextUtils.isEmpty(span)) {
				// TODO 加入一个glue
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
