package me.chan.te.typesetter;

import me.chan.te.text.BreakStrategy;
import me.chan.te.config.LineAttributes;
import me.chan.te.data.Paragraph;
import me.chan.te.data.Segment;

/**
 * 排版器
 */
public interface Typesetter {
	/**
	 * @param segment        文章段落
	 * @param lineAttributes 行信息
	 * @param breakStrategy  {@link BreakStrategy}
	 * @return 排版好的文章段落
	 */
	Paragraph typeset(Segment segment, LineAttributes lineAttributes, BreakStrategy breakStrategy);
}
