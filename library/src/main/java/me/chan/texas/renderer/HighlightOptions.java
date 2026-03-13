package me.chan.texas.renderer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import me.chan.texas.renderer.selection.Selection;

/**
 * 高亮配置，用于简化 {@link TexasView#highlightParagraphs(HighlightOptions)} 的调用。
 * <p>
 * 使用示例：
 * <pre>
 * texasView.highlightParagraphs(HighlightOptions.builder()
 *     .predicates(predicates)
 *     .styles(Selection.Styles.create(Color.YELLOW, Color.BLACK))
 *     .scrollTo(true)
 *     .scrollOffset(0)
 *     .build());
 * </pre>
 */
public final class HighlightOptions {

    @NonNull
    private final ParagraphPredicates predicates;
    @Nullable
    private final Selection.Styles styles;
    private final boolean scrollTo;
    private final int scrollOffset;

    private HighlightOptions(Builder builder) {
        this.predicates = builder.predicates;
        this.styles = builder.styles;
        this.scrollTo = builder.scrollTo;
        this.scrollOffset = builder.scrollOffset;
    }

    @NonNull
    public ParagraphPredicates getPredicates() {
        return predicates;
    }

    @Nullable
    public Selection.Styles getStyles() {
        return styles;
    }

    public boolean isScrollTo() {
        return scrollTo;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    @NonNull
    public static Builder builder(@NonNull ParagraphPredicates predicates) {
        return new Builder(predicates);
    }

    public static class Builder {
        private final ParagraphPredicates predicates;
        private Selection.Styles styles;
        private boolean scrollTo;
        private int scrollOffset;

        Builder(@NonNull ParagraphPredicates predicates) {
            this.predicates = predicates;
        }

        /**
         * 设置高亮样式，null 时使用默认高亮样式
         */
        @NonNull
        public Builder styles(@Nullable Selection.Styles styles) {
            this.styles = styles;
            return this;
        }

        /**
         * 高亮后是否滚动到高亮区域
         */
        @NonNull
        public Builder scrollTo(boolean scrollTo) {
            this.scrollTo = scrollTo;
            return this;
        }

        /**
         * 滚动时的偏移量
         */
        @NonNull
        public Builder scrollOffset(int offset) {
            this.scrollOffset = offset;
            return this;
        }

        @NonNull
        public HighlightOptions build() {
            return new HighlightOptions(this);
        }
    }
}
