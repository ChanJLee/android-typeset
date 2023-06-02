package me.chan.texas.typesetter.tex;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;
import static me.chan.texas.Texas.INFINITY;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import com.shanbay.lib.log.Log;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.typesetter.AbsParagraphTypesetter;
import me.chan.texas.typesetter.utils.ElementStream;
import me.chan.texas.utils.IntArray;

import java.util.Arrays;

@RestrictTo(LIBRARY)
public class TexParagraphTypesetterCompat extends AbsParagraphTypesetter {
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

	@VisibleForTesting
	protected IntArray __intArray;

	@Nullable
	@Override
	public boolean typeset(Paragraph paragraph, BreakStrategy breakStrategy, int width) {
		// 先获取active list，尽量从最小的伸缩度来计算
		ActiveNodes activeList = null;
		float tolerance = 1 - STRETCH_STEP_RATIO;
		for (int i = 0; i < MAX_RELAYOUT_TIMES; ++i) {
			tolerance += STRETCH_STEP_RATIO;
			activeList = createActiveNodes(paragraph, tolerance, width);
			if (!activeList.isEmpty()) {
				break;
			}
		}

		if (activeList == null ||
				activeList.isEmpty()) {
			w("can not find active nodes: " + paragraph);
			return false;
		}

		// 选择断点
		IntArray breakPoints = chooseBreakPoints(activeList);
		if (DEBUG) {
			__intArray = breakPoints;
		}
		if (breakPoints.empty()) {
			return false;
		}

		// 将断点转换成行信息
		typesetParagraph(paragraph, breakPoints, breakStrategy, width);
		activeList.recycle();

		return true;
	}

