package me.chan.texas.renderer.highlight;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;

public class ParagraphHighlight extends DefaultRecyclable {
	private static final ObjectPool<ParagraphHighlight> POOL = new ObjectPool<>(32);

	private int mTextColor;
	private Paragraph mParagraph;
	private float mYInParagraph;
	private final List<Box> mBoxes = new ArrayList<>(32);

	private ParagraphHighlight() {
	}

	public int getTextColor() {
		return mTextColor;
	}

	public void updateStyle(RenderOption renderOption) {
		mTextColor = renderOption.getSpanHighlightTextColor();
	}

	public boolean isHighlight(Box box) {
		return box.containsStatus(Box.STATUS_HIGHLIGHT);
	}

	public float getYInParagraph() {
		return mYInParagraph;
	}

	public Paragraph getParagraph() {
		return mParagraph;
	}

	@Override
	protected void onRecycle() {
		clear();

		mParagraph = null;
		mYInParagraph = 0;
		POOL.release(this);
	}

	public void clear() {
		for (Box box : mBoxes) {
			box.removeStatus(Box.STATUS_HIGHLIGHT);
		}
		mBoxes.clear();
	}

	public static ParagraphHighlight obtain(float yInParagraph, Paragraph paragraph) {
		ParagraphHighlight highlight = POOL.acquire();
		if (highlight == null) {
			highlight = new ParagraphHighlight();
		}

		highlight.mYInParagraph = yInParagraph;
		highlight.mParagraph = paragraph;
		highlight.reuse();
		return highlight;
	}

	public void addBox(Box box) {
		box.addStatus(Box.STATUS_HIGHLIGHT);
		mBoxes.add(box);
	}
}
