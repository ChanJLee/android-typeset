package me.chan.texas.renderer.highlight.visitor;

import android.graphics.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.ParagraphPredicates;
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

	private ParagraphPredicates mPredicates;
	private ParagraphHighlight mParagraphHighlight;

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {
		if (!mPredicates.acceptParagraph(paragraph.getTag())) {
			sendVisitSig(SIG_STOP_PARA_VISIT);
		}
	}

	@Override
	protected void onVisitParagraphEnd(Paragraph paragraph) {
		ParagraphHighlight highlight = paragraph.getHighlight();
		if (highlight != null) {
			highlight.recycle();
		}
		paragraph.setHighlight(mParagraphHighlight);
	}

	@Override
	protected void onVisitLineStart(Line line, float x, float y) {

	}

	@Override
	protected void onVisitLineEnd(Line line, float x, float y) {

	}

	@Override
	protected void onVisitBox(Box box, RectF inner, RectF outer, @NonNull RendererContext context) {
		if (!(box instanceof TextBox)) {
			return;
		}

		TextBox textBox = (TextBox) box;
		Object tag = textBox.getTag();
		if (!mPredicates.acceptSpan(tag)) {
			return;
		}

		if (mParagraphHighlight == null) {
			mParagraphHighlight = ParagraphHighlight.obtain(inner.top);
		}

		mParagraphHighlight.addBox(box);
	}

	public void clear() {
		mParagraphHighlight = null;
		mPredicates = null;
	}

	public ParagraphHighlight getParagraphHighlight() {
		return mParagraphHighlight;
	}

	public void setParams(ParagraphPredicates predicates) {
		mPredicates = predicates;
	}
}