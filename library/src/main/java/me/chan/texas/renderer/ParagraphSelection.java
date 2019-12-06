package me.chan.texas.renderer;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.texas.text.Paragraph;

public abstract class ParagraphSelection {
	private Paragraph mParagraph;
	private boolean mIsSelectedByLongClicked;

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

	public abstract void clearSelection();

	public abstract void draw(Canvas canvas, TextPaint textPaint, float radius);
}
