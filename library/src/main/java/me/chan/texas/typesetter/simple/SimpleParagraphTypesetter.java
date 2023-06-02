package me.chan.texas.typesetter.simple;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;

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
import me.chan.texas.utils.IntStack;

@RestrictTo(LIBRARY)
public class SimpleParagraphTypesetter extends AbsParagraphTypesetter {
	// 添加分析

	@Override
	public boolean typeset(Paragraph paragraph,
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
							 IntStack stack) {
		stack.clear();

		// 剔除非box开头项
		while (!stream.eof()) {
			int save = stream.state();
			Element element = stream.next();
			if (element instanceof Box) {
				stream.restore(save);
				break;
			}
		}

		typesetLine0(stream, breakStrategy, lineWidth, layout, stack);
	}

	private void typesetLine0(ElementStream stream,
							  BreakStrategy breakStrategy,
							  int lineWidth,
							  Layout layout,
							  IntStack stack) {
		// 保存现在的状态
		int prevState = stream.state();

		// pre-typeset
		int left = lineWidth;
		while (!stream.eof() && left >= 0) {
			left = tryTypesetUnit(stream, stack, left);
		}

		// 记录pre-typeset后的位置
		int lastState = stream.state();

		// 回退状态
		stream.restore(prevState);

		// 没有找到合适的位置可以断行
		if (stack.empty()) {
			forceBreak(stream, stack, prevState, stream.pickState(lastState, -1) /* 最后一个元素已经被读入了 */);
		}

		typesetUnit(layout, stream, stack.top(), breakStrategy, lineWidth);
	}

	private int tryTypesetUnit(ElementStream stream,
							   IntStack stack, int left) {
		int save = stream.state();

		Element element = stream.next();
		if (element instanceof Box) {
			Box box = (Box) element;
			left -= box.getWidth();
			return left;
		}

		if (element instanceof Glue) {
			if (element == Glue.TERMINAL) {
				// 这个依赖外部输入
				return 0;
			}

			Glue glue = (Glue) element;
			left -= glue.getWidth();

			Element prev = stream.tryGet(-2);
			Element next = stream.tryGet(0);
			if (prev != Penalty.FORBIDDEN_BREAK &&
					next != Penalty.FORBIDDEN_BREAK) {
				stack.push(save);
			}
			return left;
		}

		Penalty penalty = (Penalty) element;
		assert penalty != null;

		if (penalty == Penalty.FORCE_BREAK) {
			stack.push(save);
			return 0;
		}

		if (isDenotation(penalty)) {
			left -= penalty.getWidth();
			return left;
		}

		/* do nothing */
		return left;
	}

	private void forceBreak(ElementStream stream,
							IntStack stack,
							int startState, int endState) {
		// pre-condition 第一个元素一定是box

		// 不能前进一步就是当前box实在太大了
		if (startState == endState) {
			stack.push(stream.pickState(startState, 1));
			return;
		}

		try {
			stream.restore(startState);
			Element first = stream.next();

			stream.restore(endState);
			// 从后往前找，找到空格就允许断
			int offset = 0;
			Element last = null;
			while ((last = stream.tryGet(--offset)) != first && last != null) {
				if (last instanceof Glue && isDenotation((Glue) last)) {
					stack.push(stream.pickState(endState, offset));
					return;
				}
			}

			// 实在找不到断点
			// 一般情况不存在
			stack.push(endState);
		} finally {
			stream.restore(startState);
		}
	}

	private void typesetUnit(Layout layout, ElementStream stream, int endState,
							 BreakStrategy breakStrategy, int lineWidth) {
		Line line = createLine(stream, endState, breakStrategy, lineWidth);
		if (line.isEmpty()) {
			return;
		}

		layout.addLine(line);
	}
}
