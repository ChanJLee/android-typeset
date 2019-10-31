package me.chan.te.typesetter;

import me.chan.te.text.BreakStrategy;
import me.chan.te.config.LineAttributes;
import me.chan.te.text.Paragraph;

/**
 * 排版器
 */
public interface Typesetter {
	float INFINITY = 1000;
	int HYPHEN_PENALTY = 100;
	float DEMERITS_LINE = 1;
	// 对应 α
	float DEMERITS_FLAGGED = 100;
	// 对应 γ
	float DEMERITS_FITNESS = 3000;
	int MAX_RELAYOUT_TIMES = 30;
	float MIN_SHRINK_RATIO = -0.2f;
	float STRETCH_STEP_RATIO = 0.2f;

	/**
	 * @param paragraph      paragraph
	 * @param lineAttributes 行信息
	 * @param breakStrategy  {@link BreakStrategy}
	 * @return 是否排版成功
	 */
	boolean typeset(Paragraph paragraph, LineAttributes lineAttributes, BreakStrategy breakStrategy);
}
