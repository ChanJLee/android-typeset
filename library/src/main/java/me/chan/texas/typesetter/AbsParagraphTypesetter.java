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


	public abstract boolean typeset(Paragraph paragraph, BreakStrategy breakStrategy, int lineWidth);

	/**
	 * @return debug 信息
	 */
	public Object getInternalState() {
		return null;
	}

	/**
	 * 创建行
	 *
	 * @param stream   stream
	 * @param endState 结束位置
	 * @return 行
	 */
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

	/**
	 * @param glue glue
	 * @return 能显示为一个空格的glue
	 */
	public static boolean isDenotation(Glue glue) {
		return glue != null && glue != Glue.EMPTY && glue != Glue.TERMINAL;
	}

	/**
	 * @param penalty penalty
	 * @return 能追加到 text box后面的连字符
	 */
	public static boolean isDenotation(Penalty penalty) {
		return penalty != null && !penalty.isFlag() &&
				penalty != Penalty.FORBIDDEN_BREAK && penalty != Penalty.FORCE_BREAK;
	}
}
