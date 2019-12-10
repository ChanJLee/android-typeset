package com.shanbay.lib.texas.renderer;

import android.graphics.Canvas;
import android.text.TextPaint;

import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.text.Paragraph;

/**
 * 文本选中区域
 */
@Hidden
abstract class ParagraphSelection {
	private Paragraph mParagraph;
	private boolean mIsSelectedByLongClicked;
	private float mTopEdgeOnScreen = -1;
	private float mBottomEdgeOnScreen = -1;

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

	public float getTopEdgeOnScreen() {
		return mTopEdgeOnScreen;
	}

	public void setTopEdgeOnScreen(float topEdgeOnScreen) {
		mTopEdgeOnScreen = topEdgeOnScreen;
	}

	public float getBottomEdgeOnScreen() {
		return mBottomEdgeOnScreen;
	}

	public void setBottomEdgeOnScreen(float bottomEdgeOnScreen) {
		mBottomEdgeOnScreen = bottomEdgeOnScreen;
	}

	public abstract void clearSelection();

	public abstract void draw(Canvas canvas, TextPaint textPaint, float radius);
}
