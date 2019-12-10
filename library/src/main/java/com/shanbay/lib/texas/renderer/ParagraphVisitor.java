package com.shanbay.lib.texas.renderer;

import com.shanbay.lib.texas.text.Box;
import com.shanbay.lib.texas.text.Gravity;
import com.shanbay.lib.texas.text.Line;
import com.shanbay.lib.texas.text.Paragraph;

abstract class ParagraphVisitor {

	public void visit(Paragraph paragraph, float width, RenderOption renderOption) {
		onVisitParagraph(paragraph);
		float y = 0;
		int lineCount = paragraph.getLineCount();
		for (int i = 0; i < lineCount; ++i) {

			Line line = paragraph.getLine(i);
			y += line.getLineHeight();
			float x;
			Gravity gravity = line.getGravity();
			if (gravity == Gravity.CENTER) {
				x = (width - line.getLineWidth()) / 2f;
			} else if (gravity == Gravity.RIGHT) {
				x = (width - line.getLineWidth());
			} else {
				x = 0;
			}
			visitLine(line, x, y);
			y += renderOption.getLineSpace();
		}
		onVisitParagraphEnd(paragraph);
	}

	private void visitLine(Line line, float bottomX, float bottomY) {
		onVisitLine(line, bottomX, bottomY);

		float spaceWidth = line.getSpaceWidth();
		int boxSize = line.getCount();
		for (int i = 0; i < boxSize; ++i) {
			Box box = line.getBox(i);
			float width = box.getWidth();

			float left = bottomX;
			float right = (float) Math.ceil(bottomX + width);
			float top = (float) Math.ceil(bottomY - line.getLineHeight());
			float bottom = bottomY;
			onVisitBox(box, left, top, right, bottom);
			bottomX += (spaceWidth + width);
		}

		onVisitLineEnd(line, bottomX, bottomY);
	}

	protected abstract void onVisitParagraph(Paragraph paragraph);

	protected abstract void onVisitParagraphEnd(Paragraph paragraph);

	protected abstract void onVisitLine(Line line, float x, float y);

	protected abstract void onVisitLineEnd(Line line, float x, float y);

	protected abstract void onVisitBox(Box box, float left, float top, float right, float bottom);
}
