package me.chan.texas.renderer.highlight;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.ui.RendererAdapter;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.Segment;

import java.util.ArrayList;
import java.util.List;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Highlight {
	private final RendererAdapter mAdapter;
	private final List<ParagraphHighlight> mHighlights = new ArrayList<>();

	// todo 切换adapter的时候清除highlight

	public Highlight(RendererAdapter adapter) {
		mAdapter = adapter;
	}

	public int getFirstIndexInDocument() {
		if (isEmpty()) {
			return -1;
		}

		ParagraphHighlight highlight = mHighlights.get(0);
		return mAdapter.indexOf(highlight.getParagraph());
	}

	public boolean isEmpty() {
		return mHighlights.isEmpty();
	}

	public ParagraphHighlight get(int index) {
		return mHighlights.get(index);
	}

	public int getCount() {
		return mHighlights.size();
	}

	public void clear() {
		for (ParagraphHighlight highlight : mHighlights) {
			highlight.clear();
			int index = mAdapter.indexOf(highlight.getParagraph());
			if (index < 0) {
				continue;
			}

			try {
				mAdapter.notifyItemChanged(index);
			} catch (Throwable ignore) {
				/* do nothing */
			}
		}
		mHighlights.clear();
	}

	@Nullable
	public ParagraphHighlight getParagraphHighlight(Paragraph paragraph) {
		for (ParagraphHighlight paragraphHighlight : mHighlights) {
			if (paragraphHighlight.getParagraph() == paragraph) {
				return paragraphHighlight;
			}
		}

		return null;
	}

	public void add(@NonNull ParagraphHighlight paragraphHighlight) {
		mHighlights.add(paragraphHighlight);
	}
}