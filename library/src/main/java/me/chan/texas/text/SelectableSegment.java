package me.chan.texas.text;

import me.chan.texas.renderer.ui.text.ParagraphView;

/**
 * 可选中的segment协议
 * 1. segment中有多少ParagraphView需要被选中就返回多少
 * 2. segment中的ParagraphView的 {@link me.chan.texas.renderer.SpanTouchEventHandler} 以及 {@link me.chan.texas.renderer.ui.text.OnSelectedChangedListener} 会被替换。
 */
public interface SelectableSegment {
	/**
	 * @return 当前有多少段落需要参与选中
	 */
	int getParagraphCount();

	/**
	 * @param index 对应的下标
	 * @return 当前段落的view
	 */
	ParagraphView getParagraphView(int index);

	/**
	 * @param index 对应的下标
	 * @return 当前段落
	 */
	Paragraph getParagraph(int index);
}
