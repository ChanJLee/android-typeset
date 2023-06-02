package com.shanbay.lib.texas.renderer.highlight.visitor;

import android.graphics.RectF;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.renderer.ParagraphVisitor;
import com.shanbay.lib.texas.renderer.TexasView;
import com.shanbay.lib.texas.renderer.highlight.ParagraphHighlight;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.layout.Box;
import com.shanbay.lib.texas.text.layout.Line;
import com.shanbay.lib.texas.text.layout.TextBox;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class HighlightParagraphVisitor extends ParagraphVisitor {

	private Paragraph mParagraph;
	private TexasView.HighlightPredicate mPredicate;
	private ParagraphHighlight mParagraphHighlight;

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {

	}

	@Override
	protected void onVisitParagraphEnd(Paragraph paragraph) {

	}

	@Override
	protected void onVisitLineStart(Line line, float x, float y) {

	}

	@Override
	protected void onVisitLineEnd(Line line, float x, float y) {

	}

	@Override
	protected void onVisitBox(Box box, RectF inner, RectF outer) {
		if (!(box instanceof TextBox)) {
			return;
		}

		TextBox textBox = (TextBox) box;
		Object tag = textBox.getTag();
		if (!mPredicate.apply(mParagraph.getTag(), tag)) {
			return;
		}

		if (mParagraphHighlight == null) {
			mParagraphHighlight = ParagraphHighlight.obtain(inner.top, mParagraph);
		}

		mParagraphHighlight.addBox(box);
	}

	public void clear() {
		mParagraphHighlight = null;
		mPredicate = null;
		mParagraph = null;
	}

	public ParagraphHighlight getParagraphHighlight() {
		return mParagraphHighlight;
	}

	public void setParams(Paragraph paragraph, TexasView.HighlightPredicate predicate) {
		mPredicate = predicate;
		mParagraph = paragraph;
	}
}