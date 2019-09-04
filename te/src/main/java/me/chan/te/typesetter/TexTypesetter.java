package me.chan.te.typesetter;

import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.chan.te.data.Box;
import me.chan.te.data.BreakPoint;
import me.chan.te.data.Candidate;
import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.data.LineConfiguration;
import me.chan.te.data.Node;
import me.chan.te.data.Option;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.data.Point;
import me.chan.te.data.Sum;

public class TexTypesetter implements Typesetter {
	private static final int CLASS_0 = 0;
	private static final int CLASS_1 = 1;
	private static final int CLASS_2 = 2;
	private static final int CLASS_3 = 3;

	private Option mOption;
	private Paint mPaint;

	public TexTypesetter(Paint paint, Option option) {
		mOption = option;
		mPaint = paint;
	}

	@Override
	public Paragraph typeset(List<? extends Element> elements, LineConfiguration lineConfiguration) {
		Paragraph paragraph = new Paragraph();
		List<Node> activeNodes = new ArrayList<>();

		Point point = new Point();
		point.totals = new Sum();
		activeNodes.add(new Node(point, null, null));
		Sum sum = new Sum();

		for (int i = 0; i < elements.size(); ++i) {
			Element element = elements.get(i);
			if (element instanceof Box) {
				sum.width += element.getWidth();
			} else if (element instanceof Glue) {
				if (i > 0 && elements.get(i - 1) instanceof Box) {
					typesetLine(i, elements, activeNodes, sum, lineConfiguration);
				}

				Glue glue = (Glue) element;
				sum.width += glue.getWidth();
				sum.shrink += glue.getShrink();
				sum.stretch += glue.getStretch();
			} else if (element instanceof Penalty && ((Penalty) element).getPenalty() != mOption.infinity) {
				typesetLine(i, elements, activeNodes, sum, lineConfiguration);
			}
		}

		if (activeNodes.isEmpty()) {
			return paragraph;
		}

		Node tempNode = null;
		for (Node node : activeNodes) {
			if (tempNode == null || tempNode.data.demerits >= node.data.demerits) {
				tempNode = node;
			}
		}

		List<BreakPoint> breaks = new ArrayList<>();

		while (tempNode != null) {
			BreakPoint breakPoint = new BreakPoint();
			breakPoint.position = tempNode.data.position;
			breakPoint.ratio = tempNode.data.ratio;
			breaks.add(breakPoint);
			tempNode = tempNode.data.prev;
		}

		Collections.reverse(breaks);

		// TODO 分割没一行

		return paragraph;
	}

	/**
	 * 对一行进行排版
	 *
	 * @param index             当前第几个元素
	 * @param elements          需要排版的元素
	 * @param activeNodes       active nodes
	 * @param sum               sum
	 * @param lineConfiguration 行配置信息
	 */
	private void typesetLine(int index, List<? extends Element> elements,
							 List<Node> activeNodes, Sum sum,
							 LineConfiguration lineConfiguration) {
		Element element = elements.get(index);
		Node active = activeNodes.isEmpty() ? null : activeNodes.get(0);

		while (active != null) {
			Candidate[] candidates = new Candidate[4];

			while (active != null) {
				Node next = active.next;
				int currentLine = active.data.line + 1;
				float ratio = computeRatio(element, active.data, sum, lineConfiguration.getLineWidth(currentLine));

				if (ratio < -1 || (element instanceof Penalty && ((Penalty) element).getPenalty() == -mOption.infinity)) {
					removeActiveNode(active, activeNodes);
				}

				if (ratio >= -1 && ratio <= mOption.tolerance) {
					int currentClass = computeClazz(ratio);

					float demerits = computeDemerits(element, elements, ratio, active, currentClass);

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

			Sum tmpSum = computeSum(index, elements, sum);

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
					if (active.prev != null) {
						active.prev.next = node;
					}

					node.next = active;
					active.prev = node;
					activeNodes.add(activeNodes.indexOf(active), node);
				} else {
					if (!activeNodes.isEmpty()) {
						Node last = activeNodes.get(activeNodes.size() - 1);
						last.next = node;
						node.prev = last;
					}
					activeNodes.add(node);
				}
			}
		}
	}

	private int computeClazz(float ratio) {
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

	private void removeActiveNode(Node node, List<Node> activeNodes) {
		Node prev = node.prev;
		Node next = node.next;
		activeNodes.remove(node);

		if (prev != null) {
			prev.next = next;
		}

		if (next != null) {
			next.prev = prev;
		}
	}

	private float computeRatio(Element element, Point point, Sum sum, float lineLength) {
		float width = sum.width - point.totals.width;
		float stretch = 0;
		float shrink = 0;
		if (element instanceof Penalty) {
			width += element.getWidth();
		}

		if (width < lineLength) {
			stretch = sum.stretch - point.totals.stretch;
			return stretch > 0 ? (lineLength - width) / stretch : mOption.infinity;
		} else if (width > lineLength) {
			shrink = sum.shrink - point.totals.shrink;
			return shrink > 0 ? (lineLength - width) / shrink : mOption.infinity;
		}

		// perfect match
		return 0;
	}

	private float computeDemerits(Element element, List<? extends Element> elements, float ratio, Node active, int currentClass) {
		// compute demerits & class
		float badness = (float) (100 * Math.pow(Math.abs(ratio), 3));
		float demerits;

		// Positive penalty
		if (element instanceof Penalty && ((Penalty) element).getPenalty() >= 0) {
			demerits = (float) (Math.pow(mOption.demeritsLine + badness + ((Penalty) element).getPenalty(), 2));
			// Negative penalty but not a forced break
		} else if (element instanceof Penalty && ((Penalty) element).getPenalty() != -mOption.infinity) {
			demerits = (float) (Math.pow(mOption.demeritsLine + badness, 2) -
					Math.pow(((Penalty) element).getPenalty(), 2));
			// All other cases
		} else {
			demerits = (float) Math.pow(mOption.demeritsLine + badness, 2);
		}

		if (element instanceof Penalty && elements.get(active.data.position) instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			Penalty activeElement = (Penalty) elements.get(active.data.position);
			if (penalty.isFlag() && activeElement.isFlag()) {
				demerits += mOption.demeritsFlagged;
			}
		}

		// Add a fitness penalty to the demerits if the fitness classes of two adjacent lines
		// differ too much.
		if (Math.abs(currentClass - active.data.fitnessClazz) > 1) {
			demerits += mOption.demeritsFitness;
		}

		// Add the total demerits of the active node to get the total demerits of this candidate node.
		demerits += active.data.demerits;


		return demerits;
	}

	// Add width, stretch and shrink values from the current
	// break point up to the next box or forced penalty.
	private Sum computeSum(int index, List<? extends Element> elements, Sum sum) {
		Sum result = new Sum(sum);

		for (int i = index; i < elements.size(); ++i) {
			Element element = elements.get(i);
			if (element instanceof Glue) {
				Glue glue = (Glue) element;
				result.width += glue.getWidth();
				result.stretch += glue.getStretch();
				result.shrink += glue.getShrink();
			} else if (element instanceof Box ||
					(element instanceof Penalty &&
							((Penalty) element).getPenalty() == -mOption.infinity && i > index)) {
				break;
			}
		}
		return result;
	}
}
