package com.shanbay.lib.texas.renderer;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.text.Box;
import com.shanbay.lib.texas.text.Paragraph;

/**
 * 文本选中区间
 */
@Hidden
class TextParagraphSelection extends ParagraphSelection {
	private List<RectF> mBackgrounds = new ArrayList<>();
	private List<Box> mBoxes = new ArrayList<>();

	public TextParagraphSelection(Paragraph paragraph, boolean isLongClicked) {
		super(paragraph, isLongClicked);
	}

	public void addSelectArea(RectF rectF) {
		mBackgrounds.add(rectF);
	}

	@Override
	public void draw(Canvas canvas, TextPaint textPaint, float radius) {
		for (RectF rectF : mBackgrounds) {
			canvas.drawRoundRect(rectF, radius, radius, textPaint);
		}
	}

	@Override
	public void clearSelection() {
		for (Box box : mBoxes) {
			box.setSelected(false);
		}
		mBackgrounds.clear();
	}

	public void addBox(Box box) {
		mBoxes.add(box);
	}

	public boolean isEmpty() {
		return mBoxes.isEmpty();
	}
}