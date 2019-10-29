package me.chan.te.typesetter;

import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import me.chan.te.config.LineAttributes;
import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.Glue;
import me.chan.te.data.Line;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.log.Log;
import me.chan.te.text.BreakStrategy;

class TexTypesetter implements Typesetter {
	private static final int CLASS_0 = 0;
	private static final int CLASS_1 = 1;
	private static final int CLASS_2 = 2;
	private static final int CLASS_3 = 3;

	@Nullable
	public Paragraph typeset(Segment segment, LineAttributes lineAttributes, BreakStrategy breakStrategy) {
		List<Node> activeNodes = null;
		float tolerance = 0;
		for (int i = 0; i < MAX_RELAYOUT_TIMES; ++i) {
			tolerance += STRETCH_STEP_RATIO;
			activeNodes = createActiveNodes(segment, lineAttributes, tolerance);
			if (!activeNodes.isEmpty()) {
				break;
			}
		}

		if (activeNodes == null ||
				activeNodes.isEmpty()) {
			w("can not find active nodes: " + segment);
			return null;
		}

		List<BreakPoint> breakPoints = chooseBreakPoints(activeNodes);
		if (breakPoints.isEmpty()) {
			return null;
		}

		Paragraph paragraph = typesetParagraph(segment, breakPoints, lineAttributes);
		for (Node node : activeNodes) {
			node.recycle();
		}

		for (BreakPoint breakPoint : breakPoints) {
			breakPoint.recycle();
		}

		return paragraph;
	}

	private List<Node> createActiveNodes(Segment segment, LineAttributes lineAttributes, float tolerance) {
		List<? extends Element> elements = segment.getElements();
		List<Node> activeNodes = new LinkedList<>();

		// header
		Node node = Node.obtain(null, null);
		node.getData().totals = Sum.obtain();
		activeNodes.add(node);

		Sum sum = Sum.obtain();
		for (int i = 0; i < elements.size() && !activeNodes.isEmpty(); ++i) {
			Element element = elements.get(i);
			if (element instanceof Box) {
				sum.increaseWidth(getElementWidth(element));
			} else if (element instanceof Glue) {
				if (i > 0 && elements.get(i - 1) instanceof Box) {
					typesetLine(i, elements, activeNodes, sum, lineAttributes, tolerance);
				}

				Glue glue = (Glue) element;
				sum.increaseGlue(glue);
			} else if (element instanceof Penalty &&
					((Penalty) element).getPenalty() != INFINITY) {
				typesetLine(i, elements, activeNodes, sum, lineAttributes, tolerance);
			}
		}
		sum.recycle();

		return activeNodes;
	}

	private float getElementWidth(Element element) {
		if (element instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			return penalty.getWidth();
		}

		if (element instanceof Glue) {
			Glue glue = (Glue) element;
			return glue.getWidth();
		}

		return ((Box) element).getWidth();
	}

	private Paragraph typesetParagraph(Segment segment,
									   List<BreakPoint> breakPoints,
									   LineAttributes lineAttributes) {

		Paragraph paragraph = Paragraph.obtain(lineAttributes);
		List<? extends Element> elements = segment.getElements();
		int lineStart = 0;
		float lastLineWidth = 0;
		float lastLineWordSpace = 0;
		Line lastLine = null;
		int size = elements.size();
		for (int i = 1; i < breakPoints.size(); ++i) {
			BreakPoint breakPoint = breakPoints.get(i);
			int pos = breakPoint.position;
			for (int j = lineStart; j != 0 && j < elements.size(); ++j) {
				Element element = elements.get(j);
				if (element instanceof Box || (element instanceof Penalty && ((Penalty) element).getPenalty() == -INFINITY)) {
					lineStart = j;
					break;
				}
			}

			int lineEnd = pos + 1;
			if (lineEnd > size) {
				lineEnd = size;
			}

			int lineNumber = paragraph.getLineCount();
			LineAttributes.Attribute attribute = lineAttributes.get(lineNumber);
			lastLineWidth = attribute.getLineWidth();
			lastLineWordSpace = attribute.getWordSpaceWidth();
			lastLine = createLine(
					elements,
					lineStart,
					lineEnd,
					breakPoint.ratio,
					lastLineWidth,
					lastLineWordSpace);
			paragraph.add(lastLine);
			lineStart = pos;
		}

		if (lastLine == null) {
			return paragraph;
		}

		int boxCount = lastLine.getBoxes().size();
		if (lastLine.getBoxTotalWidth() + (boxCount - 1) * lastLineWordSpace <= lastLineWidth) {
			lastLine.setSpaceWidth(lastLineWordSpace);
		}

		return paragraph;
	}

