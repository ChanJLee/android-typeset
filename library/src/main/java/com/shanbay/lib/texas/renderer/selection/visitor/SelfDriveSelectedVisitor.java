package com.shanbay.lib.texas.renderer.selection.visitor;

import android.graphics.RectF;

import androidx.annotation.RestrictTo;

import com.shanbay.lib.texas.renderer.RenderOption;
import com.shanbay.lib.texas.renderer.TexasView;
import com.shanbay.lib.texas.text.Paragraph;
import com.shanbay.lib.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelfDriveSelectedVisitor extends SelectedVisitor {
	private TexasView.SelectionPredicate mPredicate;

	public void reset(RenderOption renderOption, TexasView.SelectionPredicate predicate) {
		mPredicate = predicate;
		super.reset(true, renderOption);
	}

	@Override
	protected boolean selected(Box box, RectF inner, RectF outer) {
		return mPredicate.apply(mSelection.getParagraph().getTag(), box.getTag());
	}

	@Override
	public void onVisitParagraphEnd(Paragraph paragraph) {
		mPredicate = null;
		super.onVisitParagraphEnd(paragraph);
	}
}
