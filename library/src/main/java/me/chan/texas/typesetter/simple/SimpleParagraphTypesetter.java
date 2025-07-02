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
			if (element instanceof Box) {
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

		int beforeState = stream.state();


		float leftWidth = lineWidth;
		while (!stream.eof() && leftWidth >= 0) {
			leftWidth = tryTypesetUnit(stream, breaks, leftWidth);
		}


		int afterState = stream.state();


		stream.restore(beforeState);


		if (breaks.empty()) {
			forceBreak(stream, breaks, beforeState, afterState, leftWidth);
		}

		if (breaks.empty()) {
			throw new IllegalStateException("no break found");
		}


		stream.restore(beforeState);

		typesetUnit(layout, stream, breaks.top(), breakStrategy, lineWidth);
	}

	private float tryTypesetUnit(ElementStream stream, IntStack breaks, float width) {
		int save = stream.state();

		Element element = stream.next();
		if (element instanceof Box) {
			Box box = (Box) element;
			width -= box.getWidth();
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
			breaks.push(save);
			return 0;
		}

		if (isDenotation(penalty)) {
			width -= penalty.getWidth();
			if (width >= 0 && isBreakable(stream)) {
				breaks.push(stream.state());
			}
			return width;
		}

		
		return width;
	}

	
	private static boolean isDenotation(Glue glue) {
		return glue != null && glue != Glue.EMPTY && glue != Glue.TERMINAL;
	}

	
	private static boolean isDenotation(Penalty penalty) {
		return penalty != null && !penalty.isFlag() &&
				penalty != Penalty.FORBIDDEN_BREAK && penalty != Penalty.FORCE_BREAK;
	}

	private static float getElementWidth(Element element) {
		if (element == null) {
			return 0;
		}

		if (element instanceof Box) {
			return ((Box) element).getWidth();
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



		breaks.push(endState);
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
