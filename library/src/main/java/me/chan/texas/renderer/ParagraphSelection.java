package me.chan.texas.renderer;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.texas.text.Paragraph;

public abstract class ParagraphSelection {
	private Paragraph mParagraph;
	private boolean mIsSelectedByLongClicked;
	private float mTopEdgeInWindow = -1;
	private float mBottomEdgeInWindow = -1;

	public ParagraphSelection(Paragraph paragraph, boolean isSelectedByLongClicked) {
		mParagraph = paragraph;
		mIsSelectedByLongClicked = isSelectedByLongClicked;
	}

	public Paragraph getParagraph() {
		return mParagraph;
	}

	public void setParagraph(Paragraph paragraph) {
		mParagraph = paragraph;
	}

	public boolean isSelectedByLongClick() {
		return mIsSelectedByLongClicked;
	}

	public void setSelectedByLongClicked(boolean selectedByLongClicked) {
		mIsSelectedByLongClicked = selectedByLongClicked;
	}

	public float getTopEdgeInWindow() {
		return mTopEdgeInWindow;
	}

	public void setTopEdgeInWindow(float topEdgeInWindow) {
		mTopEdgeInWindow = topEdgeInWindow;
	}

	public float getBottomEdgeInWindow() {
		return mBottomEdgeInWindow;
	}

	public void setBottomEdgeInWindow(float bottomEdgeInWindow) {
		mBottomEdgeInWindow = bottomEdgeInWindow;
	}

	public abstract void clearSelection();

	public abstract void draw(Canvas canvas, TextPaint textPaint, float radius);
}
