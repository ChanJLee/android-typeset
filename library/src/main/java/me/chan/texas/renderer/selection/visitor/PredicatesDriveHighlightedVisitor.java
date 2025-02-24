package me.chan.texas.renderer.selection.visitor;

import android.graphics.RectF;

import androidx.annotation.NonNull;

import me.chan.texas.renderer.ParagraphPredicates;
import me.chan.texas.renderer.RenderOption;
import me.chan.texas.renderer.selection.ParagraphSelection;
import me.chan.texas.renderer.selection.Selection;
import me.chan.texas.text.Paragraph;
import me.chan.texas.text.layout.Box;

public class PredicatesDriveHighlightedVisitor extends SelectedVisitor {
	private ParagraphPredicates mPredicates;

	public void reset(RenderOption renderOption, ParagraphPredicates predicates, Paragraph paragraph, @NonNull Selection.Styles styles) {
		mPredicates = predicates;

		super.reset(styles, paragraph, renderOption);
	}

	@Override
	protected void onVisitParagraphStart(Paragraph paragraph) {
		if (!mPredicates.acceptParagraph(paragraph.getTag())) {
			ParagraphSelection prev = paragraph.getHighlight();
			if (prev != null) {
				prev.recycle();
			}
			paragraph.setHighlight(null);
			sendVisitSig(SIG_STOP_PARA_VISIT);
		}
	}

	@Override
	protected final void onClearSelection(Paragraph paragraph) {
		ParagraphSelection prev = paragraph.getHighlight();
		paragraph.setHighlight(null);
		if (prev != null) {
			prev.recycle();
		}
	}

	@Override
	protected void onSetSelection(Paragraph paragraph, ParagraphSelection selection) {
		if (mSelection.isEmpty()) {
			mSelection.recycle();
		} else {
			paragraph.setHighlight(mSelection);
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
