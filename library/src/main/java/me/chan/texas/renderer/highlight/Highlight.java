package me.chan.texas.renderer.highlight;

import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.ui.TexasAdapter;
import me.chan.texas.text.Paragraph;

import java.util.ArrayList;
import java.util.List;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Highlight {
	private final TexasAdapter mAdapter;
	private final List<ParagraphHighlight> mHighlights = new ArrayList<>();
	private final int mFirstIndex;

	public Highlight(TexasAdapter adapter, int index) {
		mAdapter = adapter;
		mFirstIndex = index;
	}

	public int getFirstIndexInDocument() {
		return mFirstIndex;
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

			try {
				mAdapter.notifyItemChanged(highlight.getIndex());
			} catch (Throwable ignore) {
				/* do nothing */
			}
		}
		mHighlights.clear();
	}

	public ParagraphHighlight getParagraphHighlight(Paragraph paragraph) {
		for (ParagraphHighlight paragraphHighlight : mHighlights) {
			if (paragraphHighlight.getParagraph() == paragraph) {
				return paragraphHighlight;
			}
		}

		return null;
	}

	public void add(ParagraphHighlight paragraphHighlight) {
		mHighlights.add(paragraphHighlight);
	}
}