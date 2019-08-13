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

		Bundle bundle = new Bundle();
		bundle.activeNodes = new ArrayList<>();
		bundle.lineLengths = lineLengths;
		bundle.option = option;
		bundle.paint = paint;
		bundle.sum = new Sum();

		// create elements
		bundle.elements = createElements(content, bundle);

		bundle.activeNodes.add(new Node(new Break(), null, null));
		for (int i = 0; i < bundle.elements.size(); ++i) {
			Element element = bundle.elements.get(i);
			if (element instanceof Box) {
				bundle.sum.width += element.width;
			} else if (element instanceof Glue) {
				if (i > 0 && bundle.elements.get(i - 1) instanceof Box) {
					mainLoop(i, bundle);
				}

				Glue glue = (Glue) element;
				bundle.sum.width += glue.width;
				bundle.sum.shrink += glue.shrink;
				bundle.sum.stretch += glue.stretch;
			} else if (element instanceof Penalty && ((Penalty) element).penalty != option.getInfinity()) {
				mainLoop(i, bundle);
			}
		}

		if (bundle.activeNodes.isEmpty()) {
			return breaks;
		}

		Node tempNode = null;
		for (Node node : bundle.activeNodes) {
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

	private static void mainLoop(int index, Bundle bundle) {
		Element element = bundle.elements.get(index);
		Node active = bundle.activeNodes.get(0);
		Node next = null;
		int currentLine = 0;
		float ratio = 0;
		Clazz candidates[] = null;
		while (active != null) {
			candidates = new Clazz[]{
					new Clazz(),
					new Clazz(),
					new Clazz(),
					new Clazz(),
			};

			while (active != null) {
				next = active.next;
				currentLine = active.data.line + 1;
				ratio = computeRatio(index, active.data, currentLine, bundle);

				if (ratio < -1 || (element instanceof Penalty && ((Penalty) element).penalty == -bundle.option.getInfinity())) {
					removeActiveNode(active, bundle);
				}


			}
		}
	}

	private static void removeActiveNode(Node node, Bundle bundle) {
		Node prev = node.prev;
		Node next = node.next;
		bundle.activeNodes.remove(node);

		if (prev != null) {
			prev.next = next;
		}

		if (next != null) {
			next.prev = prev;
		}
	}

	private static float computeRatio(int index, Break active, int currentLine, Bundle bundle) {
		float width = bundle.sum.width - active.totals.width;
		float stretch = 0;
		float shrink = 0;
		float lineLength = currentLine < bundle.lineLengths.size() ?
				bundle.lineLengths.get(currentLine) :
				bundle.lineLengths.get(bundle.lineLengths.size() - 1);
		Element element = bundle.elements.get(index);
		if (element instanceof Penalty) {
			width += element.width;
		}

		if (width < lineLength) {
			stretch = bundle.sum.stretch - active.totals.stretch;
			return stretch > 0 ? (lineLength - width) / stretch : bundle.option.getInfinity();
		} else if (width > lineLength) {
			shrink = bundle.sum.shrink - active.totals.shrink;
			return shrink > 0 ? (lineLength - width) / shrink : bundle.option.getInfinity();
		}

		// perfect match
		return 0;
	}

	private static List<Element> createElements(String content, Bundle bundle) {
		List<Element> elements = new ArrayList<>();
		Hypher hypher = Hypher.getInstance();
		String[] spans = content.split("\\s");
		for (int i = 0; i < spans.length; ++i) {
			String span = spans[i];
			if (TextUtils.isEmpty(span)) {
				// TODO 加入一个glue
				continue;
			}

			if (span.length() < bundle.option.getMinHyperLength()) {
				elements.add(new Box(bundle.paint.measureText(span), span));
				continue;
			}

			List<String> hyphenated = hypher.hyphenate(span);
			if (hyphenated.isEmpty() || hyphenated.size() == 1) {
				elements.add(new Box(bundle.paint.measureText(span), span));
				continue;
			}

			int size = hyphenated.size();
			for (int j = 0; j < size; ++j) {
				String item = hyphenated.get(j);
				elements.add(new Box(bundle.paint.measureText(item), item));
				if (j != size - 1) {
					elements.add(new Penalty(bundle.option.getHyphenWidth(), bundle.option.getHyphenPenalty(), true));
				}
			}

			if (i == spans.length - 1) {
				elements.add(new Glue(0, bundle.option.getInfinity(), 0));
				elements.add(new Penalty(0, -bundle.option.getInfinity(), true));
			} else {
				elements.add(new Glue(bundle.option.getSpaceWidth(), bundle.option.getSpaceStretch(), bundle.option.getSpaceShrink()));
			}
		}
		return elements;
	}

	public static class Bundle {
		public List<Float> lineLengths;
		public Option option;
		public Paint paint;
		public Sum sum;
		public List<Node> activeNodes;
		public List<Element> elements;
	}
}
