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
import me.chan.texas.text.layout.Box;
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
	public boolean typeset(Paragraph paragraph, BreakStrategy breakStrategy, int width) {
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

		ActiveNodes activeList = createActiveNodes(args);
		if (activeList == null || activeList.isEmpty()) {
			return false;
		}


		IntStack breakPoints = selectBreakPoints(activeList);
		if (DEBUG) {
			__stackInternal = new IntStack(breakPoints);
		}

		if (breakPoints.empty()) {
			return false;
		}


		typesetParagraph(args.paragraph, breakPoints, args.breakStrategy, args.width);
		activeList.recycle();

		return true;
	}

	
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
			if (element instanceof Box) {
				context.total.increase((Box) element);
			} else if (element instanceof Glue) {
				if (stream.tryGet(save, -1) instanceof Box) {
					typesetLines(context, element, save);
				}

				context.total.increase((Glue) element);
			} else if (element instanceof Penalty &&
					((Penalty) element).getPenalty() != INFINITY_PENALTY) {
				typesetLines(context, element, save);
			}
		}
	}

	
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
				if (element instanceof Box || (element instanceof Penalty && ((Penalty) element).getPenalty() == -INFINITY_PENALTY)) {
					stream.restore(save);
					break;
				}
			}


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

	
	private IntStack selectBreakPoints(ActiveNodes activeNodes) {
		IntStack breaks = new IntStack();
		Node tempNode = null;
		for (Node node : activeNodes) {
			if (tempNode == null) {
				tempNode = node;
				continue;
			}


			if (tempNode.demerits > node.demerits) {
				tempNode = node;
			}
		}


		while (tempNode != null) {

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



		Sum left = computeSumAfter(context, state);

		createActiveNode(context, active, left, state);
		left.recycle();


		context.candidates.clear();
	}

	private Node typesetLine0(Context context, Node active, Element element) {
		while (active != null) {
			Node next = active.next;
			int currentLine = active.line + 1;
			float ratio = computeAdjustRatio(element, active, context, context.args.width);



			if (ratio < context.args.minTolerance || (element instanceof Penalty &&
					((Penalty) element).getPenalty() == -INFINITY_PENALTY)) {
				context.actives.remove(active);
			}


			if (ratio >= context.args.minTolerance && ratio <= context.args.maxTolerance) {

				int fitness = computeFitness(ratio);

				float demerits = computeDemerits(element, context, ratio, active, fitness);

				context.candidates.update(fitness, demerits, active, ratio);
			}

			active = next;


			if (active != null && active.line >= currentLine) {
				break;
			}
		}

		return active;
	}

	
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

	
	private int computeFitness(float ratio) {


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

	
	private float computeAdjustRatio(Element element, Node node, Context context, float lineWidth) {
		float width = context.total.getWidth() - node.totals.getWidth();
		if (element instanceof Penalty) {
			Penalty penalty = (Penalty) element;
			width += penalty.getWidth();
		}



		if (width < lineWidth) {
			float stretch = context.total.getStretch() - node.totals.getStretch();
			return stretch > 0 ? (lineWidth - width) / stretch : INFINITY_PENALTY;
		} else if (width > lineWidth) {
			float shrink = context.total.getShrink() - node.totals.getShrink();
			return shrink > 0 ? (lineWidth - width) / shrink : INFINITY_PENALTY;
		}

		return 0;
	}

	
	private float computeDemerits(Element element, Context context, float ratio, Node active, int fitness) {
		float demerits = computeElementDemerits(element, context, ratio);


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
				} else if (element instanceof Box ||
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


		private final float demeritsFlagged = 100;

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
