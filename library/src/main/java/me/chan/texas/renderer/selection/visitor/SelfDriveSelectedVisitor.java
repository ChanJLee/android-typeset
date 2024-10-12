package me.chan.texas.renderer.selection.visitor;

import android.graphics.RectF;

import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SelfDriveSelectedVisitor extends SelectedVisitor {
	private ParagraphPredicates mPredicates;

	public void reset(RenderOption renderOption, ParagraphPredicates predicates) {
		mPredicates = predicates;
		super.reset(true, renderOption);
	}

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {
		if (!mPredicates.acceptParagraph(paragraph.getTag())) {
			sendVisitSig(SIG_STOP_PARA_VISIT);
		}
	}

	@Override
	protected boolean selected(Box box, RectF inner, RectF outer) {
		return mPredicates.acceptSpan(box.getTag());
	}

	@Override
	public void onVisitParagraphEnd(Paragraph paragraph) {
		mPredicates = null;
		super.onVisitParagraphEnd(paragraph);
	}
}
