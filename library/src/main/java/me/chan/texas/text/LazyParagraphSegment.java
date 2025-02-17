package me.chan.texas.text;

import android.graphics.Rect;

import androidx.annotation.Nullable;

import me.chan.texas.source.ParagraphSource;

public class LazyParagraphSegment implements Segment {
	private final Rect mRect = new Rect();
	private final int mId = Segment.nextId();
	private final ParagraphSource mParagraphSource;

	public LazyParagraphSegment(ParagraphSource paragraphSource) {
		mParagraphSource = paragraphSource;
	}

	public ParagraphSource getParagraphSource() {
		return mParagraphSource;
	}

	@Nullable
	@Override
	public Object getTag() {
		return null;
	}

	@Override
	public void getRect(Rect rect) {
		rect.set(mRect);
	}

	@Nullable
	@Override
	public Rect getRect() {
		return mRect;
	}

	@Override
	public void setRect(Rect rect) {
		mRect.set(rect);
	}

	@Override
	public void recycle() {

	}

	@Override
	public boolean isRecycled() {
		return false;
	}

	@Override
	public int getId() {
		return mId;
	}
}