	private Line createLine(List<? extends Element> lineElements, int start, int end, float ratio,
							float lineWidth, float wordSpace) {
		float lineHeight = 0;
		Line line = Line.obtain();
		float boxTotalWidth = 0;
		for (int i = start; i < end; ++i) {
			Element element = lineElements.get(i);
			if (!(element instanceof Box)) {
				continue;
			}

			Box box = (Box) lineElements.get(i);
			i = mergeBox(box, i + 1, end, lineElements);

			if (lineHeight < box.getHeight()) {
				lineHeight = box.getHeight();
			}

			boxTotalWidth += box.getWidth();
			line.add(box);
		}

		int size = line.getBoxes().size();
		if (size > 1) {
			line.setSpaceWidth((lineWidth - boxTotalWidth) / (size - 1));
		} else {
			line.setSpaceWidth(wordSpace);
		}

		line.setLineHeight(lineHeight);
		line.setBoxTotalWidth(boxTotalWidth);
		line.setRatio(ratio);

		return line;
	}

	/**
	 * @param start        merge 开始的位置
	 * @param lineElements 当前行
	 * @return 最后一个能被处理的index
	 */
	private int mergeBox(Box box, int start, int end,
						 List<? extends Element> lineElements) {
		for (; start < end; ++start) {
			Element element = lineElements.get(start);
			if (element instanceof Glue) {
				break;
			}

			if (element instanceof Box) {
				Box other = (Box) element;
				if (!box.canMerge(other)) {
					--start;
					break;
				}

				box.append(other);
				continue;
			}

			if (element instanceof Penalty && start == end - 1) {
				box.append((Penalty) element);
			}
		}

		return start;
	}

	private List<BreakPoint> chooseBreakPoints(List<Node> activeNodes) {
		List<BreakPoint> breaks = new LinkedList<>();
		Node tempNode = null;
		for (Node node : activeNodes) {
			if (tempNode == null) {
				tempNode = node;
				continue;
			}

			if (tempNode.getData().demerits > node.getData().demerits) {
				tempNode = node;
			}
		}

		while (tempNode != null) {
			Node.Data data = tempNode.getData();
			breaks.add(BreakPoint.obtain(data.position, data.ratio));
			tempNode = data.prev;
		}

		Collections.reverse(breaks);

		return breaks;
	}

	/**
	 * 对一行进行排版
	 *
	 * @param index          当前第几个元素
	 * @param elements       需要排版的元素
	 * @param activeNodes    active nodes
	 * @param sum            sum
	 * @param lineAttributes 行配置信息
	 * @param tolerance      允许的缺陷阈值
	 */
	private void typesetLine(int index, List<? extends Element> elements,
							 List<Node> activeNodes, Sum sum,
							 LineAttributes lineAttributes, float tolerance) {
		Element element = elements.get(index);
		Node active = activeNodes.isEmpty() ? null : activeNodes.get(0);

		while (active != null) {
			Candidate[] candidates = new Candidate[4];

			while (active != null) {
				Node next = active.next;
				int currentLine = active.getData().line + 1;
				float ratio = computeRatio(element, active.getData(), sum, lineAttributes.get(currentLine).getLineWidth());

				if (ratio < MIN_SHRINK_RATIO || (element instanceof Penalty && ((Penalty) element).getPenalty() == -INFINITY)) {
					removeActiveNode(active, activeNodes);
				}

				if (ratio >= MIN_SHRINK_RATIO && ratio <= tolerance) {
					int currentClass = computeClazz(ratio);

					float demerits = computeDemerits(element, elements, ratio, active, currentClass);

					// Only store the best candidate for each fitness class
					if (candidates[currentClass] == null || demerits < candidates[currentClass].demerits) {
						if (candidates[currentClass] == null) {
							candidates[currentClass] = Candidate.obtain(demerits, ratio, active);
							continue;
						}

						candidates[currentClass].active = active;
						candidates[currentClass].demerits = demerits;
						candidates[currentClass].ratio = ratio;
					}
				}

				active = next;

				if (active != null && active.getData().line >= currentLine) {
					break;
				}
			}

			Sum tmpSum = computeSum(index, elements, sum);
			createIfActiveNode(active, index, activeNodes, tmpSum, candidates);
			tmpSum.recycle();
			for (Candidate candidate : candidates) {
				if (candidate != null) {
					candidate.recycle();
				}
			}
		}
	}