	/**
	 * 获取 active node 列表
	 *
	 * @param paragraph 段落
	 * @param tolerance 阈值
	 * @param lineWidth 行宽
	 * @return active node 列表
	 */
	private ActiveNodes createActiveNodes(Paragraph paragraph, float tolerance, int lineWidth) {
		ActiveNodes activeNodes = new ActiveNodes();

		Sum sum = Sum.obtain();
		int size = paragraph.getElementCount();
		Candidate[] candidates = new Candidate[4];
		for (int i = 0; i < size && !activeNodes.isEmpty(); ++i) {
			Element element = paragraph.getElement(i);
			if (element instanceof Box) {
				sum.increase((Box) element);
			} else if (element instanceof Glue) {
				if (i > 0 && paragraph.getElement(i - 1) instanceof Box) {
					typesetLine(i, paragraph, activeNodes, sum, tolerance, candidates, lineWidth);
				}

				Glue glue = (Glue) element;
				sum.increase(glue);
			} else if (element instanceof Penalty &&
					((Penalty) element).getPenalty() != INFINITY) {
				typesetLine(i, paragraph, activeNodes, sum, tolerance, candidates, lineWidth);
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

	/**
	 * 对段落进行排版
	 *
	 * @param paragraph   段落
	 * @param breakPoints 可能的断点
	 * @return 排版好的段落
	 */
	private Paragraph typesetParagraph(Paragraph paragraph,
									   IntArray breakPoints,
									   BreakStrategy breakStrategy,
									   int lineWidth) {
		ElementStream stream = new ElementStream(paragraph);
		int size = paragraph.getElementCount();
		Layout layout = Layout.obtain(paragraph.getLayout());

		for (int i = 1; i < breakPoints.size(); ++i) {
			int breakPoint = breakPoints.get(i);
			while (true) {
				if (stream.eof()) {
					return paragraph;
				}

				int save = stream.state();
				Element element = stream.next();
				if (element instanceof Box || (element instanceof Penalty && ((Penalty) element).getPenalty() == -INFINITY)) {
					stream.restore(save);
					break;
				}
			}

			// 防止越界
			int lineEnd = breakPoint + 1;
			if (lineEnd > size) {
				lineEnd = size;
			}

			Line line = createLine(stream, ElementStream.index2State(lineEnd), breakStrategy, lineWidth);
			if (line == null || line.isEmpty()) {
				break;
			}

			layout.addLine(line);
		}

		layout = paragraph.swap(layout);
		if (layout != null) {
			layout.recycle();
		}

		return paragraph;
	}

	/**
	 * 选择可能的断点
	 *
	 * @param activeNodes active node list
	 * @return 断点集合
	 */
	private IntArray chooseBreakPoints(ActiveNodes activeNodes) {
		IntArray breaks = new IntArray();
		Node tempNode = null;
		for (Node node : activeNodes) {
			if (tempNode == null) {
				tempNode = node;
				continue;
			}

			// 找到当前 active node 里缺陷评估值最小的节点
			if (tempNode.demerits > node.demerits) {
				tempNode = node;
			}
		}

		// 把它之前的节点都找出来
		while (tempNode != null) {
			breaks.add(tempNode.state);
			tempNode = tempNode.link;
		}

		// 因为找节点的时候是逆序找的，所以要反转下
		breaks.reverse();

		return breaks;
	}

	/**
	 * 对一行进行排版
	 *
	 * @param index       当前第几个元素
	 * @param paragraph   paragraph 当前需要排版的段落
	 * @param activeNodes active nodes
	 * @param sum         sum 当前的总长度
	 * @param tolerance   允许的缺陷阈值
	 * @param candidates  可选调用者，只是为了减少内存占用而多的参数
	 * @param lineWidth   行宽
	 */
	private void typesetLine(final int index, Paragraph paragraph,
							 ActiveNodes activeNodes,
							 Sum sum,
							 final float tolerance,
							 Candidate[] candidates,
							 final float lineWidth) {
		Element element = paragraph.getElement(index);
		Node active = activeNodes.getHeader();
		// 保证前置条件正确
		Arrays.fill(candidates, null);

		while (active != null) {
			boolean needCreateNode = false;
			while (active != null) {
				Node next = active.next;
				int currentLine = active.line + 1;
				float ratio = computeAdjustRatio(element, active, sum, lineWidth);

				// 如果当前node对于当前元素压缩的太多了，那么就移除当前节点
				// 或者当前节点就是一个强制断点
				if (ratio < MIN_SHRINK_RATIO || (element instanceof Penalty &&
						((Penalty) element).getPenalty() == -INFINITY)) {
					activeNodes.remove(active);
				}

				// 如果当前的元素在可接受的排版空间里
				if (ratio >= MIN_SHRINK_RATIO && ratio <= tolerance) {
					// 计算下当前属于那种等级
					int currentFitnessClazz = computeFitnessClazz(ratio);
					// 计算如果从当前节点断点会有多差
					float demerits = computeDemerits(element, paragraph, ratio, active, currentFitnessClazz);

					// 记录本行排版时同等级下最好的节点
					if (candidates[currentFitnessClazz] == null || demerits < candidates[currentFitnessClazz].demerits) {
						needCreateNode = true;

						if (candidates[currentFitnessClazz] == null) {
							candidates[currentFitnessClazz] = Candidate.obtain();
						}

						candidates[currentFitnessClazz].active = active;
						candidates[currentFitnessClazz].demerits = demerits;
						candidates[currentFitnessClazz].ratio = ratio;
					}
				}

				active = next;

				// 铺满一行为一次分界
				if (active != null && active.line >= currentLine) {
					break;
				}
			}

			// 好于同等级下最好的节点就要加入 active list
			if (needCreateNode) {
				// 计算当前节点后距离下一个文字/强制断点的长度
				Sum tmpSum = computeSumAfter(index, paragraph, sum);
				// 插入节点
				appendActiveNode(active, index, activeNodes, tmpSum, candidates);
				tmpSum.recycle();

				// 清空候选名单
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

	/**
	 * 把候选者都加入到active 列表中
	 *
	 * @param active      前一个active节点
	 * @param index       当前元素下标
	 * @param activeNodes active list
	 * @param sum         当前元素的总长
	 * @param candidates  候选者
	 */
	private void appendActiveNode(Node active, int index, ActiveNodes activeNodes,
								  Sum sum, Candidate[] candidates) {
		for (int i = 0; i < candidates.length; ++i) {
			Candidate candidate = candidates[i];
			if (candidate == null) {
				continue;
			}

			Node node = Node.obtain();
			node.state = index;
			node.demerits = candidate.demerits;
			node.ratio = candidate.ratio;
			node.line = candidate.active.line + 1;
			node.fitness = i;
			node.totals = Sum.obtain(sum);
			node.link = candidate.active;

			if (active != null) {
				activeNodes.insertBefore(active, node);
			} else {
				activeNodes.pushBack(node);
			}
		}
	}

	/**
	 * @param ratio ratio 调整比例
	 * @return fitness 类别
	 */
	private int computeFitnessClazz(float ratio) {
		// 0.5 为一档
		// class 1 为最好
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

	/**
	 * 计算拉升收缩的调整比例
	 *
	 * @param element    当前的元素
	 * @param node       node
	 * @param sum        当前的总长度
	 * @param lineLength 行长度
	 * @return 调整比例
	 */
	private float computeAdjustRatio(Element element, Node node, Sum sum, float lineLength) {
		float width = sum.getWidth() - node.totals.getWidth();
		if (element instanceof Penalty) {
			width += getElementWidth(element);
		}

		// 当前长度小于 行长度，那么就需要拉伸
		// 同理则需要收缩
		if (width < lineLength) {
			float stretch = sum.getStretch() - node.totals.getStretch();
			return stretch > 0 ? (lineLength - width) / stretch : INFINITY;
		} else if (width > lineLength) {
			float shrink = sum.getShrink() - node.totals.getShrink();
			return shrink > 0 ? (lineLength - width) / shrink : INFINITY;
		}

		return 0;
	}

	/**
	 * 计算从当前节点分行的话，缺陷评估值是多少
	 *
	 * @param element             当前元素
	 * @param paragraph           段落
	 * @param ratio               伸缩比
	 * @param active              前一个active节点
	 * @param currentFitnessClazz 当前的fitness类别
	 * @return 缺陷评估值
	 */
	private float computeDemerits(Element element, Paragraph paragraph, float ratio, Node active, int currentFitnessClazz) {
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
				paragraph.getElement(active.state) instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			Penalty activePenalty = (Penalty) paragraph.getElement(active.state);
			if (penalty.isFlag() && activePenalty.isFlag()) {
				demerits += DEMERITS_FLAGGED;
			}
		}

		if (Math.abs(currentFitnessClazz - active.fitness) > 1) {
			demerits += DEMERITS_FITNESS;
		}

		// 叠加total demerits
		demerits += active.demerits;

		return demerits;
	}

	/**
	 * @param index     当前节点位置
	 * @param paragraph 文章段落
	 * @param sum       总长度
	 * @return 计算当前节点后距离下一个文字/强制断点的长度
	 */
	private Sum computeSumAfter(int index, Paragraph paragraph, Sum sum) {
		Sum result = Sum.obtain(sum);

		int size = paragraph.getElementCount();
		for (int i = index; i < size; ++i) {
			Element element = paragraph.getElement(i);
			if (element instanceof Glue) {
				Glue glue = (Glue) element;
				result.increase(glue);
			} else if (element instanceof Box ||
					(element instanceof Penalty &&
							((Penalty) element).getPenalty() == -INFINITY && i > index)) {
				break;
			}
		}
		return result;
	}

	@Override
	public Object getInternalState() {
		return __intArray;
	}

	private static void w(String msg) {
		Log.w("TextEngineTexTypesetter", msg);
	}
}
