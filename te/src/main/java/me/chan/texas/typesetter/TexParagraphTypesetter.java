package me.chan.texas.typesetter;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.chan.texas.log.Log;
import me.chan.texas.text.Box;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.DrawableBox;
import me.chan.texas.text.Glue;
import me.chan.texas.text.Gravity;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Penalty;
import me.chan.texas.text.TextAttribute;
import me.chan.texas.text.TextBox;

class TexParagraphTypesetter implements ParagraphTypesetter {
	private static final int CLASS_0 = 0;
	private static final int CLASS_1 = 1;
	private static final int CLASS_2 = 2;
	private static final int CLASS_3 = 3;
	private static final float DEMERITS_LINE = 1;
	// 对应 α
	private static final float DEMERITS_FLAGGED = 100;
	// 对应 γ
	private static final float DEMERITS_FITNESS = 3000;
	private static final int MAX_RELAYOUT_TIMES = 60;
	private static final float MIN_SHRINK_RATIO = -1f;
	private static final float STRETCH_STEP_RATIO = 0.2f;

	@Nullable
	@Override
	public boolean typeset(Paragraph paragraph, TextAttribute textAttribute, BreakStrategy breakStrategy) {
		ActiveNodeList activeList = null;
		float tolerance = 1 - STRETCH_STEP_RATIO;
		for (int i = 0; i < MAX_RELAYOUT_TIMES; ++i) {
			tolerance += STRETCH_STEP_RATIO;
			activeList = createActiveNodes(paragraph, textAttribute, tolerance);
			if (!activeList.isEmpty()) {
				break;
			}
		}

		if (activeList == null ||
				activeList.isEmpty()) {
			w("can not find active nodes: " + paragraph);
			return false;
		}

		List<BreakPoint> breakPoints = chooseBreakPoints(activeList);
		if (breakPoints.isEmpty()) {
			return false;
		}

		typesetParagraph(paragraph, breakPoints, textAttribute);
		activeList.recycle();

		for (BreakPoint breakPoint : breakPoints) {
			breakPoint.recycle();
		}

		return true;
	}

	private ActiveNodeList createActiveNodes(Paragraph paragraph, TextAttribute textAttribute, float tolerance) {
		ActiveNodeList activeNodeList = new ActiveNodeList();

		Sum sum = Sum.obtain();
		int size = paragraph.getElementCount();
		Candidate[] candidates = new Candidate[4];
		for (int i = 0; i < size && !activeNodeList.isEmpty(); ++i) {
			Paragraph.Element element = paragraph.getElement(i);
			if (element instanceof Box) {
				sum.increaseWidth(getElementWidth(element));
			} else if (element instanceof Glue) {
				if (i > 0 && paragraph.getElement(i - 1) instanceof Box) {
					typesetLine(i, paragraph, activeNodeList, sum, textAttribute, tolerance, candidates);
				}

				Glue glue = (Glue) element;
				sum.increaseGlue(glue);
			} else if (element instanceof Penalty &&
					((Penalty) element).getPenalty() != INFINITY) {
				typesetLine(i, paragraph, activeNodeList, sum, textAttribute, tolerance, candidates);
			}
		}
		sum.recycle();

		return activeNodeList;
	}

