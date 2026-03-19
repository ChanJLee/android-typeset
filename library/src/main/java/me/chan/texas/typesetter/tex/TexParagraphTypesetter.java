package me.chan.texas.typesetter.tex;

import static me.chan.texas.Texas.INFINITY_PENALTY;

import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;

import me.chan.texas.renderer.core.TypesetEngine;
import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Span;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Layout;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.typesetter.AbsParagraphTypesetter;
import me.chan.texas.typesetter.utils.ElementStream;
import me.chan.texas.utils.IntArray;
import me.chan.texas.utils.IntStack;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class TexParagraphTypesetter extends AbsParagraphTypesetter {
	private static final int CLASS_0 = 0;
	private static final int CLASS_1 = 1;
	private static final int CLASS_2 = 2;
	private static final int CLASS_3 = 3;

	@VisibleForTesting
	protected IntStack __stackInternal;

	@Nullable
	@Override
	protected boolean onTypeset(Paragraph paragraph, BreakStrategy breakStrategy, int width) {
		TypesetArgs args = buildTypesetArgs(paragraph, breakStrategy, width);

		int typesetRound = 1;
		boolean success = false;
		for (; typesetRound < args.retryTimes; ++typesetRound) {
			if (typeset0(args)) {
				success = true;
				break;
			}

			args.adjustMaxTolerance(typesetRound);
		}

		if (STATS != null) {
			STATS.record(args, typesetRound, success);
		}

		return success;
	}

	private TypesetArgs buildTypesetArgs(Paragraph paragraph, BreakStrategy breakStrategy, int width) {
		TypesetArgs typesetArgs = buildTypesetArgs0(paragraph, breakStrategy, width);
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
			typesetArgs.retryTimes = 40;
		}
		return typesetArgs;
	}

	private TypesetArgs buildTypesetArgs0(Paragraph paragraph, BreakStrategy breakStrategy, int width) {
		TypesetArgs args = new TypesetArgs(paragraph, breakStrategy, width);
		Layout layout = paragraph.getLayout();
		if (layout == null) {
			return args;
		}

		Layout.Advise advise = layout.getAdvise();
		if (advise == null) {
			return args;
		}

		if (advise.checkTypesetPolicy(Paragraph.TYPESET_POLICY_CJK_MIX_OPTIMIZATION)) {
			args.maxTolerance = 0.2f;
			args.toleranceInterceptor = CN_TOLERANCE_INTERCEPTOR;
		}

		return args;
	}

	private boolean typeset0(TypesetArgs args) {
		// 先获取active list，尽量从最小的伸缩度来计算
		ActiveNodes activeList = createActiveNodes(args);
		if (activeList == null || activeList.isEmpty()) {
			return false;
		}

		// 选择断点
		IntStack breakPoints = selectBreakPoints(activeList);
		if (DEBUG) {
			__stackInternal = new IntStack(breakPoints);
		}

		if (breakPoints.empty()) {
			return false;
		}

		// 将断点转换成行信息
		typesetParagraph(args.paragraph, breakPoints, args.breakStrategy, args.width);
		activeList.recycle();

		return true;
	}

	/**
	 * 获取 active node 列表
	 *
	 * @param args 排版参数
	 * @return active node 列表
	 */
	private ActiveNodes createActiveNodes(TypesetArgs args) {
		Context context = new Context(args);

		createActiveNodes0(context);

		context.total.recycle();
		return context.actives;
	}

	private void createActiveNodes0(Context context) {
		ElementStream stream = context.stream;
		while (!stream.eof() && !context.actives.isEmpty()) {
			int save = stream.state();
			Element element = stream.next();
			if (element instanceof Span) {
				context.total.increase((Span) element);
			} else if (element instanceof Glue) {
				if (stream.tryGet(save, -1) instanceof Span) {
					typesetLines(context, element, save);
				}

				context.total.increase((Glue) element);
			} else if (element instanceof Penalty &&
					((Penalty) element).getPenalty() != INFINITY_PENALTY) {
				typesetLines(context, element, save);
			}
		}
	}

	/**
	 * 对段落进行排版
	 *
	 * @param paragraph   段落
	 * @param breakPoints 可能的断点
	 * @return 排版好的段落
	 */
	private Paragraph typesetParagraph(Paragraph paragraph,
									   IntStack breakPoints,
									   BreakStrategy breakStrategy,
									   int lineWidth) {
		ElementStream stream = new ElementStream(paragraph);
		int size = paragraph.getElementCount();
		Layout layout = Layout.obtain(paragraph.getLayout());
		layout.setAlgorithm("tex");

		while (!breakPoints.empty()) {
			int breakPoint = breakPoints.pop();
			while (true) {
				if (stream.eof()) {
					return paragraph;
				}

				int save = stream.state();
				Element element = stream.next();
				if (element instanceof Span || (element instanceof Penalty && ((Penalty) element).getPenalty() == -INFINITY_PENALTY)) {
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
	private IntStack selectBreakPoints(ActiveNodes activeNodes) {
		IntStack breaks = new IntStack();
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
			// 剔除0
			if (tempNode.state > 0) {
				breaks.push(tempNode.state);
			}
			tempNode = tempNode.link;
		}

		return breaks;
	}

	private void typesetLines(Context context, Element element, int state) {
		Node active = context.actives.getHeader();

		while (active != null) {
			active = typesetLine0(context, active, element);
			selectCandidate(context, active, state);
		}
	}

	private void selectCandidate(Context context, Node active, int state) {
		if (context.candidates.isEmpty()) {
			return;
		}

		// 好于同等级下最好的节点就要加入 active list
		// 计算当前节点后距离下一个文字/强制断点的长度
		Sum left = computeSumAfter(context, state);
		// 插入节点
		createActiveNode(context, active, left, state);
		left.recycle();

		// 清空候选名单
		context.candidates.clear();
	}

	private Node typesetLine0(Context context, Node active, Element element) {
		while (active != null) {
			Node next = active.next;
			int currentLine = active.line + 1;
			float ratio = computeAdjustRatio(element, active, context, context.args.width);

			// 如果当前node对于当前元素压缩的太多了，那么就移除当前节点
			// 或者当前节点就是一个强制断点
			if (ratio < context.args.minTolerance || (element instanceof Penalty &&
					((Penalty) element).getPenalty() == -INFINITY_PENALTY)) {
				context.actives.remove(active);
			}

			// 如果当前的元素在可接受的排版空间里
			if (ratio >= context.args.minTolerance && ratio <= context.args.maxTolerance) {
				// 计算下当前属于那种等级
				int fitness = computeFitness(ratio);
				// 计算如果从当前节点断点会有多差
				float demerits = computeDemerits(element, context, ratio, active, fitness);
				// 记录本行排版时同等级下最好的节点
				context.candidates.update(fitness, demerits, active, ratio);
			}

			active = next;

			// 铺满一行为一次分界
			if (active != null && active.line >= currentLine) {
				break;
			}
		}

		return active;
	}

	/**
	 * 把候选者都加入到active 列表中
	 *
	 * @param context 上下文信息0
	 * @param active  前一个active节点
	 * @param sum     当前元素的总长
	 * @param state   状态
	 */
	private void createActiveNode(Context context,
								  Node active,
								  Sum sum,
								  int state) {
		ActiveNodes actives = context.actives;

		for (int i = 0; i < context.candidates.size(); ++i) {
			Candidate candidate = context.candidates.get(i);
			if (candidate == null) {
				continue;
			}

			Node node = Node.obtain();
			node.state = state;
			node.demerits = candidate.demerits;
			node.ratio = candidate.ratio;
			node.line = candidate.active.line + 1;
			node.fitness = i;
			node.totals = Sum.obtain(sum);
			node.link = candidate.active;

			if (active != null) {
				actives.insertBefore(active, node);
			} else {
				actives.pushBack(node);
			}
		}
	}

	/**
	 * @param ratio ratio 调整比例
	 * @return fitness 类别
	 */
	private int computeFitness(float ratio) {
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
	 * @param element   当前的元素
	 * @param node      active node
	 * @param context   上下文信息
	 * @param lineWidth 行长度
	 * @return 调整比例
	 */
	private float computeAdjustRatio(Element element, Node node, Context context, float lineWidth) {
		float width = context.total.getWidth() - node.totals.getWidth();
		if (element instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			width += penalty.getWidth();
		}

		// 当前长度小于 行长度，那么就需要拉伸
		// 同理则需要收缩
		if (width < lineWidth) {
			float stretch = context.total.getStretch() - node.totals.getStretch();
			return stretch > 0 ? (lineWidth - width) / stretch : INFINITY_PENALTY;
		} else if (width > lineWidth) {
			float shrink = context.total.getShrink() - node.totals.getShrink();
			return shrink > 0 ? (lineWidth - width) / shrink : INFINITY_PENALTY;
		}

		return 0;
	}

	/**
	 * 计算从当前节点分行的话，缺陷评估值是多少
	 *
	 * @param element 当前元素
	 * @param context context
	 * @param ratio   伸缩比
	 * @param active  前一个active节点
	 * @param fitness 当前的fitness类别
	 * @return 缺陷评估值
	 */
	private float computeDemerits(Element element, Context context, float ratio, Node active, int fitness) {
		float demerits = computeElementDemerits(element, context, ratio);

		// 额外附加的
		if (element instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			Element prev = context.stream.tryGet(active.state, 0);
			if (prev instanceof Penalty) {
				Penalty activePenalty = (Penalty) prev;
				if (penalty.isFlag() && activePenalty.isFlag()) {
					demerits += context.args.demeritsFlagged;
				}
			}
		}

		if (Math.abs(fitness - active.fitness) > 1) {
			demerits += context.args.demeritsFitness;
		}

		// 叠加total demerits
		demerits += active.demerits;

		return demerits;
	}

	private float computeElementDemerits(Element element, Context context, float ratio) {
		float badness = (float) (100 * Math.pow(Math.abs(ratio), 3));
		if (element instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			if (penalty.getPenalty() >= 0) {
				return (float) (
						Math.pow(context.args.demeritsLine + badness + ((Penalty) element).getPenalty(), 2)
				);
			}

			if (penalty.getPenalty() != -INFINITY_PENALTY) {
				return (float) (
						Math.pow(context.args.demeritsLine + badness, 2) -
								Math.pow(((Penalty) element).getPenalty(), 2)
				);
			}
		}

		return (float) (
				Math.pow(context.args.demeritsLine + badness, 2)
		);
	}

	/**
	 * @param context
	 * @return 计算当前节点后距离下一个文字/强制断点的长度
	 */
	private Sum computeSumAfter(Context context, int state) {
		Sum after = Sum.obtain(context.total);
		ElementStream stream = context.stream;
		int save = stream.state();

		try {
			stream.restore(state);
			while (!stream.eof()) {
				Element element = stream.next();
				if (element instanceof Glue) {
					Glue glue = (Glue) element;
					after.increase(glue);
				} else if (element instanceof Span ||
						(element instanceof Penalty &&
								((Penalty) element).getPenalty() == -INFINITY_PENALTY)) {
					break;
				}
			}
			return after;
		} finally {
			stream.restore(save);
		}
	}

	@Override
	public Object getInternalState() {
		return __stackInternal;
	}

	private static void w(String msg) {
		Log.w("TextEngineTexTypesetter", msg);
	}

	@RestrictTo(RestrictTo.Scope.LIBRARY)
	public static String stats() {
		return STATS == null ? "" : STATS.stats();
	}

	private static final ToleranceInterceptor DEFAULT_TOLERANCE_INTERCEPTOR = new ToleranceInterceptor() {
		@Override
		public float intercept(float tolerance, int typesetRound) {
			return tolerance + 0.2f;
		}
	};

	private static final ToleranceInterceptor CN_TOLERANCE_INTERCEPTOR = new ToleranceInterceptor() {
		@Override
		public float intercept(float tolerance, int typesetRound) {
			if (tolerance > 3) {
				return tolerance + 0.2f;
			}

			return tolerance + 0.3f;
		}
	};

	private interface ToleranceInterceptor {
		float intercept(float tolerance, int typesetRound);
	}

	private static class TypesetArgs {
		private final int width;
		private float maxTolerance = 0.6f;

		private ToleranceInterceptor toleranceInterceptor = DEFAULT_TOLERANCE_INTERCEPTOR;

		private final float minTolerance = -1;

		private final float demeritsLine = 1;

		// 对应 α
		private final float demeritsFlagged = 100;
		// 对应 γ
		private final float demeritsFitness = 3000;

		private int retryTimes = 60;

		private final Paragraph paragraph;
		private final BreakStrategy breakStrategy;

		public TypesetArgs(Paragraph paragraph, BreakStrategy breakStrategy, int width) {
			this.paragraph = paragraph;
			this.width = width;
			this.breakStrategy = breakStrategy;
		}

		public void adjustMaxTolerance(int typesetRound) {
			maxTolerance = toleranceInterceptor.intercept(maxTolerance, typesetRound);
		}

		@Override
		public String toString() {
			return "TypesetArgs{" +
					"width=" + width +
					'}';
		}
	}

	private static class Context {
		private final ElementStream stream;
		private final ActiveNodes actives = new ActiveNodes();
		private final Sum total;
		private final TypesetArgs args;
		private final Candidates candidates = new Candidates();

		public Context(TypesetArgs args) {
			this.stream = new ElementStream(args.paragraph);
			this.total = Sum.obtain();
			this.args = args;
		}

		@Override
		public String toString() {
			return "Context{" +
					"total=" + total +
					", args=" + args +
					'}';
		}
	}

	private static final Stats STATS = TypesetEngine.DEBUG ? new Stats() : null;

	private static class Stats {
		private int mRequestCount = 0;
		private int mSuccessCount = 0;

		private int mTypesetCount = 0;
		private int mLineCount = 0;

		/*
		 * 0 ~ 0.2
		 * 0.2 ~ 0.4
		 * 0.4 ~ 0.6
		 * 0.6 ~ 0.8
		 * 0.8 ~ 1
		 * 1 ~ 1.2
		 * etc
		 * */
		private final IntArray mLevelMap = new IntArray(8);

		public Stats() {
			mLevelMap.zero(8);
		}

		public synchronized void record(TypesetArgs args, int typesetRound, boolean success) {
			++mRequestCount;
			if (!success) {
				return;
			}

			Layout layout = args.paragraph.getLayout();
			for (int i = 0; i < layout.getLineCount(); ++i) {
				Line line = layout.getLine(i);
				float ratio = line.getRatio();
				int bucket = -1;
				if (ratio <= 0.2) {
					bucket = 0;
				} else if (ratio <= 0.4) {
					bucket = 1;
				} else if (ratio <= 0.6) {
					bucket = 2;
				} else if (ratio <= 0.8) {
					bucket = 3;
				} else if (ratio <= 1) {
					bucket = 4;
				} else if (ratio <= 1.2) {
					bucket = 5;
				} else {
					bucket = 6;
				}

				++mLineCount;
				mLevelMap.set(bucket, mLevelMap.get(bucket) + 1);
			}

			++mSuccessCount;
			mTypesetCount += typesetRound;
		}

		public synchronized String stats() {
			if (mRequestCount == 0) {
				return "<>";
			}

			StringBuilder builder = new StringBuilder()
					.append("request: ").append(mRequestCount)
					.append(", success rate: ").append(mSuccessCount * 1.0f / mRequestCount)
					.append(", typeset count: ").append(mTypesetCount * 1.0f / mRequestCount);
			builder.append(", level map: [");
			for (int i = 0; mLineCount != 0 && i < mLevelMap.size(); ++i) {
				builder.append(mLevelMap.get(i) * 100f / mLineCount)
						.append("%, ");
			}
			builder.append("]");
			return builder.toString();
		}
	}
}
