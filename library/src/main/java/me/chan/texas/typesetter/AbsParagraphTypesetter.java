package me.chan.texas.typesetter;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import androidx.annotation.RestrictTo;

import me.chan.texas.text.BreakStrategy;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Element;
import me.chan.texas.text.layout.Glue;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.Penalty;
import me.chan.texas.typesetter.utils.ElementStream;

@RestrictTo(LIBRARY)
public abstract class AbsParagraphTypesetter {
	public static final boolean DEBUG = false;
	public static final int INFINITY_WIDTH = Integer.MAX_VALUE;


	public abstract boolean typeset(Paragraph paragraph, BreakStrategy breakStrategy, int lineWidth);

	
	public Object getInternalState() {
		return null;
	}

	
	protected static Line createLine(ElementStream stream, int endState, BreakStrategy breakStrategy, int lineWidth) {
		Line.Builder builder = Line.Builder.obtain();

		while (!stream.eof() && !stream.checkState(endState)) {
			Element element = stream.next();
			builder.add(element);
		}

		Line line = builder.build(breakStrategy, lineWidth);
		builder.recycle();
		return line;
	}
}
