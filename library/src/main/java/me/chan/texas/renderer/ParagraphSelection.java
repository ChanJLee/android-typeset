package me.chan.texas.renderer;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.text.Box;
import me.chan.texas.text.Paragraph;

@Hidden
class ParagraphSelection {
	private Paragraph mParagraph;
	private List<RectF> mBackgrounds = new ArrayList<>();
	private List<Box> mBoxes = new ArrayList<>();
	private boolean mIsLongClicked;

	public ParagraphSelection(Paragraph paragraph, boolean isLongClicked) {
		mParagraph = paragraph;
		mIsLongClicked = isLongClicked;
	}

	public Paragraph getParagraph() {
		return mParagraph;
	}

	public void setParagraph(Paragraph paragraph) {
		mParagraph = paragraph;
	}

	public void addSelectArea(RectF rectF) {
		mBackgrounds.add(rectF);
	}

	public boolean isLongClickSelected() {
		return mIsLongClicked;
	}

	public void draw(Canvas canvas, TextPaint textPaint, float radius) {
		for (RectF rectF : mBackgrounds) {
			canvas.drawRoundRect(rectF, radius, radius, textPaint);
		}
	}

	public void clear() {
		for (Box box : mBoxes) {
			box.setSelected(false);
		}
		mBackgrounds.clear();
	}

	public void addBox(Box box) {
		mBoxes.add(box);
	}
}