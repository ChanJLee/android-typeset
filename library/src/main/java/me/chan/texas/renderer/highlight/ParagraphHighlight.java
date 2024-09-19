package me.chan.texas.renderer.highlight;

import me.chan.texas.misc.BitBucket;
import me.chan.texas.misc.DefaultRecyclable;
import me.chan.texas.misc.ObjectPool;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.layout.Box;

public class ParagraphHighlight extends DefaultRecyclable {
	private static final ObjectPool<ParagraphHighlight> POOL = new ObjectPool<>(32);

	private int mTextColor;
	private float mYInParagraph;
	private final BitBucket mBitBucket = new BitBucket(128);

	private ParagraphHighlight() {
	}

	public int getTextColor() {
		return mTextColor;
	}

	public void updateStyle(RenderOption renderOption) {
		mTextColor = renderOption.getSpanHighlightTextColor();
	}

	public boolean isHighlight(Box box) {
		return mBitBucket.get(box.getSeq());
	}

	public float getYInParagraph() {
		return mYInParagraph;
	}

	@Override
	protected void onRecycle() {
		clear();
		mYInParagraph = 0;
		POOL.release(this);
	}

	public void clear() {
		mBitBucket.clear();
	}

	public static ParagraphHighlight obtain(float yInParagraph) {
		ParagraphHighlight highlight = POOL.acquire();
		if (highlight == null) {
			highlight = new ParagraphHighlight();
		}

		highlight.mYInParagraph = yInParagraph;
		highlight.reuse();
		return highlight;
	}

	public void addBox(Box box) {
		mBitBucket.set(box.getSeq(), true);
	}
}
