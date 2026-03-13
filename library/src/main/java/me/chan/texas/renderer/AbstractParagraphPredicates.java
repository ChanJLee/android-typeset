package me.chan.texas.renderer;

import androidx.annotation.Nullable;

/**
 * {@link ParagraphPredicates} 的抽象实现，{@link #acceptParagraph(Object)} 默认返回 true。
 * 当只关心 Span 级别的筛选时，只需实现 {@link #acceptSpan(Object)} 即可。
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
    public boolean acceptParagraph(@Nullable Object paragraphTag) {
        return true;
    }
}
