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
public class Selection {
	private Paragraph mParagraph;
	private List<RectF> mBackgrounds = new ArrayList<>();
	private List<Box> mBoxes = new ArrayList<>();

	public Selection(Paragraph paragraph) {
		mParagraph = paragraph;
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

	public void draw(Canvas canvas, TextPaint textPaint, float radius) {
		for (RectF rectF : mBackgrounds) {
			canvas.drawRoundRect(rectF, radius, radius, textPaint);
		}
	}

	public void addBox(Box box) {
		mBoxes.add(box);
	}

	public boolean hasContent() {
		return !mBoxes.isEmpty();
	}
}