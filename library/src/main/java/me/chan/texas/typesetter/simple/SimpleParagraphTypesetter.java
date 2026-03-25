package me.chan.texas.typesetter.simple;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;

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
import me.chan.texas.utils.IntStack;

@RestrictTo(LIBRARY)
public class SimpleParagraphTypesetter extends AbsParagraphTypesetter {
	// 添加分析

	@Override
	protected boolean onTypeset(Paragraph paragraph,
								BreakStrategy breakStrategy,
								int width) {

		return typeset0(
				new ElementStream(paragraph),
				breakStrategy,
				width,
				paragraph,
				new IntStack()
		);
	}

	private boolean typeset0(ElementStream stream,
							 BreakStrategy breakStrategy,
							 int lineWidth,
							 Paragraph paragraph,
							 IntStack stack) {
		Layout layout = Layout.obtain(paragraph.getLayout());
		layout.setAlgorithm("simple");
		eat(stream);
		while (!stream.eof()) {
			int state = stream.state();
			typesetLine(stream,
					breakStrategy,
					lineWidth,
					layout,
					stack
			);

			if (stream.checkState(state)) {
				throw new IllegalStateException("state not changed");
			}

			eat(stream);
		}

		layout = paragraph.swap(layout);
		if (layout != null) {
			layout.recycle();
		}

		/* always true */
		return true;
	}

	private void typesetLine(ElementStream stream,
							 BreakStrategy breakStrategy,
							 int lineWidth,
							 Layout layout,
							 IntStack breaks) {
		breaks.clear();
		typesetLine0(stream, breakStrategy, lineWidth, layout, breaks);
	}

	private void eat(ElementStream stream) {
		while (!stream.eof()) {
			int save = stream.state();
			Element element = stream.next();
			if (element instanceof Span) {
				stream.restore(save);
				break;
			}

			boolean isBrkSemantic = element == Glue.TERMINAL || element == Penalty.FORCE_BREAK;
			if (isBrkSemantic && !stream.isUnderTerminalSemanticState()) {
				stream.restore(save);
				break;
			}
		}
	}

	private void typesetLine0(ElementStream stream,
							  BreakStrategy breakStrategy,
							  int lineWidth,
							  Layout layout,
							  IntStack breaks) {
		// 保存现在的状态
		int beforeState = stream.state();

		// pre-typeset
		float leftWidth = lineWidth;
		while (!stream.eof() && leftWidth >= 0) {
			leftWidth = tryTypesetUnit(stream, breaks, leftWidth);
		}

		// 记录pre-typeset后的位置
		int afterState = stream.state();

		// 回退状态
		stream.restore(beforeState);

		// 没有找到合适的位置可以断行
		if (breaks.empty()) {
			forceBreak(stream, breaks, beforeState, afterState, leftWidth);
		}

		if (breaks.empty()) {
			throw new IllegalStateException("no break found");
		}

		// 回退状态
		stream.restore(beforeState);

		typesetUnit(layout, stream, breaks.top(), breakStrategy, lineWidth);
	}

	private float tryTypesetUnit(ElementStream stream, IntStack breaks, float width) {
		int save = stream.state();

		Element element = stream.next();
		if (element instanceof Span) {
			Span span = (Span) element;
			width -= span.getWidth();
			return width;
		}

		if (element instanceof Glue) {
			if (element == Glue.TERMINAL) {
				breaks.push(save);
				return 0;
			}

			Glue glue = (Glue) element;
			width -= glue.getWidth();

			if (isBreakable(stream)) {
				breaks.push(save);
			}
			return width;
		}

		Penalty penalty = (Penalty) element;
		assert penalty != null;

		if (penalty == Penalty.FORCE_BREAK) {
			breaks.push(stream.state());
			return -1;
		}

		if (isDenotation(penalty)) {
			width -= penalty.getWidth();
			if (width >= 0 && isBreakable(stream)) {
				breaks.push(stream.state());
			}
			return width;
		}

		/* do nothing */
		return width;
	}

	/**
	 * @param glue glue
	 * @return 能显示为一个空格的glue
	 */
	private static boolean isDenotation(Glue glue) {
		return glue != null && glue != Glue.EMPTY && glue != Glue.TERMINAL;
	}

	/**
	 * @param penalty penalty
	 * @return 能追加到 text box后面的连字符
	 */
	private static boolean isDenotation(Penalty penalty) {
		return penalty != null && !penalty.isFlag() &&
				penalty != Penalty.FORBIDDEN_BREAK && penalty != Penalty.FORCE_BREAK;
	}

	private static float getElementWidth(Element element) {
		if (element == null) {
			return 0;
		}

		if (element instanceof Span) {
			return ((Span) element).getWidth();
		}

		if (element instanceof Glue) {
			if (element == Glue.TERMINAL) {
				return 0;
			}
			return ((Glue) element).getWidth();
		}

		Penalty penalty = (Penalty) element;
		if (penalty != Penalty.FORBIDDEN_BREAK &&
				penalty != Penalty.FORCE_BREAK &&
				penalty != Penalty.ADVISE_BREAK) {
			return penalty.getWidth();
		}

		return 0;
	}

	/**
	 * @param stream stream
	 * @return 是否可以断行
	 */
	private static boolean isBreakable(ElementStream stream) {
		Element prev = stream.tryGet(-2);
		Element next = stream.tryGet(0);
		return prev != Penalty.FORBIDDEN_BREAK &&
				next != Penalty.FORBIDDEN_BREAK;
	}

	private void forceBreak(ElementStream stream,
							IntStack breaks,
							final int startState,
							final int endState,
							float leftWidth) {
		// pre-condition 第一个元素一定是box
		if (startState >= endState) {
			throw new IllegalStateException("startState >= endState");
		}

		stream.restore(endState);
		while (stream.state() != startState) {
			Element element = stream.prev();
			if (!(element instanceof Penalty) ||
					element == Penalty.FORBIDDEN_BREAK ||
					element == Penalty.FORCE_BREAK ||
					element == Penalty.ADVISE_BREAK) {
				leftWidth += getElementWidth(element);
				continue;
			}

			float elementWidth = getElementWidth(element);
			if (leftWidth - elementWidth >= 0) {
				int candidate = stream.pickState(stream.state(), 1);
				breaks.push(candidate);
				return;
			}
		}

		stream.restore(endState);
		while (stream.state() != startState) {
			Element element = stream.prev();
			if (element instanceof Glue && isDenotation((Glue) element)) {
				breaks.push(stream.state());
				return;
			}
		}

		// 实在找不到断点
		// 一般情况不存在
		breaks.push(endState);
	}

	private void typesetUnit(Layout layout, ElementStream stream, int endState,
							 BreakStrategy breakStrategy, int lineWidth) {
		Line line = createLine(stream, endState, breakStrategy, lineWidth);
		layout.addLine(line);
	}
}
