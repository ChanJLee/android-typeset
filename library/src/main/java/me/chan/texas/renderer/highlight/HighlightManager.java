package me.chan.texas.renderer.highlight;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.highlight.visitor.HighlightParagraphVisitor;
import me.chan.texas.renderer.ui.RendererAdapter;
import me.chan.texas.text.Document;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class HighlightManager {
	private final RendererAdapter mAdapter;
	/**
	 * 当前高亮
	 */
	private Highlight mHighlight;
	private final HighlightParagraphVisitor mHighlightParagraphVisitor = new HighlightParagraphVisitor();

	public HighlightManager(RendererAdapter adapter) {
		mAdapter = adapter;
	}

	public void clear() {
		if (mHighlight != null) {
			mHighlight.clear();
			mHighlight = null;
		}
	}

	public Highlight highlightParagraphs(TexasView.HighlightPredicate predicate) {
		Document document = mAdapter.getDocument();
		if (document == null) {
			return null;
		}

		// 清除之前的高亮
		clear();

		// 找到我们需要找到的paragraph
		int count = document.getSegmentCount();
		for (int i = 0; i < count; ++i) {
			Segment segment = document.getSegment(i);
			if (segment == null || segment.isRecycled()) {
				continue;
			}

			if (!(segment instanceof Paragraph)) {
				continue;
			}

			Paragraph paragraph = (Paragraph) segment;
			ParagraphHighlight paragraphHighlight = highlightParagraph(paragraph, predicate);
			if (paragraphHighlight == null) {
				continue;
			}

			handleParagraphHighlighted(paragraph, paragraphHighlight, i);
		}

		return mHighlight;
	}

	private void handleParagraphHighlighted(Paragraph paragraph, ParagraphHighlight paragraphHighlight, int index) {
		if (mHighlight == null) {
			mHighlight = new Highlight(mAdapter);
		}

		mHighlight.add(paragraph, paragraphHighlight);

		try {
			mAdapter.sendSignal(index, RendererAdapter.SIG_HIGHLIGHT_CHANGED);
		} catch (Throwable ignore) {
			/* do nothing */
		}
	}

	private ParagraphHighlight highlightParagraph(Paragraph paragraph, TexasView.HighlightPredicate predicate) {
		try {
			mHighlightParagraphVisitor.setParams(predicate);
			mHighlightParagraphVisitor.visit(paragraph);
			return mHighlightParagraphVisitor.getParagraphHighlight();
		} catch (ParagraphVisitor.VisitException ignore) {
			/* do nothing */
		} finally {
			mHighlightParagraphVisitor.clear();
		}
		return null;
	}
}
