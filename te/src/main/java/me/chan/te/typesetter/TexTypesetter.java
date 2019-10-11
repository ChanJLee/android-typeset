package me.chan.te.typesetter;

import android.support.annotation.Nullable;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.chan.te.text.BreakStrategy;
import me.chan.te.config.LineAttributes;
import me.chan.te.config.Option;
import me.chan.te.data.Box;
import me.chan.te.data.Element;
import me.chan.te.data.ElementFactory;
import me.chan.te.data.Glue;
import me.chan.te.data.Line;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Penalty;
import me.chan.te.data.Segment;
import me.chan.te.log.Log;

class TexTypesetter implements Typesetter {
	private static final int CLASS_0 = 0;
	private static final int CLASS_1 = 1;
	private static final int CLASS_2 = 2;
	private static final int CLASS_3 = 3;

	private Option mOption;
	private TextPaint mPaint;
	private TextPaint mWorkPaint = new TextPaint();
	private Box.Bound mBound = new Box.Bound();

	private ElementFactory mElementFactory;

	TexTypesetter(TextPaint paint, Option option, ElementFactory elementFactory) {
		mOption = option;
		mPaint = paint;
		mElementFactory = elementFactory;
	}

	@Nullable
	public Paragraph typeset(Segment segment, LineAttributes lineAttributes, BreakStrategy breakStrategy) {
		Paragraph paragraph = new Paragraph(lineAttributes);
		List<Node> activeNodes = null;
		float tolerance = 0;
		for (int i = 0; i < mOption.MAX_RELAYOUT_TIMES; ++i) {
			tolerance += mOption.STRETCH_STEP_RATIO;
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

		List<Line> lines = typesetParagraph(segment, breakPoints, lineAttributes);
		paragraph.setLines(lines);

		return paragraph;
	}

	private List<Node> createActiveNodes(Segment segment, LineAttributes lineAttributes, float tolerance) {
		List<? extends Element> elements = segment.getElements();
		List<Node> activeNodes = new ArrayList<>();

		Node.Data data = new Node.Data();
		data.totals = new Sum();
		activeNodes.add(new Node(data, null, null));
		Sum sum = new Sum();
		for (int i = 0; i < elements.size() && !activeNodes.isEmpty(); ++i) {
			Element element = elements.get(i);
			if (element instanceof Box) {
				sum.width += getElementWidth(element);
			} else if (element instanceof Glue) {
				if (i > 0 && elements.get(i - 1) instanceof Box) {
					typesetLine(i, elements, activeNodes, sum, lineAttributes, tolerance);
				}

				Glue glue = (Glue) element;
				sum.width += glue.getWidth();
				sum.shrink += glue.getShrink();
				sum.stretch += glue.getStretch();
			} else if (element instanceof Penalty &&
					((Penalty) element).getPenalty() != mOption.INFINITY) {
				typesetLine(i, elements, activeNodes, sum, lineAttributes, tolerance);
			}
		}

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

		Box.Bound bound = getBoxBound((Box) element);
		return bound.getWidth();
	}

	private Box.Bound getBoxBound(Box box) {
		TextPaint textPaint = getInternalPaint();
		box.getBound(textPaint, mBound);
		return mBound;
	}

	private TextPaint getInternalPaint() {
		TextPaint textPaint = mWorkPaint;
		textPaint.set(mPaint);
		return textPaint;
	}

	private List<Line> typesetParagraph(Segment segment,
										List<BreakPoint> breakPoints,
										LineAttributes lineAttributes) {
		List<? extends Element> elements = segment.getElements();
		List<Line> lines = new ArrayList<>();
		int lineStart = 0;
		int size = elements.size();
		for (int i = 1; i < breakPoints.size(); ++i) {
			BreakPoint breakPoint = breakPoints.get(i);
			int pos = breakPoint.position;
			for (int j = lineStart; j != 0 && j < elements.size(); ++j) {
				Element element = elements.get(j);
				if (element instanceof Box || (element instanceof Penalty && ((Penalty) element).getPenalty() == -mOption.INFINITY)) {
					lineStart = j;
					break;
				}
			}

			int lineEnd = pos + 1;
			if (lineEnd > size) {
				lineEnd = size;
			}

			lines.add(createLine(
					elements,
					lineStart,
					lineEnd,
					lineAttributes,
					i + 1 == breakPoints.size(),
					breakPoint.ratio,
					i - 1));
			lineStart = pos;
		}
		return lines;
	}

	private Line createLine(List<? extends Element> lineElements, int start, int end,
							LineAttributes lineAttributes, boolean lastLine, float ratio,
							int lineNumber) {
		float lineHeight = 0;
		float wordWidth = 0;
		List<Box> boxes = new ArrayList<>();
		for (int i = start; i < end; ++i) {
			Element element = lineElements.get(i);
			if (!(element instanceof Box)) {
				mElementFactory.recycle(element);
				continue;
			}

			Box box = (Box) lineElements.get(i);
			i = mergeBox(box, i + 1, end, lineElements, mElementFactory);

			Box.Bound bound = getBoxBound(box);
			if (lineHeight < bound.getHeight()) {
				lineHeight = bound.getHeight();
			}
			wordWidth += bound.getWidth();
			boxes.add(box);
		}

		float spaceWidth = mOption.getSpaceWidth();
		int boxCount = boxes.size();
		float lineWidth = lineAttributes.get(lineNumber).getLineWidth();
		if (boxCount > 1) {
			spaceWidth = (lineWidth - wordWidth) / (boxCount - 1);
		}

		// 最后一行如果我能放的下，没必要压缩或者拉伸
		if (lastLine && (wordWidth + (boxCount - 1) * mOption.getSpaceWidth()) <= lineWidth) {
			spaceWidth = mOption.getSpaceWidth();
		}

		return new Line(
				boxes,
				lineHeight,
				spaceWidth,
				ratio
		);
	}

	/**
	 * @param start        merge 开始的位置
	 * @param lineElements 当前行
	 * @param factory
	 * @return 最后一个能被处理的index
	 */
	private int mergeBox(Box box, int start, int end,
						 List<? extends Element> lineElements,
						 ElementFactory factory) {
		for (; start < end; ++start) {
			Element element = lineElements.get(start);
			if (element instanceof Glue) {
				factory.recycle(element);
				break;
			}

			if (element instanceof Box) {
				Box other = (Box) element;
				if (!box.canMerge(other)) {
					--start;
					break;
				}

				factory.recycle(element);
				box.append(other);
				continue;
			}

			if (element instanceof Penalty && start == end - 1) {
				factory.recycle(element);
				box.append("-");
				box.setPenalty(true);
			}
		}

		return start;
	}

	private List<BreakPoint> chooseBreakPoints(List<Node> activeNodes) {
		List<BreakPoint> breaks = new ArrayList<>();
		Node tempNode = null;
		for (Node node : activeNodes) {
			if (tempNode == null || tempNode.data.demerits > node.data.demerits) {
				tempNode = node;
			}
		}

		while (tempNode != null) {
			BreakPoint breakPoint = new BreakPoint();
			breakPoint.position = tempNode.data.position;
			breakPoint.ratio = tempNode.data.ratio;
			breaks.add(breakPoint);
			tempNode = tempNode.data.prev;
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
				int currentLine = active.data.line + 1;
				float ratio = computeRatio(element, active.data, sum, lineAttributes.get(currentLine).getLineWidth());

				if (ratio < mOption.MIN_SHRINK_RATIO || (element instanceof Penalty && ((Penalty) element).getPenalty() == -mOption.INFINITY)) {
					removeActiveNode(active, activeNodes);
				}

				if (ratio >= mOption.MIN_SHRINK_RATIO && ratio <= tolerance) {
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
			createIfActiveNode(active, index, element, activeNodes, tmpSum, candidates);
		}
	}

	private void createIfActiveNode(Node active, int index, Element element, List<Node> activeNodes,
									Sum sum, Candidate[] candidates) {
		for (int i = 0; i < candidates.length; ++i) {
			Candidate candidate = candidates[i];
			if (candidate == null) {
				continue;
			}

			Node.Data data = new Node.Data();
			data.position = index;
			data.demerits = candidate.demerits;
			data.ratio = candidate.ratio;
			data.line = candidate.active.data.line + 1;
			data.fitnessClazz = i;
			data.totals = sum;
			data.prev = candidate.active;

			Node node = new Node(data, null, null);
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
		float width = sum.width - data.totals.width;
		float stretch = 0;
		float shrink = 0;
		if (element instanceof Penalty) {
			width += getElementWidth(element);
		}

		if (width < lineLength) {
			stretch = sum.stretch - data.totals.stretch;
			return stretch > 0 ? (lineLength - width) / stretch : mOption.INFINITY;
		} else if (width > lineLength) {
			shrink = sum.shrink - data.totals.shrink;
			return shrink > 0 ? (lineLength - width) / shrink : mOption.INFINITY;
		}

		return 0;
	}

	private float computeDemerits(Element element, List<? extends Element> elements, float ratio, Node active, int currentClass) {
		float badness = (float) (100 * Math.pow(Math.abs(ratio), 3));
		float demerits;

		if (element instanceof Penalty && ((Penalty) element).getPenalty() >= 0) {
			demerits = (float) (
					Math.pow(mOption.DEMERITS_LINE + badness + ((Penalty) element).getPenalty(), 2)
			);
		} else if (element instanceof Penalty && ((Penalty) element).getPenalty() != -mOption.INFINITY) {
			demerits = (float) (
					Math.pow(mOption.DEMERITS_LINE + badness, 2) -
							Math.pow(((Penalty) element).getPenalty(), 2)
			);
		} else {
			demerits = (float) (
					Math.pow(mOption.DEMERITS_LINE + badness, 2)
			);
		}

		if (element instanceof Penalty &&
				elements.get(active.data.position) instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			Penalty activePenalty = (Penalty) elements.get(active.data.position);
			if (penalty.isFlag() && activePenalty.isFlag()) {
				demerits += mOption.DEMERITS_FLAGGED;
			}
		}

		if (Math.abs(currentClass - active.data.fitnessClazz) > 1) {
			demerits += mOption.DEMERITS_FITNESS;
		}

		// 叠加total demerits
		demerits += active.data.demerits;

		return demerits;
	}

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
							((Penalty) element).getPenalty() == -mOption.INFINITY && i > index)) {
				break;
			}
		}
		return result;
	}

	private static void w(String msg) {
		Log.w("TexTypesetter", msg);
	}
}
