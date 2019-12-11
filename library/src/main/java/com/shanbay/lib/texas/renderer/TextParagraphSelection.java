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

	TextParagraphSelection(Paragraph paragraph, boolean isSelectedByLongClicked, float touchYInView, float touchYOnScreen, float width) {
		super(paragraph, isSelectedByLongClicked, touchYInView, touchYOnScreen, width);
	}

	void addSelectArea(RectF rectF) {
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

	void addBox(Box box) {
		mBoxes.add(box);
	}

	boolean isEmpty() {
		return mBoxes.isEmpty();
	}
}