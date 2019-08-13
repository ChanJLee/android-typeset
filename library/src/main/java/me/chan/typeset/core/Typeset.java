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

	private static final int CLASS_0 = 0;
	private static final int CLASS_1 = 1;
	private static final int CLASS_2 = 2;
	private static final int CLASS_3 = 3;

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

				if (ratio >= -1 && ratio <= bundle.option.getTolerance()) {
					int currentClass = computeClazz(ratio);

					float demerits = computeDemerits(element, ratio, bundle, active, currentClass);

					// Only store the best candidate for each fitness class
					if (demerits < candidates[currentClass].demerits) {
						candidates[currentClass].active = active;
						candidates[currentClass].demerits = demerits;
						candidates[currentClass].ratio = ratio;
					}
				}

				active = next;

				if (active != null && active.data.line >= currentLine) {
					break;
				}
			}

			Sum tmpSum = computeSum(index, bundle);

			for (int i = 0; i < candidates.length; ++i) {
				Clazz candidate = candidates[i];

				if (candidate.demerits < bundle.option.getInfinity()) {
					Break point = new Break();
					point.position = index;
					point.demerits = candidate.demerits;
					point.ratio = candidate.ratio;
					point.line = candidate.active.data.line + 1;
					point.fitnessClazz = i;
					point.totals = tmpSum;
					point.prev = candidate.active;

					Node node = new Node(point, null, null);
					if (active != null) {
						node.prev = active.prev;
						active.prev.next = node;

						node.next = active;
						active.prev = node;
						bundle.activeNodes.add(bundle.activeNodes.indexOf(active) - 1, node);
					} else {
						// TODO test
						bundle.activeNodes.add(node);
					}
				}
			}
		}
	}

	// Add width, stretch and shrink values from the current
	// break point up to the next box or forced penalty.
	private static Sum computeSum(int index, Bundle bundle) {
		Sum result = new Sum(bundle.sum);
		int i = 0;

		for (i = index; i < bundle.elements.size(); i += 1) {
			Element element = bundle.elements.get(i);
			if (element instanceof Glue) {
				Glue glue = (Glue) element;
				result.width += glue.width;
				result.stretch += glue.stretch;
				result.shrink += glue.shrink;
			} else if (element instanceof Box ||
					(element instanceof Penalty &&
							((Penalty) element).penalty == -bundle.option.getInfinity() && i > index)) {
				break;
			}
		}
		return result;
	}

	private static int computeClazz(float ratio) {
		if (ratio < -0.5) {
			return CLASS_0;
		} else if (ratio <= 0.5) {
			return CLASS_1;
		} else if (ratio <= 1) {
			return CLASS_2;
		} else {
			return CLASS_3;
		}
	}

	private static float computeDemerits(Element element, float ratio, Bundle bundle, Node active, int currentClass) {
		// compute demerits & class
		float badness = (float) (100 * Math.pow(Math.abs(ratio), 3));
		float demerits = 0;

		// Positive penalty
		if (element instanceof Penalty && ((Penalty) element).penalty >= 0) {
			demerits = (float) (Math.pow(bundle.option.getDemeritsLine() + badness, 2) + Math.pow(((Penalty) element).penalty, 2));
			// Negative penalty but not a forced break
		} else if (element instanceof Penalty && ((Penalty) element).penalty != -bundle.option.getInfinity()) {
			demerits = (float) (Math.pow(bundle.option.getDemeritsLine() + badness, 2) - Math.pow(((Penalty) element).penalty, 2));
			// All other cases
		} else {
			demerits = (float) Math.pow(bundle.option.getDemeritsLine() + badness, 2);
		}

		if (element instanceof Penalty && bundle.elements.get(active.data.position) instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			Penalty activeElement = (Penalty) bundle.elements.get(active.data.position);
			if (penalty.flag && activeElement.flag) {
				// TODO
				demerits += bundle.option.getDemeritsFlagged();
			}
		}

		// Add a fitness penalty to the demerits if the fitness classes of two adjacent lines
		// differ too much.
		if (Math.abs(currentClass - active.data.fitnessClazz) > 1) {
			demerits += bundle.option.getDemeritsFitness();
		}

		// Add the total demerits of the active node to get the total demerits of this candidate node.
		demerits += active.data.demerits;


		return demerits;
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
