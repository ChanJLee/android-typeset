package me.chan.texas.renderer.selection.visitor;

import me.chan.texas.misc.RectF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.TextStyles;
import me.chan.texas.text.layout.Box;

@RestrictTo(RestrictTo.Scope.LIBRARY)
public class PredicatesDriveSelectedVisitor extends SelectedVisitor {
	private ParagraphPredicates mPredicates;

	public void reset(Selection.Type type, RenderOption renderOption, ParagraphPredicates predicates, Paragraph paragraph, @NonNull Selection.Styles styles) {
		mPredicates = predicates;

		super.reset(type, styles, paragraph, renderOption);
	}

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {
		if (paragraph != null && !mPredicates.acceptParagraph(paragraph)) {
			ParagraphSelection prev = paragraph.getSelection(mType);
			if (prev != null) {
				prev.recycle();
			}
			paragraph.setSelection(mType, null);
			sendVisitSig(SIG_STOP_PARA_VISIT);
		}
	}

	@Override
	protected boolean selected(Box box, RectF inner, RectF outer) {
		return mPredicates.acceptSpan(box);
	}

	@Override
	public void onVisitParagraphEnd(Paragraph paragraph) {
		mPredicates = null;
		super.onVisitParagraphEnd(paragraph);
	}
}
