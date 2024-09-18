package me.chan.texas.renderer.selection.visitor;

import android.graphics.RectF;

import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.TexasView;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelfDriveSelectedVisitor extends SelectedVisitor {
	private TexasView.SelectionPredicate mPredicate;
	private Object mParagraphTag;

	public void reset(RenderOption renderOption, TexasView.SelectionPredicate predicate) {
		mPredicate = predicate;
		super.reset(true, renderOption);
	}

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {
		super.onVisitParagraphStart(paragraph);
		mParagraphTag = paragraph.getTag();
	}

	@Override
	protected boolean selected(Box box, RectF inner, RectF outer) {
		return mPredicate.apply(mParagraphTag, box.getTag());
	}

	@Override
	public void onVisitParagraphEnd(Paragraph paragraph) {
		mPredicate = null;
		super.onVisitParagraphEnd(paragraph);
	}
}
