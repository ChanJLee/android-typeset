package me.chan.texas.renderer.highlight.visitor;

import android.graphics.RectF;

import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.ParagraphVisitor;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.renderer.highlight.ParagraphHighlight;
import me.chan.texas.text.Paragraph;
import me.chan.texas.renderer.RendererContext;
import me.chan.texas.text.layout.Box;
import me.chan.texas.text.layout.Line;
import me.chan.texas.text.layout.TextBox;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class HighlightParagraphVisitor extends ParagraphVisitor {

	private TexasView.HighlightPredicate mPredicate;
	private ParagraphHighlight mParagraphHighlight;
	private Object mParagraphTag;
	private Paragraph mParagraph;

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {
		mParagraphTag = paragraph.getTag();
		mParagraph = paragraph;
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
	protected void onVisitBox(Box box, RectF inner, RectF outer, RendererContext context) {
		if (!(box instanceof TextBox)) {
			return;
		}

		TextBox textBox = (TextBox) box;
		Object tag = textBox.getTag();
		if (!mPredicate.apply(mParagraphTag, tag)) {
			textBox.removeStatus(Box.STATUS_HIGHLIGHT);
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
		mParagraphTag = null;
	}

	public ParagraphHighlight getParagraphHighlight() {
		return mParagraphHighlight;
	}

	public void setParams(Paragraph paragraph, TexasView.HighlightPredicate predicate) {
		mPredicate = predicate;
		mParagraph = paragraph;
	}
}