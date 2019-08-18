package me.chan.typeset.core;

import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.chan.hypher.Hypher;
import me.chan.typeset.elements.Box;
import me.chan.typeset.elements.Element;
import me.chan.typeset.elements.Glue;
import me.chan.typeset.elements.Penalty;

public class Typeset {

	public static Result linkBreak(String content, List<Float> lineLengths, Option option, Paint paint) {

		Bundle bundle = new Bundle();
		bundle.activeNodes = new ArrayList<>();
		bundle.lineLengths = lineLengths;
		bundle.option = option;
		bundle.paint = paint;
		bundle.sum = new Sum();

		// create elements
		Result result = new Result();
		bundle.elements = result.elements = createElements(content, bundle);
		result.breaks = new ArrayList<>();

		Point point = new Point();
		point.totals = new Sum();
		bundle.activeNodes.add(new Node(point, null, null));
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
			} else if (element instanceof Penalty && ((Penalty) element).penalty != option.infinity) {
				mainLoop(i, bundle);
			}
		}

		if (bundle.activeNodes.isEmpty()) {
			return result;
		}

		Node tempNode = null;
		for (Node node : bundle.activeNodes) {
			if (tempNode == null || tempNode.data.demerits >= node.data.demerits) {
				tempNode = node;
			}
		}

		while (tempNode != null) {
			Break b = new Break();
			b.position = tempNode.data.position;
			b.ratio = tempNode.data.ratio;
			result.breaks.add(b);
			tempNode = tempNode.data.prev;
		}

		Collections.reverse(result.breaks);
		return result;
	}

	private static final int CLASS_0 = 0;
	private static final int CLASS_1 = 1;
	private static final int CLASS_2 = 2;
	private static final int CLASS_3 = 3;

	private static void mainLoop(int index, Bundle bundle) {
		Element element = bundle.elements.get(index);
		Node active = bundle.activeNodes.isEmpty() ? null : bundle.activeNodes.get(0);

		while (active != null) {
			Candidate[] candidates = new Candidate[4];

			while (active != null) {
				Node next = active.next;
				int currentLine = active.data.line + 1;
				float ratio = computeRatio(index, active.data, currentLine, bundle);

				if (ratio < -1 || (element instanceof Penalty && ((Penalty) element).penalty == -bundle.option.infinity)) {
					removeActiveNode(active, bundle);
				}

				if (ratio >= -1 && ratio <= bundle.option.tolerance) {
					int currentClass = computeClazz(ratio);

					float demerits = computeDemerits(element, ratio, bundle, active, currentClass);

					// Only store the best candidate for each fitness class
					if (candidates[currentClass] == null || demerits < candidates[currentClass].demerits) {
						if (candidates[currentClass] == null) {
							candidates[currentClass] = new Candidate();
						}
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
				Candidate candidate = candidates[i];
				if (candidate == null) {
					continue;
				}

				Point point = new Point();
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
					bundle.activeNodes.add(bundle.activeNodes.indexOf(active), node);
				} else {
					if (!bundle.activeNodes.isEmpty()) {
						Node last = bundle.activeNodes.get(bundle.activeNodes.size() - 1);
						last.next = node;
						node.prev = last;
					}
					bundle.activeNodes.add(node);
				}
			}
		}
	}

	// Add width, stretch and shrink values from the current
	// break point up to the next box or forced penalty.
	private static Sum computeSum(int index, Bundle bundle) {
		Sum result = new Sum(bundle.sum);

		for (int i = index; i < bundle.elements.size(); ++i) {
			Element element = bundle.elements.get(i);
			if (element instanceof Glue) {
				Glue glue = (Glue) element;
				result.width += glue.width;
				result.stretch += glue.stretch;
				result.shrink += glue.shrink;
			} else if (element instanceof Box ||
					(element instanceof Penalty &&
							((Penalty) element).penalty == -bundle.option.infinity && i > index)) {
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
		float demerits;

		// Positive penalty
		if (element instanceof Penalty && ((Penalty) element).penalty >= 0) {
			demerits = (float) (Math.pow(bundle.option.demeritsLine + badness, 2) +
					Math.pow(((Penalty) element).penalty, 2)
			);
			// Negative penalty but not a forced break
		} else if (element instanceof Penalty && ((Penalty) element).penalty != -bundle.option.infinity) {
			demerits = (float) (Math.pow(bundle.option.demeritsLine + badness, 2) -
					Math.pow(((Penalty) element).penalty, 2));
			;
			// All other cases
		} else {
			demerits = (float) Math.pow(bundle.option.demeritsLine + badness, 2);
		}

		if (element instanceof Penalty &&
				bundle.elements.get(active.data.position) instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			Penalty activeElement = (Penalty) bundle.elements.get(active.data.position);
			if (penalty.flag && activeElement.flag) {
				demerits += bundle.option.demeritsFlagged;
			}
		}

		// Add a fitness penalty to the demerits if the fitness classes of two adjacent lines
		// differ too much.
		if (Math.abs(currentClass - active.data.fitnessClazz) > 1) {
			demerits += bundle.option.demeritsFitness;
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

	private static float computeRatio(int index, Point point, int currentLine, Bundle bundle) {
		float width = bundle.sum.width - point.totals.width;
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
			stretch = bundle.sum.stretch - point.totals.stretch;
			return stretch > 0 ? (lineLength - width) / stretch : bundle.option.infinity;
		} else if (width > lineLength) {
			shrink = bundle.sum.shrink - point.totals.shrink;
			return shrink > 0 ? (lineLength - width) / shrink : bundle.option.infinity;
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
				continue;
			}

			List<String> hyphenated = hypher.hyphenate(span);
			int size = hyphenated.size();
			if (size > 1 && span.length() > bundle.option.minHyperLength) {
				for (int j = 0; j < size; ++j) {
					String item = hyphenated.get(j);
					d("add box: " + item);
					elements.add(new Box(bundle.paint.measureText(item), item));
					if (j != size - 1) {
						d("add <->");
						elements.add(new Penalty(bundle.option.hyphenWidth, bundle.option.hyphenPenalty, true));
					}
				}
			} else {
				d("add box: " + span);
				elements.add(new Box(bundle.paint.measureText(span), span));
			}

			if (i == spans.length - 1) {
				d("add <glue penalty>");
				elements.add(new Glue(0, bundle.option.infinity, 0));
				elements.add(new Penalty(0, -bundle.option.infinity, true));
			} else {
				d("add <glue>");
				elements.add(new Glue(bundle.option.spaceWidth, bundle.option.spaceStretch, bundle.option.spaceShrink));
			}
		}
		return elements;
	}

	private static void d(String msg) {
		Log.d("Typeset", msg);
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
