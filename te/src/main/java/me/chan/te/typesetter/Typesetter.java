package me.chan.te.typesetter;

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
	 * @param policy         {@link Policy}
	 * @return 排版好的文章段落
	 */
	Paragraph typeset(Segment segment, LineAttributes lineAttributes, Policy policy);

	enum Policy {
		/**
		 * 尽可能的排满一行
		 */
		FIT,
		/**
		 * 两边对齐
		 */
		FILL
	}
}