	private void createIfActiveNode(Node active, int index, List<Node> activeNodes,
									Sum sum, Candidate[] candidates) {
		for (int i = 0; i < candidates.length; ++i) {
			Candidate candidate = candidates[i];
			if (candidate == null) {
				continue;
			}

			Node node = Node.obtain(null, null);
			Node.Data data = node.getData();
			data.position = index;
			data.demerits = candidate.demerits;
			data.ratio = candidate.ratio;
			data.line = candidate.active.getData().line + 1;
			data.fitnessClazz = i;
			data.totals = Sum.obtain(sum);
			data.prev = candidate.active;

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

	private float computeRatio(Element element, Node.Data data, Sum sum, float lineLength) {
		float width = sum.getWidth() - data.totals.getWidth();
		if (element instanceof Penalty) {
			width += getElementWidth(element);
		}

		if (width < lineLength) {
			float stretch = sum.getStretch() - data.totals.getStretch();
			return stretch > 0 ? (lineLength - width) / stretch : INFINITY;
		} else if (width > lineLength) {
			float shrink = sum.getShrink() - data.totals.getShrink();
			return shrink > 0 ? (lineLength - width) / shrink : INFINITY;
		}

		return 0;
	}

	private float computeDemerits(Element element, List<? extends Element> elements, float ratio, Node active, int currentClass) {
		float badness = (float) (100 * Math.pow(Math.abs(ratio), 3));
		float demerits;

		if (element instanceof Penalty && ((Penalty) element).getPenalty() >= 0) {
			demerits = (float) (
					Math.pow(DEMERITS_LINE + badness + ((Penalty) element).getPenalty(), 2)
			);
		} else if (element instanceof Penalty && ((Penalty) element).getPenalty() != -INFINITY) {
			demerits = (float) (
					Math.pow(DEMERITS_LINE + badness, 2) -
							Math.pow(((Penalty) element).getPenalty(), 2)
			);
		} else {
			demerits = (float) (
					Math.pow(DEMERITS_LINE + badness, 2)
			);
		}

		if (element instanceof Penalty &&
				elements.get(active.getData().position) instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			Penalty activePenalty = (Penalty) elements.get(active.getData().position);
			if (penalty.isFlag() && activePenalty.isFlag()) {
				demerits += DEMERITS_FLAGGED;
			}
		}

		if (Math.abs(currentClass - active.getData().fitnessClazz) > 1) {
			demerits += DEMERITS_FITNESS;
		}

		// 叠加total demerits
		demerits += active.getData().demerits;

		return demerits;
	}

	private Sum computeSum(int index, List<? extends Element> elements, Sum sum) {
		Sum result = Sum.obtain(sum);

		for (int i = index; i < elements.size(); ++i) {
			Element element = elements.get(i);
			if (element instanceof Glue) {
				Glue glue = (Glue) element;
				result.increaseGlue(glue);
			} else if (element instanceof Box ||
					(element instanceof Penalty &&
							((Penalty) element).getPenalty() == -INFINITY && i > index)) {
				break;
			}
		}
		return result;
	}

	private static void w(String msg) {
		Log.w("TextEngineTexTypesetter", msg);
	}
}