	private float getElementWidth(Paragraph.Element element) {
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

	private Paragraph typesetParagraph(Paragraph paragraph,
									   List<BreakPoint> breakPoints,
									   TextAttribute textAttribute) {

		int lineStart = 0;
		int size = paragraph.getElementCount();
		for (int i = 1; i < breakPoints.size(); ++i) {
			BreakPoint breakPoint = breakPoints.get(i);
			int pos = breakPoint.position;
			for (int j = lineStart; j != 0 && j < size; ++j) {
				Paragraph.Element element = paragraph.getElement(j);
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
			TextAttribute.LineAttribute attribute = textAttribute.get(lineNumber);
			paragraph.addLine(createLine(
					paragraph,
					lineStart,
					lineEnd,
					breakPoint.ratio,
					attribute,
					i == breakPoints.size() - 1,
					textAttribute.getSpaceWidth()));
			lineStart = pos;
		}

		return paragraph;
	}

	/**
	 * @param paragraph  paragraph
	 * @param start      开始位置
	 * @param end        结束位置
	 * @param ratio      ratio
	 * @param attribute  attribute
	 * @param isLastLine 是否是最后一行
	 * @return 行
	 */
	private Paragraph.Line createLine(Paragraph paragraph, int start, int end, float ratio,
									  TextAttribute.LineAttribute attribute, boolean isLastLine, float expectWordSpace) {
		float lineWidth = attribute.getLineWidth();
		Gravity gravity = attribute.getGravity();
		float lineHeight = 0;
		Paragraph.Line line = Paragraph.Line.obtain();
		float boxTotalWidth = 0;
		for (int i = start; i < end; ++i) {
			Paragraph.Element element = paragraph.getElement(i);
			if (!(element instanceof Box)) {
				continue;
			}

			Box box = (Box) element;
			i = mergeBox(box, i + 1, end, paragraph);

			if (lineHeight < box.getHeight()) {
				lineHeight = box.getHeight();
			}

			boxTotalWidth += box.getWidth();
			line.add(box);
		}

		int size = line.getCount();
		if (size > 1) {
			line.setSpaceWidth((lineWidth - boxTotalWidth) / (size - 1));
		} else {
			line.setSpaceWidth(expectWordSpace);
		}

		if (isLastLine && boxTotalWidth + expectWordSpace * (size - 1) < lineWidth) {
			line.setSpaceWidth(expectWordSpace);
		}

		line.setLineWidth(size > 1 ? boxTotalWidth + line.getSpaceWidth() * (size - 1) : boxTotalWidth);
		line.setLineHeight(lineHeight);
		line.setGravity(gravity);
		line.setRatio(ratio);

		return line;
	}

	/**
	 * @param box       box
	 * @param start     merge 开始的位置
	 * @param end       end
	 * @param paragraph paragraph
	 * @return 最后一个能被处理的index
	 */
	private int mergeBox(Box box, int start, int end,
						 Paragraph paragraph) {
		if (!(box instanceof TextBox)) {
			return start;
		}

		TextBox current = (TextBox) box;
		for (; start < end; ++start) {
			Paragraph.Element element = paragraph.getElement(start);
			if (element instanceof Glue) {
				break;
			}

			if (element instanceof DrawableBox) {
				break;
			}

			if (element instanceof TextBox) {
				TextBox other = (TextBox) element;
				current.append(other);
				continue;
			}

			if (element instanceof Penalty && start == end - 1) {
				Penalty penalty = (Penalty) element;
				if (penalty.isFlag()) {
					current.append((Penalty) element);
				}
			}
		}

		return start;
	}

	private List<BreakPoint> chooseBreakPoints(ActiveNodeList activeNodeList) {
		List<BreakPoint> breaks = new ArrayList<>(128);
		Node tempNode = null;
		for (Node node : activeNodeList) {
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
	 * @param paragraph      paragraph
	 * @param activeNodeList active nodes
	 * @param sum            sum
	 * @param textAttribute  行配置信息
	 * @param tolerance      允许的缺陷阈值
	 * @param candidates     候选人
	 */
	private void typesetLine(int index, Paragraph paragraph,
							 ActiveNodeList activeNodeList, Sum sum,
							 TextAttribute textAttribute, float tolerance,
							 Candidate[] candidates) {
		Paragraph.Element element = paragraph.getElement(index);
		Node active = activeNodeList.getHeader();

		while (active != null) {
			boolean needCreateNode = false;
			while (active != null) {
				Node next = active.next;
				int currentLine = active.getData().line + 1;
				float ratio = computeRatio(element, active.getData(), sum, textAttribute.get(currentLine).getLineWidth());

				if (ratio < MIN_SHRINK_RATIO || (element instanceof Penalty &&
						((Penalty) element).getPenalty() == -INFINITY)) {
					activeNodeList.remove(active);
				}

				if (ratio >= MIN_SHRINK_RATIO && ratio <= tolerance) {
					int currentClass = computeClazz(ratio);

					float demerits = computeDemerits(element, paragraph, ratio, active, currentClass);

					// Only store the best candidate for each fitness class
					if (candidates[currentClass] == null || demerits < candidates[currentClass].demerits) {
						needCreateNode = true;

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

			if (needCreateNode) {
				Sum tmpSum = computeSum(index, paragraph, sum);
				createIfActiveNode(active, index, activeNodeList, tmpSum, candidates);
				tmpSum.recycle();

				for (int i = 0; i < candidates.length; ++i) {
					Candidate candidate = candidates[i];
					if (candidate != null) {
						candidate.recycle();
						candidates[i] = null;
					}
				}
			}
		}
	}

	private void createIfActiveNode(Node active, int index, ActiveNodeList activeNodeList,
									Sum sum, Candidate[] candidates) {
		for (int i = 0; i < candidates.length; ++i) {
			Candidate candidate = candidates[i];
			if (candidate == null) {
				continue;
			}

			Node node = Node.obtain();
			Node.Data data = node.getData();
			data.position = index;
			data.demerits = candidate.demerits;
			data.ratio = candidate.ratio;
			data.line = candidate.active.getData().line + 1;
			data.fitnessClazz = i;
			data.totals = Sum.obtain(sum);
			data.prev = candidate.active;

			if (active != null) {
				activeNodeList.insertBefore(active, node);
			} else {
				activeNodeList.pushBack(node);
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

	private float computeRatio(Paragraph.Element element, Node.Data data, Sum sum, float lineLength) {
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

	private float computeDemerits(Paragraph.Element element, Paragraph paragraph, float ratio, Node active, int currentClass) {
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
				paragraph.getElement(active.getData().position) instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			Penalty activePenalty = (Penalty) paragraph.getElement(active.getData().position);
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

	private Sum computeSum(int index, Paragraph paragraph, Sum sum) {
		Sum result = Sum.obtain(sum);

		int size = paragraph.getElementCount();
		for (int i = index; i < size; ++i) {
			Paragraph.Element element = paragraph.getElement(i);
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
