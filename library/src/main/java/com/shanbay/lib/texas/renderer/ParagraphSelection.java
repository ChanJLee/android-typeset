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
	private float mTopEdgeInScreen = -1;
	private float mBottomEdgeInScreen = -1;

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

	public float getTopEdgeInScreen() {
		return mTopEdgeInScreen;
	}

	public void setTopEdgeInScreen(float topEdgeInScreen) {
		mTopEdgeInScreen = topEdgeInScreen;
	}

	public float getBottomEdgeInScreen() {
		return mBottomEdgeInScreen;
	}

	public void setBottomEdgeInScreen(float bottomEdgeInScreen) {
		mBottomEdgeInScreen = bottomEdgeInScreen;
	}

	public abstract void clearSelection();

	public abstract void draw(Canvas canvas, TextPaint textPaint, float radius);
}
