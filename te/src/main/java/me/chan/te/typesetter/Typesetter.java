package me.chan.te.typesetter;

import me.chan.te.text.BreakStrategy;
import me.chan.te.config.LineAttributes;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;

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
	 * @param segment        文章段落
	 * @param lineAttributes 行信息
	 * @param breakStrategy  {@link BreakStrategy}
	 * @return 排版好的文章段落
	 */
	Paragraph typeset(Segment segment, LineAttributes lineAttributes, BreakStrategy breakStrategy);
}
