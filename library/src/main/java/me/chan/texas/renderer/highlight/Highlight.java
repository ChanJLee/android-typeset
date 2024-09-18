package me.chan.texas.renderer.highlight;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.renderer.ui.RendererAdapter;
import me.chan.texas.text.Paragraph;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Highlight {
	private final RendererAdapter mAdapter;
	private final List<Paragraph> mHighlights = new ArrayList<>();

	// todo 切换adapter的时候清除highlight

	public Highlight(RendererAdapter adapter) {
		mAdapter = adapter;
	}

	public int getFirstIndexInDocument() {
		if (isEmpty()) {
			return -1;
		}

		Paragraph paragraph = mHighlights.get(0);
		return mAdapter.indexOf(paragraph);
	}

	public boolean isEmpty() {
		return mHighlights.isEmpty();
	}

	public ParagraphHighlight get(int index) {
		Paragraph paragraph = mHighlights.get(index);
		return paragraph.getHighlight();
	}

	public int getCount() {
		return mHighlights.size();
	}

	public void clear() {
		for (Paragraph paragraph : mHighlights) {
			ParagraphHighlight highlight = paragraph.getHighlight();
			if (highlight != null) {
				highlight.clear();
				highlight.recycle();
				paragraph.setHighlight(null);
			}
			int index = mAdapter.indexOf(paragraph);
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
		return paragraph.getHighlight();
	}

	public void add(@NonNull Paragraph paragraph, @NonNull ParagraphHighlight paragraphHighlight) {
		paragraph.setHighlight(paragraphHighlight);
		mHighlights.add(paragraph);
	}
}