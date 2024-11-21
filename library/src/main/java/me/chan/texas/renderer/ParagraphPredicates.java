package me.chan.texas.renderer;

import androidx.annotation.Nullable;

import me.chan.texas.annotations.Idempotent;

public interface ParagraphPredicates {

	/**
	 * @param spanTag {@link me.chan.texas.text.Paragraph.SpanBuilder#tag(Object)}
	 * @return 是否选中，这个函数必须是幂等的
	 */
	@Idempotent
	boolean acceptSpan(@Nullable Object spanTag);

	/**
	 * @param paragraphTag {@link me.chan.texas.text.Paragraph.Builder#tag(Object)}
	 * @return 是否选中，这个函数必须是幂等的
	 */
	@Idempotent
	boolean acceptParagraph(@Nullable Object paragraphTag);
}
