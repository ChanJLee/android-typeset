package me.chan.texas.renderer;

import android.graphics.Canvas;
import android.text.TextPaint;

import me.chan.texas.text.DrawableBox;
import me.chan.texas.text.Paragraph;

public class DrawableParagraphSelection extends ParagraphSelection {

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
