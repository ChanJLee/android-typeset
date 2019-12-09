package me.chan.texas.renderer;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.texas.annotations.Hidden;
import me.chan.texas.text.DrawableBox;
import me.chan.texas.text.Paragraph;

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
