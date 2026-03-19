package me.chan.texas.renderer;

import androidx.annotation.NonNull;

import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Span;

/**
 * {@link ParagraphPredicates} 的抽象实现，{@link #acceptParagraph(Paragraph)} 默认返回 true。
 * 当只关心 Span 级别的筛选时，只需实现 {@link #acceptSpan(Span)} 即可。
 * <p>
 * 使用示例：
 * <pre>
 * texasView.highlightParagraphs(new AbstractParagraphPredicates() {
 *     {@literal @}Override
 *     public boolean acceptSpan(@Nullable Object spanTag) {
 *         return spanTag instanceof SpanTag
 *             && ((SpanTag) spanTag).word.contains(keyword);
 *     }
 * });
 * </pre>
 */
public abstract class AbstractParagraphPredicates implements ParagraphPredicates {

	@Override
	public boolean acceptParagraph(@NonNull Paragraph paragraph) {
		return true;
	}
}
