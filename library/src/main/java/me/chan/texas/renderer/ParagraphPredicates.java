package me.chan.texas.renderer;

import androidx.annotation.NonNull;

import me.chan.texas.annotations.Idempotent;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Span;

public interface ParagraphPredicates {

	/**
	 * @param span {@link me.chan.texas.text.Paragraph.SpanBuilder#tag(Object)}
	 * @return 是否选中，这个函数必须是幂等的
	 */
	@Idempotent
	boolean acceptSpan(@NonNull Span span);

	/**
	 * @param paragraph {@link me.chan.texas.text.Paragraph.Builder#tag(Object)}
	 * @return 是否选中，这个函数必须是幂等的
	 */
	@Idempotent
	boolean acceptParagraph(@NonNull Paragraph paragraph);
}
