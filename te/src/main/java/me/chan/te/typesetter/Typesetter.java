package me.chan.te.typesetter;

import me.chan.te.text.BreakStrategy;
import me.chan.te.config.LineAttributes;
import me.chan.te.text.Figure;
import me.chan.te.text.Paragraph;
import me.chan.te.text.Segment;

/**
 * 排版器
 */
public abstract class Typesetter {

	public static float INFINITY = 1000;
	public static int HYPHEN_PENALTY = 100;
	public static float DEMERITS_LINE = 1;
	// 对应 α
	public static float DEMERITS_FLAGGED = 100;
	// 对应 γ
	public static float DEMERITS_FITNESS = 3000;
	public static int MAX_RELAYOUT_TIMES = 30;
	public static float MIN_SHRINK_RATIO = -0.2f;
	public static float STRETCH_STEP_RATIO = 0.2f;

	/**
	 * @param segment        segment
	 * @param lineAttributes 行信息
	 * @param breakStrategy  {@link BreakStrategy}
	 * @return 是否排版成功
	 */
	public final boolean typeset(Segment segment, LineAttributes lineAttributes, BreakStrategy breakStrategy) {
		if (segment instanceof Paragraph) {
			return typeset((Paragraph) segment, lineAttributes, breakStrategy);
		}

		if (segment instanceof Figure) {
			return typeset((Figure) segment, lineAttributes);
		}

		return false;
	}

	protected boolean typeset(Figure figure, LineAttributes lineAttributes) {
		LineAttributes.Attribute attribute = lineAttributes.getDefaultAttribute();

		float lineWidth = attribute.getLineWidth();

		float width = figure.getWidth();
		float height = figure.getHeight();

		float ratio = width <= 0 || height <= 0 ? Figure.DEFAULT_RATIO : height / width;
		figure.setWidth(lineWidth);
		figure.setHeight(lineWidth / ratio);

		return true;
	}

	/**
	 * @param paragraph      paragraph
	 * @param lineAttributes 行信息
	 * @param breakStrategy  {@link BreakStrategy}
	 * @return 是否排版成功
	 */
	protected abstract boolean typeset(Paragraph paragraph, LineAttributes lineAttributes, BreakStrategy breakStrategy);
}
