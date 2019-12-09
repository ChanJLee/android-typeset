package com.shanbay.lib.texas.renderer;

import android.graphics.Canvas;
import android.text.TextPaint;

import com.shanbay.lib.texas.annotations.Hidden;
import com.shanbay.lib.texas.text.DrawableBox;
import com.shanbay.lib.texas.text.Paragraph;

/**
 * drawable被选中的区域
 */
@Hidden
class DrawableParagraphSelection extends ParagraphSelection {

	private DrawableBox mDrawableBox;

	DrawableParagraphSelection(Paragraph paragraph, boolean isSelectedByLongClicked, DrawableBox drawableBox) {
		super(paragraph, isSelectedByLongClicked);
		mDrawableBox = drawableBox;
	}

	@Override
	public void clearSelection() {
		mDrawableBox.setSelected(false);
	}

	@Override
	public void draw(Canvas canvas, TextPaint textPaint, float radius) {
		/* do nothing */
	}
}
